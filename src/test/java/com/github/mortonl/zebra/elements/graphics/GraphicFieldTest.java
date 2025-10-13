package com.github.mortonl.zebra.elements.graphics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.elements.graphics.CompressionType.ASCII_HEX;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GraphicField creation and validation")
@Tag("unit")
@Tag("graphics")
class GraphicFieldTest
{

    private static final String SAMPLE_DATA = "A5A5";

    private static final int BINARY_BYTE_COUNT = 2;

    private static final int GRAPHIC_FIELD_COUNT = 1;

    private static final int BYTES_PER_ROW = 1;

    private static final double VALID_X_POSITION = 50.0;

    private static final double VALID_Y_POSITION = 75.0;

    private GraphicField classUnderTest;

    private static Stream<Arguments> validParametersForValidateInContext()
    {
        return Stream.of(
            Arguments.of(0.0, 0.0, "Minimum position values"),
            Arguments.of(50.0, 75.0, "Middle position values"),
            Arguments.of(101.6, 152.4, "Maximum position values")
        );
    }

    private static Stream<Arguments> invalidParametersForValidateInContext()
    {
        return Stream.of(
            Arguments.of(-1.0, 0.0, "Negative X position"),
            Arguments.of(0.0, -1.0, "Negative Y position"),
            Arguments.of(102.0, 75.0, "X position exceeds label width (102mm > 101.6mm)"),
            Arguments.of(50.0, 153.0, "Y position exceeds label height (153mm > 152.4mm)")
        );
    }

    @BeforeEach
    void setUp()
    {
        classUnderTest = GraphicField
            .createGraphicField()
            .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .build();
    }

    @Test
    @DisplayName("build throws exception when graphic data is null")
    @Tag("validation")
    void Given_NullGraphicData_When_Build_Then_ThrowsException()
    {
        // Given, When & Then
        assertThrows(NullPointerException.class, () ->
                GraphicField
                    .createGraphicField()
                    .withPosition(0.0, 0.0)
                    .withBinaryByteCount(BINARY_BYTE_COUNT)
                    .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
                    .withBytesPerRow(BYTES_PER_ROW)
                    .build(),
            "Should throw NullPointerException when graphic data is not provided"
        );
    }

    @ParameterizedTest(name = "validateInContext accepts {2}")
    @MethodSource("validParametersForValidateInContext")
    @DisplayName("validateInContext accepts valid parameters")
    @Tag("validation")
    void Given_ValidParams_When_ValidateInContext_Then_NoException(Double xPositionMm, Double yPositionMm, String scenario)
    {
        // Given
        GraphicField validField = GraphicField
            .createGraphicField()
            .withPosition(xPositionMm, yPositionMm)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .build();

        // When & Then
        assertDoesNotThrow(() -> validField.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext rejects {2}")
    @MethodSource("invalidParametersForValidateInContext")
    @DisplayName("validateInContext throws exception for invalid parameters")
    @Tag("validation")
    void Given_InvalidParams_When_ValidateInContext_Then_ThrowsException(Double xPositionMm, Double yPositionMm, String scenario)
    {
        // Given
        GraphicField invalidField = GraphicField
            .createGraphicField()
            .withPosition(xPositionMm, yPositionMm)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .build();

        // When & Then
        assertThrows(IllegalStateException.class,
            () -> invalidField.validateInContext(LABEL_4X6, DPI_203, null),
            "Failed scenario: " + scenario);
    }

    @Test
    @DisplayName("toZplString generates complete ZPL with compression type")
    @Tag("zpl-generation")
    void Given_CompleteField_When_ToZplString_Then_GeneratesCompleteZpl()
    {
        // Given
        GraphicField completeField = GraphicField
            .createGraphicField()
            .withPosition(VALID_X_POSITION, VALID_Y_POSITION)
            .withData(SAMPLE_DATA)
            .withBinaryByteCount(BINARY_BYTE_COUNT)
            .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
            .withBytesPerRow(BYTES_PER_ROW)
            .withCompressionType(ASCII_HEX)
            .build();

        // When
        String actualZplString = completeField.toZplString(DPI_203);

        // Then
        String foCommand = actualZplString.substring(
            actualZplString.indexOf("^FO") + 3,
            actualZplString.indexOf("^GF")
        );
        String[] actualFoParameters = foCommand.split(",", -1);

        String gfCommand = actualZplString.substring(
            actualZplString.indexOf("^GF") + 3,
            actualZplString.indexOf(SAMPLE_DATA)
        );
        String[] actualGfParameters = gfCommand.split(",", -1);

        String expectedXPosition       = "400";
        String expectedYPosition       = "600";
        String expectedCompressionType = "A";

        assertAll(
            () -> assertTrue(actualZplString.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(actualZplString.contains("^GF"), "Should contain ^GF"),
            () -> assertTrue(actualZplString.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals(expectedXPosition, actualFoParameters[0], "X position should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals(expectedYPosition, actualFoParameters[1], "Y position should be 600 dots (75mm * 8 dots/mm)"),
            () -> assertEquals(expectedCompressionType, actualGfParameters[0], "Compression type should be A for ASCII HEX"),
            () -> assertEquals(String.valueOf(BINARY_BYTE_COUNT), actualGfParameters[1], "Binary byte count should match"),
            () -> assertEquals(String.valueOf(GRAPHIC_FIELD_COUNT), actualGfParameters[2], "Graphic field count should match"),
            () -> assertEquals(String.valueOf(BYTES_PER_ROW), actualGfParameters[3], "Bytes per row should match"),
            () -> assertTrue(actualZplString.contains(SAMPLE_DATA), "Should contain graphic data")
        );
    }

    @Test
    @DisplayName("toZplString generates ZPL with null compression type")
    @Tag("zpl-generation")
    void Given_NullCompressionType_When_ToZplString_Then_GeneratesZplWithEmpty()
    {
        // Given (classUnderTest has null compression type by default)

        // When
        String actualCompleteZpl = classUnderTest.toZplString(DPI_203);

        // Then
        String foCommand = actualCompleteZpl.substring(
            actualCompleteZpl.indexOf("^FO") + 3,
            actualCompleteZpl.indexOf("^GF")
        );
        String[] actualFoParameters = foCommand.split(",", -1);

        String gfCommand = actualCompleteZpl.substring(
            actualCompleteZpl.indexOf("^GF") + 3,
            actualCompleteZpl.indexOf(SAMPLE_DATA)
        );
        String[] actualGfParameters = gfCommand.split(",", -1);

        String expectedXPosition = "400";
        String expectedYPosition = "600";
        String expectedEmpty     = "";

        assertAll(
            () -> assertTrue(actualCompleteZpl.startsWith("^FO"), "Should start with ^FO"),
            () -> assertTrue(actualCompleteZpl.contains("^GF"), "Should contain ^GF"),
            () -> assertTrue(actualCompleteZpl.endsWith("^FS"), "Should end with ^FS"),
            () -> assertEquals(expectedXPosition, actualFoParameters[0], "X position should be 400 dots (50mm * 8 dots/mm)"),
            () -> assertEquals(expectedYPosition, actualFoParameters[1], "Y position should be 600 dots (75mm * 8 dots/mm)"),
            () -> assertEquals(expectedEmpty, actualGfParameters[0], "Compression type parameter should be empty when null"),
            () -> assertEquals(String.valueOf(BINARY_BYTE_COUNT), actualGfParameters[1], "Binary byte count should match"),
            () -> assertEquals(String.valueOf(GRAPHIC_FIELD_COUNT), actualGfParameters[2], "Graphic field count should match"),
            () -> assertEquals(String.valueOf(BYTES_PER_ROW), actualGfParameters[3], "Bytes per row should match"),
            () -> assertTrue(actualCompleteZpl.contains(SAMPLE_DATA), "Should contain graphic data")
        );
    }
}
