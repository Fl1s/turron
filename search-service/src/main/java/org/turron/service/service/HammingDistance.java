package org.turron.service.service;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for calculating Hamming distances between hash strings.
 */
@UtilityClass
public class HammingDistance {

    /**
     * Calculates the Hamming distance between two strings of equal length.
     *
     * @param hash1 the first hash string
     * @param hash2 the second hash string
     * @return the number of differing characters
     * @throws IllegalArgumentException if the lengths of the input strings are not equal
     */
    public int hammingDistance(String hash1, String hash2) {
        if (hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("Hash lengths must be equal");
        }

        int count = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates the Hamming distances between two lists of hash strings.
     *
     * @param hashes1 the first list of hashes
     * @param hashes2 the second list of hashes
     * @return a list of distances (one for each pair of hashes at the same index)
     */
    public List<Double> calculateAllDistances(List<String> hashes1, List<String> hashes2) {
        int len = Math.min(hashes1.size(), hashes2.size());
        if (len == 0) return Collections.singletonList(Double.MAX_VALUE);

        List<Double> distances = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            int d = hammingDistance(hashes1.get(i), hashes2.get(i));
            distances.add((double) d);
        }
        return distances;
    }
}

