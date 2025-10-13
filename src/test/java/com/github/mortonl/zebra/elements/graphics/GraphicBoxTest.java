package com.github.mortonl.zebra.elements.graphics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.mortonl.zebra.formatting.LineColor.BLACK;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GraphicBox creation and validation")
@Tag("unit")
@Tag("graphics")
class GraphicBoxTest
{

    private static final double VALID_WIDTH_MM = 50.0;

    private static final double VALID_HEIGHT_MM = 75.0;

    private static final double VALID_THICKNESS_MM = 1.0;

    private static final int VALID_ROUNDNESS = 4;

    private static final double HORIZONTAL_LINE_LENGTH = 100.0;

    private static final double VERTICAL_LINE_LENGTH = 100.0;

    private static final double LINE_THICKNESS = 1.0;

    private GraphicBox classUnderTest;

    @BeforeEach
    void setUp()
    {
        classUnderTest = GraphicBox
            .createGraphicBox()
            .withWidthMm(VALID_WIDTH_MM)
            .withHeightMm(VALID_HEIGHT_MM)
            .withThicknessMm(VALID_THICKNESS_MM)
            .withRoundness(VALID_ROUNDNESS)
            .build();
    }

    @Test
    @DisplayName("validateInContext accepts complete graphic box")
    @Tag("validation")
    void Given_CompleteGraphicBox_When_ValidateInContext_Then_NoException()
    {
        // Given (classUnderTest is already configured with complete parameters)

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @Test
    @DisplayName("validateInContext accepts null parameters")
    @Tag("validation")
    void Given_NullParameters_When_ValidateInContext_Then_NoException()
    {
        // Given
        GraphicBox nullParametersBox = GraphicBox
            .createGraphicBox()
            .build();

        // When & Then
        assertDoesNotThrow(() -> nullParametersBox.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext rejects roundness {0}")
    @ValueSource(ints = {-1,
        9})
    @DisplayName("validateInContext throws exception for invalid roundness")
    @Tag("validation")
    void Given_InvalidRoundness_When_ValidateInContext_Then_ThrowsException(int invalidRoundness)
    {
        // Given
        GraphicBox invalidRoundnessBox = GraphicBox
            .createGraphicBox()
            .withRoundness(invalidRoundness)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> invalidRoundnessBox.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("Roundness"));
    }

    @Test
    @DisplayName("validateInContext throws exception for thickness below minimum")
    @Tag("validation")
    void Given_ThicknessBelowMin_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        GraphicBox thinBox = GraphicBox
            .createGraphicBox()
            .withThicknessMm(0.03)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> thinBox.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("Thickness"));
    }

    @Test
    @DisplayName("validateInContext throws exception for thickness above maximum")
    @Tag("validation")
    void Given_ThicknessAboveMax_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        GraphicBox thickBox = GraphicBox
            .createGraphicBox()
            .withThicknessMm(1400.0)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> thickBox.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("Thickness"));
    }

    @Test
    @DisplayName("validateInContext throws exception for width exceeding label")
    @Tag("validation")
    void Given_WidthExceedsLabel_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        GraphicBox wideBox = GraphicBox
            .createGraphicBox()
            .withWidthMm(102.0) // Exceeds LABEL_4X6 width (101.6mm)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> wideBox.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("Width"));
    }

    @Test
    @DisplayName("validateInContext throws exception for height exceeding label")
    @Tag("validation")
    void Given_HeightExceedsLabel_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        GraphicBox tallBox = GraphicBox
            .createGraphicBox()
            .withHeightMm(153.0) // Exceeds LABEL_4X6 height (152.4mm)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> tallBox.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("Height"));
    }

    @Test
    @DisplayName("validateInContext throws exception for width less than thickness")
    @Tag("validation")
    void Given_WidthLessThanThickness_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        GraphicBox narrowBox = GraphicBox
            .createGraphicBox()
            .withWidthMm(1.0)
            .withThicknessMm(2.0)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> narrowBox.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("Width"));
    }

    @Test
    @DisplayName("validateInContext throws exception for height less than thickness")
    @Tag("validation")
    void Given_HeightLessThanThickness_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        GraphicBox shortBox = GraphicBox
            .createGraphicBox()
            .withHeightMm(1.0)
            .withThicknessMm(2.0)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> shortBox.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("Height"));
    }

    @Test
    @DisplayName("horizontalLine creates correct horizontal line")
    @Tag("builder")
    void Given_HorizontalLineParams_When_HorizontalLine_Then_CreatesCorrectLine()
    {
        // Given & When
        GraphicBox actualHorizontalLine = GraphicBox
            .horizontalLine(HORIZONTAL_LINE_LENGTH, LINE_THICKNESS)
            .build();

        // Then
        assertAll(
            () -> assertEquals(HORIZONTAL_LINE_LENGTH, actualHorizontalLine.getWidthMm()),
            () -> assertEquals(LINE_THICKNESS, actualHorizontalLine.getHeightMm()),
            () -> assertEquals(LINE_THICKNESS, actualHorizontalLine.getThicknessMm())
        );
    }

    @Test
    @DisplayName("verticalLine creates correct vertical line")
    @Tag("builder")
    void Given_VerticalLineParams_When_VerticalLine_Then_CreatesCorrectLine()
    {
        // Given & When
        GraphicBox actualVerticalLine = GraphicBox
            .verticalLine(VERTICAL_LINE_LENGTH, LINE_THICKNESS)
            .build();

        // Then
        assertAll(
            () -> assertEquals(LINE_THICKNESS, actualVerticalLine.getWidthMm()),
            () -> assertEquals(VERTICAL_LINE_LENGTH, actualVerticalLine.getHeightMm()),
            () -> assertEquals(LINE_THICKNESS, actualVerticalLine.getThicknessMm())
        );
    }

    @Test
    @DisplayName("toZplString generates complete ZPL with all parameters")
    @Tag("zpl-generation")
    void Given_CompleteBox_When_ToZplString_Then_GeneratesCompleteZpl()
    {
        // Given
        GraphicBox completeBox = GraphicBox
            .createGraphicBox()
            .withWidthMm(VALID_WIDTH_MM)
            .withHeightMm(VALID_HEIGHT_MM)
            .withThicknessMm(VALID_THICKNESS_MM)
            .withColor(BLACK)
            .withRoundness(VALID_ROUNDNESS)
            .build();

        // When
        String actualZplString = completeBox.toZplString(DPI_203);

        // Then
        assertAll(
            () -> assertTrue(actualZplString.contains("^GB")),
            () -> assertTrue(actualZplString.contains("400")),  // 50mm * 8 dots/mm = 400 dots
            () -> assertTrue(actualZplString.contains("600")),  // 75mm * 8 dots/mm = 600 dots
            () -> assertTrue(actualZplString.contains("8")),    // 1mm * 8 dots/mm = 8 dots
            () -> assertTrue(actualZplString.contains("B")),    // color code
            () -> assertTrue(actualZplString.contains("4"))     // roundness
        );
    }

    @Test
    @DisplayName("toZplString generates ZPL with null optional parameters")
    @Tag("zpl-generation")
    void Given_NullOptionalParams_When_ToZplString_Then_GeneratesZplWithEmpty()
    {
        // Given
        GraphicBox partialBox = GraphicBox
            .createGraphicBox()
            .withWidthMm(VALID_WIDTH_MM)
            .withHeightMm(VALID_HEIGHT_MM)
            .build();

        // When
        String actualZplString = partialBox.toZplString(DPI_203);

        // Then
        String gbCommand = actualZplString.substring(
            actualZplString.indexOf("^GB") + 3,
            actualZplString.indexOf("^FS")
        );
        String[] actualParameters = gbCommand.split(",", -1);

        String expectedWidth  = "400";
        String expectedHeight = "600";
        String expectedEmpty  = "";

        assertAll(
            () -> assertTrue(actualZplString.startsWith("^FO")),
            () -> assertTrue(actualZplString.endsWith("^FS")),
            () -> assertEquals(expectedWidth, actualParameters[0]),
            () -> assertEquals(expectedHeight, actualParameters[1]),
            () -> assertEquals(expectedEmpty, actualParameters[2]),
            () -> assertEquals(expectedEmpty, actualParameters[3]),
            () -> assertEquals(expectedEmpty, actualParameters[4])
        );
    }
}
