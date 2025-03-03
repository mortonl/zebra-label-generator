package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;

/**
 * Represents standard label sizes for Zebra printers with dimensions in millimeters.
 * Provides predefined sizes for common shipping labels, product labels, and international paper formats.
 *
 * <p>Categories of label sizes:</p>
 * <ul>
 *     <li>Shipping Labels: Standard sizes optimized for shipping and logistics (4x6", 4x4")</li>
 *     <li>Product Labels: Smaller sizes for product identification (2x1", 2x2", 3x1")</li>
 *     <li>Large Format: Larger sizes for shipping and signage (6x4", 8x6")</li>
 *     <li>Specialty Sizes: Non-standard dimensions for specific applications</li>
 *     <li>European Formats: Standard ISO paper sizes (A4, A5, A6)</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Find a matching label size within 1mm tolerance
 * Optional<LabelSize> size = LabelSize.findClosestSize(100.0, 150.0, 1.0);
 *
 * // Get dimensions in dots for a specific print density
 * PrintDensity density = PrintDensity.DPI_203;
 * int widthDots = LabelSize.LABEL_4X6.getWidthInDots(density);
 * int heightDots = LabelSize.LABEL_4X6.getHeightInDots(density);
 *
 * // Access dimensions directly
 * double widthMm = LabelSize.LABEL_4X6.getWidthMm();
 * double heightMm = LabelSize.LABEL_4X6.getHeightMm();
 * }</pre>
 *
 * @see PrintDensity for supported printer resolutions
 */
@Getter
@AllArgsConstructor
public enum LabelSize
{
    // Standard shipping labels
    /**
     * 4" x 6" shipping label (101.6mm x 152.4mm).
     * Common size for shipping and logistics applications.
     */
    LABEL_4X6(101.6, 152.4, "4\" x 6\""),

    /**
     * 4" x 4" square label (101.6mm x 101.6mm).
     */
    LABEL_4X4(101.6, 101.6, "4\" x 4\""),

    // Small package/product labels
    /**
     * 2" x 1" small label (50.8mm x 25.4mm).
     * Suitable for product labeling and small packages.
     */
    LABEL_2X1(50.8, 25.4, "2\" x 1\""),

    /**
     * 2" x 2" square label (50.8mm x 50.8mm).
     */
    LABEL_2X2(50.8, 50.8, "2\" x 2\""),

    /**
     * 3" x 1" label (76.2mm x 25.4mm).
     */
    LABEL_3X1(76.2, 25.4, "3\" x 1\""),

    /**
     * 3" x 2" label (76.2mm x 50.8mm).
     */
    LABEL_3X2(76.2, 50.8, "3\" x 2\""),

    // Large format labels
    /**
     * 6" x 4" large label (152.4mm x 101.6mm).
     * Suitable for shipping and product identification.
     */
    LABEL_6X4(152.4, 101.6, "6\" x 4\""),

    /**
     * 8" x 6" large label (203.2mm x 152.4mm).
     */
    LABEL_8X6(203.2, 152.4, "8\" x 6\""),

    // Specialty sizes
    /**
     * 2.25" x 1.25" specialty label (57.15mm x 31.75mm).
     */
    LABEL_2_25X1_25(57.15, 31.75, "2.25\" x 1.25\""),

    /**
     * 2.25" x 4" specialty label (57.15mm x 101.6mm).
     */
    LABEL_2_25X4(57.15, 101.6, "2.25\" x 4\""),

    /**
     * 3.5" x 1" specialty label (88.9mm x 25.4mm).
     */
    LABEL_3_5X1(88.9, 25.4, "3.5\" x 1\""),

    // European sizes
    /**
     * A4 paper size (210.0mm x 297.0mm).
     * Standard European paper size.
     */
    LABEL_A4(210.0, 297.0, "A4"),

    /**
     * A5 paper size (148.0mm x 210.0mm).
     */
    LABEL_A5(148.0, 210.0, "A5"),

    /**
     * A6 paper size (105.0mm x 148.0mm).
     */
    LABEL_A6(105.0, 148.0, "A6");

    /**
     * Width of the label in millimeters.
     * This measurement represents the horizontal dimension of the label
     * when viewed in its normal orientation (non-rotated).
     *
     * @return the label width in millimeters
     */
    private final double widthMm;

    /**
     * Height of the label in millimeters.
     * This measurement represents the vertical dimension of the label
     * when viewed in its normal orientation (non-rotated).
     *
     * @return the label height in millimeters
     */
    private final double heightMm;

    /**
     * Human-readable description of the label size.
     * Provides a formatted string representing the label dimensions
     * in standard notation (e.g., "4\" x 6\"" or "A4").
     *
     * @return the formatted description of the label size
     */
    private final String description;

    /**
     * Find the closest standard label size matching the given dimensions
     *
     * @param widthMm     width in millimeters
     * @param heightMm    height in millimeters
     * @param toleranceMm tolerance in millimeters for matching
     * @return Optional containing the matching label size, or empty if no match found within tolerance
     */
    public static Optional<LabelSize> findClosestSize(double widthMm, double heightMm, double toleranceMm)
    {
        return Arrays
            .stream(values())
            .filter(size ->
                Math.abs(size.widthMm - widthMm) <= toleranceMm &&
                    Math.abs(size.heightMm - heightMm) <= toleranceMm)
            .min((a, b) -> {
                double aDiff = Math.abs(a.widthMm - widthMm) + Math.abs(a.heightMm - heightMm);
                double bDiff = Math.abs(b.widthMm - widthMm) + Math.abs(b.heightMm - heightMm);
                return Double.compare(aDiff, bDiff);
            });
    }

    /**
     * Check if the dimensions match this label size within a tolerance
     *
     * @param widthMm     width in millimeters
     * @param heightMm    height in millimeters
     * @param toleranceMm tolerance in millimeters
     * @return true if dimensions match within tolerance
     */
    public boolean matches(double widthMm, double heightMm, double toleranceMm)
    {
        return Math.abs(this.widthMm - widthMm) <= toleranceMm &&
            Math.abs(this.heightMm - heightMm) <= toleranceMm;
    }

    /**
     * Returns a string representation of the label size including both the description
     * and dimensions in millimeters.
     *
     * @return formatted string with description and dimensions
     */
    @Override
    public String toString()
    {
        return String.format("%s (%.1fmm x %.1fmm)", description, widthMm, heightMm);
    }

    /**
     * Converts the label height to dots based on the specified print density.
     * The result is rounded down to ensure the label fits within the specified dimensions.
     *
     * <p>Example at 203 DPI (8 dots/mm):</p>
     * <ul>
     *     <li>25.4 mm → 203 dots</li>
     *     <li>25.6 mm → 204 dots (25.6 * 8 = 204.8 rounds down to 204)</li>
     *     <li>25.9 mm → 207 dots (25.9 * 8 = 207.2 rounds down to 207)</li>
     * </ul>
     *
     * @param density the print density to use for conversion
     * @return height in dots, rounded down to the nearest integer
     * @see PrintDensity
     */
    public int getHeightInDots(PrintDensity density)
    {
        return density.toDots(heightMm, RoundingMode.DOWN);
    }

    /**
     * Converts the label width to dots based on the specified print density.
     * The result is rounded down to ensure the label fits within the specified dimensions.
     *
     * <p>Example at 203 DPI (8 dots/mm):</p>
     * <ul>
     *     <li>101.6 mm → 812 dots</li>
     *     <li>101.8 mm → 814 dots (101.8 * 8 = 814.4 rounds down to 814)</li>
     *     <li>102.0 mm → 816 dots (102.0 * 8 = 816.0)</li>
     * </ul>
     *
     * @param density the print density to use for conversion
     * @return width in dots, rounded down to the nearest integer
     * @see PrintDensity
     */
    public int getWidthInDots(PrintDensity density)
    {
        return density.toDots(widthMm, RoundingMode.DOWN);
    }
}
