package com.github.mortonl.zebra.printer_configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrintDensityTest
{
    private static Stream<Arguments> conversionTestData()
    {
        return Stream.of(
            // DPI, mm, expected dots, actual DPI
            Arguments.of(PrintDensity.DPI_152, 1.0, 6, 152),
            Arguments.of(PrintDensity.DPI_203, 1.0, 8, 203),
            Arguments.of(PrintDensity.DPI_300, 1.0, 12, 300),
            Arguments.of(PrintDensity.DPI_600, 1.0, 24, 600),
            Arguments.of(PrintDensity.DPI_152, 10.0, 60, 152),
            Arguments.of(PrintDensity.DPI_203, 10.0, 80, 203),
            Arguments.of(PrintDensity.DPI_300, 10.0, 120, 300),
            Arguments.of(PrintDensity.DPI_600, 10.0, 240, 600),
            Arguments.of(PrintDensity.DPI_203, 0.5, 4, 203),
            Arguments.of(PrintDensity.DPI_300, 1.5, 18, 300),

            // Zero values
            Arguments.of(PrintDensity.DPI_152, 0.0, 0, 152),
            Arguments.of(PrintDensity.DPI_600, 0.0, 0, 600),

            // Negative values
            Arguments.of(PrintDensity.DPI_203, -1.0, -8, 203),
            Arguments.of(PrintDensity.DPI_300, -1.5, -18, 300),

            // Round trip cases
            Arguments.of(PrintDensity.DPI_300, 10.5, 126, 300)
        );
    }

    private static Stream<Arguments> reverseConversionTestData()
    {
        return Stream.of(
            // DPI, dots, expected mm, actual DPI
            Arguments.of(PrintDensity.DPI_152, 6, 1.0, 152),
            Arguments.of(PrintDensity.DPI_203, 8, 1.0, 203),
            Arguments.of(PrintDensity.DPI_300, 12, 1.0, 300),
            Arguments.of(PrintDensity.DPI_600, 24, 1.0, 600),
            Arguments.of(PrintDensity.DPI_152, 60, 10.0, 152),
            Arguments.of(PrintDensity.DPI_203, 80, 10.0, 203),
            Arguments.of(PrintDensity.DPI_300, 120, 10.0, 300),
            Arguments.of(PrintDensity.DPI_600, 240, 10.0, 600)
        );
    }

    @Test
    void testGetMinDotsPerMillimetre()
    {
        assertEquals(6, PrintDensity.getMinDotsPerMillimetre());
    }

    @Test
    void testGetMaxDotsPerMillimetre()
    {
        assertEquals(24, PrintDensity.getMaxDotsPerMillimetre());
    }

    @ParameterizedTest(name = "{0} ({3} DPI) converting {1}mm should be {2} dots")
    @MethodSource("conversionTestData")
    void testToDots(PrintDensity dpi, double mm, int expectedDots, int expectedDpi)
    {
        assertEquals(expectedDots, dpi.toDots(mm));
        assertEquals(expectedDpi, dpi.getDotsPerInch());
    }

    @ParameterizedTest(name = "{0} ({3} DPI) converting {1} dots should be {2}mm")
    @MethodSource("reverseConversionTestData")
    void testToMillimetres(PrintDensity dpi, int dots, double expectedMm, int expectedDpi)
    {
        assertEquals(expectedMm, dpi.toMillimetres(dots), 0.0001);
        assertEquals(expectedDpi, dpi.getDotsPerInch());
    }

    @Test
    void testPrecisionHandling()
    {
        PrintDensity dpi = PrintDensity.DPI_300;
        double mm = 1.0 / 3.0; // A recurring decimal
        int dots = dpi.toDots(mm);
        double convertedBack = dpi.toMillimetres(dots);
        assertEquals(mm, convertedBack, 0.01);
    }

    @Test
    void testLargeNumberConversion()
    {
        PrintDensity dpi = PrintDensity.DPI_600;
        int largeDots = 100000;
        double mm = dpi.toMillimetres(largeDots);
        assertEquals(largeDots, dpi.toDots(mm));
    }

    @Test
    void testFromDotsPerInch()
    {
        assertEquals(PrintDensity.DPI_152, PrintDensity.fromDotsPerInch(152));
        assertEquals(PrintDensity.DPI_203, PrintDensity.fromDotsPerInch(203));
        assertEquals(PrintDensity.DPI_300, PrintDensity.fromDotsPerInch(300));
        assertEquals(PrintDensity.DPI_600, PrintDensity.fromDotsPerInch(600));
    }

    @Test
    void testFromDotsPerMillimetre()
    {
        assertEquals(PrintDensity.DPI_152, PrintDensity.fromDotsPerMillimetre(6));
        assertEquals(PrintDensity.DPI_203, PrintDensity.fromDotsPerMillimetre(8));
        assertEquals(PrintDensity.DPI_300, PrintDensity.fromDotsPerMillimetre(12));
        assertEquals(PrintDensity.DPI_600, PrintDensity.fromDotsPerMillimetre(24));
    }

    @Test
    void testFromDotsPerInch_InvalidValue()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> PrintDensity.fromDotsPerInch(100)
        );
        assertEquals("No PrintDensity found for 100 DPI", exception.getMessage());
    }

    @Test
    void testFromDotsPerMillimetre_InvalidValue()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> PrintDensity.fromDotsPerMillimetre(10)
        );
        assertEquals("No PrintDensity found for 10 dots per millimetre", exception.getMessage());
    }
}
