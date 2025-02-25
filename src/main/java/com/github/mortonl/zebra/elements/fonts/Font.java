package com.github.mortonl.zebra.elements.fonts;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.SET_FONT;

@Getter
@SuperBuilder(setterPrefix = "with")
public class Font implements LabelElement
{
    private static final int MIN_DOTS = 10;
    private static final int MAX_DOTS = 32000;

    private char fontDesignation;

    private Orientation orientation;

    private double heightMm;

    private double widthMm;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        int heightDots = dpi.toDots(heightMm);
        int widthDots = dpi.toDots(widthMm);

        return ZplCommand.generateZplIICommand(
                // Fonts are a special case where the commands first parameter (character designation) is used as part of the command itself
                SET_FONT + fontDesignation,
                orientation.getValue(),
                heightDots,
                widthDots
        );
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        validateFontName();
        validateDimension("height", heightMm, dpi);
        validateDimension("width", widthMm, dpi);
    }

    private void validateFontName()
    {
        boolean isValidFont = (fontDesignation >= 'A' && fontDesignation <= 'Z') ||
                (fontDesignation >= '0' && fontDesignation <= '9');

        if (!isValidFont) {
            throw new IllegalStateException("Font name must be A-Z or 0-9");
        }
    }

    private void validateDimension(String dimensionName, double dimensionValue, PrintDensity dpi)
    {
        int dots = dpi.toDots(dimensionValue);

        if (dots < MIN_DOTS) {
            throw new IllegalStateException(
                    String.format("Font %s %.2fmm is too small. Minimum %s is %.2fmm for %d DPI / %s dots per mm",
                            dimensionName, dimensionValue, dimensionName,
                            dpi.toMillimetres(MIN_DOTS),
                            dpi.getDotsPerInch(),
                            dpi.getDotsPerMillimetre()));
        }
        if (dots > MAX_DOTS) {
            throw new IllegalStateException(
                    String.format("Font %s %.2fmm is too large. Maximum %s is %.2fmm for %d DPI / %s dots per mm",
                            dimensionName, dimensionValue, dimensionName,
                            dpi.toMillimetres(MAX_DOTS),
                            dpi.getDotsPerInch(),
                            dpi.getDotsPerMillimetre()));
        }
    }

    public static abstract class FontBuilder<C extends Font, B extends FontBuilder<C, B>>
    {
        public B withSize(double widthMm, double heightMm)
        {
            this.widthMm = widthMm;
            this.heightMm = heightMm;
            return self();
        }
    }
}
