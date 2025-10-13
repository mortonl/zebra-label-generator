package com.github.mortonl.zebra.elements.fonts;

import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.CHANGE_ALPHANUMERIC_DEFAULT_FONT;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

/**
 * Sets the default font for a label using the ^CF command.
 * This font will be used by text elements that don't explicitly specify their own font.
 */
@SuperBuilder(builderMethodName = "createDefaultFont", setterPrefix = "with")
public class DefaultFont extends Font
{

    @Override
    public String toZplString(PrintDensity dpi)
    {
        int heightDots = dpi.toDots(getHeightMm());
        int widthDots  = dpi.toDots(getWidthMm());

        return generateZplIICommand(
            CHANGE_ALPHANUMERIC_DEFAULT_FONT,
            getFontDesignation(),
            heightDots,
            widthDots
        );
    }

    @Override
    public void validateInContext(com.github.mortonl.zebra.label_settings.LabelSize size, com.github.mortonl.zebra.printer_configuration.PrintDensity dpi, DefaultFont defaultFont) throws IllegalStateException
    {
        super.validateInContext(size, dpi, defaultFont);
        validateNotSameAsExistingDefault(defaultFont);
    }

    /**
     * Validates that this default font is not identical to an existing default font.
     */
    private void validateNotSameAsExistingDefault(DefaultFont existingDefault)
    {
        if (existingDefault != null && isSameAsDefault(existingDefault)) {
            throw new IllegalStateException(
                "Default font specification matches existing default font"
            );
        }
    }

    /**
     * Builder class for DefaultFont objects.
     * 
     * @param <C> the concrete DefaultFont type being built
     * @param <B> the concrete builder type (self-referential for method chaining)
     */
    public static abstract class DefaultFontBuilder<C extends DefaultFont, B extends DefaultFontBuilder<C, B>>
        extends FontBuilder<C, B>
    {

    }
}
