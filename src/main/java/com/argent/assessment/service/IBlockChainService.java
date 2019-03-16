package com.argent.assessment.service;

import com.argent.assessment.data.Address;
import com.argent.assessment.data.BlockNode;

import java.util.List;

public interface IBlockChainService {

    List<BlockNode> fetchBlockChain();

    void invokeSmartContractTransfer(Address addressTo, int value);

    void changeOwner(Address newAddressOwner);
}
