package com.github.mortonl.zebra.elements;

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
    @DisplayName("validateInContext throws exception when X-axis exceeds maximum")
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
    @DisplayName("withPosition sets both axis locations correctly")
    @Tag("builder")
    void Given_PositionValues_When_WithPosition_Then_SetsBothAxes()
    {
        // Given & When (classUnderTest is built with position values)

        // Then
        assertAll(
            () -> assertEquals(VALID_X_POSITION, classUnderTest.getXAxisLocationMm()),
            () -> assertEquals(VALID_Y_POSITION, classUnderTest.getYAxisLocationMm())
        );
    }

    // Concrete implementation for testing abstract class
    @Getter
    @SuperBuilder(builderMethodName = "createTestElement", setterPrefix = "with")
    private static class TestPositionedElement extends PositionedElement
    {

    }
}
