package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.validation.Validator.validateNotNull;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

/**
 * <p>Implements an Interleaved 2 of 5 barcode, commonly used in industrial and distribution
 * applications for encoding pairs of digits.</p>
 *
 * <p>Interleaved 2 of 5 characteristics:</p>
 * <ul>
 *     <li>Numeric data only (0-9)</li>
 *     <li>Must have an even number of digits (unless using check digit)</li>
 *     <li>Optional MOD 10 check digit calculation</li>
 *     <li>Variable height and orientation</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>{@code
 * // Basic usage without check digit (even number of digits required)
 * BarcodeInterleaved2of5.builder()
 *     .withHeightMm(15.0)
 *     .withOrientation(Orientation.NORMAL)
 *     .withPlainTextContent("1234")
 *     .build();
 *
 * // With check digit (odd number of digits required)
 * BarcodeInterleaved2of5.builder()
 *     .withHeightMm(15.0)
 *     .withCalculateAndPrintMod10CheckDigit(true)
 *     .withPlainTextContent("12345")
 *     .build();
 * }</pre>
 *
 * @see Barcode The parent class for all barcode implementations
 * @see Orientation Available orientation options
 * @see <a href="https://support.zebra.com/cpws/docs/zpl/2i.htm">Zebra ZPL II Manual - Interleaved 2 of 5</a>
 */
@Getter
@SuperBuilder(setterPrefix = "with")
public class BarcodeInterleaved2of5 extends Barcode
{
    /**
     * The orientation of the barcode.
     * Controls how the barcode is rotated on the label.
     * If not specified, the printer's default orientation will be used.
     *
     * @param orientation the orientation setting to control barcode rotation
     * @return the current orientation setting of the barcode
     * @see Orientation
     */
    private final Orientation orientation;

    /**
     * The height of the barcode in millimeters.
     * Must be between {1.0/DPI} and {32000.0/DPI} millimeters,
     * and must fit within the label height.
     *
     * @param heightInMillimetres the height of the barcode in millimeters, must be within DPI-dependent range
     * @return the height of the barcode in millimeters
     */
    private final double heightInMillimetres;

    /**
     * Controls whether to print the interpretation line (human-readable text)
     * below the barcode.
     * Default printer value is typically true.
     *
     * @param printInterpretationLine true to show interpretation line below barcode, false to hide it
     * @return true if interpretation line is shown below barcode, false otherwise
     */
    private final boolean printInterpretationLine;

    /**
     * Controls whether to print the interpretation line (human-readable text)
     * above the barcode instead of below.
     * Default printer value is typically false.
     *
     * @param printInterpretationLineAbove true to show interpretation line above barcode, false to show it below
     * @return true if interpretation line is shown above barcode, false if shown below
     */
    private final boolean printInterpretationLineAbove;

    /**
     * Controls whether to calculate and print a MOD 10 check digit.
     * When enabled:
     * <ul>
     *     <li>Input data length must be odd</li>
     *     <li>Check digit is calculated and appended</li>
     *     <li>Final barcode length will be even</li>
     * </ul>
     *
     * @param calculateAndPrintMod10CheckDigit true to enable MOD 10 check digit calculation and printing, false to disable it
     * @return true if MOD 10 check digit calculation is enabled, false otherwise
     */
    private final boolean calculateAndPrintMod10CheckDigit;

    /**
     * {@inheritDoc}
     *
     * <p>Generates Interleaved 2 of 5 specific ZPL commands including:</p>
     * <ul>
     *     <li>^B2 command with barcode parameters</li>
     *     <li>Orientation setting</li>
     *     <li>Height in dots</li>
     *     <li>Interpretation line settings</li>
     *     <li>Check digit configuration</li>
     * </ul>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        zplCommand
            .append(super.toZplString(dpi))
            .append(generateZplIICommand(
                ZplCommand.BARCODE_INTERLEAVED_2_OF_5,
                orientation != null ? orientation.getValue() : null,
                dpi.toDots(heightInMillimetres),
                printInterpretationLine ? "Y" : "N",
                printInterpretationLineAbove ? "Y" : "N",
                calculateAndPrintMod10CheckDigit ? "Y" : "N"))
            .append(getData().toZplString(dpi));

        return zplCommand.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>For Interleaved 2 of 5 barcodes, additionally validates:</p>
     * <ul>
     *     <li>Orientation is specified</li>
     *     <li>Height is within valid range for the DPI</li>
     *     <li>Height fits within label dimensions</li>
     *     <li>Data contains only numeric characters</li>
     *     <li>Data length is appropriate for check digit setting:
     *         <ul>
     *             <li>Odd length when using check digit</li>
     *             <li>Even length when not using check digit</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @throws IllegalStateException if any validation fails
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateParameters(size, dpi);
    }

    /**
     * Performs detailed parameter validation for the Interleaved 2 of 5 barcode.
     *
     * @param size The label size constraints
     * @param dpi  The printer density configuration
     * @throws IllegalStateException if any validation fails
     */
    private void validateParameters(LabelSize size, PrintDensity dpi)
    {
        validateNotNull(orientation, "Orientation");

        // Calculate height limits in millimeters
        double minHeightMm = 1.0 / PrintDensity.getMaxDotsPerMillimetre();
        double maxHeightMm = 32000.0 / PrintDensity.getMinDotsPerMillimetre();

        // Validate height range
        validateRange(heightInMillimetres, minHeightMm, maxHeightMm, "Bar code height");

        // Validate height fits within label
        validateRange(heightInMillimetres, 0, size.getHeightMm(), "Bar code height");

        // Validate data
        String data = getData().getData();
        validateNotNull(data, "Barcode data");

        if (!data.matches("\\d+")) {
            throw new IllegalStateException("Interleaved 2 of 5 bar code only accepts numeric data");
        }

        if (calculateAndPrintMod10CheckDigit && data.length() % 2 != 1) {
            throw new IllegalStateException("When using check digit, data length must be odd " +
                "to result in even total length after check digit is added");
        }

        if (!calculateAndPrintMod10CheckDigit && data.length() % 2 != 0) {
            throw new IllegalStateException("Data length must be even when not using check digit");
        }
    }
}
