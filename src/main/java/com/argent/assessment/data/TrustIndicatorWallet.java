package com.argent.assessment.data;

import com.argent.assessment.service.BlockChainService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;

@Slf4j
@Component
public class TrustIndicatorWallet {

    @Getter private String addressOwner;
    @Getter private int trustIndicatorMinimum;
    @Getter private boolean walletTrustIndicatorCalculationExclusion;

    private Map<String, Map<String, String>> trustIndicatorMap;

    private BlockChainService blockChainService;

    public TrustIndicatorWallet(@Value("${wallet.address.owner}") String addressOwner,
                                @Value("${wallet.trustIndicator.minimum:0}") int trustIndicatorMinimum,
                                @Value("${wallet.trustIndicator.exclusion}") boolean walletTrustIndicatorCalculationExclusion) {
        this.addressOwner = addressOwner;
        this.trustIndicatorMinimum = trustIndicatorMinimum;
        this.walletTrustIndicatorCalculationExclusion = walletTrustIndicatorCalculationExclusion;
        this.trustIndicatorMap = new HashMap<>();
    }

    public void transfer(Address addressTo, int value) {
        if (!addressTo.trustIndicatorCalculationExclusion) {
            final int trustIndicatorToAddress = getTrustIndicatorToAddress(addressTo);
            if (trustIndicatorToAddress < trustIndicatorMinimum) {
                log.warn(format("Transfer to address [%s] under minimum trust indicator of [%s]", addressTo, trustIndicatorMinimum));
            }
        }

        log.info("Invoke Ethereum smart contract wallet to transfer = %s", value);
        blockChainService.invokeSmartContractTransfer(addressTo, value);
    }

    public void changeOwner(String newAddressOwner) {
        this.addressOwner = newAddressOwner;

        log.info("Change Ethereum owner address from [%s] to new address = [%s]", addressOwner, newAddressOwner);
        blockChainService.changeOwner(new Address(newAddressOwner, false));
    }

    public int getTrustIndicatorToAddress(Address addressTo) {
        return geTrustIndicator(new Address(addressOwner, walletTrustIndicatorCalculationExclusion), addressTo);
    }

    public int getTrustIndicatorBetweenAddresses(Address addressFrom, Address addressTo) {
        return geTrustIndicator(addressFrom, addressTo);
    }

    @Autowired
    public void setBlockChainService(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    private int geTrustIndicator(Address addressFrom, Address addressTo) {
        final List<BlockNode> blockChain = blockChainService.fetchBlockChain();
        updateTrustIndicatorMap(blockChain);
        final String shortestTrustIndicatorString = trustIndicatorMap.get(addressFrom.getAddress()).values().stream()
                .filter(v -> v.contains(addressTo.getAddress()))
                .collect(Collectors.toList()).stream()
                .sorted((String s1, String s2) -> valueOf(s1.length()).compareTo(valueOf(s2)))
                .findFirst()
                .orElse("");
        return shortestTrustIndicatorString.indexOf(addressTo.getAddress());
    }

    private void updateTrustIndicatorMap(List<BlockNode> blockChain) {
        if (blockChain.size() > trustIndicatorMap.size()) {
            blockChain.forEach(block -> {

                if (!block.addressFrom.trustIndicatorCalculationExclusion) {

                    final String blockAddressFrom = block.getAddressFrom().getAddress();
                    final String blockAddressTo = block.getAddressTo().getAddress();
                    final String hashLabel = blockAddressFrom + blockAddressTo;

                    Map<String, String> addressFromIndicatorNodeMap = trustIndicatorMap.get(blockAddressFrom);
                    if (addressFromIndicatorNodeMap == null) {
                        addressFromIndicatorNodeMap = new HashMap<>();
                    }

                    addressFromIndicatorNodeMap.putIfAbsent(blockAddressTo, hashLabel);
                    trustIndicatorMap.put(blockAddressFrom, addressFromIndicatorNodeMap);

                    final Iterator<BlockNode> iterator = new LinkedList<>(blockChain.subList(0, blockChain.indexOf(block))).descendingIterator();
                    BlockNode currentBlock = block;

                    while (iterator.hasNext()) {
                        final BlockNode previousBlock = iterator.next();

                        final String currBlockAddFrom = currentBlock.getAddressFrom().getAddress();
                        final String prevBlockAddFrom = previousBlock.getAddressFrom().getAddress();
                        final String prevBlockAddTo = previousBlock.getAddressTo().getAddress();

                        if (!previousBlock.equals(currentBlock) && prevBlockAddTo.equalsIgnoreCase(currBlockAddFrom)) {
                            final Map<String, String> indicatorMapFromAddress = trustIndicatorMap.get(prevBlockAddFrom);
                            String currentHashLabel = indicatorMapFromAddress.get(currBlockAddFrom);
                            String newHashLabel = currentHashLabel + blockAddressTo;
                            indicatorMapFromAddress.put(currBlockAddFrom, newHashLabel);
                        }

                        currentBlock = previousBlock;
                    }
                }

            });
        }
    }
}
