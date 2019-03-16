package com.argent.assessment.service;

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
    public void invokeSmartContractTransfer(String addressTo, int value) {
        // interact with Ethereum smart contract Wallet
    }

}
