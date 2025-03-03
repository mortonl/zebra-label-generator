package com.github.mortonl.zebra.formatting;

import com.github.mortonl.zebra.elements.text.TextBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents text alignment options within text blocks in ZPL II commands.
 * These values determine how text content is aligned within its containing block.
 *
 * <p>When no text justification is specified, the printer will use LEFT alignment
 * or the last alignment set by a relevant command.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * TextBlock block = TextBlock.builder()
 *     .withPosition(100, 100)
 *     .withTextJustification(TextJustification.CENTER)
 *     .withPlainTextContent("Sample text")
 *     .build();
 * }</pre>
 *
 * <p>Justification modes:</p>
 * <ul>
 *     <li>LEFT ("L") - Aligns text to the left edge of the block (default)</li>
 *     <li>CENTER ("C") - Centers text within the block</li>
 *     <li>RIGHT ("R") - Aligns text to the right edge of the block</li>
 *     <li>JUSTIFIED ("J") - Spreads text evenly across the block width</li>
 * </ul>
 *
 * @see TextBlock For creating text blocks with specified text justification
 */
@Getter
@AllArgsConstructor
public enum TextJustification
{
    /**
     * Left alignment.
     * Text is aligned to the left edge of the block.
     * This is the default alignment for most printers.
     */
    LEFT("L"),

    /**
     * Center alignment.
     * Text is centered within the block.
     */
    CENTER("C"),

    /**
     * Right alignment.
     * Text is aligned to the right edge of the block.
     */
    RIGHT("R"),

    /**
     * Justified alignment.
     * Text is spread evenly across the block width.
     */
    JUSTIFIED("J");

    /**
     * Single-character ZPL code for the justification mode.
     *
     * @param value the ZPL justification code
     * @return the command parameter
     */
    private final String value;
}
