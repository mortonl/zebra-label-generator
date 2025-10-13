package com.github.mortonl.zebra.formatting;

import com.github.mortonl.zebra.elements.barcodes.code_128.BarcodeCode128;
import com.github.mortonl.zebra.elements.barcodes.interleaved_2OF5.BarcodeInterleaved2of5;
import com.github.mortonl.zebra.elements.barcodes.pdf_417.BarcodePDF417;
import com.github.mortonl.zebra.elements.fonts.Font;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents field orientation options for ZPL II commands related to barcodes and fonts.
 * These values determine how barcodes and font text are rotated when printed.
 *
 * <p>When no orientation is specified, the printer will use NORMAL orientation
 * or the last orientation set by a relevant command.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * BarcodeCode128.createCode128Barcode()
 *     .withHeightMm(15.0)
 *     .withOrientation(Orientation.NORMAL)
 *     .withPrintInterpretationLine(true)
 *     .withMode(Code128Mode.AUTO)
 *     .withPlainTextContent("12345")
 *     .addToLabel(label);
 * }</pre>
 *
 * <p>Orientation angles:</p>
 * <ul>
 *     <li>NORMAL - 0 degrees (default)</li>
 *     <li>ROTATED - 90 degrees clockwise</li>
 *     <li>INVERTED - 180 degrees</li>
 *     <li>BOTTOM_UP - 270 degrees clockwise (90 degrees counterclockwise)</li>
 * </ul>
 *
 * @see BarcodeCode128 For creating Code 128 barcodes with specified orientation
 * @see BarcodeInterleaved2of5 For creating Interleaved 2 of 5 barcodes with specified orientation
 * @see BarcodePDF417 For creating PDF417 barcodes with specified orientation
 * @see Font For creating text with specified orientation
 */
@Getter
@AllArgsConstructor
public enum Orientation
{
    /**
     * Normal orientation (0 degrees).
     * This is the default orientation for most printers.
     */
    NORMAL("N"),

    /**
     * Rotated 90 degrees clockwise.
     */
    ROTATED("R"),

    /**
     * Inverted 180 degrees.
     */
    INVERTED("I"),

    /**
     * Rotated 270 degrees clockwise (90 degrees counterclockwise).
     */
    BOTTOM_UP("B");

    /**
     * The ZPL II command value for this orientation.
     * Single-character code used in font and field rotation commands.
     *
     * @param value the orientation command code
     * @return the ZPL command parameter
     * @throws IllegalArgumentException if value is invalid
     */
    private final String value;
}
