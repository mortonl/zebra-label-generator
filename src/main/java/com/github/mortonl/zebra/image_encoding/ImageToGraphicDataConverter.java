package com.github.mortonl.zebra.image_encoding;

import com.github.mortonl.zebra.elements.graphics.CompressionType;

import java.awt.image.BufferedImage;

/**
 * Converts images to GraphicField objects for use in ZPL labels.
 * Handles image processing, format conversion, and data encoding.
 */
public class ImageToGraphicDataConverter
{
    /** Number of bits per byte */
    private static final int BITS_PER_BYTE = 8;

    /** The bit alignment offset for byte calculation */
    private static final int BYTE_ALIGNMENT_OFFSET = 7;

    /** Red channel weight for grayscale conversion */
    private static final double RED_WEIGHT = 0.299;

    /** Green channel weight for grayscale conversion */
    private static final double GREEN_WEIGHT = 0.587;

    /** Blue channel weight for grayscale conversion */
    private static final double BLUE_WEIGHT = 0.114;

    /** Threshold for black/white conversion (0-255) */
    private static final int GRAYSCALE_THRESHOLD = 128;

    /** Bit shift for red channel extraction */
    private static final int RED_SHIFT = 16;

    /** Bit shift for green channel extraction */
    private static final int GREEN_SHIFT = 8;

    /** Mask for extracting byte value from integer */
    private static final int BYTE_MASK = 0xFF;

    /** Hexadecimal format string for byte conversion */
    private static final String HEX_FORMAT = "%02X";

    /**
     * Converts a BufferedImage to data string in the specified compression format.
     *
     * @param image           the image to convert
     * @param compressionType the compression type to use
     *
     * @return data string in the specified format
     */
    public static String convertToData(BufferedImage image, CompressionType compressionType)
    {
        if (compressionType == CompressionType.ASCII_HEX) {
            return bytesToHex(convertToMonochrome(image));
        }
        throw new UnsupportedOperationException("Only ASCII_HEX compression is currently supported");
    }

    /**
     * Converts a BufferedImage to hexadecimal data string.
     *
     * @param image the image to convert
     *
     * @return hexadecimal representation of the image data
     */
    public static String convertToHexData(BufferedImage image)
    {
        return convertToData(image, CompressionType.ASCII_HEX);
    }

    /**
     * Calculates the number of bytes per row for the given image width.
     *
     * @param imageWidth the width of the image in pixels
     *
     * @return the number of bytes needed per row
     */
    public static int calculateBytesPerRow(int imageWidth)
    {
        return (imageWidth + BYTE_ALIGNMENT_OFFSET) / BITS_PER_BYTE;
    }

    /**
     * Converts image pixels to monochrome bitmap data.
     *
     * @param image the image to convert
     *
     * @return byte array representing monochrome bitmap
     */
    public static byte[] convertToMonochrome(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        int bytesPerRow = calculateBytesPerRow(width);
        byte[] bitmap = new byte[bytesPerRow * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int gray = (int) (RED_WEIGHT * ((rgb >> RED_SHIFT) & BYTE_MASK) +
                    GREEN_WEIGHT * ((rgb >> GREEN_SHIFT) & BYTE_MASK) +
                    BLUE_WEIGHT * (rgb & BYTE_MASK));

                if (gray < GRAYSCALE_THRESHOLD) {
                    int byteIndex = y * bytesPerRow + x / BITS_PER_BYTE;
                    int bitIndex = BYTE_ALIGNMENT_OFFSET - (x % BITS_PER_BYTE);
                    bitmap[byteIndex] |= (1 << bitIndex);
                }
            }
        }

        return bitmap;
    }

    /**
     * Calculates the total number of bytes for the given image and compression type.
     *
     * @param image           the image to calculate bytes for
     * @param compressionType the compression type being used
     *
     * @return the total number of bytes
     */
    public static int calculateTotalBytes(BufferedImage image, CompressionType compressionType)
    {
        return calculateBytesPerRow(image.getWidth()) * image.getHeight();
    }

    /**
     * Converts byte array to hexadecimal string.
     *
     * @param data the byte array to convert
     *
     * @return hexadecimal string representation
     */
    public static String bytesToHex(byte[] data)
    {
        StringBuilder hex = new StringBuilder();
        for (byte b : data) {
            hex.append(String.format(HEX_FORMAT, b & BYTE_MASK));
        }
        return hex.toString();
    }
}
