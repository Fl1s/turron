package org.turron.service.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.Arrays;

/**
 * Utility class to compute perceptual hashes of images using Discrete Cosine Transform (DCT).
 * Implements a variation of pHash.
 */
@Slf4j
@UtilityClass
public class PerceptualHash {

    private static final int SIZE = 32;
    private static final int SMALL_SIZE = 8;

    /**
     * Computes a perceptual hash (pHash) for the given image.
     *
     * @param image the input image
     * @return the binary hash string
     */
    public String compute(BufferedImage image) {
        if (image == null) throw new IllegalArgumentException("Image cannot be null");

        BufferedImage gray = toGrayscale(resize(image, SIZE, SIZE));
        double[][] pixels = getGrayMatrix(gray);
        double[][] dct = applyDCT(pixels);

        double[] dctVals = new double[SMALL_SIZE * SMALL_SIZE - 1];
        int index = 0;
        for (int x = 0; x < SMALL_SIZE; x++) {
            for (int y = 0; y < SMALL_SIZE; y++) {
                if (x == 0 && y == 0) continue;
                dctVals[index++] = dct[x][y];
            }
        }

        double median = median(dctVals);
        StringBuilder hash = new StringBuilder();
        for (double v : dctVals) {
            hash.append(v > median ? '1' : '0');
        }

        return hash.toString();
    }

    private BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resized.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return resized;
    }

    private BufferedImage toGrayscale(BufferedImage image) {
        BufferedImage gray = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        ColorConvertOp op = new ColorConvertOp(image.getColorModel().getColorSpace(), gray.getColorModel().getColorSpace(), null);
        op.filter(image, gray);
        return gray;
    }

    private double[][] getGrayMatrix(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        double[][] result = new double[w][h];
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = image.getRGB(x, y) & 0xFF;
                result[x][y] = rgb;
            }
        }
        return result;
    }

    private double[][] applyDCT(double[][] f) {
        int N = f.length;
        double[][] F = new double[N][N];
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        sum += Math.cos(((2 * i + 1) * u * Math.PI) / (2 * N)) *
                                Math.cos(((2 * j + 1) * v * Math.PI) / (2 * N)) *
                                f[i][j];
                    }
                }
                double cu = (u == 0) ? 1.0 / Math.sqrt(2) : 1.0;
                double cv = (v == 0) ? 1.0 / Math.sqrt(2) : 1.0;
                F[u][v] = 0.25 * cu * cv * sum;
            }
        }
        return F;
    }

    private double median(double[] input) {
        double[] copy = input.clone();
        Arrays.sort(copy);
        int mid = copy.length / 2;
        return (copy.length % 2 == 0) ? (copy[mid - 1] + copy[mid]) / 2.0 : copy[mid];
    }
}

