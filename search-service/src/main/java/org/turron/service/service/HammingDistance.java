package org.turron.service.service;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class HammingDistance {
    public double calculateAverageHammingDistance(List<String> hashes1, List<String> hashes2) {
        int len = Math.min(hashes1.size(), hashes2.size());
        if (len == 0) {
            return Double.MAX_VALUE;
        }

        int totalDistance = 0;
        for (int i = 0; i < len; i++) {
            totalDistance += hammingDistance(hashes1.get(i), hashes2.get(i));
        }
        return (double) totalDistance / len;
    }

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
}
