package com.github.mortonl.zebra.elements.fonts;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Builder;
import lombok.Data;

import static com.github.mortonl.zebra.ZplCommand.SET_FONT;

@Data
@Builder
public class Font implements LabelElement
{
    private static final int MIN_DOTS = 10;
    private static final int MAX_DOTS = 32000;

    @Builder.Default
    private String fontName = "0";  // Default font

    @Builder.Default
    private Orientation orientation = Orientation.NORMAL;

    @Builder.Default
    private double heightMm = 2.0;  // Default height in millimeters

    @Builder.Default
    private double widthMm = 2.0;   // Default width in millimeters

    @Override
    public String toZplString(PrintDensity dpi)
    {
        int heightDots = dpi.toDots(heightMm);
        int widthDots = dpi.toDots(widthMm);

        return ZplCommand.generateZplIICommand(
            SET_FONT,
            fontName,
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
        if (fontName == null || fontName.isEmpty()) {
            throw new IllegalStateException("Font name cannot be null or empty");
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
}
