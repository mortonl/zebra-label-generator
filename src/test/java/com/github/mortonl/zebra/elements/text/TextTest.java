package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.elements.fonts.Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName("Text element creation and validation")
@Tag("unit")
@Tag("text")
@ExtendWith(MockitoExtension.class)
class TextTest
{

    private static final String TEST_CONTENT = "test content";

    private static final String EXPECTED_POSITION_COMMAND = "^FO80,160";

    private static final String MOCK_FONT_COMMAND = "^A0N,50,50";

    private static final String MOCK_FIELD_COMMAND = "^FDtest content^FS";

    private static final double VALID_X_POSITION = 10.0;

    private static final double VALID_Y_POSITION = 20.0;

    private static final String EXPECTED_NO_DEFAULT_FONT_MESSAGE = "No default font set on label";

    private static final String EXPECTED_NULL_TEXT_MESSAGE = "Text cannot be null";

    private static final String EXPECTED_EMPTY_TEXT_MESSAGE = "Text cannot be empty";

    @Mock
    private Font mockFont;

    @Mock
    private Field mockField;

    @Mock
    private DefaultFont mockDefaultFont;



    @Test
    @DisplayName("toZplString includes reverse print command when reversed is true")
    @Tag("zpl-generation")
    void Given_ReversedTrue_When_ToZplString_Then_IncludesReverseCommand()
    {
        // Given
        when(mockFont.toZplString(DPI_203)).thenReturn(MOCK_FONT_COMMAND);
        when(mockField.toZplString(DPI_203)).thenReturn(MOCK_FIELD_COMMAND);

        Text reversedText = Text.createText()
                                .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
                                .withColorAndBackgroundReversed(true)
                                .withFont(mockFont)
                                .withContent(mockField)
                                .build();

        // When
        String actualResult = reversedText.toZplString(DPI_203);

        // Then
        String expectedResult = EXPECTED_POSITION_COMMAND +
            ZplCommand.FIELD_REVERSE_PRINT +
            MOCK_FONT_COMMAND +
            MOCK_FIELD_COMMAND;
        assertEquals(expectedResult, actualResult);
        verify(mockFont).toZplString(DPI_203);
        verify(mockField).toZplString(DPI_203);
        verifyNoMoreInteractions(mockFont, mockField);
    }

    @Test
    @DisplayName("toZplString excludes reverse print command when reversed is false")
    @Tag("zpl-generation")
    void Given_ReversedFalse_When_ToZplString_Then_ExcludesReverseCommand()
    {
        // Given
        when(mockFont.toZplString(DPI_203)).thenReturn(MOCK_FONT_COMMAND);
        when(mockField.toZplString(DPI_203)).thenReturn(MOCK_FIELD_COMMAND);

        Text normalText = Text.createText()
                              .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
                              .withColorAndBackgroundReversed(false)
                              .withFont(mockFont)
                              .withContent(mockField)
                              .build();

        // When
        String actualResult = normalText.toZplString(DPI_203);

        // Then
        String expectedResult = EXPECTED_POSITION_COMMAND +
            MOCK_FONT_COMMAND +
            MOCK_FIELD_COMMAND;
        assertEquals(expectedResult, actualResult);
        verify(mockFont).toZplString(DPI_203);
        verify(mockField).toZplString(DPI_203);
        verifyNoMoreInteractions(mockFont, mockField);
    }

    @Test
    @DisplayName("toZplString excludes reverse print command when reversed is null")
    @Tag("zpl-generation")
    void Given_ReversedNull_When_ToZplString_Then_ExcludesReverseCommand()
    {
        // Given
        when(mockFont.toZplString(DPI_203)).thenReturn(MOCK_FONT_COMMAND);
        when(mockField.toZplString(DPI_203)).thenReturn(MOCK_FIELD_COMMAND);

        Text classUnderTest = Text.createText()
                                  .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
                                  .withFont(mockFont)
                                  .withContent(mockField)
                                  .build();

        // When
        String actualResult = classUnderTest.toZplString(DPI_203);

        // Then
        String expectedResult = EXPECTED_POSITION_COMMAND +
            MOCK_FONT_COMMAND +
            MOCK_FIELD_COMMAND;
        assertEquals(expectedResult, actualResult);
        verify(mockFont).toZplString(DPI_203);
        verify(mockField).toZplString(DPI_203);
        verifyNoMoreInteractions(mockFont, mockField);
    }

    @Test
    @DisplayName("validateInContext accepts text with font and no default font")
    @Tag("validation")
    void Given_FontSetNoDefault_When_ValidateInContext_Then_NoException()
    {
        // Given
        when(mockField.getData()).thenReturn(TEST_CONTENT);

        Text classUnderTest = Text.createText()
                                  .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
                                  .withFont(mockFont)
                                  .withContent(mockField)
                                  .build();

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, null));
        verify(mockFont).validateInContext(LABEL_4X6, DPI_203, null);
        verifyNoMoreInteractions(mockFont);
    }

    @Test
    @DisplayName("validateInContext accepts text with default font and no element font")
    @Tag("validation")
    void Given_DefaultFontNoElementFont_When_ValidateInContext_Then_NoException()
    {
        // Given
        Text textWithoutFont = Text.createText()
                                   .withContent(mockField)
                                   .build();

        // When & Then
        when(mockField.getData()).thenReturn(TEST_CONTENT);

        assertDoesNotThrow(() -> textWithoutFont.validateInContext(LABEL_4X6, DPI_203, mockDefaultFont));
        verifyNoMoreInteractions(mockDefaultFont);
    }

    @Test
    @DisplayName("validateInContext accepts text with both element and default fonts")
    @Tag("validation")
    void Given_BothFonts_When_ValidateInContext_Then_NoException()
    {
        // Given
        when(mockField.getData()).thenReturn(TEST_CONTENT);

        Text classUnderTest = Text.createText()
                                  .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
                                  .withFont(mockFont)
                                  .withContent(mockField)
                                  .build();

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, mockDefaultFont));
        verify(mockFont).validateInContext(LABEL_4X6, DPI_203, mockDefaultFont);
        verifyNoMoreInteractions(mockFont, mockDefaultFont);
    }

    @Test
    @DisplayName("validateInContext throws exception when no font available")
    @Tag("validation")
    void Given_NoFonts_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        Text textWithoutFont = Text.createText()
                                   .withContent(mockField)
                                   .build();

        // When & Then
        when(mockField.getData()).thenReturn(TEST_CONTENT);

        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> textWithoutFont.validateInContext(LABEL_4X6, DPI_203, null)
        );
        assertEquals(EXPECTED_NO_DEFAULT_FONT_MESSAGE, actualException.getMessage());
    }


    @Test
    @DisplayName("validateInContext throws exception for null content")
    @Tag("validation")
    void Given_NullContent_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        when(mockField.getData()).thenReturn(null);
        Text nullContentText = Text.createText()
                                   .withContent(mockField)
                                   .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> nullContentText.validateInContext(LABEL_4X6, DPI_203, null)
        );
        assertEquals(EXPECTED_NULL_TEXT_MESSAGE, actualException.getMessage());
    }

    @ParameterizedTest(name = "validateInContext rejects content '{0}'")
    @EmptySource
    @ValueSource(strings = {" ",
        "\t",
        "\n"})
    @DisplayName("validateInContext throws exception for empty or blank content")
    @Tag("validation")
    void Given_EmptyOrBlankContent_When_ValidateInContext_Then_ThrowsException(String invalidContent)
    {
        // Given
        when(mockField.getData()).thenReturn(invalidContent);
        Text emptyContentText = Text.createText()
                                    .withContent(mockField)
                                    .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> emptyContentText.validateInContext(LABEL_4X6, DPI_203, null)
        );
        assertEquals(EXPECTED_EMPTY_TEXT_MESSAGE, actualException.getMessage());
    }

    @Test
    @DisplayName("withHexadecimalContent creates text with content field")
    @Tag("builder")
    void Given_HexContent_When_WithHexadecimalContent_Then_CreatesContentField()
    {
        // Given
        String testHexContent = "test";

        // When
        Text actualText = Text.createText()
                              .withHexadecimalContent(testHexContent)
                              .build();

        // Then
        assertNotNull(actualText.getContent());
    }
}
