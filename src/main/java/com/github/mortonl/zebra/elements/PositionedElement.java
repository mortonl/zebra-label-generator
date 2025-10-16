package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.formatting.OriginJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_ORIGIN;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

/**
 * Base class for elements that can be positioned on a label.
 * Provides x and y coordinate positioning functionality.
 *
 * <p>Coordinates are specified in millimeters from the label's origin point.
 * The origin (0,0) is typically at the top-left corner of the label, with:
 * </p>
 * <ul>
 *     <li>X increasing from left to right</li>
 *     <li>Y increasing from top to bottom</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * PositionedElement element = SomePositionedElement.createPositionedElement()
 *     .withPosition(10.0, 20.0)
 *     .build();
 * }</pre>
 *
 * @see LabelElement The parent class for all label elements
 */
@Getter
@SuperBuilder(builderMethodName = "createPositionedElement", setterPrefix = "with")
public abstract class PositionedElement extends LabelElement
{
    /**
     * The minimum allowed value in dots for both X and Y axis positions.
     * Used as the lower bound when validating element positions after
     * conversion from millimeters to dots.
     */
    public static final int MIN_AXIS_VALUE = 0;

    /**
     * The maximum allowed value in dots for both X and Y axis positions.
     * Used as the upper bound when validating element positions after
     * conversion from millimeters to dots.
     */
    public static final int MAX_AXIS_VALUE = 32000;

    /**
     * The horizontal position in millimeters from the left edge of the label.
     * When not specified, the printer's default position is used.
     * The value will be converted to dots based on the printer's DPI setting,
     * and must fall between {@value MIN_AXIS_VALUE} and {@value MAX_AXIS_VALUE} dots.
     *
     * @param xAxisLocationMm the horizontal position in millimeters (will be converted to dots using printer DPI)
     * @return the horizontal position in millimeters from the left edge
     */
    private final double xAxisLocationMm;

    /**
     * The vertical position in millimeters from the top edge of the label.
     * When not specified, the printer's default position is used.
     * The value will be converted to dots based on the printer's DPI setting,
     * and must fall between {@value MIN_AXIS_VALUE} and {@value MAX_AXIS_VALUE} dots.
     *
     * @param yAxisLocationMm the vertical position in millimeters (will be converted to dots using printer DPI)
     * @return the vertical position in millimeters from the top edge
     */
    private final double yAxisLocationMm;

    /**
     * The justification origin point for the element's position.
     *
     * @param zOriginJustification the origin justification to use for positioning
     * @return the origin justification point used for element positioning
     */
    private final OriginJustification zOriginJustification;

    /**
     * {@inheritDoc}
     *
     * <p>For positioned elements, this includes the field position (^FO) command
     * if x or y positions are specified.</p>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplString = new StringBuilder();

        zplString.append(generateZplIICommand(
            FIELD_ORIGIN,
            dpi.toDots(xAxisLocationMm),
            dpi.toDots(yAxisLocationMm)
        ));

        if (zOriginJustification != null) {
            zplString
                .append(",")
                .append(zOriginJustification.getValue());
        }

        return zplString.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>For positioned elements, this includes validation of:</p>
     * <ul>
     *     <li>X position within label width</li>
     *     <li>Y position within label height</li>
     * </ul>
     *
     * @throws IllegalStateException if the position is outside the label boundaries
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        validateAxisValue(xAxisLocationMm, "X-axis", dpi);
        validateAxisValue(yAxisLocationMm, "Y-axis", dpi);
        validatePositionedOnLabel(size, dpi);
    }

    private void validateAxisValue(double value, String axis, PrintDensity dpi)
    {
        if (value < 0) {
            throw new IllegalStateException(
                String.format("%s position cannot be negative: %.2f",
                    axis, value)
            );
        }

        double dotsValue = dpi.toDots(value);
        if (dotsValue < MIN_AXIS_VALUE || dotsValue > MAX_AXIS_VALUE) {
            throw new IllegalStateException(
                String.format("%s location must be between %d and %d dots",
                    axis, MIN_AXIS_VALUE, MAX_AXIS_VALUE)
            );
        }
    }

    private void validatePositionedOnLabel(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        final double labelWidthMm = size.getWidthMm();
        final double labelHeightMm = size.getHeightMm();

        if (xAxisLocationMm > labelWidthMm || yAxisLocationMm > labelHeightMm) {
            StringBuilder errorMessage = new StringBuilder();
            if (xAxisLocationMm > labelWidthMm) {
                errorMessage.append(String.format("X-axis position (%.2f mm) exceeds label width (%.2f mm). ",
                    xAxisLocationMm, labelWidthMm));
            }
            if (yAxisLocationMm > labelHeightMm) {
                errorMessage.append(String.format("Y-axis position (%.2f mm) exceeds label height (%.2f mm). ",
                    yAxisLocationMm, labelHeightMm));
            }
            errorMessage.append("The element must be positioned within the label dimensions.");

            throw new IllegalStateException(errorMessage.toString());
        }
    }

    /**
     * Abstract builder class for elements that can be positioned on a label.
     * Provides common positioning functionality for all label elements.
     *
     * @param <C> the type of PositionedElement being built
     * @param <B> the concrete builder type (self-referential for method chaining)
     */
    protected static abstract class PositionedElementBuilder<C extends PositionedElement, B extends PositionedElementBuilder<C, B>>
        extends LabelElementBuilder<C, B>
    {
        // Dynamic positioning markers
        private DynamicPosition dynamicXPosition;

        private DynamicPosition dynamicYPosition;

        private Double xOffsetMm;

        private Double yOffsetMm;

        /**
         * Sets the position of the element on the label.
         *
         * <p>The position is specified in millimeters from the left and top edges of the label.
         * These values will be converted to dots based on the printer's DPI setting.</p>
         *
         * @param xAxisLocationMm horizontal position in millimeters from the left edge
         * @param yAxisLocationMm vertical position in millimeters from the top edge
         *
         * @return this builder for method chaining
         */
        public B withPosition(double xAxisLocationMm, double yAxisLocationMm)
        {
            this.xAxisLocationMm  = xAxisLocationMm;
            this.yAxisLocationMm  = yAxisLocationMm;
            this.dynamicXPosition = null;
            this.dynamicYPosition = null;
            this.xOffsetMm        = null;
            this.yOffsetMm        = null;
            return self();
        }

        /**
         * Positions the element at the left edge of the label (x = 0).
         *
         * @return this builder for method chaining
         */
        public B onLeftEdge()
        {
            this.dynamicXPosition = DynamicPosition.LEFT;
            this.xOffsetMm        = null;
            return self();
        }

        /**
         * Positions the element at the right edge of the label.
         * Note: For sized elements, this accounts for the element's width.
         *
         * @return this builder for method chaining
         */
        public B onRightEdge()
        {
            this.dynamicXPosition = DynamicPosition.RIGHT;
            this.xOffsetMm        = null;
            return self();
        }

        /**
         * Centers the element horizontally on the label.
         * Note: For sized elements, this accounts for the element's width.
         *
         * @return this builder for method chaining
         */
        public B centeredHorizontally()
        {
            this.dynamicXPosition = DynamicPosition.CENTER;
            this.xOffsetMm        = null;
            return self();
        }

        /**
         * Positions the element at the top edge of the label (y = 0).
         *
         * @return this builder for method chaining
         */
        public B onTopEdge()
        {
            this.dynamicYPosition = DynamicPosition.TOP;
            this.yOffsetMm        = null;
            return self();
        }

        /**
         * Positions the element at the bottom edge of the label.
         * Note: For sized elements, this accounts for the element's height.
         *
         * @return this builder for method chaining
         */
        public B onBottomEdge()
        {
            this.dynamicYPosition = DynamicPosition.BOTTOM;
            this.yOffsetMm        = null;
            return self();
        }

        /**
         * Centers the element vertically on the label.
         * Note: For sized elements, this accounts for the element's height.
         *
         * @return this builder for method chaining
         */
        public B centeredVertically()
        {
            this.dynamicYPosition = DynamicPosition.CENTER;
            this.yOffsetMm        = null;
            return self();
        }

        /**
         * Centers the element both horizontally and vertically on the label.
         * Note: For sized elements, this accounts for the element's dimensions.
         *
         * @return this builder for method chaining
         */
        public B centered()
        {
            return centeredHorizontally().centeredVertically();
        }

        /**
         * Adds a horizontal offset to the current position.
         * Can be used with dynamic positioning methods.
         *
         * @param offsetMm offset in millimeters (positive = right, negative = left)
         *
         * @return this builder for method chaining
         */
        public B withXOffset(double offsetMm)
        {
            this.xOffsetMm = offsetMm;
            return self();
        }

        /**
         * Adds a vertical offset to the current position.
         * Can be used with dynamic positioning methods.
         *
         * @param offsetMm offset in millimeters (positive = down, negative = up)
         *
         * @return this builder for method chaining
         */
        public B withYOffset(double offsetMm)
        {
            this.yOffsetMm = offsetMm;
            return self();
        }

        /**
         * Resolves dynamic positioning to actual coordinates.
         * This method should be called before build() to resolve any dynamic positions.
         *
         * @param labelSize     the label size to use for dynamic positioning
         * @param elementWidth  the width of the element (null if not applicable)
         * @param elementHeight the height of the element (null if not applicable)
         */
        protected void resolveDynamicPositioning(LabelSize labelSize, Double elementWidth, Double elementHeight)
        {
            if (dynamicXPosition != null) {
                xAxisLocationMm = resolveDynamicX(labelSize, elementWidth);
            }
            if (dynamicYPosition != null) {
                yAxisLocationMm = resolveDynamicY(labelSize, elementHeight);
            }
        }

        private double resolveDynamicX(LabelSize labelSize, Double elementWidth)
        {
            double position = switch (dynamicXPosition) {
                case LEFT -> 0.0;
                case RIGHT -> elementWidth != null ?
                    labelSize.getWidthMm() - elementWidth :
                    labelSize.getWidthMm();
                case CENTER -> elementWidth != null ?
                    (labelSize.getWidthMm() - elementWidth) / 2.0 :
                    labelSize.getWidthMm() / 2.0;
                default -> 0.0;
            };
            return xOffsetMm != null ? position + xOffsetMm : position;
        }

        private double resolveDynamicY(LabelSize labelSize, Double elementHeight)
        {
            double position = switch (dynamicYPosition) {
                case TOP -> 0.0;
                case BOTTOM -> elementHeight != null ?
                    labelSize.getHeightMm() - elementHeight :
                    labelSize.getHeightMm();
                case CENTER -> elementHeight != null ?
                    (labelSize.getHeightMm() - elementHeight) / 2.0 :
                    labelSize.getHeightMm() / 2.0;
                default -> 0.0;
            };
            return yOffsetMm != null ? position + yOffsetMm : position;
        }

        /**
         * Validates and adds this element to the specified label, resolving any dynamic positioning first.
         */
        @Override
        public C addToLabel(ZebraLabel label) throws IllegalStateException
        {
            // Resolve any dynamic positioning using the provided label context before building
            resolveDynamicPositioning(label.getSize(), null, null);
            return super.addToLabel(label);
        }

        /**
         * Enum for dynamic positioning options
         */
        protected enum DynamicPosition
        {
            LEFT, RIGHT, CENTER, TOP, BOTTOM
        }
    }
}
