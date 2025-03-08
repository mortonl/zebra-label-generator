package com.github.mortonl.zebra.elements.graphics;

import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GraphicField Tests")
class GraphicFieldTest
{
    private static final String SAMPLE_DATA = "A5A5"; // Example graphic data
    private static final int BINARY_BYTE_COUNT = 2;
    private static final int GRAPHIC_FIELD_COUNT = 1;
    private static final int BYTES_PER_ROW = 1;

    private static Stream<Arguments> validGraphicFieldParameters()
    {
        return Stream.of(
            Arguments.of(0.0, 0.0, "Minimum position values"),
            Arguments.of(50.0, 75.0, "Middle position values"),
            Arguments.of(101.6, 152.4, "Maximum position values")
        );
    }

    private static Stream<Arguments> invalidGraphicFieldParameters()
    {
        return Stream.of(
            Arguments.of(-1.0, 0.0, "Negative X position"),
            Arguments.of(0.0, -1.0, "Negative Y position"),
            Arguments.of(102.0, 75.0, "X position exceeds label width (102mm > 101.6mm)"),
            Arguments.of(50.0, 153.0, "Y position exceeds label height (153mm > 152.4mm)")
        );
    }

    @Test
    @DisplayName("Should require graphic data")
    void shouldRequireGraphicData()
    {
        assertThrows(NullPointerException.class, () ->
                GraphicField
                    .builder()
                    .withPosition(0.0, 0.0)
                    .withBinaryByteCount(BINARY_BYTE_COUNT)
                    .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
                    .withBytesPerRow(BYTES_PER_ROW)
                    .build(),
            "Should throw NullPointerException when graphic data is not provided"
        );
    }

    @ParameterizedTest(name = "Should accept valid parameters: {2}")
    @MethodSource("validGraphicFieldParameters")
    void shouldAcceptValidParameters(Double xPositionMm, Double yPositionMm, String scenario)
    {
        GraphicField field = GraphicField
            .builder()
            .withPosition(xPositionMm, yPositionMm)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .build();

        assertDoesNotThrow(() -> field.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "Should reject invalid parameters: {2}")
    @MethodSource("invalidGraphicFieldParameters")
    void shouldRejectInvalidParameters(Double xPositionMm, Double yPositionMm, String scenario)
    {
        GraphicField field = GraphicField
            .builder()
            .withPosition(xPositionMm, yPositionMm)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .build();

        assertThrows(IllegalStateException.class,
            () -> field.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203),
            "Failed scenario: " + scenario);
    }

    @Test
    @DisplayName("Should generate complete ZPL string")
    void shouldGenerateCompleteZplString()
    {
        GraphicField field = GraphicField
            .builder()
            .withPosition(50.0, 75.0)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .withCompressionType(CompressionType.ASCII_HEX)
            .build();

        String zplString = field.toZplString(PrintDensity.DPI_203);

        // Extract the FO command parameters
        String foCommand = zplString.substring(
            zplString.indexOf("^FO") + 3,
            zplString.indexOf("^GF")
        );
        String[] foParameters = foCommand.split(",", -1);

        // Extract the GF command parameters
        String gfCommand = zplString.substring(
            zplString.indexOf("^GF") + 3,
            zplString.indexOf(SAMPLE_DATA)
        );
        String[] gfParameters = gfCommand.split(",", -1);

        assertAll(
            () -> assertTrue(zplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(zplString.contains("^GF"), "Should contain ^GF"),
            () -> assertTrue(zplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals("400", foParameters[0], "X position should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals("600", foParameters[1], "Y position should be 600 dots (75mm * 8 dots/mm)"),
            () -> assertEquals("A", gfParameters[0], "Compression type should be A for ASCII HEX"),
            () -> assertEquals(String.valueOf(BINARY_BYTE_COUNT), gfParameters[1], "Binary byte count should match"),
            () -> assertEquals(String.valueOf(GRAPHIC_FIELD_COUNT), gfParameters[2], "Graphic field count should match"),
            () -> assertEquals(String.valueOf(BYTES_PER_ROW), gfParameters[3], "Bytes per row should match"),
            () -> assertTrue(zplString.contains(SAMPLE_DATA), "Should contain graphic data")
        );
    }

    @Test
    @DisplayName("Should generate ZPL string with null compression type")
    void shouldGenerateZplStringWithNullCompressionType()
    {
        GraphicField field = GraphicField
            .builder()
            .withPosition(50.0, 75.0)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .build();

        String completeZpl = field.toZplString(PrintDensity.DPI_203);

        // Extract the FO command parameters
        String foCommand = completeZpl.substring(
            completeZpl.indexOf("^FO") + 3,
            completeZpl.indexOf("^GF")
        );
        String[] foParameters = foCommand.split(",", -1);

        // Extract the GF command parameters
        String gfCommand = completeZpl.substring(
            completeZpl.indexOf("^GF") + 3,
            completeZpl.indexOf(SAMPLE_DATA)
        );
        String[] gfParameters = gfCommand.split(",", -1);

        assertAll(
            () -> assertTrue(completeZpl.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(completeZpl.contains("^GF"), "Should contain ^GF"),
            () -> assertTrue(completeZpl.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals("400", foParameters[0], "X position should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals("600", foParameters[1], "Y position should be 600 dots (75mm * 8 dots/mm)"),
            () -> assertTrue(gfParameters[0].isEmpty(), "Compression type parameter should be empty when null"),
            () -> assertEquals(String.valueOf(BINARY_BYTE_COUNT), gfParameters[1], "Binary byte count should match"),
            () -> assertEquals(String.valueOf(GRAPHIC_FIELD_COUNT), gfParameters[2], "Graphic field count should match"),
            () -> assertEquals(String.valueOf(BYTES_PER_ROW), gfParameters[3], "Bytes per row should match"),
            () -> assertTrue(completeZpl.contains(SAMPLE_DATA), "Should contain graphic data")
        );
    }

}
