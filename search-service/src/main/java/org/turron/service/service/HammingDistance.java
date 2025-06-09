package org.turron.service.service;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for calculating Hamming distances between pHash values represented as long integers.
 * Much faster than comparing binary strings. Welcome to the 21st century.
 */
@UtilityClass
public class HammingDistance {

    /**
     * Calculates the Hamming distance between two 64-bit hashes using XOR and bit count.
     *
     * @param hash1 the first hash as a long
     * @param hash2 the second hash as a long
     * @return the number of differing bits
     */
    public int hammingDistance(long hash1, long hash2) {
        return Long.bitCount(hash1 ^ hash2);
    }
}
