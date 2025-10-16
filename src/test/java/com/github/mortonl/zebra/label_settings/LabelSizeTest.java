package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_300;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_600;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LabelSize dimension calculations and ZPL generation")
@Tag("unit")
@Tag("label-settings")
class LabelSizeTest
{
    private static final Pattern PRINT_WIDTH_PATTERN = Pattern.compile("\\^PW(\\d+)");

    private static final Pattern LABEL_LENGTH_PATTERN = Pattern.compile("\\^LL(\\d+)");

    private static final int EXPECTED_WIDTH_DPI_203 = 808;

    private static final int EXPECTED_HEIGHT_DPI_203 = 1215;

    private static final double EXACT_MATCH_TOLERANCE = 0.1;

    private static final double WITHIN_TOLERANCE = 1.0;

    private static final double OUTSIDE_TOLERANCE = 0.01;

    private static final String EXPECTED_TOSTRING_FORMAT = "4\" x 6\" (101.6 mm x 152.4 mm)";

    private static LabelSize classUnderTest;

    @BeforeAll
    static void setUp()
    {
        classUnderTest = LABEL_4X6;
    }

    private static Stream<Arguments> densityWidthDataForGetWidthInDots()
    {
        return Stream.of(
            Arguments.of(DPI_203, 808),  // 101.6 mm * 8
            Arguments.of(DPI_300, 1215), // 101.6 mm * 12
            Arguments.of(DPI_600, 2434)  // 101.6 mm * 24
        );
    }

    private static Stream<Arguments> densityHeightDataForGetHeightInDots()
    {
        return Stream.of(
            Arguments.of(DPI_203, 1215), // 152.4 mm * 8
            Arguments.of(DPI_300, 1824), // 152.4 mm * 12
            Arguments.of(DPI_600, 3653)  // 152.4 mm * 24
        );
    }

    private static Stream<Arguments> dimensionDataForFindClosestSize()
    {
        return Stream.of(
            // Exact match
            Arguments.of(101.6, 152.4, EXACT_MATCH_TOLERANCE, Optional.of(LABEL_4X6)),
            // Within tolerance
            Arguments.of(101.0, 152.0, WITHIN_TOLERANCE, Optional.of(LABEL_4X6)),
            // Outside tolerance
            Arguments.of(200.0, 300.0, EXACT_MATCH_TOLERANCE, Optional.empty())
        );
    }

    @ParameterizedTest(name = "getWidthInDots calculates {1} dots for {0}")
    @MethodSource("densityWidthDataForGetWidthInDots")
    @DisplayName("getWidthInDots calculates correct width for different densities")
    @Tag("calculation")
    void Given_PrintDensity_When_GetWidthInDots_Then_CalculatesCorrectWidth(PrintDensity density, int expectedDots)
    {
        // Given (density parameter)

        // When
        int actualDots = classUnderTest.getWidthInDots(density);

        // Then
        assertEquals(expectedDots, actualDots,
            String.format("Width in dots for %s density should be %d", density, expectedDots));
    }

    @ParameterizedTest(name = "getHeightInDots calculates {1} dots for {0}")
    @MethodSource("densityHeightDataForGetHeightInDots")
    @DisplayName("getHeightInDots calculates correct height for different densities")
    @Tag("calculation")
    void Given_PrintDensity_When_GetHeightInDots_Then_CalculatesCorrectHeight(PrintDensity density, int expectedDots)
    {
        // Given (density parameter)

        // When
        int actualDots = classUnderTest.getHeightInDots(density);

        // Then
        assertEquals(expectedDots, actualDots,
            String.format("Height in dots for %s density should be %d", density, expectedDots));
    }

    @ParameterizedTest(name = "findClosestSize with width={0}, height={1}, tolerance={2}")
    @MethodSource("dimensionDataForFindClosestSize")
    @DisplayName("findClosestSize finds correct label size within tolerance")
    @Tag("search")
    void Given_Dimensions_When_FindClosestSize_Then_ReturnsCorrectResult(double width, double height, double tolerance, Optional<LabelSize> expectedResult)
    {
        // Given (parameters)

        // When
        Optional<LabelSize> actualResult = LabelSize.findClosestSize(width, height, tolerance);

        // Then
        assertAll("Find closest size results",
            () -> assertEquals(expectedResult.isPresent(), actualResult.isPresent(), "Presence of result should match expected"),
            () -> actualResult.ifPresent(size -> assertEquals(expectedResult.get(), size, "Found size should match expected"))
        );
    }

    @Test
    @DisplayName("toZplString generates correct ZPL commands")
    @Tag("zpl-generation")
    void Given_LabelSize_When_ToZplString_Then_GeneratesCorrectZpl()
    {
        // Given (classUnderTest is already configured)

        // When
        String actualZplCommands = classUnderTest.toZplString(DPI_203);

        // Then
        Matcher pwMatcher = PRINT_WIDTH_PATTERN.matcher(actualZplCommands);
        Matcher llMatcher = LABEL_LENGTH_PATTERN.matcher(actualZplCommands);

        assertAll("ZPL commands validation",
            () ->
            {
                assertTrue(pwMatcher.find(), () ->
                    String.format("Print width command not found in ZPL commands: %s", actualZplCommands));
                assertEquals(EXPECTED_WIDTH_DPI_203, Integer.parseInt(pwMatcher.group(1)),
                    () -> String.format("Print width value incorrect. Expected: ^PW%d, Actual: ^PW%d",
                        EXPECTED_WIDTH_DPI_203, Integer.parseInt(pwMatcher.group(1))));
            },
            () ->
            {
                assertTrue(llMatcher.find(), () ->
                    String.format("Label length command not found in ZPL commands: %s", actualZplCommands));
                assertEquals(EXPECTED_HEIGHT_DPI_203, Integer.parseInt(llMatcher.group(1)),
                    () -> String.format("Label length value incorrect. Expected: ^LL%d, Actual: ^LL%d",
                        EXPECTED_HEIGHT_DPI_203, Integer.parseInt(llMatcher.group(1))));
            }
        );
    }

    @Test
    @DisplayName("values returns enum values with valid properties")
    @Tag("validation")
    void Given_AllEnumValues_When_CheckProperties_Then_AllHaveValidProperties()
    {
        // Given & When
        LabelSize[] actualValues = LabelSize.values();

        // Then
        for (LabelSize size : actualValues) {
            String sizeName = size.name();
            assertAll("Label size " + sizeName + " properties",
                () -> assertTrue(size.getWidthMm() > 0,
                    sizeName + " should have positive width"),
                () -> assertTrue(size.getHeightMm() > 0,
                    sizeName + " should have positive height"),
                () -> assertTrue(size.getDescription() != null && !size.getDescription()
                                                                       .isEmpty(),
                    sizeName + " should have non-empty description")
            );
        }
    }

    @Test
    @DisplayName("matches returns true when dimensions are within tolerance")
    @Tag("validation")
    void Given_DimensionsWithinTolerance_When_Matches_Then_ReturnsTrue()
    {
        // Given
        double givenWidth = 101.5;
        double givenHeight = 152.3;
        double givenTolerance = WITHIN_TOLERANCE;

        // When
        boolean actualResult = classUnderTest.matches(givenWidth, givenHeight, givenTolerance);

        // Then
        assertTrue(actualResult, "Should match when dimensions are within tolerance");
    }

    @Test
    @DisplayName("matches returns false when dimensions are outside tolerance")
    @Tag("validation")
    void Given_DimensionsOutsideTolerance_When_Matches_Then_ReturnsFalse()
    {
        // Given
        double givenWidth = 200.0;
        double givenHeight = 300.0;
        double givenTolerance = OUTSIDE_TOLERANCE;

        // When
        boolean actualResult = classUnderTest.matches(givenWidth, givenHeight, givenTolerance);

        // Then
        assertEquals(false, actualResult, "Should not match when dimensions are outside tolerance");
    }

    @Test
    @DisplayName("toString returns formatted string with description and dimensions")
    @Tag("formatting")
    void Given_LabelSize_When_ToString_Then_ReturnsFormattedString()
    {
        // Given (classUnderTest is LABEL_4X6)

        // When
        String actualResult = classUnderTest.toString();

        // Then
        assertEquals(EXPECTED_TOSTRING_FORMAT, actualResult, "Should return formatted string with description and dimensions");
    }

    @Test
    @DisplayName("findClosestSize returns closest match when multiple sizes within tolerance")
    @Tag("search")
    void Given_MultipleSizesWithinTolerance_When_FindClosestSize_Then_ReturnsClosest()
    {
        // Given - dimensions that could match multiple label sizes with large tolerance
        double givenWidth = 75.0;
        double givenHeight = 40.0;
        double largeTolerance = 50.0;

        // When
        Optional<LabelSize> actualResult = LabelSize.findClosestSize(givenWidth, givenHeight, largeTolerance);

        // Then
        assertTrue(actualResult.isPresent(), "Should find a matching label size");
    }
}
