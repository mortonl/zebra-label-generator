package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.formatting.OriginJustification;
import com.github.mortonl.zebra.label_settings.InternationalCharacterSet;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.elements.PositionedElement.MAX_AXIS_VALUE;
import static com.github.mortonl.zebra.formatting.OriginJustification.LEFT;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PositionedElement positioning and validation")
@Tag("unit")
@Tag("positioning")
class PositionedElementTest
{
    private static final double VALID_X_POSITION = 10.0;

    private static final double VALID_Y_POSITION = 20.0;

    private static final String EXPECTED_BASIC_ZPL = "^FO80,160";

    private static final String EXPECTED_JUSTIFIED_ZPL = "^FO80,160,0";

    private static final String EXPECTED_POSITION_ERROR_SUFFIX = " The element must be positioned within the label dimensions.";

    private TestPositionedElement classUnderTest;

    private ZebraLabel testLabel;

    private final PrinterConfiguration givenPrinterConfiguration = PrinterConfiguration
        .createPrinterConfiguration()
        .forDpi(DPI_203)
        .forLoadedMedia(LoadedMedia.fromLabelSize(LABEL_4X6))
        .build();

    private final InternationalCharacterSet givenCharacterSet = InternationalCharacterSet
        .createInternationalCharacterSet()
        .withEncoding(FontEncoding.UTF_8)
        .build();

    private static Stream<Arguments> validPositionsForValidateInContext()
    {
        return Stream.of(
            Arguments.of(0.0, 0.0, "Minimum values"),
            Arguments.of(50.0, 75.0, "Middle of label"),
            Arguments.of(99.0, 149.0, "Near maximum values")
        );
    }

    private static Stream<Arguments> invalidPositionsForValidateInContext()
    {
        return Stream.of(
            Arguments.of(150.0, 75.0, "X exceeds width", "X-axis position (150.00 mm) exceeds label width (101.60 mm)."),
            Arguments.of(50.0, 200.0, "Y exceeds height", "Y-axis position (200.00 mm) exceeds label height (152.40 mm)."),
            Arguments.of(150.0, 200.0, "Both exceed dimensions", "X-axis position (150.00 mm) exceeds label width (101.60 mm). Y-axis position (200.00 mm) exceeds label height (152.40 mm).")
        );
    }

    @BeforeEach
    void setUp()
    {
        classUnderTest = TestPositionedElement
            .createTestElement()
            .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
            .build();

        testLabel = ZebraLabel
            .createLabel()
            .forSize(LABEL_4X6)
            .forPrinter(givenPrinterConfiguration)
            .forInternationalCharacterSet(givenCharacterSet)
            .build();
    }

    private static Stream<Arguments> edgePositionScenarios()
    {
        return Stream.of(
            Arguments.of("Left only", TestPositionedElement
                .createTestElement()
                .onLeftEdge()
                .withYAxisLocationMm(0.0), 0.0, 0.0, null, false, "^FO0,0"),
            Arguments.of("Right only", TestPositionedElement
                .createTestElement()
                .onRightEdge()
                .withYAxisLocationMm(0.0), LABEL_4X6.getWidthMm(), 0.0, OriginJustification.RIGHT, false, "^FO813,0,1"),
            Arguments.of("Top only", TestPositionedElement
                .createTestElement()
                .onTopEdge()
                .withXAxisLocationMm(0.0), 0.0, 0.0, null, false, "^FO0,0"),
            Arguments.of("Bottom only", TestPositionedElement
                .createTestElement()
                .onBottomEdge()
                .withXAxisLocationMm(0.0), 0.0, LABEL_4X6.getHeightMm(), null, true, "^FT0,1219"),
            Arguments.of("Top & Left", TestPositionedElement
                .createTestElement()
                .onTopEdge()
                .onLeftEdge(), 0.0, 0.0, null, false, "^FO0,0"),
            Arguments.of("Top & Right", TestPositionedElement
                .createTestElement()
                .onTopEdge()
                .onRightEdge(), LABEL_4X6.getWidthMm(), 0.0, OriginJustification.RIGHT, false, "^FO813,0,1"),
            Arguments.of("Bottom & Left", TestPositionedElement
                .createTestElement()
                .onBottomEdge()
                .onLeftEdge(), 0.0, LABEL_4X6.getHeightMm(), null, true, "^FT0,1219"),
            Arguments.of("Bottom & Right", TestPositionedElement
                .createTestElement()
                .onBottomEdge()
                .onRightEdge(), LABEL_4X6.getWidthMm(), LABEL_4X6.getHeightMm(), OriginJustification.RIGHT, true, "^FT813,1219,1"),
            Arguments.of("With position only", TestPositionedElement
                .createTestElement()
                .withPosition(VALID_X_POSITION, VALID_Y_POSITION), VALID_X_POSITION, VALID_Y_POSITION, null, false, "^FO80,160")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("edgePositionScenarios")
    @DisplayName("Given edge scenario, When resolved, Then position and flags and full zpl string are correct.")
    @Tag("builder")
    void Given_EdgeScenario_When_Resolved_Then_PositionAndFlagsAreCorrect(
        String scenarioName,
        TestPositionedElement.TestPositionedElementBuilderImpl builder,
        double expectedX,
        double expectedY,
        OriginJustification expectedJustification,
        boolean expectedTypeset,
        String expectedZPL)
    {
        TestPositionedElement actualElement = builder.addToLabel(testLabel);
        String actualZplString = actualElement.toZplString(DPI_203);

        assertAll(
            () -> assertEquals(expectedX, actualElement.getXAxisLocationMm(), scenarioName + " - X position"),
            () -> assertEquals(expectedY, actualElement.getYAxisLocationMm(), scenarioName + " - Y position"),
            () ->
            {
                if (expectedJustification != null) {
                    assertEquals(expectedJustification, actualElement.getZOriginJustification(), scenarioName + " - Justification");
                }
            },
            () -> assertEquals(expectedTypeset, actualElement.isTypeset(), scenarioName + " - Typeset"),
            () -> assertEquals(expectedZPL, actualZplString, scenarioName + " - ZPL string")
        );
    }

    @Test
    @DisplayName("toZplString generates ZPL without justification")
    @Tag("zpl-generation")
    void Given_NoJustification_When_ToZplString_Then_GeneratesBasicZpl()
    {
        // Given (classUnderTest has no justification by default)

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_BASIC_ZPL, actualZplString);
    }

    @Test
    @DisplayName("toZplString generates ZPL with justification")
    @Tag("zpl-generation")
    void Given_WithJustification_When_ToZplString_Then_GeneratesJustifiedZpl()
    {
        // Given
        TestPositionedElement justifiedElement = TestPositionedElement
            .createTestElement()
            .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
            .withZOriginJustification(LEFT)
            .build();

        // When
        String actualZplString = justifiedElement.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_JUSTIFIED_ZPL, actualZplString);
    }

    @ParameterizedTest(name = "validateInContext accepts {2}")
    @MethodSource("validPositionsForValidateInContext")
    @DisplayName("validateInContext accepts valid positions")
    @Tag("validation")
    void Given_ValidPositions_When_ValidateInContext_Then_NoException(double x, double y, String testName)
    {
        // Given
        TestPositionedElement validElement = TestPositionedElement
            .createTestElement()
            .withPosition(x, y)
            .build();

        // When & Then
        assertDoesNotThrow(() -> validElement.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext rejects {2}")
    @MethodSource("invalidPositionsForValidateInContext")
    @DisplayName("validateInContext throws exception for invalid positions")
    @Tag("validation")
    void Given_InvalidPositions_When_ValidateInContext_Then_ThrowsException(double x, double y, String testName, String expectedError)
    {
        // Given
        TestPositionedElement invalidElement = TestPositionedElement
            .createTestElement()
            .withPosition(x, y)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> invalidElement.validateInContext(LABEL_4X6, DPI_203, null)
        );

        String expectedFullError = expectedError + EXPECTED_POSITION_ERROR_SUFFIX;
        assertEquals(expectedFullError, actualException.getMessage());
    }

    @Test
    @DisplayName("validateInContext throws an exception when X-axis exceeds maximum")
    @Tag("validation")
    void Given_XAxisExceedsMax_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        TestPositionedElement exceedsMaxElement = TestPositionedElement
            .createTestElement()
            .withXAxisLocationMm(DPI_203.toMillimetres(MAX_AXIS_VALUE + 1))
            .withYAxisLocationMm(VALID_Y_POSITION)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> exceedsMaxElement.validateInContext(LABEL_4X6, DPI_203, null)
        );

        assertTrue(actualException.getMessage()
                                  .contains("X-axis location must be between"));
    }

    @Test
    @DisplayName("onLeftEdge with X offset resolves correctly")
    @Tag("builder")
    void Given_OnLeftEdgeWithOffset_When_Resolved_Then_XIsOffset()
    {
        double offset = 5.0;
        TestPositionedElement element = TestPositionedElement
            .createTestElement()
            .onLeftEdge()
            .withXOffset(offset)
            .withYAxisLocationMm(VALID_Y_POSITION)
            .addToLabel(testLabel);

        assertAll(
            () -> assertEquals(offset, element.getXAxisLocationMm())
        );
    }

    // Concrete implementation for testing abstract class
    @Getter
    @SuperBuilder(builderMethodName = "createTestElement", setterPrefix = "with")
    private static class TestPositionedElement extends PositionedElement
    {
    }
}
