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
 * {@inheritDoc}
 *
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
 * BarcodeInterleaved2of5.createInterleaved2of5Barcode()
 *     .withHeightMm(15.0)
 *     .withOrientation(Orientation.NORMAL)
 *     .withPlainTextContent("1234")
 *     .addToLabel(label);
 *
 * // With check digit (odd number of digits required)
 * BarcodeInterleaved2of5.createInterleaved2of5Barcode()
 *     .withHeightMm(15.0)
 *     .withCalculateAndPrintMod10CheckDigit(true)
 *     .withPlainTextContent("12345")
 *     .addToLabel(label);
 * }</pre>
 *
 * @see Barcode The parent class for all barcode implementations
 * @see Orientation Available orientation options
 * @see <a href="https://support.zebra.com/cpws/docs/zpl/2i.htm">Zebra ZPL II Manual - Interleaved 2 of 5</a>
 */
@Getter
@SuperBuilder(builderMethodName = "createInterleaved2of5Barcode", setterPrefix = "with")
public class BarcodeInterleaved2of5 extends Barcode
{
    /**
     * The orientation of the barcode.
     * Controls how the barcode is rotated on the label.
     * If not specified, the printer's default orientation will be used.
     *
     * @see Orientation
     */
    private final Orientation orientation;

    /**
     * The height of the barcode in millimeters.
     * Must be between {1.0/DPI} and {32000.0/DPI} millimeters,
     * and must fit within the label height.
     */
    private final double heightMm;

    /**
     * Controls whether to print the interpretation line (human-readable text)
     * below the barcode.
     * Default printer value is typically true.
     */
    private final boolean printInterpretationLine;

    /**
     * Controls whether to print the interpretation line (human-readable text)
     * above the barcode instead of below.
     * Default printer value is typically false.
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
                dpi.toDots(heightMm),
                printInterpretationLine ? "Y" : "N",
                printInterpretationLineAbove ? "Y" : "N",
                calculateAndPrintMod10CheckDigit ? "Y" : "N"))
            .append(getContent().toZplString(dpi));

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
     * @param dpi The printer density configuration
     * @throws IllegalStateException if any validation fails
     */
    private void validateParameters(LabelSize size, PrintDensity dpi)
    {
        validateNotNull(orientation, "Orientation");

        // Calculate height limits in millimeters
        double minHeightMm = 1.0 / PrintDensity.getMaxDotsPerMillimetre();
        double maxHeightMm = 32000.0 / PrintDensity.getMinDotsPerMillimetre();

        // Validate height range
        validateRange(heightMm, minHeightMm, maxHeightMm, "Bar code height");

        // Validate height fits within label
        validateRange(heightMm, 0, size.getHeightMm(), "Bar code height");

        // Validate data
        String data = getContent().getData();
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
