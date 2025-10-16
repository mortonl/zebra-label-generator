package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.formatting.TextJustification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.github.mortonl.zebra.formatting.TextJustification.CENTER;
import static com.github.mortonl.zebra.formatting.TextJustification.LEFT;
import static com.github.mortonl.zebra.formatting.TextJustification.RIGHT;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TextBlock validation and ZPL generation")
@Tag("unit")
@Tag("text-elements")
@ExtendWith(MockitoExtension.class)
class TextBlockTest
{
    private static final String TEST_PLAIN_TEXT = "test";

    private static final String TEST_HEX_CONTENT = "48656C6C6F";

    private static final double TYPICAL_WIDTH_MM = 50.0;

    private static final int TYPICAL_MAX_LINES = 5;

    private static final double TYPICAL_LINE_SPACING_MM = 1.0;

    private static final double TYPICAL_HANGING_INDENT_MM = 2.0;

    @Mock
    private DefaultFont mockDefaultFont;

    private static Stream<Arguments> validParametersForValidateInContext()
    {
        return Stream.of(
            Arguments.of(0.0, 1, 0.0, LEFT, 0.0, "Minimum allowed values"),
            Arguments.of(50.0, 100, 2.0, CENTER, 5.0, "Typical values"),
            Arguments.of(101.0, 9999, 10.0, RIGHT, 10.0, "Maximum allowed values")
        );
    }

    private static Stream<Arguments> invalidParametersForValidateInContext()
    {
        return Stream.of(
            Arguments.of(150.0, 1, 0.0, 0.0, "Width exceeds label width (150mm > 101.6mm)"),
            Arguments.of(50.0, 0, 0.0, 0.0, "MaxLines below minimum (0 < 1)"),
            Arguments.of(50.0, 10000, 0.0, 0.0, "MaxLines above maximum (10000 > 9999)"),
            Arguments.of(50.0, 1, -10000.0, 0.0, "Line spacing below minimum (-10000mm)"),
            Arguments.of(50.0, 1, 10000.0, 0.0, "Line spacing above maximum (10000mm)"),
            Arguments.of(50.0, 1, 0.0, -1.0, "Negative hanging indent (-1mm)"),
            Arguments.of(50.0, 1, 0.0, 10000.0, "Hanging indent too large (10000mm)")
        );
    }


    @Test
    @DisplayName("validateInContext accepts null optional parameters")
    @Tag("validation")
    void Given_NullOptionalParams_When_ValidateInContext_Then_NoException()
    {
        // Given
        TextBlock classUnderTest = TextBlock.createTextBlock()
                                            .withPlainTextContent(TEST_PLAIN_TEXT)
                                            .build();

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, mockDefaultFont));
    }

    @ParameterizedTest(name = "validateInContext accepts valid parameters: {5}")
    @MethodSource("validParametersForValidateInContext")
    @Tag("validation")
    void Given_ValidParams_When_ValidateInContext_Then_NoException(
        Double width, Integer maxLines, Double lineSpacing,
        TextJustification justification, Double hangingIndent, String scenario
    )
    {
        // Given
        TextBlock classUnderTest = TextBlock
            .createTextBlock()
            .withWidthMm(width)
            .withMaxLines(maxLines)
            .withLineSpacingMm(lineSpacing)
            .withJustification(justification)
            .withHangingIndentMm(hangingIndent)
            .withHexadecimalContent(TEST_HEX_CONTENT)
            .build();

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, mockDefaultFont));
    }

    @ParameterizedTest(name = "validateInContext rejects invalid parameters: {4}")
    @MethodSource("invalidParametersForValidateInContext")
    @Tag("validation")
    void Given_InvalidParams_When_ValidateInContext_Then_ThrowsException(
        Double width, Integer maxLines, Double lineSpacing,
        Double hangingIndent, String scenario
    )
    {
        // Given
        TextBlock classUnderTest = TextBlock
            .createTextBlock()
            .withWidthMm(width)
            .withMaxLines(maxLines)
            .withLineSpacingMm(lineSpacing)
            .withHangingIndentMm(hangingIndent)
            .withJustification(LEFT)
            .withPlainTextContent(TEST_PLAIN_TEXT)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, mockDefaultFont),
            "Failed scenario: " + scenario);
    }

    @Test
    @DisplayName("toZplString generates complete ZPL with all parameters")
    @Tag("zpl-generation")
    void Given_AllParams_When_ToZplString_Then_GeneratesCompleteZpl()
    {
        // Given
        TextBlock classUnderTest = TextBlock
            .createTextBlock()
            .withWidthMm(TYPICAL_WIDTH_MM)
            .withMaxLines(TYPICAL_MAX_LINES)
            .withLineSpacingMm(TYPICAL_LINE_SPACING_MM)
            .withJustification(CENTER)
            .withHangingIndentMm(TYPICAL_HANGING_INDENT_MM)
            .withPlainTextContent(TEST_PLAIN_TEXT)
            .build();

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        String fbCommand = actualZplString.substring(
            actualZplString.indexOf("^FB") + 3,
            actualZplString.indexOf("^FD")
        );
        String[] actualParameters = fbCommand.split(",", -1);

        String expectedWidth = "400";
        String expectedMaxLines = "5";
        String expectedLineSpacing = "8";
        String expectedJustification = "C";
        String expectedHangingIndent = "16";

        assertAll(
            () -> assertTrue(actualZplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(actualZplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals(expectedWidth, actualParameters[0], "Width should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals(expectedMaxLines, actualParameters[1], "MaxLines should be 5"),
            () -> assertEquals(expectedLineSpacing, actualParameters[2], "LineSpacing should be 8 dots (1mm * 8 dots/mm)"),
            () -> assertEquals(expectedJustification, actualParameters[3], "Justification should be Center"),
            () -> assertEquals(expectedHangingIndent, actualParameters[4], "HangingIndent should be 16 dots (2mm * 8 dots/mm)")
        );
    }

    @Test
    @DisplayName("toZplString generates ZPL with null optional parameters")
    @Tag("zpl-generation")
    void Given_NullOptionalParams_When_ToZplString_Then_GeneratesZplWithEmpty()
    {
        // Given
        TextBlock classUnderTest = TextBlock
            .createTextBlock()
            .withWidthMm(TYPICAL_WIDTH_MM)
            .withHexadecimalContent(TEST_HEX_CONTENT)
            .build();

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        String fbCommand = actualZplString.substring(
            actualZplString.indexOf("^FB") + 3,
            actualZplString.indexOf("^FH")
        );
        String[] actualParameters = fbCommand.split(",", -1);

        String expectedWidth = "400";
        String expectedEmpty = "";

        assertAll(
            () -> assertTrue(actualZplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(actualZplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals(expectedWidth, actualParameters[0], "Width should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals(expectedEmpty, actualParameters[1], "MaxLines should be empty"),
            () -> assertEquals(expectedEmpty, actualParameters[2], "LineSpacing should be empty"),
            () -> assertEquals(expectedEmpty, actualParameters[3], "Justification should be empty"),
            () -> assertEquals(expectedEmpty, actualParameters[4], "HangingIndent should be empty")
        );
    }

    @Test
    @DisplayName("toZplString generates ZPL with partial parameters")
    @Tag("zpl-generation")
    void Given_PartialParams_When_ToZplString_Then_GeneratesZplWithMixed()
    {
        // Given
        TextBlock classUnderTest = TextBlock
            .createTextBlock()
            .withWidthMm(TYPICAL_WIDTH_MM)
            .withMaxLines(TYPICAL_MAX_LINES)
            .withJustification(CENTER)
            .withPlainTextContent(TEST_PLAIN_TEXT)
            .build();

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        String fbCommand = actualZplString.substring(
            actualZplString.indexOf("^FB") + 3,
            actualZplString.indexOf("^FD")
        );
        String[] actualParameters = fbCommand.split(",", -1);

        String expectedWidth = "400";
        String expectedMaxLines = "5";
        String expectedEmpty = "";
        String expectedJustification = "C";

        assertAll(
            () -> assertTrue(actualZplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(actualZplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals(expectedWidth, actualParameters[0], "Width should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals(expectedMaxLines, actualParameters[1], "MaxLines should be 5"),
            () -> assertEquals(expectedEmpty, actualParameters[2], "LineSpacing should be empty"),
            () -> assertEquals(expectedJustification, actualParameters[3], "Justification should be Center"),
            () -> assertEquals(expectedEmpty, actualParameters[4], "HangingIndent should be empty")
        );
    }

    @Test
    @DisplayName("toZplString maintains correct command order with hex content")
    @Tag("zpl-generation")
    void Given_HexContent_When_ToZplString_Then_MaintainsCorrectOrder()
    {
        // Given
        TextBlock classUnderTest = TextBlock
            .createTextBlock()
            .withWidthMm(TYPICAL_WIDTH_MM)
            .withHexadecimalContent(TEST_HEX_CONTENT)
            .build();

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        String[] actualCommands = Arrays.stream(actualZplString.split("\\^"))
                                        .filter(cmd -> !cmd.isEmpty())
                                        .toArray(String[]::new);

        String expectedFirstCommand = "FO0,0";
        String expectedSecondCommand = "FB400,,,,";
        String expectedThirdCommand = "FH";
        String expectedFourthCommand = "FD48656C6C6F";
        String expectedLastCommand = "FS";
        int expectedCommandCount = 5;

        assertAll(
            () -> assertEquals(expectedFirstCommand, actualCommands[0], "First command should be FO with coordinates"),
            () -> assertEquals(expectedSecondCommand, actualCommands[1], "Second command should be FB with parameters"),
            () -> assertEquals(expectedThirdCommand, actualCommands[2], "Third command should be FH"),
            () -> assertEquals(expectedFourthCommand, actualCommands[3], "Fourth command should be FD with hex content"),
            () -> assertEquals(expectedLastCommand, actualCommands[4], "Last command should be FS"),
            () -> assertEquals(expectedCommandCount, actualCommands.length, "Should have exactly 5 commands")
        );
    }

    @Test
    @DisplayName("toZplString correctly formats hexadecimal content")
    @Tag("zpl-generation")
    void Given_HexContent_When_ToZplString_Then_FormatsHexCorrectly()
    {
        // Given
        TextBlock classUnderTest = TextBlock
            .createTextBlock()
            .withWidthMm(TYPICAL_WIDTH_MM)
            .withHexadecimalContent(TEST_HEX_CONTENT)
            .build();

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        String[] actualCommands = Arrays
            .stream(actualZplString.split("\\^"))
            .filter(cmd -> !cmd.isEmpty())
            .toArray(String[]::new);

        String expectedFdCommand = "FD48656C6C6F";

        assertAll(
            () -> assertTrue(actualZplString.contains("^FH"), "Should include hexadecimal indicator"),
            () -> assertEquals(expectedFdCommand,
                Arrays
                    .stream(actualCommands)
                    .filter(cmd -> cmd.startsWith("FD"))
                    .findFirst()
                    .orElseThrow(),
                "Should include hex content without modification")
        );
    }
}
