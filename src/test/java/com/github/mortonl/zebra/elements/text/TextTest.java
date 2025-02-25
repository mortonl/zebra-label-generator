package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.elements.fonts.Font;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TextTest
{
    private static final PrintDensity TEST_DPI = PrintDensity.DPI_203;
    private static final LabelSize TEST_SIZE = LabelSize.LABEL_4X6;
    private static final String TEST_CONTENT = "test content";
    private static final String EXPECTED_POSITION_COMMAND = "^FO80,160";
    private static final String MOCK_FONT_COMMAND = "^A0N,50,50";
    private static final String MOCK_FIELD_COMMAND = "^FDtest content^FS";

    @Test
    void testToZplString_WithReversedTrue()
    {
        // Arrange
        Font mockFont = mock(Font.class);
        Field mockField = mock(Field.class);
        when(mockFont.toZplString(TEST_DPI)).thenReturn(MOCK_FONT_COMMAND);
        when(mockField.toZplString(TEST_DPI)).thenReturn(MOCK_FIELD_COMMAND);

        Text text = Text
            .builder()
            .withPosition(10, 20)
            .withColorAndBackgroundReversed(true)
            .withFont(mockFont)
            .withContent(mockField)
            .build();

        // Act
        String result = text.toZplString(TEST_DPI);

        // Assert
        String expected = EXPECTED_POSITION_COMMAND +
            ZplCommand.FIELD_REVERSE_PRINT +
            MOCK_FONT_COMMAND +
            MOCK_FIELD_COMMAND;
        assertEquals(expected, result);
        verify(mockFont).toZplString(TEST_DPI);
        verify(mockField).toZplString(TEST_DPI);
    }

    @Test
    void testToZplString_WithReversedFalse()
    {
        // Arrange
        Font mockFont = mock(Font.class);
        Field mockField = mock(Field.class);
        when(mockFont.toZplString(TEST_DPI)).thenReturn(MOCK_FONT_COMMAND);
        when(mockField.toZplString(TEST_DPI)).thenReturn(MOCK_FIELD_COMMAND);

        Text text = Text
            .builder()
            .withPosition(10, 20)
            .withColorAndBackgroundReversed(false)
            .withFont(mockFont)
            .withContent(mockField)
            .build();

        // Act
        String result = text.toZplString(TEST_DPI);

        // Assert
        String expected = EXPECTED_POSITION_COMMAND +
            MOCK_FONT_COMMAND +
            MOCK_FIELD_COMMAND;
        assertEquals(expected, result);
        verify(mockFont).toZplString(TEST_DPI);
        verify(mockField).toZplString(TEST_DPI);
    }

    @Test
    void testToZplString_WithReversedNull()
    {
        // Arrange
        Font mockFont = mock(Font.class);
        Field mockField = mock(Field.class);
        when(mockFont.toZplString(TEST_DPI)).thenReturn(MOCK_FONT_COMMAND);
        when(mockField.toZplString(TEST_DPI)).thenReturn(MOCK_FIELD_COMMAND);

        Text text = Text
            .builder()
            .withPosition(10, 20)
            .withColorAndBackgroundReversed(null)
            .withFont(mockFont)
            .withContent(mockField)
            .build();

        // Act
        String result = text.toZplString(TEST_DPI);

        // Assert
        String expected = EXPECTED_POSITION_COMMAND +
            MOCK_FONT_COMMAND +
            MOCK_FIELD_COMMAND;
        assertEquals(expected, result);
        verify(mockFont).toZplString(TEST_DPI);
        verify(mockField).toZplString(TEST_DPI);
    }

    @Test
    void testValidateInContext_ValidContent()
    {
        Field mockField = mock(Field.class);
        when(mockField.getData()).thenReturn(TEST_CONTENT);

        Text text = Text
            .builder()
            .withContent(mockField)
            .build();

        assertDoesNotThrow(() -> text.validateInContext(TEST_SIZE, TEST_DPI));
    }

    @Test
    void testValidateInContext_NullContent()
    {
        Field mockField = mock(Field.class);
        when(mockField.getData()).thenReturn(null);

        Text text = Text
            .builder()
            .withContent(mockField)
            .build();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> text.validateInContext(TEST_SIZE, TEST_DPI)
        );
        assertEquals("Text cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void testValidateInContext_EmptyOrBlankContent(String invalidContent)
    {
        Field mockField = mock(Field.class);
        when(mockField.getData()).thenReturn(invalidContent);

        Text text = Text
            .builder()
            .withContent(mockField)
            .build();

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> text.validateInContext(TEST_SIZE, TEST_DPI)
        );
        assertEquals("Text cannot be empty", exception.getMessage());
    }

    @Test
    void testWithHexadecimalContent()
    {
        String testContent = "test";
        Text text = Text
            .builder()
            .withHexadecimalContent(testContent)
            .build();

        assertNotNull(text.getContent());
    }
}
