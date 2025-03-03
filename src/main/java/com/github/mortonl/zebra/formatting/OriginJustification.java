package com.github.mortonl.zebra.formatting;

import com.github.mortonl.zebra.elements.PositionedElement;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents justification options for positioned elements in ZPL II commands.
 * These values determine how an element's position coordinates are interpreted relative to the element itself.
 *
 * <p>When no justification is specified, the printer will use LEFT justification
 * or the last justification set by a relevant command.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * PositionedElement element = new PositionedElement()
 *     .withPosition(100, 100)
 *     .withJustification(OriginJustification.RIGHT)
 *     .build();
 * }</pre>
 *
 * <p>Justification modes:</p>
 * <ul>
 *     <li>LEFT (0) - The element's position coordinates represent its left edge (default)</li>
 *     <li>RIGHT (1) - The element's position coordinates represent its right edge</li>
 *     <li>AUTO (2) - The justification is determined automatically based on context</li>
 * </ul>
 *
 * @see PositionedElement For creating elements with specified origin justification
 */
@Getter
@AllArgsConstructor
public enum OriginJustification
{
    /**
     * Left justification (0).
     * Position coordinates represent the element's left edge.
     * This is the default justification for most printers.
     */
    LEFT(0),

    /**
     * Right justification (1).
     * Position coordinates represent the element's right edge.
     */
    RIGHT(1),

    /**
     * Automatic justification (2).
     * Justification is determined automatically based on context.
     */
    AUTO(2);

    /**
     * The ZPL II command value for this justification mode.
     * Internal numeric representation used in field position commands.
     *
     * @param value the justification mode value
     * @return the numeric ZPL command parameter
     * @throws IllegalArgumentException if value is invalid
     */
    private final Integer value;
}
