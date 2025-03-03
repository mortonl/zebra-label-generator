package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.formatting.OriginJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.elements.PositionedElement.MAX_AXIS_VALUE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PositionedElementTest
{
    private static final LabelSize TEST_LABEL = LabelSize.LABEL_4X6;
    private static final PrintDensity X8_DOTS_PER_MM = PrintDensity.DPI_203;

    private static Stream<Arguments> validPositions()
    {
        return Stream.of(
            Arguments.of(0.0, 0.0, "Minimum values"),
            Arguments.of(50.0, 75.0, "Middle of label"),
            Arguments.of(99.0, 149.0, "Near maximum values")
        );
    }

    private static Stream<Arguments> invalidPositions()
    {
        return Stream.of(
            Arguments.of(150.0, 75.0, "X exceeds width", "X-axis position (150.00 mm) exceeds label width (101.60 mm)."),
            Arguments.of(50.0, 200.0, "Y exceeds height", "Y-axis position (200.00 mm) exceeds label height (152.40 mm)."),
            Arguments.of(150.0, 200.0, "Both exceed dimensions", "X-axis position (150.00 mm) exceeds label width (101.60 mm). Y-axis position (200.00 mm) exceeds label height (152.40 mm).")
        );
    }

    @Test
    void testToZplStringWithoutJustification()
    {
        TestPositionedElement element = TestPositionedElement
            .createTestElement()
            .withPosition(10.0, 20.0)
            .build();

        String expected = "^FO" + X8_DOTS_PER_MM.toDots(10.0) + "," + X8_DOTS_PER_MM.toDots(20.0);
        assertEquals(expected, element.toZplString(X8_DOTS_PER_MM));
    }

    @Test
    void testToZplStringWithJustification()
    {
        TestPositionedElement element = TestPositionedElement
            .createTestElement()
            .withPosition(10.0, 20.0)
            .withZOriginJustification(OriginJustification.LEFT)
            .build();

        String expected = "^FO80,160,0";
        assertEquals(expected, element.toZplString(X8_DOTS_PER_MM));
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("validPositions")
    void testValidateInContextWithValidPositions(double x, double y, String testName)
    {
        TestPositionedElement element = TestPositionedElement
            .createTestElement()
            .withPosition(x, y)
            .build();

        assertDoesNotThrow(() -> element.validateInContext(TEST_LABEL, X8_DOTS_PER_MM));
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("invalidPositions")
    void testValidateInContextWithInvalidPositions(double x, double y, String testName, String expectedError)
    {
        TestPositionedElement element = TestPositionedElement
            .createTestElement()
            .withPosition(x, y)
            .build();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> element.validateInContext(TEST_LABEL, X8_DOTS_PER_MM)
        );

        assertEquals(expectedError + " The element must be positioned within the label dimensions.", exception.getMessage());
    }

    @Test
    void testValidateAxisValueExceedsMaximum()
    {
        TestPositionedElement element = TestPositionedElement
            .createTestElement()
            .withXAxisLocationMm(X8_DOTS_PER_MM.toMillimetres(MAX_AXIS_VALUE + 1))
            .withYAxisLocationMm(20.0)
            .build();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> element.validateInContext(TEST_LABEL, X8_DOTS_PER_MM)
        );

        assertTrue(exception
            .getMessage()
            .contains("X-axis location must be between"));
    }

    @Test
    void testBuilderAtMethod()
    {

        TestPositionedElement element = TestPositionedElement
            .createTestElement()
            .withPosition(10.0, 20.0)
            .build();

        assertEquals(10.0, element.getXAxisLocationMm());
        assertEquals(20.0, element.getYAxisLocationMm());
    }

    // Concrete implementation for testing abstract class
    @Getter
    @SuperBuilder(builderMethodName = "createTestElement", setterPrefix = "with")
    private static class TestPositionedElement extends PositionedElement
    {
        @Override
        public String toZplString(PrintDensity dpi)
        {
            return super.toZplString(dpi);
        }

        @Override
        public void validateInContext(LabelSize size, PrintDensity dpi)
        {
            super.validateInContext(size, dpi);
        }

        protected static abstract class TestPositionedElementBuilder<C extends TestPositionedElement, B extends TestPositionedElementBuilder<C, B>>
            extends PositionedElementBuilder<C, B>
        {
        }
    }

}
