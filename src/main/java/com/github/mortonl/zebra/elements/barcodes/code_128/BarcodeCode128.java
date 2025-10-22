package com.github.mortonl.zebra.elements.barcodes.code_128;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.elements.barcodes.Barcode;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.validation.Validator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

import static com.github.mortonl.zebra.ZplCommand.BARCODE_CODE_128;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.getMaxDotsPerMillimetre;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.getMinDotsPerMillimetre;

/**
 * Represents a Code 128 barcode element for Zebra label generation.
 * This class generates the ZPL command for a Code 128 barcode with configurable parameters.
 * When parameters are not specified, they are omitted from the ZPL command, allowing printer
 * defaults or previously set default commands to take effect.
 *
 * <p>Code 128 is a high-density linear barcode symbology that can encode text, numbers,
 * and various ASCII characters. It's commonly used in shipping, packaging, and general logistics.</p>
 *
 * <p>This class extends {@link Barcode} and uses the builder pattern for instantiation.</p>
 *
 * <p><strong>Usage examples:</strong></p>
 * <pre>{@code
 * // Method 1: Create and add to label in one step (recommended)
 * ZebraLabel label = new ZebraLabel(labelSize, printDensity);
 * // Using plain text content (recommended for most cases)
 * BarcodeCode128.createCode128Barcode()
 *     .withHeightMm(15.0)
 *     .withOrientation(Orientation.NORMAL)
 *     .withPrintInterpretationLine(true)
 *     .withMode(Code128Mode.AUTO)
 *     .withPlainTextContent("12345")  // Automatically configures Field for plain text
 *     .addToLabel(label); // Validates and adds to label automatically
 *
 * // Method 2: Build separately (when deferred addition is needed)
 * BarcodeCode128 barcode = BarcodeCode128.createCode128Barcode()
 *     .withHeightMm(15.0)
 *     .withPlainTextContent("12345")
 *     .build();  // Note: No validation occurs until added to label
 * label.addElement(barcode);  // Validation occurs here
 *
 * // Using hexadecimal content (for special characters or binary data)
 * BarcodeCode128.createCode128Barcode()
 *     .withHeightMm(15.0)
 *     .withOrientation(Orientation.NORMAL)
 *     .withHexadecimalContent("48656C6C6F")  // Automatically configures Field for hex data
 *     .addToLabel(label);
 *
 * // Minimal configuration with plain text
 * BarcodeCode128.createCode128Barcode()
 *     .withHeightMm(15.0)
 *     .withPlainTextContent("12345")
 *     .addToLabel(label);
 * }</pre>
 *
 * <p><strong>Content Setting:</strong></p>
 * <ul>
 *     <li>{@code withPlainTextContent(String)} - Use for regular text and numbers</li>
 *     <li>{@code withHexadecimalContent(String)} - Use for binary data or special characters</li>
 *     <li>{@code withContent(Field)} - Advanced usage only, when direct Field configuration is needed</li>
 * </ul>
 *
 * <p><strong>Validation:</strong></p>
 * <p>When using {@code addToLabel()}, the barcode is automatically validated against the label's
 * size and printer density settings. This includes checking:</p>
 * <ul>
 *     <li>Barcode height is within valid range for the specified DPI</li>
 *     <li>Content restrictions (e.g., max 19 digits for UCC case mode)</li>
 *     <li>Position and rotation fit within label boundaries</li>
 *     <li>Other label-specific constraints</li>
 * </ul>
 *
 * @see Barcode The parent class for all barcode implementations
 * @see Code128Mode Available modes for Code 128 barcode generation
 * @see Orientation Possible orientation values for the barcode
 * @see PrintDensity Printer DPI configuration
 * @see LabelSize Label dimension constraints
 * @see ZebraLabel The label class that this barcode can be added to
 * @see Field The class representing field content and formatting
 */
@Getter
@SuperBuilder(builderMethodName = "createCode128Barcode", setterPrefix = "with")
public class BarcodeCode128 extends Barcode
{
    /**
     * The height of the barcode in millimeters.
     * <p>Valid height range depends on the printer's DPI settings:</p>
     * <ul>
     *     <li>Minimum: 1.0 / maxDotsPerMillimetre</li>
     *     <li>Maximum: 32000.0 / minDotsPerMillimetre</li>
     * </ul>
     * <p>This parameter is required and must be specified.</p>
     *
     * @param heightMm the height of the barcode in millimeters, must be within valid DPI-dependent range
     * @return the height of the barcode in millimeters
     */
    private final @Nullable Double heightMm;

    /**
     * The orientation of the barcode.
     * <p>Supported orientations:</p>
     * <ul>
     *     <li>N = normal</li>
     *     <li>R = rotated 90 degrees clockwise</li>
     *     <li>I = inverted 180 degrees</li>
     *     <li>B = read from bottom up, 270 degrees</li>
     * </ul>
     * <p>If not specified (null), the orientation parameter is omitted from the ZPL command.
     * The printer will use either:</p>
     * <ul>
     *     <li>The last ^FW (Default Orientation) command value, or</li>
     *     <li>The printer's default orientation (normal) if no ^FW was specified</li>
     * </ul>
     *
     * @param orientation the orientation setting for the barcode (N, R, I, or B)
     * @return the current orientation setting of the barcode
     * @see Orientation
     */
    private final @Nullable Orientation orientation;

    /**
     * Controls the display of the interpretation line below the barcode.
     * The interpretation line shows the human-readable text representation
     * of the barcode content.
     *
     * <p>If not specified (null), this parameter is omitted from the ZPL command.
     * The printer will use its default value (Y - enabled) unless modified by
     * previous commands.</p>
     *
     * @param printInterpretationLine true to show interpretation line below barcode, false to hide it
     * @return true if the interpretation line below barcode is enabled, false otherwise
     */
    private final @Nullable Boolean printInterpretationLine;

    /**
     * Controls the display of the interpretation line above the barcode.
     * When enabled, shows the human-readable text above instead of below
     * the barcode.
     *
     * <p>If not specified (null), this parameter is omitted from the ZPL command.
     * The printer will use its default value (N - disabled) unless modified by
     * previous commands.</p>
     *
     * @param printInterpretationLineAbove true to show interpretation line above barcode, false to show it below
     * @return true if the interpretation line is shown above the barcode, false if shown below
     */
    private final @Nullable Boolean printInterpretationLineAbove;

    /**
     * Enables or disables the UCC check digit calculation.
     * When enabled, the printer automatically calculates and includes
     * the check digit for UCC/EAN-128 formats.
     *
     * <p>If not specified (null), this parameter is omitted from the ZPL command.
     * The printer will use its default value (N - disabled) unless modified by
     * previous commands.</p>
     *
     * @param uccCheckDigitEnabled true to enable automatic UCC check digit calculation, false to disable it
     * @return true if UCC check digit calculation is enabled, false otherwise
     */
    private final @Nullable Boolean uccCheckDigitEnabled;

    /**
     * Specifies the Code 128 encoding mode.
     * <p>Available modes:</p>
     * <ul>
     *     <li>AUTO - Automatically selects optimal encoding</li>
     *     <li>UCC_CASE - UCC case mode (limited to 19 digits)</li>
     *     <li>MANUAL - Manual code set selection</li>
     * </ul>
     *
     * <p>If not specified (null), this parameter is omitted from the ZPL command.
     * The printer will use its default mode (AUTO) unless modified by
     * previous commands.</p>
     *
     * @param mode the Code 128 encoding mode to set
     * @return the current Code 128 encoding mode
     * @see Code128Mode
     */
    private final Code128Mode mode;

    /**
     * {@inheritDoc}
     *
     * <p>Generates Code 128 specific barcode commands including:</p>
     * <ul>
     *     <li>^BC command with Code 128 parameters</li>
     *     <li>Interpretation line settings</li>
     *     <li>UCC check digit configuration</li>
     *     <li>Mode selection</li>
     * </ul>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        zplCommand
            .append(super.toZplString(dpi))
            .append(generateZplIICommand(BARCODE_CODE_128,
                orientation != null ? orientation.getValue() : null,
                dpi.toDots(heightMm),
                printInterpretationLine != null ? printInterpretationLine ? "Y" : "N" : null,
                printInterpretationLineAbove != null ? printInterpretationLineAbove ? "Y" : "N" : null,
                uccCheckDigitEnabled != null ? uccCheckDigitEnabled ? "Y" : "N" : null,
                mode != null ? mode.getValue() : null
            ))
            .append(getContent().toZplString(dpi));

        return zplCommand.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Additional validations for Code 128 barcodes:</p>
     * <ul>
     *     <li>Barcode height must be between 1.0/DPI and 32000.0/DPI mm</li>
     *     <li>When using UCC case mode, content must not exceed 19 digits</li>
     * </ul>
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont)
    {
        double minValidHeightMm = 1.0 / getMaxDotsPerMillimetre();
        double maxValidHeightMm = 32000.0 / getMinDotsPerMillimetre();

        Validator.validateRange(heightMm,
            minValidHeightMm,
            maxValidHeightMm,
            "Barcode height (mm)");

        // Validate mode-specific requirements
        if (mode == Code128Mode.UCC_CASE) {
            if (content == null || content.getData() == null) {
                throw new IllegalStateException("Data cannot be null when using UCC Case Mode");
            }

            // Check digit count for UCC Case Mode
            long digitCount = content.getData()
                                     .chars()
                                     .filter(Character::isDigit)
                                     .count();

            if (digitCount > 19) {
                throw new IllegalStateException("UCC Case Mode cannot handle more than 19 digits");
            }
        }

        super.validateInContext(size, dpi, defaultFont);
    }
}
