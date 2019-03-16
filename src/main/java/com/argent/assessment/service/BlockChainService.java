package com.argent.assessment.service;

import com.argent.assessment.data.Address;
import com.argent.assessment.data.BlockChain;
import com.argent.assessment.data.BlockNode;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public void changeOwner(Address newAddressOwner) {
        // interact with Ethereum smart contract Wallet to transfer value from Wallet to address
    }
}
