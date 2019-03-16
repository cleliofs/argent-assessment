package com.argent.assessment.service;

import com.argent.assessment.data.Address;
import com.argent.assessment.data.BlockChain;
import com.argent.assessment.data.BlockNode;
import com.argent.assessment.service.ethereum.Web3JClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.math.BigDecimal.valueOf;
import static org.web3j.tx.Transfer.sendFunds;
import static org.web3j.utils.Convert.Unit.ETHER;

@Slf4j
@Service
public class BlockChainService implements IBlockChainService {

    @Override
    public List<BlockNode> fetchBlockChain() {
        final BlockChain blockChain = BlockChain.getInstance();
        return blockChain.getBlockChainList();
    }

    @Override
    public void invokeSmartContractTransfer(Address addressTo, int value) {
        // interact with Ethereum smart contract Wallet
    }

    @Override
    public void invokeSmartContractChangeOwner(Address newAddressOwner) {
        // interact with Ethereum smart contract Wallet to transfer value from Wallet to address
    }

    @Override
    public void sendFundsToAccount(String privateKey, Address addressTo, int value) throws InterruptedException, IOException, TransactionException, ExecutionException {
        final Web3j web3 = new Web3JClient().getWeb3j();

        BigInteger key = new BigInteger(privateKey,16);
        ECKeyPair ecKeyPair = ECKeyPair.create(key.toByteArray());
        Credentials credentials = Credentials.create(ecKeyPair);

        final RemoteCall<TransactionReceipt> transactionReceiptRemoteCall = sendFunds(web3, credentials, addressTo.getAddress(), valueOf(value), ETHER);
        final TransactionReceipt transactionReceipt = transactionReceiptRemoteCall.sendAsync().get();
        log.info("Transaction hash information = [%s]", transactionReceipt.getTransactionHash());
    }
}
