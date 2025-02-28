package com.github.mortonl.zebra.elements.graphics;

import com.github.mortonl.zebra.elements.PositionedAndSizedElement;
import com.github.mortonl.zebra.formatting.LineColor;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.GRAPHIC_BOX;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

@Getter
@SuperBuilder(builderMethodName = "createGraphicBox", setterPrefix = "with")
public class GraphicBox extends PositionedAndSizedElement
{
    private static final double MAX_DIMENSION = 1333.33;
    private static final double MIN_THICKNESS = 0.04;

    private static final String DIMENSION_ERROR_MESSAGE =
        "Maximum value for width, height and thickness is " + MAX_DIMENSION;

    private final Double thicknessMm;

    private final LineColor color;

    private final Integer roundness;

    /**
     * Creates a builder pre-configured for a horizontal line.
     *
     * @param widthMm     Width in millimeters (thickness-32000)
     * @param thicknessMm Line thickness in millimeters (1-32000)
     * @return a builder instance configured for a horizontal line
     */
    public static GraphicBoxBuilder<?, ?> horizontalLine(double widthMm, double thicknessMm)
    {
        return createGraphicBox()
            .withSize(widthMm, thicknessMm) // Height must equal thicknessMm for a horizontal line
            .withThicknessMm(thicknessMm);
    }

    /**
     * Creates a builder pre-configured for a vertical line.
     *
     * @param heightMm    Height in millimeters (thickness-32000)
     * @param thicknessMm Line thickness in millimeters (1-32000)
     * @return a builder instance configured for a vertical line
     */
    public static GraphicBoxBuilder<?, ?> verticalLine(double heightMm, double thicknessMm)
    {
        return createGraphicBox()
            .withSize(thicknessMm, heightMm) // Width must equal thicknessMm for a vertical line
            .withThicknessMm(thicknessMm);
    }

    @Override
    public String toZplString(PrintDensity dpi)
    {
        return super.toZplString(dpi) + generateZplIICommand(GRAPHIC_BOX,
            widthMm != null ? dpi.toDots(widthMm) : null,
            heightMm != null ? dpi.toDots(heightMm) : null,
            thicknessMm != null ? dpi.toDots(thicknessMm) : null,
            color != null ? color.getCode() : null,
            roundness
        ) + FIELD_END;
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        // If all parameters are null, that's valid
        if (widthMm == null && heightMm == null && thicknessMm == null && color == null && roundness == null) {
            return;
        }

        validateThickness();
        validateWidth(size);
        validateHeight(size);
        validateRoundness();
    }

    private void validateRoundness()
    {
        if (roundness != null) {
            validateRange(roundness, 0, 8, "Roundness");
        }
    }

    private void validateHeight(LabelSize size)
    {
        if (heightMm != null) {
            if (thicknessMm != null) {
                if (heightMm < thicknessMm) {
                    throw new IllegalStateException(
                        String.format("Height must be at least equal to thickness (%.2f)", thicknessMm)
                    );
                }
            }

            validateRange(heightMm, 0, MAX_DIMENSION, "Height");

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
            if (thicknessMm != null) {
                if (widthMm < thicknessMm) {
                    throw new IllegalStateException(
                        String.format("Width must be at least equal to thickness (%.2f)", thicknessMm)
                    );
                }
            }

            validateRange(widthMm, 0, MAX_DIMENSION, "Width");

            if (widthMm > size.getWidthMm()) {
                throw new IllegalStateException(
                    String.format("Width (%.2f mm) exceeds label width (%.2f mm)",
                        widthMm, size.getWidthMm())
                );
            }
        }
    }

    private void validateThickness()
    {
        if (thicknessMm != null) {
            validateRange(thicknessMm, MIN_THICKNESS, MAX_DIMENSION, "Thickness");
        }
    }

    /**
     * Calculates the rounding radius based on the ZPL specification
     *
     * @return The calculated rounding radius
     */
    private int calculateRoundingRadius()
    {
        if (roundness == 0) {
            return 0;
        }
        double shorterSide = Math.min(widthMm, heightMm);
        return (int) ((roundness * shorterSide) / 16); // (roundness/8) * (shorterSide/2)
    }

    public static abstract class GraphicBoxBuilder<C extends GraphicBox, B extends GraphicBoxBuilder<C, B>>
        extends PositionedAndSizedElementBuilder<C, B>
    {
    }
}
