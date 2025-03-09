package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Label Size Tests")
class LabelSizeTest
{
    private static final Pattern PRINT_WIDTH_PATTERN = Pattern.compile("\\^PW(\\d+)");
    private static final Pattern LABEL_LENGTH_PATTERN = Pattern.compile("\\^LL(\\d+)");

    @ParameterizedTest(name = "Width in dots for {0} should be {1}")
    @MethodSource("providePrintDensityWidthTestData")
    @DisplayName("Calculate width in dots for different print densities")
    void testGetWidthInDots(PrintDensity density, int expectedDots)
    {
        int actualDots = LabelSize.LABEL_4X6.getWidthInDots(density);
        assertEquals(expectedDots, actualDots,
            String.format("Width in dots for %s density should be %d", density, expectedDots));
    }

    private static Stream<Arguments> providePrintDensityWidthTestData()
    {
        return Stream.of(
            Arguments.of(PrintDensity.DPI_203, 812),  // 101.6mm * 8
            Arguments.of(PrintDensity.DPI_300, 1219), // 101.6mm * 12
            Arguments.of(PrintDensity.DPI_600, 2438)  // 101.6mm * 24
        );
    }

    @ParameterizedTest(name = "Height in dots for {0} should be {1}")
    @MethodSource("providePrintDensityHeightTestData")
    @DisplayName("Calculate height in dots for different print densities")
    void testGetHeightInDots(PrintDensity density, int expectedDots)
    {
        int actualDots = LabelSize.LABEL_4X6.getHeightInDots(density);
        assertEquals(expectedDots, actualDots,
            String.format("Height in dots for %s density should be %d", density, expectedDots));
    }

    private static Stream<Arguments> providePrintDensityHeightTestData()
    {
        return Stream.of(
            Arguments.of(PrintDensity.DPI_203, 1219), // 152.4mm * 8
            Arguments.of(PrintDensity.DPI_300, 1828), // 152.4mm * 12
            Arguments.of(PrintDensity.DPI_600, 3657)  // 152.4mm * 24
        );
    }

    @ParameterizedTest(name = "Finding closest size with width={0}, height={1}, tolerance={2}")
    @MethodSource("provideFindClosestSizeTestData")
    @DisplayName("Find closest label size with different dimensions and tolerances")
    void testFindClosestSize(double width, double height, double tolerance, Optional<LabelSize> expected)
    {
        Optional<LabelSize> result = LabelSize.findClosestSize(width, height, tolerance);
        assertAll("Find closest size results",
            () -> assertEquals(expected.isPresent(), result.isPresent(), "Presence of result should match expected"),
            () -> result.ifPresent(size -> assertEquals(expected.get(), size, "Found size should match expected"))
        );
    }

    private static Stream<Arguments> provideFindClosestSizeTestData()
    {
        return Stream.of(
            // Exact match
            Arguments.of(101.6, 152.4, 0.1, Optional.of(LabelSize.LABEL_4X6)),
            // Within tolerance
            Arguments.of(101.0, 152.0, 1.0, Optional.of(LabelSize.LABEL_4X6)),
            // Outside tolerance
            Arguments.of(200.0, 300.0, 0.1, Optional.empty())
        );
    }

    @Test
    @DisplayName("Generate correct ZPL commands for default size and DPI")
void testGetZplCommands() {
    int expectedPrintWidth = 812;  // 101.6mm * 8
    int expectedLabelLength = 1219; // 152.4mm * 8

    String zplCommands = LabelSize.LABEL_4X6.toZplString(PrintDensity.DPI_203);

    Matcher pwMatcher = PRINT_WIDTH_PATTERN.matcher(zplCommands);
    Matcher llMatcher = LABEL_LENGTH_PATTERN.matcher(zplCommands);

    assertAll("ZPL commands validation",
        () -> {
            assertTrue(pwMatcher.find(), () ->
                String.format("Print width command not found in ZPL commands: %s", zplCommands));
            assertEquals(expectedPrintWidth, Integer.parseInt(pwMatcher.group(1)),
                () -> String.format("Print width value incorrect. Expected: ^PW%d, Actual: ^PW%d",
                    expectedPrintWidth, Integer.parseInt(pwMatcher.group(1))));
        },
        () -> {
            assertTrue(llMatcher.find(), () ->
                String.format("Label length command not found in ZPL commands: %s", zplCommands));
            assertEquals(expectedLabelLength, Integer.parseInt(llMatcher.group(1)),
                () -> String.format("Label length value incorrect. Expected: ^LL%d, Actual: ^LL%d",
                    expectedLabelLength, Integer.parseInt(llMatcher.group(1))));
        }
        );
    }

    @Test
    @DisplayName("Validate all enum values have valid properties")
    void testAllEnumValues()
    {
        for (LabelSize size : LabelSize.values()) {
            String sizeName = size.name();
            assertAll("Label size " + sizeName + " properties",
                () -> assertTrue(size.getWidthMm() > 0,
                    sizeName + " should have positive width"),
                () -> assertTrue(size.getHeightMm() > 0,
                    sizeName + " should have positive height"),
                () -> assertTrue(size.getDescription() != null && !size.getDescription().isEmpty(),
                    sizeName + " should have non-empty description")
            );
        }
    }
}
