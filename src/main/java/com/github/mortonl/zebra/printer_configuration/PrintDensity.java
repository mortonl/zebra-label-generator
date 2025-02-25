package com.github.mortonl.zebra.printer_configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum PrintDensity
{
    DPI_152(152, 6),
    DPI_203(203, 8),
    DPI_300(300, 12),
    DPI_600(600, 24);

    private final int dotsPerInch;
    private final int dotsPerMillimetre;

    public static PrintDensity fromDotsPerInch(int dpi)
    {
        return Arrays
            .stream(values())
            .filter(density -> density.dotsPerInch == dpi)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No PrintDensity found for %d DPI", dpi)));
    }

    public static PrintDensity fromDotsPerMillimetre(int dotsPerMm)
    {
        return Arrays
            .stream(values())
            .filter(density -> density.dotsPerMillimetre == dotsPerMm)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No PrintDensity found for %d dots per millimetre", dotsPerMm)));
    }

    public static int getMinDotsPerMillimetre()
    {
        return Stream
            .of(PrintDensity.values())
            .mapToInt(PrintDensity::getDotsPerMillimetre)
            .min()
            .orElseThrow();
    }

    public static int getMaxDotsPerMillimetre()
    {
        return Stream
            .of(PrintDensity.values())
            .mapToInt(PrintDensity::getDotsPerMillimetre)
            .max()
            .orElseThrow();
    }

    public int toDots(double millimeters)
    {
        return (int) Math.round(millimeters * dotsPerMillimetre);
    }

    public final double toMillimetres(final int dots)
    {
        return BigDecimal
            .valueOf(dots)
            .divide(BigDecimal.valueOf(dotsPerMillimetre), 10, RoundingMode.HALF_UP)
            .doubleValue();
    }
}
