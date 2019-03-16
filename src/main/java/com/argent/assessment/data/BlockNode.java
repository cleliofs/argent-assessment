package com.argent.assessment.data;

import lombok.Data;

import java.time.Instant;

import static java.time.Instant.now;

@Data
public class BlockNode {
    public final Address addressFrom;
    public final Address addressTo;
    public final int value;
    public final String hash;
    public final String previousHash;
    public final Instant timestamp = now();
}
