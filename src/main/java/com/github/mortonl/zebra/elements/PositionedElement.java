package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.formatting.OriginJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.validation.ValidationLevel;
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
 * <ul>
 *     <li>X increasing from left to right</li>
 *     <li>Y increasing from top to bottom</li>
 * </ul></p>
 *
 * <p>Example usage:
 * <pre>{@code
 * PositionedElement element = SomePositionedElement.createPositionedElement()
 *     .withPosition(10.0, 20.0)
 *     .build();
 * }</pre></p>
 *
 * @see LabelElement The parent class for all label elements
 */
@Getter
@SuperBuilder(builderMethodName = "createPositionedElement", setterPrefix = "with")
public abstract class PositionedElement extends LabelElement
{
    public static final int MIN_AXIS_VALUE = 0;
    public static final int MAX_AXIS_VALUE = 32000;

    /**
     * The horizontal position in millimeters from the left edge of the label.
     * When not specified, the printer's default position is used.
     */
    private final double xAxisLocationMm;

    /**
     * The vertical position in millimeters from the top edge of the label.
     * When not specified, the printer's default position is used.
     */
    private final double yAxisLocationMm;

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
     * <p>For positioned elements, this includes validation of:
     * <ul>
     *     <li>X position within label width</li>
     *     <li>Y position within label height</li>
     * </ul></p>
     *
     * @throws IllegalStateException if the position is outside the label boundaries
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        if (validationLevel == ValidationLevel.OFF) {
            return; // Skip validation
        }

        // Perform validation based on the level
        switch (validationLevel) {
            case STRICT:
            case NORMAL:
                validateAxisValue(dpi.toDots(xAxisLocationMm), "X-axis");
                validateAxisValue(dpi.toDots(yAxisLocationMm), "Y-axis");
                validatePositionedOnLabel(size, dpi);
                break;
            case LENIENT:
                break;
            default:
                break;
        }
    }

    private void validateAxisValue(double value, String axis)
    {
        if (value < MIN_AXIS_VALUE || value > MAX_AXIS_VALUE) {
            throw new IllegalStateException(
                String.format("%s location must be between %d and %d dots",
                    axis, MIN_AXIS_VALUE, MAX_AXIS_VALUE)
            );
        }
    }

    private void validatePositionedOnLabel(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        if (xAxisLocationMm > size.getWidthMm() || yAxisLocationMm > size.getHeightMm()) {
            StringBuilder errorMessage = new StringBuilder();
            if (xAxisLocationMm > size.getWidthMm()) {
                errorMessage.append(String.format("X-axis position (%.2f mm) exceeds label width (%.2f mm). ",
                    xAxisLocationMm, size.getWidthMm()));
            }
            if (yAxisLocationMm > size.getHeightMm()) {
                errorMessage.append(String.format("Y-axis position (%.2f mm) exceeds label height (%.2f mm). ",
                    yAxisLocationMm, size.getHeightMm()));
            }
            errorMessage.append("The element must be positioned within the label dimensions.");

            throw new IllegalStateException(errorMessage.toString());
        }
    }

    protected static abstract class PositionedElementBuilder<C extends PositionedElement, B extends PositionedElementBuilder<C, B>>
        extends LabelElementBuilder<C, B>
    {
        public B withPosition(double xAxisLocationMm, double yAxisLocationMm)
        {
            this.xAxisLocationMm = xAxisLocationMm;
            this.yAxisLocationMm = yAxisLocationMm;
            return self();
        }
    }
}
