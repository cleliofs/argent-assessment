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

    private Map<String, Map<String, String>> trustIndicatorMap;

    private BlockChainService blockChainService;

    public TrustIndicatorWallet(@Value("${wallet.address.owner}") String addressOwner,
                                @Value("${wallet.trustIndicator.minimum:0}") int trustIndicatorMinimum) {
        this.addressOwner = addressOwner;
        this.trustIndicatorMinimum = trustIndicatorMinimum;
        this.trustIndicatorMap = new HashMap<>();
    }

    public void transfer(String addressTo, int value) {
        final int trustIndicatorToAddress = getTrustIndicatorToAddress(addressTo);
        if (trustIndicatorToAddress < trustIndicatorMinimum) {
            log.warn(format("Transfer to address [%s] under minimum trust indicator of [%s]", addressTo, trustIndicatorMinimum));
        }

        blockChainService.invokeSmartContractTransfer(addressTo, value);
    }

    public int getTrustIndicatorToAddress(String addressTo) {
        return geTrustIndicator(addressOwner, addressTo);
    }

    public int getTrustIndicatorBetweenAddresses(String addressFrom, String addressTo) {
        return geTrustIndicator(addressFrom, addressTo);
    }

    @Autowired
    public void setBlockChainService(BlockChainService blockChainService) {
        this.blockChainService = blockChainService;
    }

    private int geTrustIndicator(String addressFrom, String addressTo) {
        final List<BlockNode> blockChain = blockChainService.fetchBlockChain();
        updateTrustIndicatorMap(blockChain);
        final String shortestTrustIndicatorString = trustIndicatorMap.get(addressFrom).values().stream()
                .filter(v -> v.contains(addressTo))
                .collect(Collectors.toList()).stream()
                .sorted((String s1, String s2) -> valueOf(s1.length()).compareTo(valueOf(s2)))
                .findFirst()
                .orElse("");
        return shortestTrustIndicatorString.indexOf(addressTo);
    }

    private void updateTrustIndicatorMap(List<BlockNode> blockChain) {
        if (blockChain.size() > trustIndicatorMap.size()) {
            blockChain.forEach(block -> {
                final String addressFrom = block.getAddressFrom();
                final String addressTo = block.getAddressTo();
                final String hashLabel = addressFrom + addressTo;

                Map<String, String> addressFromIndicatorNodeMap = trustIndicatorMap.get(addressFrom);
                if (addressFromIndicatorNodeMap == null) {
                    addressFromIndicatorNodeMap = new HashMap<>();
                }

                addressFromIndicatorNodeMap.putIfAbsent(addressTo, hashLabel);
                trustIndicatorMap.put(addressFrom, addressFromIndicatorNodeMap);

                final Iterator<BlockNode> iterator = new LinkedList<>(blockChain.subList(0, blockChain.indexOf(block))).descendingIterator();
                BlockNode currentBlock = block;

                while (iterator.hasNext()) {
                    final BlockNode previousBlock = iterator.next();

                    if (!previousBlock.equals(currentBlock) && previousBlock.getAddressTo().equalsIgnoreCase(currentBlock.getAddressFrom())) {
                        final Map<String, String> indicatorMapFromAddress = trustIndicatorMap.get(previousBlock.getAddressFrom());
                        String currentHashLabel = indicatorMapFromAddress.get(currentBlock.getAddressFrom());
                        String newHashLabel = currentHashLabel + block.getAddressTo();
                        indicatorMapFromAddress.put(currentBlock.getAddressFrom(), newHashLabel);
                    }

                    currentBlock = previousBlock;
                }

            });
        }
    }
}
