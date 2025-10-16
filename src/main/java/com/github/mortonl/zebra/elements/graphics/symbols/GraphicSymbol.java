package com.github.mortonl.zebra.elements.graphics.symbols;

import com.github.mortonl.zebra.elements.PositionedAndSizedElement;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.FIELD_START;
import static com.github.mortonl.zebra.ZplCommand.GRAPHIC_SYMBOL;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

@Getter
@SuperBuilder(builderMethodName = "createGraphicSymbol", setterPrefix = "with")
public class GraphicSymbol extends PositionedAndSizedElement
{
    /**
     * Maximum allowed dimension for width, height in dots.
     * This limit (32000 dots) is based on printer hardware constraints
     * and ensures reliable printing across different printer models.
     */
    private static final double MAX_DIMENSION = 32000;

    /**
     * The orientation of the Symbol.
     * <p>Supported orientations:</p>
     * <ul>
     *     <li>N = normal</li>
     *     <li>R = rotated 90 degrees clockwise</li>
     *     <li>I = inverted 180 degrees</li>
     *     <li>B = read from bottom up, 270 degrees</li>
     * </ul>
     * <p>If not specified (null), the orientation parameter is omitted from the ZPL command.
     * The printer will use either:</p>
     * <ul>
     *     <li>The last ^FW (Default Orientation) command value, or</li>
     *     <li>The printer's default orientation (normal) if no ^FW was specified.</li>
     * </ul>
     *
     * @param orientation the orientation setting for the barcode (N, R, I, or B)
     * @return the current orientation setting of the barcode
     * @see Orientation
     */
    private final @Nullable Orientation orientation;

    /**
     * The symbol displayed.
     *
     * @param symbol the symbol to display
     * @return the current symbol
     * @see SymbolDesignation For values
     *
     */
    private final SymbolDesignation symbol;

    /**
     * {@inheritDoc}
     *
     * <p>The command format follows: ^GSo,h,w where:</p>
     * <ul>
     *     <li>o = orientation</li>
     *     <li>h = height</li>
     *     <li>w = width</li>
     * </ul>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        zplCommand.append(super.toZplString(dpi));

        zplCommand.append(generateZplIICommand(GRAPHIC_SYMBOL,
            orientation != null ? orientation.getValue() : null,
            heightMm != null ? dpi.toDots(heightMm) : null,
            widthMm != null ? dpi.toDots(widthMm) : null
        ));

        zplCommand
            .append(FIELD_START)
            .append(symbol.getValue())
            .append(FIELD_END);

        return zplCommand.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Additional validation for GraphicSymbol includes:
     * <ul>
     *     <li></li>
     * </ul>
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        super.validateInContext(size, dpi, defaultFont);

        // If all parameters are null, that's valid
        if (widthMm == null && heightMm == null) {
            return;
        }

        if (heightMm != null) {
            validateRange(dpi.toDots(heightMm), 0, MAX_DIMENSION, "Height");
        }
        if (widthMm != null) {
            validateRange(dpi.toDots(widthMm), 0, MAX_DIMENSION, "Width");
        }
    }
}
