package com.argent.assessment.service;

import com.argent.assessment.data.BlockNode;

import java.util.List;

public interface IBlockChainService {

    List<BlockNode> fetchBlockChain();

    void invokeSmartContractTransfer(String addressTo, int value);
}
