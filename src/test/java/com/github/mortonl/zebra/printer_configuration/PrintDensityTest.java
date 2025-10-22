package com.github.mortonl.zebra.printer_configuration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_152;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_300;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_600;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("PrintDensity conversion and lookup functionality")
@Tag("unit")
@Tag("printer-configuration")
class PrintDensityTest
{
    private static final int MIN_DOTS_PER_MM = 6;

    private static final int MAX_DOTS_PER_MM = 24;

    private static final double PRECISION_DELTA = 0.0001;

    private static final double PRECISION_TOLERANCE = 0.01;

    private static final int LARGE_DOTS_VALUE = 100000;

    private static final int INVALID_DPI = 100;

    private static final int INVALID_DOTS_PER_MM = 10;

    private static final String EXPECTED_INVALID_DPI_MESSAGE = "No PrintDensity found for 100 DPI";

    private static final String EXPECTED_INVALID_DOTS_MM_MESSAGE = "No PrintDensity found for 10 dots per millimetre";

    private static Stream<Arguments> conversionDataForToDots()
    {
        return Stream.of(
            // DPI, mm, expected dots, actual DPI
            Arguments.of(DPI_152, 1.0, 6, 152),
            Arguments.of(DPI_203, 1.0, 8, 203),
            Arguments.of(DPI_300, 1.0, 12, 300),
            Arguments.of(DPI_600, 1.0, 24, 600),
            Arguments.of(DPI_152, 10.0, 60, 152),
            Arguments.of(DPI_203, 10.0, 80, 203),
            Arguments.of(DPI_300, 10.0, 120, 300),
            Arguments.of(DPI_600, 10.0, 240, 600),
            Arguments.of(DPI_203, 0.5, 4, 203),
            Arguments.of(DPI_300, 1.5, 18, 300),
            // Zero values
            Arguments.of(DPI_152, 0.0, 0, 152),
            Arguments.of(DPI_600, 0.0, 0, 600),
            // Negative values
            Arguments.of(DPI_203, -1.0, -8, 203),
            Arguments.of(DPI_300, -1.5, -18, 300),
            // Round trip cases
            Arguments.of(DPI_300, 10.5, 126, 300)
        );
    }

    private static Stream<Arguments> conversionDataForToMillimetres()
    {
        return Stream.of(
            // DPI, dots, expected mm, actual DPI
            Arguments.of(DPI_152, 6, 1.0, 152),
            Arguments.of(DPI_203, 8, 1.0, 203),
            Arguments.of(DPI_300, 12, 1.0, 300),
            Arguments.of(DPI_600, 24, 1.0, 600),
            Arguments.of(DPI_152, 60, 10.0, 152),
            Arguments.of(DPI_203, 80, 10.0, 203),
            Arguments.of(DPI_300, 120, 10.0, 300),
            Arguments.of(DPI_600, 240, 10.0, 600)
        );
    }

    @Test
    @DisplayName("getMinDotsPerMillimetre returns minimum dots per millimetre")
    @Tag("static-methods")
    void Given_StaticCall_When_GetMinDotsPerMillimetre_Then_ReturnsMinValue()
    {
        // Given & When
        int actualMinDots = PrintDensity.getMinDotsPerMillimetre();

        // Then
        assertEquals(MIN_DOTS_PER_MM, actualMinDots);
    }

    @Test
    @DisplayName("getMaxDotsPerMillimetre returns maximum dots per millimetre")
    @Tag("static-methods")
    void Given_StaticCall_When_GetMaxDotsPerMillimetre_Then_ReturnsMaxValue()
    {
        // Given & When
        int actualMaxDots = PrintDensity.getMaxDotsPerMillimetre();

        // Then
        assertEquals(MAX_DOTS_PER_MM, actualMaxDots);
    }

    @ParameterizedTest(name = "toDots converts {1}mm to {2} dots for {0} ({3} DPI)")
    @MethodSource("conversionDataForToDots")
    @DisplayName("toDots converts millimetres to dots correctly")
    @Tag("conversion")
    void Given_Millimetres_When_ToDots_Then_ConvertsCorrectly(PrintDensity dpi, double mm, int expectedDots, int expectedDpi)
    {
        // Given (parameters)

        // When
        int actualDots = dpi.toDots(mm);

        // Then
        assertAll(
            () -> assertEquals(expectedDots, actualDots),
            () -> assertEquals(expectedDpi, dpi.getDotsPerInch())
        );
    }

    @ParameterizedTest(name = "toMillimetres converts {1} dots to {2}mm for {0} ({3} DPI)")
    @MethodSource("conversionDataForToMillimetres")
    @DisplayName("toMillimetres converts dots to millimetres correctly")
    @Tag("conversion")
    void Given_Dots_When_ToMillimetres_Then_ConvertsCorrectly(PrintDensity dpi, int dots, double expectedMm, int expectedDpi)
    {
        // Given (parameters)

        // When
        double actualMm = dpi.toMillimetres(dots);

        // Then
        assertAll(
            () -> assertEquals(expectedMm, actualMm, PRECISION_DELTA),
            () -> assertEquals(expectedDpi, dpi.getDotsPerInch())
        );
    }

    @Test
    @DisplayName("toDots and toMillimetres handle precision correctly")
    @Tag("conversion")
    void Given_RecurringDecimal_When_ConvertRoundTrip_Then_MaintainsPrecision()
    {
        // Given
        PrintDensity dpi = DPI_300;
        double originalMm = 1.0 / 3.0; // A recurring decimal

        // When
        int dots = dpi.toDots(originalMm);
        double actualConvertedBack = dpi.toMillimetres(dots);

        // Then
        assertEquals(originalMm, actualConvertedBack, PRECISION_TOLERANCE);
    }

    @Test
    @DisplayName("toDots and toMillimetres handle large numbers correctly")
    @Tag("conversion")
    void Given_LargeNumbers_When_ConvertRoundTrip_Then_MaintainsAccuracy()
    {
        // Given
        PrintDensity dpi = DPI_600;

        // When
        double mm = dpi.toMillimetres(LARGE_DOTS_VALUE);
        int actualConvertedBack = dpi.toDots(mm);

        // Then
        assertEquals(LARGE_DOTS_VALUE, actualConvertedBack);
    }

    @Test
    @DisplayName("fromDotsPerInch returns correct enum values")
    @Tag("lookup")
    void Given_ValidDpiValues_When_FromDotsPerInch_Then_ReturnsCorrectEnum()
    {
        // Given & When & Then
        assertAll(
            () -> assertEquals(DPI_152, PrintDensity.fromDotsPerInch(152)),
            () -> assertEquals(DPI_203, PrintDensity.fromDotsPerInch(203)),
            () -> assertEquals(DPI_300, PrintDensity.fromDotsPerInch(300)),
            () -> assertEquals(DPI_600, PrintDensity.fromDotsPerInch(600))
        );
    }

    @Test
    @DisplayName("fromDotsPerMillimetre returns correct enum values")
    @Tag("lookup")
    void Given_ValidDotsPerMm_When_FromDotsPerMillimetre_Then_ReturnsCorrectEnum()
    {
        // Given & When & Then
        assertAll(
            () -> assertEquals(DPI_152, PrintDensity.fromDotsPerMillimetre(6)),
            () -> assertEquals(DPI_203, PrintDensity.fromDotsPerMillimetre(8)),
            () -> assertEquals(DPI_300, PrintDensity.fromDotsPerMillimetre(12)),
            () -> assertEquals(DPI_600, PrintDensity.fromDotsPerMillimetre(24))
        );
    }

    @Test
    @DisplayName("fromDotsPerInch throws exception for invalid DPI")
    @Tag("lookup")
    void Given_InvalidDpi_When_FromDotsPerInch_Then_ThrowsException()
    {
        // Given (INVALID_DPI constant)

        // When & Then
        IllegalArgumentException actualException = assertThrows(
            IllegalArgumentException.class,
            () -> PrintDensity.fromDotsPerInch(INVALID_DPI)
        );
        assertEquals(EXPECTED_INVALID_DPI_MESSAGE, actualException.getMessage());
    }

    @Test
    @DisplayName("fromDotsPerMillimetre throws exception for invalid dots per mm")
    @Tag("lookup")
    void Given_InvalidDotsPerMm_When_FromDotsPerMillimetre_Then_ThrowsException()
    {
        // Given (INVALID_DOTS_PER_MM constant)

        // When & Then
        IllegalArgumentException actualException = assertThrows(
            IllegalArgumentException.class,
            () -> PrintDensity.fromDotsPerMillimetre(INVALID_DOTS_PER_MM)
        );
        assertEquals(EXPECTED_INVALID_DOTS_MM_MESSAGE, actualException.getMessage());
    }
}
