package com.argent.assessment.data;

import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.hash.Hashing.sha256;

public class BlockChain {

    private static BlockChain blockChainInstance;

    @Getter
    private List<BlockNode> blockChainList;

    private BlockChain() {
        this.blockChainList = new LinkedList<>();
    }

    public static BlockChain getInstance() {
        if (blockChainInstance == null) {
            blockChainInstance = new BlockChain();
        }

        blockChainInstance.createBlockChain();
        return blockChainInstance;
    }

    /**
     *
     * Returns a disgest hex hashing (sha256) for giving message
     *
     * @param message
     * @return
     */
    public static String sha256Hex(String message) {
        return sha256().hashString(message, StandardCharsets.UTF_8).toString();
    }

    private void createBlockChain() {
        final BlockNode genesisBlockN1 = new BlockNode("A", "B", 100, sha256Hex("AB"), "0");
        // B -> C
        final BlockNode blockN2 = new BlockNode("B", "C", 500, sha256Hex("BC"), genesisBlockN1.getHash());
        // C -> D
        final BlockNode blockN3 = new BlockNode("C", "D", 20, sha256Hex("CD"), blockN2.getHash());
        // D -> E
        final BlockNode blockN4 = new BlockNode("D", "E", 50, sha256Hex("DE"), blockN3.getHash());
        // E -> F
        final BlockNode blockN5 = new BlockNode("E", "F", 25, sha256Hex("EF"), blockN4.getHash());

        blockChainList.add(genesisBlockN1);
        blockChainList.add(blockN2);
        blockChainList.add(blockN3);
        blockChainList.add(blockN4);
        blockChainList.add(blockN5);
    }
}
