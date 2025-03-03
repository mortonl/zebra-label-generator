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

/**
 * Represents a graphic box or line element in ZPL format (^GB command).
 * This class can be used to create boxes, rectangles, and lines with various attributes
 * such as thickness, color, and corner roundness.
 *
 * <p>The graphic box can be configured with:</p>
 * <ul>
 *     <li>Width and height (inherited from PositionedAndSizedElement)</li>
 *     <li>Line thickness</li>
 *     <li>Line color (black or white)</li>
 *     <li>Corner roundness (for rounded rectangles)</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * GraphicBox box = GraphicBox.builder()
 *     .withPosition(100, 100)
 *     .withSize(200, 100)
 *     .withThicknessMm(0.4)
 *     .withColor(LineColor.BLACK)
 *     .withRoundness(8)
 *     .build();
 *
 * // Create a horizontal line
 * GraphicBox line = GraphicBox.horizontalLine(100, 0.4)
 *     .withPosition(50, 200)
 *     .withColor(LineColor.BLACK)
 *     .build();
 * }</pre>
 *
 * <p><strong>Note:</strong> When values are not explicitly set, they will not be included
 * in the ZPL command. This allows the printer to use its default values.</p>
 *
 * @see PositionedAndSizedElement For positioning and sizing capabilities
 * @see LineColor For available line colors
 */

@Getter
@SuperBuilder(setterPrefix = "with")
public class GraphicBox extends PositionedAndSizedElement
{
    /**
     * Maximum allowed dimension for width, height and thickness in millimeters.
     * This limit (1333.33mm or approximately 52.5 inches) is based on printer hardware constraints
     * and ensures reliable printing across different printer models.
     */
    private static final double MAX_DIMENSION = 1333.33;

    /**
     * Minimum allowed thickness in millimeters for box borders and lines.
     * This limit (0.04mm or approximately 0.0016 inches) represents the finest line
     * that can be consistently printed across supported printer resolutions.
     */
    private static final double MIN_THICKNESS = 0.04;

    /**
     * Error message template used when dimension validation fails.
     * Used for consistent error reporting across width, height, and thickness validation.
     */
    private static final String DIMENSION_ERROR_MESSAGE =
        "Maximum value for width, height and thickness is " + MAX_DIMENSION;

    /**
     * The thickness of the box border or line in millimeters.
     * Controls the weight of the printed line.
     *
     * <p>Constraints:</p>
     * <ul>
     *     <li>Minimum: {@value #MIN_THICKNESS} mm (finest printable line)</li>
     *     <li>Maximum: {@value #MAX_DIMENSION} mm (printer limit)</li>
     *     <li>When null: Uses printer's default line thickness</li>
     * </ul>
     *
     * <p>The actual printed thickness may vary slightly based on printer DPI and media type.</p>
     *
     * @param thicknessMm the desired border thickness in millimeters
     * @return the border thickness in millimeters
     */
    private final Double thicknessMm;

    /**
     * The color of the box border or line.
     * Determines the printed color of the element.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When specified: Uses the selected color if supported by printer</li>
     *     <li>When null: Uses printer's default color (typically black)</li>
     *     <li>Color support depends on printer model and installed ribbon</li>
     * </ul>
     *
     * @param color the desired line color
     * @return the line color setting
     * @see LineColor for available color options
     */
    private final LineColor color;

    /**
     * The degree of corner rounding for the box.
     * Controls how sharp or rounded the box corners appear.
     *
     * <p>Valid values:</p>
     * <ul>
     *     <li>0: Sharp corners (no rounding)</li>
     *     <li>1: Slight rounding</li>
     *     <li>2-7: Increasing degrees of rounding</li>
     *     <li>8: Maximum rounding (quarter circle)</li>
     *     <li>null: Uses printer default (typically 0)</li>
     * </ul>
     *
     * <p>The visual effect of rounding depends on:</p>
     * <ul>
     *     <li>Box dimensions (larger boxes show more pronounced rounding)</li>
     *     <li>Line thickness (thicker lines appear more rounded)</li>
     *     <li>Printer resolution (higher DPI provides smoother curves)</li>
     * </ul>
     *
     * @param roundness the corner rounding value (0-8)
     * @return the corner rounding setting
     */
    private final Integer roundness;

    /**
     * Creates a builder pre-configured for a horizontal line.
     * The height of the line will be set equal to the thickness.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * GraphicBox line = GraphicBox.horizontalLine(100, 0.4)
     *     .withPosition(50, 200)
     *     .withColor(LineColor.BLACK)
     *     .build();
     * }</pre>
     *
     * @param widthMm     Width in millimeters (thickness-32000)
     * @param thicknessMm Line thickness in millimeters (1-32000)
     * @return a builder instance configured for a horizontal line
     * @throws IllegalArgumentException if width or thickness is outside valid range
     */
    public static GraphicBoxBuilder<?, ?> horizontalLine(double widthMm, double thicknessMm)
    {
        return builder()
            .withSize(widthMm, thicknessMm) // Height must equal thicknessMm for a horizontal line
            .withThicknessMm(thicknessMm);
    }

    /**
     * Creates a builder pre-configured for a vertical line.
     * The height of the line will be set equal to the thickness.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * GraphicBox line = GraphicBox.verticalLine(100, 0.4)
     *     .withPosition(50, 200)
     *     .withColor(LineColor.BLACK)
     *     .build();
     * }</pre>
     *
     * @param heightMm    Height in millimeters (thickness-32000)
     * @param thicknessMm Line thickness in millimeters (1-32000)
     * @return a builder instance configured for a vertical line
     * @throws IllegalArgumentException if height or thickness is outside valid range
     */
    public static GraphicBoxBuilder<?, ?> verticalLine(double heightMm, double thicknessMm)
    {
        return builder()
            .withSize(thicknessMm, heightMm) // Width must equal thicknessMm for a vertical line
            .withThicknessMm(thicknessMm);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The command format follows: ^GBw,h,t,c,r where:</p>
     * <ul>
     *     <li>w = width</li>
     *     <li>h = height</li>
     *     <li>t = border thickness</li>
     *     <li>c = line color</li>
     *     <li>r = degree of corner rounding</li>
     * </ul>
     */
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

    /**
     * {@inheritDoc}
     *
     * <p>Additional validation for GraphicBox includes:
     * <ul>
     *     <li>Thickness range check ({@value #MIN_THICKNESS}-{@value #MAX_DIMENSION} mm)</li>
     *     <li>Roundness range check (0-8)</li>
     * </ul>
     */
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
