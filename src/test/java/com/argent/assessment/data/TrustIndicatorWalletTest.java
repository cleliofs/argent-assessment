package com.argent.assessment.data;

import com.argent.assessment.service.BlockChainService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static com.argent.assessment.data.BlockChain.sha256Hex;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TrustIndicatorWalletTest {

    private TrustIndicatorWallet trustIndicatorWallet;

    private List<BlockNode> blockChainList;

    @Mock
    private BlockChainService blockChainService;

    private final Address addressA = new Address("A", false);
    private final Address addressB = new Address("B", false);
    private final Address addressC = new Address("C", false);
    private final Address addressD = new Address("D", false);
    private final Address addressE = new Address("E", false);
    private final Address addressF = new Address("F", false);

    @Before
    public void setup() {
        blockChainList = createBlockChain();
        trustIndicatorWallet = new TrustIndicatorWallet("A", 0, false);
        trustIndicatorWallet.setBlockChainService(blockChainService);
    }

    @Test
    public void testTrustIndicatorFromWalletOwnerToAddresses() {
        // when
        when(blockChainService.fetchBlockChain()).thenReturn(blockChainList);

        final int trustIndicatorFromOwnerToB = trustIndicatorWallet.getTrustIndicatorToAddress(addressB);
        final int trustIndicatorFromOwnerToC = trustIndicatorWallet.getTrustIndicatorToAddress(addressC);
        final int trustIndicatorFromOwnerToD = trustIndicatorWallet.getTrustIndicatorToAddress(addressD);

        verify(blockChainService, times(3)).fetchBlockChain();

        assertThat(trustIndicatorFromOwnerToB, is(1));
        assertThat(trustIndicatorFromOwnerToC, is(2));
        assertThat(trustIndicatorFromOwnerToD, is(3));
    }

    @Test
    public void testTrustIndicatorBetweenAddresses() {
        // when
        when(blockChainService.fetchBlockChain()).thenReturn(blockChainList);

        final int trustIndicatorAB = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressA, addressB);
        final int trustIndicatorAC = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressA, addressC);
        final int trustIndicatorAD = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressA, addressD);
        final int trustIndicatorAE = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressA, addressE);
        final int trustIndicatorAF = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressA, addressF);
        final int trustIndicatorBC = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressB, addressC);
        final int trustIndicatorBD = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressB, addressD);
        final int trustIndicatorBE = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressB, addressE);
        final int trustIndicatorBF = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressB, addressF);
        final int trustIndicatorCD = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressC, addressD);
        final int trustIndicatorCE = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressC, addressE);
        final int trustIndicatorCF = trustIndicatorWallet.getTrustIndicatorBetweenAddresses(addressC, addressF);

        // then
        verify(blockChainService, times(12)).fetchBlockChain();

        assertThat(trustIndicatorAB, is(1));
        assertThat(trustIndicatorAC, is(2));
        assertThat(trustIndicatorAD, is(3));
        assertThat(trustIndicatorAE, is(4));
        assertThat(trustIndicatorAF, is(5));
        assertThat(trustIndicatorBC, is(1));
        assertThat(trustIndicatorBD, is(2));
        assertThat(trustIndicatorBE, is(3));
        assertThat(trustIndicatorBF, is(4));
        assertThat(trustIndicatorCD, is(1));
        assertThat(trustIndicatorCE, is(2));
        assertThat(trustIndicatorCF, is(3));
    }

    @Test
    public void testTransferToAccountWhenExcludingCalculation() {
        // given
        final Address addressG = new Address("G", true);

        // when
        trustIndicatorWallet.transfer(addressG, 20);

        // then
        verify(blockChainService).invokeSmartContractTransfer(addressG, 20);
    }

    @Test
    public void testChangeOwnerAddressUsingWallet() {
        // given
        final String newOwnerAddress = "B";

        // when
        trustIndicatorWallet.changeOwner(newOwnerAddress);

        // then
        verify(blockChainService).invokeSmartContractChangeOwner(new Address(newOwnerAddress, false));
    }

    private List<BlockNode> createBlockChain() {
        final BlockNode genesisBlockN1 = new BlockNode(addressA, addressB, 100, sha256Hex("AB"), "0");
        // B -> C
        final BlockNode blockN2 = new BlockNode(addressB, addressC, 500, sha256Hex("BC"), genesisBlockN1.getHash());
        // C -> D
        final BlockNode blockN3 = new BlockNode(addressC, addressD, 20, sha256Hex("CD"), blockN2.getHash());
        // D -> E
        final BlockNode blockN4 = new BlockNode(addressD, addressE, 50, sha256Hex("DE"), blockN3.getHash());
        // E -> F
        final BlockNode blockN5 = new BlockNode(addressE, addressF, 25, sha256Hex("EF"), blockN4.getHash());

        blockChainList = new LinkedList<>();
        blockChainList.add(genesisBlockN1);
        blockChainList.add(blockN2);
        blockChainList.add(blockN3);
        blockChainList.add(blockN4);
        blockChainList.add(blockN5);
        return blockChainList;
    }
}
