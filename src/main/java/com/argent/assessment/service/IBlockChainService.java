package com.argent.assessment.service;

import com.argent.assessment.data.Address;
import com.argent.assessment.data.BlockNode;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IBlockChainService {

    List<BlockNode> fetchBlockChain();

    void invokeSmartContractTransfer(Address addressTo, int value);

    void invokeSmartContractChangeOwner(Address newAddressOwner);

    void sendFundsToAccount(String privateKey, Address addressTo, int value) throws InterruptedException, IOException, TransactionException, ExecutionException;
}
