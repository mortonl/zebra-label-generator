package com.github.mortonl.zebra.elements.barcodes.pdf_417;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.barcodes.Barcode;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

/**
 * <p>Implements a PDF417 (Portable Data File) 2D barcode, a stacked linear barcode format
 * consisting of multiple rows of linear barcodes. The name "PDF417" derives from its structure:
 * each pattern consists of 4 bars and spaces in a pattern that is 17 units long.</p>
 *
 * <p>PDF417 characteristics:</p>
 * <ul>
 *     <li>High data capacity (up to 3000 characters)</li>
 *     <li>Reed-Solomon error correction for data integrity</li>
 *     <li>Adjustable dimensions (3-90 rows, 1-30 columns)</li>
 *     <li>Public domain format (no licensing fees)</li>
 *     <li>Supports text, numbers, and binary data</li>
 *     <li>Maintains readability even when partially damaged</li>
 * </ul>
 *
 * <p><strong>Common Applications:</strong></p>
 * <ul>
 *     <li>Identification Documents:
 *         <ul>
 *             <li>Driver's licenses</li>
 *             <li>Government ID cards</li>
 *         </ul>
 *     </li>
 *     <li>Transportation:
 *         <ul>
 *             <li>Boarding passes</li>
 *             <li>Shipping labels</li>
 *         </ul>
 *     </li>
 *     <li>Inventory Management:
 *         <ul>
 *             <li>Warehouse tracking</li>
 *             <li>Retail product management</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>{@code
 * // Basic usage with default settings
 * BarcodePDF417.createPDF417Barcode()
 *     .withRowHeightMm(0.5)
 *     .withSecurityLevel(2)
 *     .withPlainTextContent("Sample PDF417 Data")
 *     .addToLabel(label);
 *
 * // Advanced configuration with error correction
 * BarcodePDF417.createPDF417Barcode()
 *     .withRowHeightMm(0.5)
 *     .withSecurityLevel(5)      // Higher level for better error correction
 *     .withDataColumns(8)
 *     .withRows(30)
 *     .withEnableRightSideTruncation(true)
 *     .withPlainTextContent("Large amount of data...")
 *     .addToLabel(label);
 * }</pre>
 *
 * @see Barcode The parent class for all barcode implementations
 * @see Orientation Available orientation options
 * @see <a href="https://support.zebra.com/cpws/docs/zpl/pdf417.htm">Zebra ZPL II Manual - PDF417</a>
 */
@Getter
@SuperBuilder(builderMethodName = "createPDF417Barcode", setterPrefix = "with")
public class BarcodePDF417 extends Barcode
{
    /**
     * The orientation of the barcode.
     * Controls how the barcode is rotated on the label.
     * If null, the printer's default orientation (current ^FW value) will be used.
     *
     * @param orientation the orientation setting to control barcode rotation
     * @return the current orientation setting of the barcode
     * @see Orientation
     */
    private final @Nullable Orientation orientation;

    /**
     * The height of each row in millimeters.
     * Must be greater than 0 and fit within the label height (1 is not recommended).
     * If null, the value set by the last ^BY command or the printer's default row height will be used.
     *
     * @param rowHeightMm the height of each barcode row in millimeters
     * @return the height of each barcode row in millimeters
     */
    private final @Nullable Double rowHeightMm;

    /**
     * The error correction level (0-8).
     * Higher levels provide better error correction but require more space.
     * If null, the printer will use level 0 (error detection only).
     * <ul>
     *     <li>0 = Error detection only</li>
     *     <li>1-8 = Increasing levels of error correction</li>
     * </ul>
     *
     * @param securityLevel the error correction level between 0 (detection only) and 8 (maximum correction)
     * @return the current error correction level
     */
    private final @Nullable Integer securityLevel;

    /**
     * The number of data columns (1-30).
     * Controls the width of the barcode.
     * If null, the printer will use a 1:2 row-to-column aspect ratio.
     *
     * @param dataColumns the number of data columns between 1 and 30
     * @return the number of data columns in the barcode
     */
    private final @Nullable Integer dataColumns;

    /**
     * The number of rows (3-90).
     * Controls the height of the barcode.
     * If null, the printer will use a 1:2 row-to-column aspect ratio.
     *
     * @param rows the number of rows between 3 and 90
     * @return the number of rows in the barcode
     */
    private final @Nullable Integer rows;

    /**
     * Controls whether to enable right-side truncation.
     * When enabled, removes the right row indicators and stop pattern to save space.
     * If null, the printer will use N (no truncation).
     *
     * @param enableRightSideTruncation whether to enable right-side truncation
     * @return the current truncation setting
     */
    private final @Nullable Boolean enableRightSideTruncation;

    /**
     * {@inheritDoc}
     *
     * <p>Generates PDF417 specific ZPL commands including:</p>
     * <ul>
     *     <li>^B7 command with PDF417 parameters</li>
     *     <li>Row height in dots</li>
     *     <li>Security level</li>
     *     <li>Data columns and rows configuration</li>
     *     <li>Truncation settings</li>
     * </ul>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        zplCommand
            .append(super.toZplString(dpi))
            .append(generateZplIICommand(
                ZplCommand.BARCODE_PDF_417,
                orientation != null ? orientation.getValue() : null,
                dpi.toDots(rowHeightMm),
                securityLevel,
                dataColumns,
                rows,
                enableRightSideTruncation != null ? enableRightSideTruncation ? "Y" : "N" : null))
            .append(content.toZplString(dpi));

        return zplCommand.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>For PDF417 barcodes, additionally validates:</p>
     * <ul>
     *     <li>Data length is not more than 3000 characters</li>
     *     <li>Row height is positive</li>
     *     <li>Security level is between 0 and 8</li>
     *     <li>Data columns are between 1 and 30</li>
     *     <li>Rows are between 3 and 90</li>
     * </ul>
     *
     * @throws IllegalStateException if any validation fails
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        super.validateInContext(size, dpi, defaultFont);
        validateParameters();
    }

    /**
     * Performs detailed parameter validation for the PDF417 barcode.
     *
     * @throws IllegalStateException if any validation fails
     */
    private void validateParameters()
    {
        if (content.getData()
                   .length() > 3000) {
            throw new IllegalStateException("Field data is limited to 3K characters");
        }
        if (rowHeightMm < 0) {
            throw new IllegalStateException("Row height must be greater than 0");
        }
        if (securityLevel < 0 || securityLevel > 8) {
            throw new IllegalStateException("Security level must be between 0 and 8");
        }
        if (dataColumns < 0 || dataColumns > 30) {
            throw new IllegalStateException("Number of data columns must be between 1 and 30");
        }
        if (rows < 0 || rows > 90) {
            throw new IllegalStateException("Number of rows must be between 3 and 90");
        }
    }

    public static abstract class BarcodePDF417Builder<C extends BarcodePDF417, B extends BarcodePDF417Builder<C, B>>
        extends Barcode.BarcodeBuilder<C, B>
    {
        @Override
        public C addToLabel(ZebraLabel label) throws IllegalStateException
        {
            Double elementHeightMm = null;
            if (this.rows != null && this.rowHeightMm != null) {
                elementHeightMm = this.rows * this.rowHeightMm;
            }
            resolveDynamicPositioning(label.getSize(), null, elementHeightMm);
            return super.addToLabel(label);
        }
    }
}
