package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.formatting.OriginJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_ORIGIN;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

@Data
@SuperBuilder(setterPrefix = "with")
@AllArgsConstructor
public abstract class PositionedElement implements LabelElement
{
    public static final int MIN_AXIS_VALUE = 0;
    public static final int MAX_AXIS_VALUE = 32000;

    private final double xAxisLocationMm;

    private final double yAxisLocationMm;

    private final OriginJustification zOriginJustification;

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

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        validateAxisValue(dpi.toDots(xAxisLocationMm), "X-axis");
        validateAxisValue(dpi.toDots(yAxisLocationMm), "Y-axis");
        validatePositionedOnLabel(size, dpi);
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
    {
        public B withPosition(double xAxisLocationMm, double yAxisLocationMm)
        {
            this.xAxisLocationMm = xAxisLocationMm;
            this.yAxisLocationMm = yAxisLocationMm;
            return self();
        }
    }
}
