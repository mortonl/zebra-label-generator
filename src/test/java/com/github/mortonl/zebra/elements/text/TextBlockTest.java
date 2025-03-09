package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.formatting.TextJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TextBlock Tests")
class TextBlockTest
{
    private static Stream<Arguments> validTextBlockParameters()
    {
        return Stream.of(
            Arguments.of(0.0, 1, 0.0, TextJustification.LEFT, 0.0, "Minimum allowed values"),
            Arguments.of(50.0, 100, 2.0, TextJustification.CENTER, 5.0, "Typical values"),
            Arguments.of(101.0, 9999, 10.0, TextJustification.RIGHT, 10.0, "Maximum allowed values")
        );
    }

    private static Stream<Arguments> invalidTextBlockParameters()
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
    @DisplayName("Should allow all null parameters")
    void shouldAllowAllNullParameters()
    {
        TextBlock block = TextBlock.createTextBlock()
                                   .withPlainTextContent("test")
                                   .build();
        assertDoesNotThrow(() -> block.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "Should accept valid parameters: {5}")
    @MethodSource("validTextBlockParameters")
    void shouldAcceptValidParameters(
        Double width, Integer maxLines, Double lineSpacing,
        TextJustification justification, Double hangingIndent, String scenario
    )
    {
        TextBlock block = TextBlock
            .createTextBlock()
            .withWidthMm(width)
            .withMaxLines(maxLines)
            .withLineSpacingMm(lineSpacing)
            .withJustification(justification)
            .withHangingIndentMm(hangingIndent)
            .withHexadecimalContent("48656C6C6F") // "Hello" in hex
            .build();

        assertDoesNotThrow(() -> block.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "Should reject invalid parameters: {4}")
    @MethodSource("invalidTextBlockParameters")
    void shouldRejectInvalidParameters(
        Double width, Integer maxLines, Double lineSpacing,
        Double hangingIndent, String scenario
    )
    {
        TextBlock block = TextBlock
            .createTextBlock()
            .withWidthMm(width)
            .withMaxLines(maxLines)
            .withLineSpacingMm(lineSpacing)
            .withHangingIndentMm(hangingIndent)
            .withJustification(TextJustification.LEFT)
            .withPlainTextContent("test")
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> block.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203),
            "Failed scenario: " + scenario);
    }

    @Test
    @DisplayName("Should generate complete ZPL string with all parameters")
    void shouldGenerateCompleteZplString()
    {
        TextBlock block = TextBlock
            .createTextBlock()
            .withWidthMm(50.0)
            .withMaxLines(5)
            .withLineSpacingMm(1.0)
            .withJustification(TextJustification.CENTER)
            .withHangingIndentMm(2.0)
            .withPlainTextContent("test")
            .build();

        String zplString = block.toZplString(PrintDensity.DPI_203);

        // Extract the FB command parameters
        String fbCommand = zplString.substring(
            zplString.indexOf("^FB") + 3,
            zplString.indexOf("^FD")
        );
        String[] parameters = fbCommand.split(",", -1);

        assertAll(
            () -> assertTrue(zplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(zplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals("400", parameters[0], "Width should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals("5", parameters[1], "MaxLines should be 5"),
            () -> assertEquals("8", parameters[2], "LineSpacing should be 8 dots (1mm * 8 dots/mm)"),
            () -> assertEquals("C", parameters[3], "Justification should be Center"),
            () -> assertEquals("16", parameters[4], "HangingIndent should be 16 dots (2mm * 8 dots/mm)")
        );
    }

    @Test
    @DisplayName("Should generate ZPL string with null values")
    void shouldGenerateZplStringWithNullValues()
    {
        TextBlock block = TextBlock
            .createTextBlock()
            .withWidthMm(50.0) // Only setting width
            .withHexadecimalContent("48656C6C6F") // "Hello" in hex
            .build();

        String zplString = block.toZplString(PrintDensity.DPI_203);

        // Extract the FB command parameters
        String fbCommand = zplString.substring(
            zplString.indexOf("^FB") + 3,
            zplString.indexOf("^FH")
        );
        String[] parameters = fbCommand.split(",", -1);

        assertAll(
            () -> assertTrue(zplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(zplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals("400", parameters[0], "Width should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals("", parameters[1], "MaxLines should be empty"),
            () -> assertEquals("", parameters[2], "LineSpacing should be empty"),
            () -> assertEquals("", parameters[3], "Justification should be empty"),
            () -> assertEquals("", parameters[4], "HangingIndent should be empty")
        );
    }

    @Test
    @DisplayName("Should generate ZPL string with partial parameters")
    void shouldAllowPartialParameters()
    {
        TextBlock block = TextBlock
            .createTextBlock()
            .withWidthMm(50.0)
            .withMaxLines(5)
            // Omitting lineSpacing
            .withJustification(TextJustification.CENTER)
            // Omitting hangingIndent
            .withPlainTextContent("test")
            .build();

        String zplString = block.toZplString(PrintDensity.DPI_203);

        // Extract the FB command parameters
        String fbCommand = zplString.substring(
            zplString.indexOf("^FB") + 3,
            zplString.indexOf("^FD")
        );
        String[] parameters = fbCommand.split(",", -1);

        assertAll(
            () -> assertTrue(zplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(zplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals("400", parameters[0], "Width should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals("5", parameters[1], "MaxLines should be 5"),
            () -> assertEquals("", parameters[2], "LineSpacing should be empty"),
            () -> assertEquals("C", parameters[3], "Justification should be Center"),
            () -> assertEquals("", parameters[4], "HangingIndent should be empty")
        );
    }

    @Test
    @DisplayName("Should maintain correct command order with hexadecimal content")
    void shouldMaintainCorrectCommandOrder()
    {
        TextBlock block = TextBlock
            .createTextBlock()
            .withWidthMm(50.0)
            .withHexadecimalContent("48656C6C6F")
            .build();

        String zplString = block.toZplString(PrintDensity.DPI_203);
        String[] commands = Arrays.stream(zplString.split("\\^"))
                                  .filter(cmd -> !cmd.isEmpty())
                                  .toArray(String[]::new);

        assertAll(
            () -> assertEquals("FO0,0", commands[0], "First command should be FO with coordinates"),
            () -> assertEquals("FB400,,,,", commands[1], "Second command should be FB with parameters"),
            () -> assertEquals("FH", commands[2], "Third command should be FH"),
            () -> assertEquals("FD48656C6C6F", commands[3], "Fourth command should be FD with hex content"),
            () -> assertEquals("FS", commands[4], "Last command should be FS"),
            () -> assertEquals(5, commands.length, "Should have exactly 5 commands")
        );
    }

    @Test
    @DisplayName("Should correctly format hexadecimal content")
    void shouldFormatHexadecimalContent()
    {
        TextBlock block = TextBlock
            .createTextBlock()
            .withWidthMm(50.0)
            .withHexadecimalContent("48656C6C6F")
            .build();

        String zplString = block.toZplString(PrintDensity.DPI_203);
        String[] commands = Arrays
            .stream(zplString.split("\\^"))
            .filter(cmd -> !cmd.isEmpty())
            .toArray(String[]::new);

        assertAll(
            () -> assertTrue(zplString.contains("^FH"), "Should include hexadecimal indicator"),
            () -> assertEquals("FD48656C6C6F",
                Arrays
                    .stream(commands)
                    .filter(cmd -> cmd.startsWith("FD"))
                    .findFirst()
                    .orElseThrow(),
                "Should include hex content without modification")
        );
    }
}
