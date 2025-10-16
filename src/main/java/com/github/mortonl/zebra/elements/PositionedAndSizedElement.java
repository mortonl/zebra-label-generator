package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Base class for elements that can be positioned and sized on a label.
 * Extends {@link PositionedElement} to add width and height dimensions.
 *
 * <p>Dimensions are specified in millimeters. Both position and size are validated
 * to ensure the element fits within the label boundaries.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * PositionedAndSizedElement element = SomePositionedAndSizedElement.builder()
 *     .withPosition(10.0, 20.0)
 *     .withSize(50.0, 30.0)
 *     .build();
 * }</pre>
 *
 * @see PositionedElement For positioning functionality
 */
@Getter
@SuperBuilder(builderMethodName = "createPositionedAndSizedElement", setterPrefix = "with")
public abstract class PositionedAndSizedElement extends PositionedElement
{
    /**
     * The width of the element in millimeters.
     * Specifies the horizontal dimension of the printed element.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When null: Uses printer's default width for the element type</li>
     *     <li>When specified: Must be a positive value within printer limitations</li>
     * </ul>
     *
     * <p>The final printed width in dots is calculated using the printer's DPI setting.</p>
     *
     * @param widthMm the desired width in millimeters, or null for printer default
     * @return the element width in millimeters
     */
    protected Double widthMm;

    /**
     * The height of the element in millimeters.
     * Specifies the vertical dimension of the printed element.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When null: Uses printer's default height for the element type</li>
     *     <li>When specified: Must be a positive value within printer limitations</li>
     * </ul>
     *
     * <p>The final printed height in dots is calculated using the printer's DPI setting.</p>
     *
     * @param heightMm the desired height in millimeters, or null for printer default
     * @return the element height in millimeters
     */
    protected Double heightMm;

    /**
     * {@inheritDoc}
     *
     * <p>Additional validation for PositionedAndSizedElement includes:
     * <ul>
     *     <li>Width less than or equal to label width check</li>
     *     <li>Height less than or equal to label height check</li>
     * </ul>
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        super.validateInContext(size, dpi, defaultFont);
        validateWidth(size);
        validateHeight(size);
    }

    private void validateHeight(LabelSize size)
    {
        if (heightMm != null) {
            if (heightMm > size.getHeightMm()) {
                throw new IllegalStateException(
                    String.format("Height (%.2f mm) exceeds label height (%.2f mm)",
                        heightMm, size.getHeightMm())
                );
            }
        }
    }

    private void validateWidth(LabelSize size)
    {
        if (widthMm != null) {
            if (widthMm > size.getWidthMm()) {
                throw new IllegalStateException(
                    String.format("Width (%.2f mm) exceeds label width (%.2f mm)",
                        widthMm, size.getWidthMm())
                );
            }
        }
    }

    /**
     * Builder for creating elements that have both position and size.
     * Extends {@link PositionedElementBuilder} to add size configuration methods.
     *
     * <p>This builder provides methods for setting:</p>
     * <ul>
     *     <li>Position (inherited from PositionedElementBuilder)</li>
     *     <li>Width</li>
     *     <li>Height</li>
     * </ul>
     *
     * <p>Example usage:</p>
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
     * }</pre>
     *
     * @param <C> The type of the element being built
     * @param <B> The type of the builder itself (for method chaining)
     *
     * @see PositionedElementBuilder For position setting capabilities
     * @see PositionedAndSizedElement For the element class this builds
     */
    public abstract static class PositionedAndSizedElementBuilder<C extends PositionedAndSizedElement, B extends PositionedAndSizedElementBuilder<C, B>>
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
         *
         * @return The builder instance for method chaining
         *
         * @throws IllegalArgumentException if either dimension is negative
         */
        public B withSize(double widthMm, double heightMm)
        {
            this.widthMm  = widthMm;
            this.heightMm = heightMm;
            return self();
        }
    }
}
