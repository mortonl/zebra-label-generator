package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum LabelSize
{
    // Standard shipping labels
    LABEL_4X6(101.6, 152.4, "4\" x 6\""),
    LABEL_4X4(101.6, 101.6, "4\" x 4\""),

    // Small package/product labels
    LABEL_2X1(50.8, 25.4, "2\" x 1\""),
    LABEL_2X2(50.8, 50.8, "2\" x 2\""),
    LABEL_3X1(76.2, 25.4, "3\" x 1\""),
    LABEL_3X2(76.2, 50.8, "3\" x 2\""),

    // Large format labels
    LABEL_6X4(152.4, 101.6, "6\" x 4\""),
    LABEL_8X6(203.2, 152.4, "8\" x 6\""),

    // Specialty sizes
    LABEL_2_25X1_25(57.15, 31.75, "2.25\" x 1.25\""),
    LABEL_2_25X4(57.15, 101.6, "2.25\" x 4\""),
    LABEL_3_5X1(88.9, 25.4, "3.5\" x 1\""),

    // European sizes
    LABEL_A4(210.0, 297.0, "A4"),
    LABEL_A5(148.0, 210.0, "A5"),
    LABEL_A6(105.0, 148.0, "A6");

    private final double widthMm;
    private final double heightMm;
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

    @Override
    public String toString()
    {
        return String.format("%s (%.1fmm x %.1fmm)", description, widthMm, heightMm);
    }

    public int getHeightInDots(PrintDensity density)
    {
        return density.toDots(heightMm);
    }

    public int getWidthInDots(PrintDensity density)
    {
        return density.toDots(widthMm);
    }
}
