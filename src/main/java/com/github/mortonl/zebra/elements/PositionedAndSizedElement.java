package com.github.mortonl.zebra.elements;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Base class for elements that can be positioned and sized on a label.
 * Extends {@link PositionedElement} to add width and height dimensions.
 *
 * <p>Dimensions are specified in millimeters. Both position and size are validated
 * to ensure the element fits within the label boundaries.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * PositionedAndSizedElement element = SomePositionedAndSizedElement.builder()
 *     .withPosition(10.0, 20.0)
 *     .withSize(50.0, 30.0)
 *     .build();
 * }</pre></p>
 *
 * @see PositionedElement For positioning functionality
 */
@Getter
@SuperBuilder(builderMethodName = "createPositionedAndSizedElement", setterPrefix = "with")
public abstract class PositionedAndSizedElement extends PositionedElement
{
    /**
     * The width of the element in millimeters.
     * When not specified, the printer's default width is used.
     */
    protected Double widthMm;

    /**
     * The height of the element in millimeters.
     * When not specified, the printer's default height is used.
     */
    protected Double heightMm;

    /**
     * Builder for creating elements that have both position and size.
     * Extends {@link PositionedElementBuilder} to add size configuration methods.
     *
     * <p>This builder provides methods for setting:
     * <ul>
     *     <li>Position (inherited from PositionedElementBuilder)</li>
     *     <li>Width</li>
     *     <li>Height</li>
     * </ul></p>
     *
     * <p>Example usage:
     * <pre>{@code
     * SomeElement element = SomeElement.builder()
     *     .withPosition(10.0, 20.0)  // From PositionedElementBuilder
     *     .withSize(50.0, 30.0)      // From this builder
     *     .build();
     *
     * // Or using individual dimension setters
     * SomeElement element = SomeElement.builder()
     *     .withPosition(10.0, 20.0)
     *     .withWidth(50.0)
     *     .withHeight(30.0)
     *     .build();
     * }</pre></p>
     *
     * @param <C> The type of the element being built
     * @param <B> The type of the builder itself (for method chaining)
     * @see PositionedElementBuilder For position setting capabilities
     * @see PositionedAndSizedElement For the element class this builds
     */
    public static abstract class PositionedAndSizedElementBuilder<C extends PositionedAndSizedElement, B extends PositionedAndSizedElementBuilder<C, B>>
        extends PositionedElementBuilder<C, B>
    {
        /**
         * Sets both width and height dimensions simultaneously.
         *
         * <p>This is a convenience method equivalent to calling {@link #withWidthMm(Double)}
         * and {@link #withHeightMm(Double)} separately.</p>
         *
         * @param widthMm  The width in millimeters
         * @param heightMm The height in millimeters
         * @return The builder instance for method chaining
         * @throws IllegalArgumentException if either dimension is negative
         */
        public B withSize(double widthMm, double heightMm)
        {
            this.widthMm = widthMm;
            this.heightMm = heightMm;
            return self();
        }
    }
}
