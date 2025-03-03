package com.github.mortonl.zebra.printer_configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Represents supported print densities for Zebra printers, defined in both dots per inch (DPI)
 * and dots per millimeter.
 *
 * <p>Print density determines the resolution at which elements are printed on labels.
 * Higher densities provide better print quality but require more printer memory and
 * processing time.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Convert physical measurements
 * PrintDensity density = PrintDensity.DPI_203;
 * int dots = density.toDots(10.0);        // Convert 10mm to dots
 * double mm = density.toMillimetres(80);   // Convert 80 dots to mm
 *
 * // Find density from printer specifications
 * PrintDensity byDpi = PrintDensity.fromDotsPerInch(300);
 * PrintDensity byDpmm = PrintDensity.fromDotsPerMillimetre(12);
 * }</pre>
 */
@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum PrintDensity
{
    /**
     * 152 DPI (6 dots/mm) - Basic resolution suitable for large text and simple barcodes
     */
    DPI_152(152, 6),

    /**
     * 203 DPI (8 dots/mm) - Standard resolution suitable for most applications
     */
    DPI_203(203, 8),

    /**
     * 300 DPI (12 dots/mm) - High resolution for small text and detailed graphics
     */
    DPI_300(300, 12),

    /**
     * 600 DPI (24 dots/mm) - Very high resolution for precise graphics and tiny text
     */
    DPI_600(600, 24);

    /**
     * The number of dots per inch (DPI) for this print density, usually derived from the DPMM value.
     *
     * @param dotsPerInch the printer's horizontal and vertical resolution
     * @return the resolution in dots per inch
     */
    private final int dotsPerInch;

    /**
     * The number of dots per millimeter (DPMM) for this print density.
     *
     * @param dotsPerMillimetre the printer's metric resolution
     * @return the resolution in dots per millimeter
     */
    private final int dotsPerMillimetre;

    /**
     * Finds the PrintDensity matching a specific DPI value.
     *
     * @param dpi The dots per inch value to match
     * @return The matching PrintDensity
     * @throws IllegalArgumentException if no matching density is found
     */
    public static PrintDensity fromDotsPerInch(int dpi)
    {
        return Arrays
            .stream(values())
            .filter(density -> density.dotsPerInch == dpi)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No PrintDensity found for %d DPI", dpi)));
    }

    /**
     * Finds the PrintDensity matching a specific dots per millimeter value.
     *
     * @param dotsPerMm The dots per millimeter value to match
     * @return The matching PrintDensity
     * @throws IllegalArgumentException if no matching density is found
     */
    public static PrintDensity fromDotsPerMillimetre(int dotsPerMm)
    {
        return Arrays
            .stream(values())
            .filter(density -> density.dotsPerMillimetre == dotsPerMm)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No PrintDensity found for %d dots per millimetre", dotsPerMm)));
    }

    /**
     * Gets the minimum supported dots per millimeter across all print densities.
     *
     * @return The minimum dots per millimeter value
     */
    public static int getMinDotsPerMillimetre()
    {
        return Stream
            .of(PrintDensity.values())
            .mapToInt(PrintDensity::getDotsPerMillimetre)
            .min()
            .orElseThrow();
    }

    /**
     * Gets the maximum supported dots per millimeter across all print densities.
     *
     * @return The maximum dots per millimeter value
     */
    public static int getMaxDotsPerMillimetre()
    {
        return Stream
            .of(PrintDensity.values())
            .mapToInt(PrintDensity::getDotsPerMillimetre)
            .max()
            .orElseThrow();
    }

    /**
     * Converts a measurement in millimeters to dots at this print density,
     * using standard arithmetic rounding (rounds to nearest integer, with ties rounding up).
     *
     * <p>Examples at 203 DPI (8 dots/mm):</p>
     * <ul>
     *     <li>1.0 mm → 8 dots</li>
     *     <li>1.4 mm → 11 dots (1.4 * 8 = 11.2 rounds to 11)</li>
     *     <li>1.5 mm → 12 dots (1.5 * 8 = 12.0)</li>
     *     <li>1.6 mm → 13 dots (1.6 * 8 = 12.8 rounds to 13)</li>
     *     <li>2.0 mm → 16 dots</li>
     * </ul>
     *
     * @param millimeters The measurement in millimeters
     * @return The equivalent number of dots, rounded to the nearest integer
     */
    public int toDots(double millimeters)
    {
        return (int) Math.round(millimeters * dotsPerMillimetre);
    }

    /**
     * Converts a measurement in dots to millimeters at this print density.
     * Uses high precision decimal arithmetic to ensure accurate conversion.
     *
     * @param dots The number of dots
     * @return The equivalent measurement in millimeters, with 10 decimal places precision
     */
    public final double toMillimetres(final int dots)
    {
        return BigDecimal
            .valueOf(dots)
            .divide(BigDecimal.valueOf(dotsPerMillimetre), 10, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
