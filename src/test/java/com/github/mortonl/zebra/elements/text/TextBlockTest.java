package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.formatting.OriginJustification;
import com.github.mortonl.zebra.formatting.TextJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("TextBlock Tests")
class TextBlockTest
{
    private static final LabelSize TEST_LABEL = LabelSize.LABEL_4X6;
    private static final PrintDensity TEST_DPI = PrintDensity.DPI_203;

    private static Stream<Arguments> validTextBlockParameters()
    {
        return Stream.of(
            Arguments.of(0.0, 1, 0.0, TextJustification.LEFT, 0.0, "Minimum values"),
            Arguments.of(50.0, 100, 2.0, TextJustification.CENTER, 5.0, "Middle values"),
            Arguments.of(99.0, 9999, 10.0, TextJustification.RIGHT, 10.0, "Maximum values")
        );
    }

    private static Stream<Arguments> invalidTextBlockParameters()
    {
        return Stream.of(
            Arguments.of(150.0, 1, 0.0, 0.0, "Width exceeds label width"),
            Arguments.of(50.0, 0, 0.0, 0.0, "MaxLines below minimum"),
            Arguments.of(50.0, 10000, 0.0, 0.0, "MaxLines above maximum"),
            Arguments.of(50.0, 1, -10000.0, 0.0, "Line spacing below minimum"),
            Arguments.of(50.0, 1, 10000.0, 0.0, "Line spacing above maximum"),
            Arguments.of(50.0, 1, 0.0, -1.0, "Negative hanging indent"),
            Arguments.of(50.0, 1, 0.0, 10000.0, "Hanging indent too large")
        );
    }

    @Test
    @DisplayName("Should generate correct ZPL string")
    void testToZplString()
    {
        Field mockField = mock(Field.class);
        when(mockField.toZplString(TEST_DPI)).thenReturn("^FDTest Text^FS");

        TextBlock textBlock = TextBlock
            .builder()
            .xAxisLocationMm(10)
            .yAxisLocationMm(20)
            .widthMm(50)
            .maxLines(2)
            .lineSpacingMm(1)
            .justification(TextJustification.CENTER)
            .hangingIndentMm(2)
            .text(mockField)
            .zOriginJustification(OriginJustification.LEFT)
            .build();

        String expected = "^FO80,160,0^FB400,2,8,C,16^FDTest Text^FS";

        assertEquals(expected, textBlock.toZplString(TEST_DPI));
    }

    @ParameterizedTest(name = "Valid parameters: {5}")
    @MethodSource("validTextBlockParameters")
    void testValidateInContextWithValidParameters(
        double width, int maxLines, double lineSpacing,
        TextJustification justification, double hangingIndent, String testName
    )
    {
        Field mockField = mock(Field.class);
        when(mockField.getData()).thenReturn("Test Text");

        TextBlock textBlock = TextBlock
            .builder()
            .xAxisLocationMm(10)
            .yAxisLocationMm(10)
            .widthMm(width)
            .maxLines(maxLines)
            .lineSpacingMm(lineSpacing)
            .justification(justification)
            .hangingIndentMm(hangingIndent)
            .text(mockField)
            .build();

        assertDoesNotThrow(() -> textBlock.validateInContext(TEST_LABEL, TEST_DPI));
    }

    @ParameterizedTest(name = "Invalid parameters: {4}")
    @MethodSource("invalidTextBlockParameters")
    void testValidateInContextWithInvalidParameters(
        double width, int maxLines, double lineSpacing,
        double hangingIndent, String testName
    )
    {
        Field mockField = mock(Field.class);
        when(mockField.toZplString(TEST_DPI)).thenReturn("^FDTest Text^FS");

        TextBlock textBlock = TextBlock
            .builder()
            .xAxisLocationMm(10)
            .yAxisLocationMm(10)
            .widthMm(width)
            .maxLines(maxLines)
            .lineSpacingMm(lineSpacing)
            .hangingIndentMm(hangingIndent)
            .text(mockField)
            .build();

        assertThrows(IllegalStateException.class,
            () -> textBlock.validateInContext(TEST_LABEL, TEST_DPI));
    }

    @Test
    @DisplayName("Should throw exception for empty text")
    void testValidateInContextWithEmptyText()
    {
        Field mockField = mock(Field.class);
        when(mockField.getData()).thenReturn("");

        TextBlock textBlock = TextBlock
            .builder()
            .xAxisLocationMm(10)
            .yAxisLocationMm(10)
            .text(mockField)
            .build();

        assertThrows(IllegalStateException.class,
            () -> textBlock.validateInContext(TEST_LABEL, TEST_DPI));
    }

    @Test
    @DisplayName("Should throw exception for null text")
    void testValidateInContextWithNullText()
    {
        assertThrows(NullPointerException.class, () ->
            TextBlock
                .builder()
                .xAxisLocationMm(10)
                .yAxisLocationMm(10)
                .build()
                .validateInContext(TEST_LABEL, TEST_DPI));
    }
}
