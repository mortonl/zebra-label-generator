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

@Getter
@SuperBuilder(setterPrefix = "with")
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
     * Creates a horizontal line with specified widthMm
     *
     * @param widthMm     Width in dots (thicknessMm-32000)
     * @param thicknessMm Line thicknessMm in dots (1-32000)
     * @return GraphicBox configured as a horizontal line
     */
    public static GraphicBox horizontalLine(double widthMm, double thicknessMm)
    {
        return GraphicBox
            .builder()
            .withSize(widthMm, thicknessMm) // Height must equal thicknessMm for a horizontal line
            .withThicknessMm(thicknessMm)
            .build();
    }

    /**
     * Creates a vertical line with specified heightMm
     *
     * @param heightMm    Height in dots (thicknessMm-32000)
     * @param thicknessMm Line thicknessMm in dots (1-32000)
     * @return GraphicBox configured as a vertical line
     */
    public static GraphicBox verticalLine(double heightMm, double thicknessMm)
    {
        return GraphicBox
            .builder()
            .withSize(thicknessMm, heightMm) // Width must equal thicknessMm for a vertical line
            .withThicknessMm(thicknessMm)
            .build();
    }

    @Override
    public String toZplString(PrintDensity dpi)
    {
        return super.toZplString(dpi) + generateZplIICommand(GRAPHIC_BOX,
            dpi.toDots(widthMm),
            dpi.toDots(heightMm),
            dpi.toDots(thicknessMm),
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
        if (roundness != null && (roundness < 0 || roundness > 8)) {
            throw new IllegalArgumentException("Roundness must be between 0 and 8");
        }
    }

    private void validateHeight(LabelSize size)
    {
        if (heightMm != null) {
            if (thicknessMm != null && heightMm < thicknessMm) {
                throw new IllegalArgumentException(
                    String.format("Height must be at least equal to thickness (%d)", thicknessMm)
                );
            }
            if (heightMm > MAX_DIMENSION) {
                throw new IllegalArgumentException(DIMENSION_ERROR_MESSAGE);
            }
            // Check if height fits within label height
            if (heightMm > size.getHeightMm()) {
                throw new IllegalArgumentException(
                    String.format("Height (%d mm) exceeds label height (%d mm)",
                        heightMm, size.getHeightMm())
                );
            }
        }
    }

    private void validateWidth(LabelSize size)
    {
        if (widthMm != null) {
            if (thicknessMm != null && widthMm < thicknessMm) {
                throw new IllegalArgumentException(
                    String.format("Width must be at least equal to thickness (%d)", thicknessMm)
                );
            }
            if (widthMm > MAX_DIMENSION) {
                throw new IllegalArgumentException(DIMENSION_ERROR_MESSAGE);
            }
            // Check if width fits within label width
            if (widthMm > size.getWidthMm()) {
                throw new IllegalArgumentException(
                    String.format("Width (%d mm) exceeds label width (%d mm)",
                        widthMm, size.getWidthMm())
                );
            }
        }
    }

    private void validateThickness()
    {
        if (thicknessMm != null) {
            if (thicknessMm < MIN_THICKNESS) {
                throw new IllegalArgumentException("Thickness must be at least " + MIN_THICKNESS);
            }
            if (thicknessMm > MAX_DIMENSION) {
                throw new IllegalArgumentException(DIMENSION_ERROR_MESSAGE);
            }
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
}
