package com.github.mortonl.zebra.formatting;

import com.github.mortonl.zebra.elements.graphics.GraphicBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents line colors for ZPL II commands that support color specification.
 * These values correspond to the color parameter in various ZPL II commands.
 *
 * <p>When no color is specified in a command, the printer will use its default color
 * or the last color set by a relevant command. This allows for effective use of printer
 * default settings.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * GraphicBox box = GraphicBox.createGraphicBox()
 *     .withPosition(100.0, 100.0)
 *     .withSize(200.0, 100.0)
 *     .withThicknessMm(0.4)
 *     .withColor(LineColor.BLACK)
 *     .withRoundness(8)
 *     .build();
 * }</pre></p>
 *
 * @see GraphicBox For creating boxes with specified colors
 * // * @see GraphicCircle For creating circles with specified colors
 * // * @see GraphicDiagonalLine For creating diagonal lines with specified colors
 */
@Getter
@AllArgsConstructor
public enum LineColor
{
    /**
     * Black color (default for most printers).
     * Used for standard printing on light backgrounds.
     */
    BLACK('B'),

    /**
     * White color.
     * Typically used for inverse printing or printing on dark backgrounds.
     */
    WHITE('W');

    /**
     * The ZPL II command code for this color.
     */
    private final char code;

    /**
     * Find LineColor by ZPL code
     *
     * @param code the ZPL code ('B' or 'W')
     * @return the matching LineColor
     * @throws IllegalArgumentException if code is invalid
     */
    public static LineColor fromCode(char code)
    {
        for (LineColor color : values()) {
            if (color.code == code) {
                return color;
            }
        }
        throw new IllegalArgumentException(
            "Invalid color code: '" + code + "'. Must be 'B' for black or 'W' for white"
        );
    }
}
