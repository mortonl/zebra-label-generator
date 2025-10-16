package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Optional;

import static com.github.mortonl.zebra.ZplCommand.LABEL_LENGTH;
import static com.github.mortonl.zebra.ZplCommand.LINE_SEPERATOR;
import static com.github.mortonl.zebra.ZplCommand.PRINT_WIDTH;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

/**
 * Represents standard label sizes for Zebra printers with dimensions in millimeters.
 * Provides predefined sizes for common shipping labels, product labels, and international paper formats.
 *
 * <p>Categories of label sizes:</p>
 * <ul>
 *     <li>Shipping Labels: Standard sizes optimised for shipping and logistics (4x6", 4x4")</li>
 *     <li>Product Labels: Smaller sizes for product identification (2x1", 2x2", 3x1")</li>
 *     <li>Large Format: Larger sizes for shipping and signage (6x4", 8x6")</li>
 *     <li>Specialty Sizes: Non-standard dimensions for specific applications</li>
 *     <li>European Formats: Standard ISO paper sizes (A4, A5, A6)</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Find a matching label size within 1 mm tolerance
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
    // Small package/product labels
    /**
     * 2" x 1" small label (51mm x 25mm).
     * Suitable for product labelling and small packages.
     */
    LABEL_2X1(51, 25, "2\" x 1\""),

    /**
     * 2" x 2" square label (51mm x 51mm).
     */
    LABEL_2X2(51, 51, "2\" x 2\""),

    /**
     * 3" x 1" label (76mm x 25mm).
     */
    LABEL_3X1(76, 25, "3\" x 1\""),

    /**
     * 3" x 2" label (76mm x 51mm).
     */
    LABEL_3X2(76, 51, "3\" x 2\""),

    // Standard shipping labels
    /**
     * 4" x 6" shipping label (101.6 mm x 152.4 mm).
     * Common size for shipping and logistics applications.
     */
    LABEL_4X6(101.6, 152.4, "4\" x 6\""),

    /**
     * 4" x 4" square label (101.6 mm x 101.6 mm).
     */
    LABEL_4X4(101.6, 101.6, "4\" x 4\""),

    // Large format labels
    /**
     * 6" x 4" large label (152mm x 102mm).
     * Suitable for shipping and product identification.
     */
    LABEL_6X4(152, 102, "6\" x 4\""),

    /**
     * 8" x 6" large label (203mm x 152mm).
     */
    LABEL_8X6(203, 152, "8\" x 6\""),

    // Specialty sizes
    /**
     * 2.25" x 1.25" specialty label (57mm x 32mm).
     */
    LABEL_2_25X1_25(57, 32, "2.25\" x 1.25\""),

    /**
     * 2.25" x 4" specialty label (57mm x 102mm).
     */
    LABEL_2_25X4(57, 102, "2.25\" x 4\""),

    /**
     * 3.5" x 1.5" specialty label (89mm x 38mm).
     */
    LABEL_3_5X1_5(89, 38, "3.5\" x 1.5\""),

    /**
     * 4" x 3" label (102mm x 76mm).
     */
    LABEL_4X3(102, 76, "4\" x 3\""),

    // European Paper Sizes
    /**
     * A4 paper size (210.0 mm x 297.0 mm).
     * Standard European paper size.
     */
    LABEL_A4(210.0, 297.0, "A4"),

    /**
     * A5 paper size (148.0 mm x 210.0 mm).
     */
    LABEL_A5(148.0, 210.0, "A5"),

    /**
     * A6 paper size (105.0 mm x 148.0 mm).
     */
    LABEL_A6(105.0, 148.0, "A6"),

    // Metric Shipping Labels
    /**
     * 102 mm x 152 mm label (102.0 mm x 152.0 mm).
     */
    LABEL_102X152(102.0, 152.0, "102 mm x 152 mm"),

    /** 102 mm x 159 mm label (102.0 mm x 159.0 mm). */
    LABEL_102X159(102.0, 159.0, "102 mm x 159 mm"),

    /** 102 mm x 102 mm label (102.0 mm x 102.0 mm). */
    LABEL_102X102(102.0, 102.0, "102 mm x 102 mm"),

    // Metric Product Labels
    /** 51 mm x 25 mm label (51.0 mm x 25.0 mm). */
    LABEL_51X25(51.0, 25.0, "51 mm x 25 mm"),

    /** 2.25" x 4" label (57.0 mm x 102.0 mm). */
    LABEL_57X102(57.0, 102.0, "2.25\"x 4\""),

    /** 76 mm x 51 mm label (76.0 mm x 51.0 mm). */
    LABEL_76X51(76.0, 51.0, "76 mm x 51 mm"),

    /** 102 mm x 38 mm label (102.0 mm x 38.0 mm). */
    LABEL_102X38(102.0, 38.0, "102 mm x 38 mm"),

    /** 102 mm x 64 mm label (102.0 mm x 64.0 mm). */
    LABEL_102X64(102.0, 64.0, "102 mm x 64 mm"),

    /** 102 mm x 76 mm label (102.0 mm x 76.0 mm). */
    LABEL_102X76(102.0, 76.0, "102 mm x 76 mm");

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
     * in standard notation (for example, "4\" x 6\"" or "A4").
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
     *
     * @return Optional containing the matching label size, or empty if no match found within tolerance.
     */
    public static Optional<LabelSize> findClosestSize(double widthMm, double heightMm, double toleranceMm)
    {
        return Arrays
            .stream(values())
            .filter(size ->
                Math.abs(size.widthMm - widthMm) <= toleranceMm &&
                    Math.abs(size.heightMm - heightMm) <= toleranceMm)
            .min((a, b) ->
            {
                double aDiff = Math.abs(a.widthMm - widthMm) + Math.abs(a.heightMm - heightMm);
                double bDiff = Math.abs(b.widthMm - widthMm) + Math.abs(b.heightMm - heightMm);
                return Double.compare(aDiff, bDiff);
            });
    }

    /**
     * Check if the dimensions match this label size within a tolerance.
     *
     * @param widthMm     width in millimeters
     * @param heightMm    height in millimeters
     * @param toleranceMm tolerance in millimeters
     *
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
        return String.format("%s (%.1f mm x %.1f mm)", description, widthMm, heightMm);
    }

    /**
     * Converts the label height to dots based on the specified print density.
     * The base value is rounded down, then reduced by 4 dots to match the
     * maximum accepted value for ZPL commands on many devices/viewers.
     *
     * <p>Why minus 4?</p>
     * <ul>
     *   <li>Empirical behaviour: The Labelary Online ZPL Viewer caps ^LL values at
     *       physical height (in dots) minus 4. Values exceeding the maximum are truncated.</li>
     *   <li>Printer firmware: Several Zebra firmware versions appear to reserve
     *       a 2‑dot non-printable margin at top and bottom, effectively reducing
     *       the usable range by 4 dots.</li>
     * </ul>
     *
     * <p>Example at 203 DPI (8 dots/mm):</p>
     * <ul>
     *     <li>152.4 mm → 1219 dots base (152.4 × 8 = 1219.2 → 1219), then 1219 − 4 = 1215.</li>
     * </ul>
     *
     * @param density the print density to use for conversion
     *
     * @return height in dots (rounded down, then minus 4 to reflect max allowed)
     *
     * @see PrintDensity
     * @see <a href="https://labelary.com/viewer.html">Labelary Online ZPL Viewer</a>
     * @see <a href="https://www.zebra.com/content/dam/zebra/manuals/printers/common/programming/zpl-zbi2-pm-en.pdf">Zebra ZPL II Programming Guide</a>
     */
    public int getHeightInDots(PrintDensity density)
    {
        return density.toDots(heightMm, RoundingMode.DOWN) - 4;
    }

    /**
     * Converts the label width to dots based on the specified print density for use with ^PW.
     * The base value is rounded down, then reduced by 4 dots to reflect the maximum
     * effective print width observed by common renderers and firmware.
     *
     * <p>Why minus 4?</p>
     * <ul>
     *   <li>Labelary behaviour: The Labelary Online ZPL Viewer limits ^PW to
     *       physical width (in dots) minus 4; higher values are capped.</li>
     *   <li>Firmware margins: Many printers appear to reserve an internal ~2‑dot
     *       margin on each side, making 4 dots in total unavailable.</li>
     * </ul>
     *
     * <p>Example at 203 DPI (8 dots/mm):</p>
     * <ul>
     *     <li>101.6 mm → 812 dots base (101.6 × 8 = 812.8 → 812), then 812 − 4 = 808.</li>
     * </ul>
     *
     * @param density the print density to use for conversion
     *
     * @return width in dots (rounded down, then minus 4 to reflect max allowed)
     *
     * @see PrintDensity
     * @see <a href="https://labelary.com/viewer.html">Labelary Online ZPL Viewer</a>
     * @see <a href="https://www.zebra.com/content/dam/zebra/manuals/printers/common/programming/zpl-zbi2-pm-en.pdf">Zebra ZPL II Programming Guide</a>
     */
    public int getWidthInDots(PrintDensity density)
    {
        return density.toDots(widthMm, RoundingMode.DOWN) - 4;
    }

    /**
     * Generates the ZPL II commands for setting the label dimensions.
     *
     * <p>Generates the ^PW (Print Width) and ^LL (Label Length) commands based on
     * the label's physical dimensions converted to dots for the specified print density.
     * Values reflect the maximum accepted by many renderers/printers (base dots rounded
     * down, then minus 4). For example, with 203 DPI and a 4×6 inch label, this generates:
     * {@code ^PW808^LL1215}</p>
     *
     * <p>The width and length values are automatically converted from millimeters
     * to the appropriate number of dots based on the printer's DPI setting.
     * The extra 4-dot reduction aligns with Labelary's handling and common firmware
     * behaviour that seems to reserve ~2 dots on each edge.</p>
     *
     * @param dpi The print density of the target printer (for example, 203, 300, or 600 DPI)
     *
     * @return A String containing the ^PW and ^LL commands with appropriate values
     *
     * @see PrintDensity
     * @see <a href="https://www.zebra.com/content/dam/zebra/manuals/printers/common/programming/zpl-zbi2-pm-en.pdf">ZPL Manual</a>
     * @see <a href="https://labelary.com/viewer.html">Labelary Online ZPL Viewer</a>
     */
    public String toZplString(PrintDensity dpi)
    {
        return generateZplIICommand(PRINT_WIDTH, getWidthInDots(dpi)) + LINE_SEPERATOR +
            generateZplIICommand(LABEL_LENGTH, getHeightInDots(dpi)) + LINE_SEPERATOR;
    }
}
