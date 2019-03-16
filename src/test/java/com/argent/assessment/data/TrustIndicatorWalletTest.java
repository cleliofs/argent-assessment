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

    @Before
    public void setup() {
        blockChainList = createBlockChain();
        trustIndicatorWallet = new TrustIndicatorWallet("A", 0);
        trustIndicatorWallet.setBlockChainService(blockChainService);
    }

    @Test
    public void testTrustIndicatorFromWalletOwnerToAddresses() {
        // when
        when(blockChainService.fetchBlockChain()).thenReturn(blockChainList);

        final int trustIndicatorFromOwnerToB = trustIndicatorWallet.getTrustIndicatorToAddress("B");
        final int trustIndicatorFromOwnerToC = trustIndicatorWallet.getTrustIndicatorToAddress("C");
        final int trustIndicatorFromOwnerToD = trustIndicatorWallet.getTrustIndicatorToAddress("D");

        verify(blockChainService, times(3)).fetchBlockChain();

        assertThat(trustIndicatorFromOwnerToB, is(1));
        assertThat(trustIndicatorFromOwnerToC, is(2));
        assertThat(trustIndicatorFromOwnerToD, is(3));
    }

    @Test
    public void testTrustIndicatorBetweenAddresses() {
        // when
        when(blockChainService.fetchBlockChain()).thenReturn(blockChainList);

        final int trustIndicatorAB = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("A", "B");
        final int trustIndicatorAC = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("A", "C");
        final int trustIndicatorAD = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("A", "D");
        final int trustIndicatorAE = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("A", "E");
        final int trustIndicatorAF = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("A", "F");
        final int trustIndicatorBC = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("B", "C");
        final int trustIndicatorBD = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("B", "D");
        final int trustIndicatorBE = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("B", "E");
        final int trustIndicatorBF = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("B", "F");
        final int trustIndicatorCD = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("C", "D");
        final int trustIndicatorCE = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("C", "E");
        final int trustIndicatorCF = trustIndicatorWallet.getTrustIndicatorBetweenAddresses("C", "F");

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

    private List<BlockNode> createBlockChain() {
        // A-> B
        final BlockNode genesisBlockN1 = new BlockNode("A", "B", 100, sha256Hex("AB"), "0");
        // B -> C
        final BlockNode blockN2 = new BlockNode("B", "C", 500, sha256Hex("BC"), genesisBlockN1.getHash());
        // C -> D
        final BlockNode blockN3 = new BlockNode("C", "D", 20, sha256Hex("CD"), blockN2.getHash());
        // D -> E
        final BlockNode blockN4 = new BlockNode("D", "E", 50, sha256Hex("DE"), blockN3.getHash());
        // E -> F
        final BlockNode blockN5 = new BlockNode("E", "F", 25, sha256Hex("EF"), blockN4.getHash());

        blockChainList = new LinkedList<>();
        blockChainList.add(genesisBlockN1);
        blockChainList.add(blockN2);
        blockChainList.add(blockN3);
        blockChainList.add(blockN4);
        blockChainList.add(blockN5);
        return blockChainList;
    }
}
