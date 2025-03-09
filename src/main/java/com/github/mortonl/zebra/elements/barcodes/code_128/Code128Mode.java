package com.github.mortonl.zebra.elements.barcodes.code_128;

import com.github.mortonl.zebra.elements.barcodes.BarcodeCode128;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the different modes for Code 128 barcodes.
 * Each mode is associated with a specific character value used in the ZPL command.
 * The mode selection affects how the barcode data is encoded and interpreted.
 *
 * <p>These modes correspond to the mode parameter in the ^BC ZPL command for
 * Code 128 barcodes. The selected mode determines the encoding rules and
 * validation requirements for the barcode content.</p>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>{@code
 * BarcodeCode128.createCode128Barcode()
 *     .withMode(Code128Mode.AUTOMATIC)
 *     .withPlainTextContent("12345")
 *     .addToLabel(label);
 * }</pre>
 *
 * @see BarcodeCode128 The barcode class that uses these modes
 * @see <a href="https://support.zebra.com/cpws/docs/zpl/bc_128.htm">Zebra Code 128 Documentation</a>
 */
@Getter
@AllArgsConstructor
public enum Code128Mode
{
    /**
     * No specific mode (Standard Code 128).
     * Represented by "N" in ZPL command.
     *
     * <p>Uses standard Code 128 encoding without any special processing.
     * Suitable for general-purpose barcode generation where no specific
     * format is required.</p>
     *
     * @see BarcodeCode128
     */
    NONE("N"),

    /**
     * UCC Case mode.
     * Represented by "U" in ZPL command.
     *
     * <p>Used for UCC/EAN-128 Case shipping container identification.
     * When this mode is selected:</p>
     * <ul>
     *     <li>Content must not exceed 19 digits</li>
     *     <li>Only numeric data is allowed</li>
     *     <li>FNC1 is automatically inserted at the start</li>
     * </ul>
     *
     * @see BarcodeCode128#validateInContext(LabelSize, PrintDensity)
     */
    UCC_CASE("U"),

    /**
     * Automatic mode.
     * Represented by "A" in ZPL command.
     *
     * <p>Automatically selects the optimal Code 128 subset (A, B, or C)
     * based on the content. This is the recommended mode for most
     * general-purpose applications.</p>
     *
     * <p>Benefits:</p>
     * <ul>
     *     <li>Optimal data density</li>
     *     <li>Automatic subset switching</li>
     *     <li>Support for full ASCII character set</li>
     * </ul>
     */
    AUTOMATIC("A"),

    /**
     * UCC/EAN mode.
     * Represented by "D" in ZPL command.
     *
     * <p>Used for UCC/EAN-128 format that requires specific data formatting.
     * This mode:</p>
     * <ul>
     *     <li>Automatically adds FNC1 at the start</li>
     *     <li>Supports Application Identifiers (AI)</li>
     *     <li>Enforces UCC/EAN-128 formatting rules</li>
     * </ul>
     */
    UCC_EAN("D");

    /**
     * The character value used in the ZPL command to represent this mode.
     * This value is automatically included in the generated ZPL command
     * when the mode is specified.
     *
     * @see BarcodeCode128#toZplString(PrintDensity)
     */
    private final String value;
}

