package com.github.mortonl.zebra.elements.graphics;

import com.github.mortonl.zebra.elements.PositionedAndSizedElement;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Image element that supports positioning and sizing in millimetres and renders
 * to a ZPL ^GF Graphic Field at the appropriate printer DPI. The source image is
 * dynamically resized (preserving the aspect ratio) to match the requested size in mm.
 * <p><strong>Usage examples:</strong></p>
 * <pre>{@code
 * Image img = Image.createImage()
 * .withImage(bufferedImage)
 * .withPosition(0, 0)
 * .withSize(50.0, 30.0) // target size in mm; aspect ratio preserved within box
 * .build();
 * }</pre>
 */
@Getter
@SuperBuilder(builderMethodName = "createImageElement", setterPrefix = "with")
public class ImageElement extends PositionedAndSizedElement
{
    /** The source image to print. */
    @NonNull
    private final BufferedImage image;

    /** Compression type for ^GF. Defaults to ASCII_HEX. */
    private final CompressionType compressionType;

    /** Optional flag to enable alternative ASCII_HEX compression. */
    private final Boolean enableAlternativeDataCompression;

    // --- Image encoding helpers moved from ImageToGraphicDataConverter ---
    private static final int BITS_PER_BYTE = 8;
    private static final int BYTE_ALIGNMENT_OFFSET = 7;
    private static final double RED_WEIGHT = 0.299;
    private static final double GREEN_WEIGHT = 0.587;
    private static final double BLUE_WEIGHT = 0.114;
    private static final int GRAYSCALE_THRESHOLD = 128;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int BYTE_MASK = 0xFF;
    private static final String HEX_FORMAT = "%02X";

    public static int calculateBytesPerRow(int imageWidth)
    {
        return (imageWidth + BYTE_ALIGNMENT_OFFSET) / BITS_PER_BYTE;
    }

    public static int calculateTotalBytes(BufferedImage image)
    {
        return calculateBytesPerRow(image.getWidth()) * image.getHeight();
    }

    public static byte[] convertToMonochrome(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        int bytesPerRow = calculateBytesPerRow(width);
        byte[] bitmap = new byte[bytesPerRow * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);

                int alpha = (argb >>> 24) & BYTE_MASK;
                if (alpha < GRAYSCALE_THRESHOLD) {
                    continue; // treat transparent as white
                }

                int r = (argb >> RED_SHIFT) & BYTE_MASK;
                int g = (argb >> GREEN_SHIFT) & BYTE_MASK;
                int b = argb & BYTE_MASK;
                int gray = (int) (RED_WEIGHT * r + GREEN_WEIGHT * g + BLUE_WEIGHT * b);

                if (gray < GRAYSCALE_THRESHOLD) {
                    int byteIndex = y * bytesPerRow + x / BITS_PER_BYTE;
                    int bitIndex = BYTE_ALIGNMENT_OFFSET - (x % BITS_PER_BYTE);
                    bitmap[byteIndex] |= (1 << bitIndex);
                }
            }
        }
        return bitmap;
    }

    public static String bytesToHex(byte[] data)
    {
        StringBuilder hex = new StringBuilder();
        for (byte b : data) {
            hex.append(String.format(HEX_FORMAT, b & BYTE_MASK));
        }
        return hex.toString();
    }

    public static String convertToData(BufferedImage image, CompressionType compressionType)
    {
        if (compressionType == CompressionType.ASCII_HEX) {
            return bytesToHex(convertToMonochrome(image));
        }
        throw new UnsupportedOperationException("Only ASCII_HEX compression is currently supported");
    }

    public static String convertToHexData(BufferedImage image)
    {
        return convertToData(image, CompressionType.ASCII_HEX);
    }

    private static BufferedImage resize(BufferedImage src, int widthDots, int heightDots)
    {
        if (src.getWidth() == widthDots && src.getHeight() == heightDots) {
            return src;
        }
        // Simple scaling using getScaledInstance with smoothing, then draw into ARGB buffer
        Image tmp = src.getScaledInstance(widthDots, heightDots, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(widthDots, heightDots, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        try {
            g2d.drawImage(tmp, 0, 0, null);
        } finally {
            g2d.dispose();
        }
        return dimg;
    }

    @Override
    public String toZplString(PrintDensity dpi)
    {
        // Compute target size in dots
        DimensionDots dims = computeTargetDots(dpi);
        BufferedImage resized = resize(image, dims.widthDots, dims.heightDots);

        // Build a GraphicField at this element's position
        CompressionType ct = compressionType != null ? compressionType : CompressionType.ASCII_HEX;
        String data = convertToData(resized, ct);
        int bytesPerRow = calculateBytesPerRow(resized.getWidth());
        int totalBytes = calculateTotalBytes(resized);

        GraphicField field = GraphicField
            .createGraphicField()
            .withPosition(getXAxisLocationMm(), getYAxisLocationMm())
            .withCompressionType(ct)
            .withEnableAlternativeDataCompression(enableAlternativeDataCompression)
            .withData(data)
            .withBytesPerRow(bytesPerRow)
            .withBinaryByteCount(totalBytes)
            .withGraphicFieldCount(totalBytes)
            .build();

        return field.toZplString(dpi);
    }

    private DimensionDots computeTargetDots(PrintDensity dpi)
    {
        int sourceWidthPixels = image.getWidth();
        int sourceHeightPixels = image.getHeight();
        double aspectRatio = (double) sourceWidthPixels / sourceHeightPixels;

        // If neither width nor height specified, use native pixels
        if (widthMm == null && heightMm == null) {
            return new DimensionDots(sourceWidthPixels, sourceHeightPixels);
        }

        Integer targetWidthDots = widthMm != null ? dpi.toDots(widthMm) : null;
        Integer targetHeightDots = heightMm != null ? dpi.toDots(heightMm) : null;

        if (targetWidthDots != null && targetHeightDots != null) {
            // When both dimensions are provided, render to the exact target size in dots.
            // This ensures the ^GF bytesPerRow matches the expected printer output and
            // aligns with reference assets irrespective of source aspect ratio.
            int w = Math.max(1, targetWidthDots);
            int h = Math.max(1, targetHeightDots);
            return new DimensionDots(w, h);
        } else if (targetWidthDots != null) {
            int w = Math.max(1, targetWidthDots);
            int h = Math.max(1, (int) Math.round(w / aspectRatio));
            return new DimensionDots(w, h);
        } else { // targetHeightDots != null
            int h = Math.max(1, targetHeightDots);
            int w = Math.max(1, (int) Math.round(h * aspectRatio));
            return new DimensionDots(w, h);
        }
    }

    private record DimensionDots(int widthDots, int heightDots)
    {
    }
}
