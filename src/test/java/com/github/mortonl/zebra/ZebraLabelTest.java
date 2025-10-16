package com.github.mortonl.zebra;

import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.elements.text.Text;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.mortonl.zebra.elements.fonts.DefaultFont.createDefaultFont;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ZebraLabel creation and element management")
@Tag("unit")
@Tag("label")
class ZebraLabelTest
{
    private static final char DEFAULT_FONT_DESIGNATION = '0';

    private static final double DEFAULT_FONT_WIDTH = 8.75;

    private static final double DEFAULT_FONT_HEIGHT = 11.625;

    private static final String FIRST_TEXT_CONTENT = "First Text";

    private static final String SECOND_TEXT_CONTENT = "Second Text";

    private static final String EXAMPLE_TEXT_CONTENT = "Example Text";

    private static final String EXPECTED_NULL_ELEMENT_MESSAGE = "Cannot add null elements to Label";

    private static final double FIRST_X_POSITION = 1.25;

    private static final double FIRST_Y_POSITION = 2.5;

    private static final double SECOND_X_POSITION = 12.5;

    private static final double SECOND_Y_POSITION = 25.0;

    private ZebraLabel classUnderTest;

    @BeforeEach
    void setUp()
    {
        PrinterConfiguration printerConfiguration = PrinterConfiguration
            .createPrinterConfiguration()
            .forDpi(DPI_203)
            .forLoadedMedia(LoadedMedia.fromLabelSize(LABEL_4X6))
            .build();

        DefaultFont defaultFont = createDefaultFont()
            .withFontDesignation(DEFAULT_FONT_DESIGNATION)
            .withSize(DEFAULT_FONT_WIDTH, DEFAULT_FONT_HEIGHT)
            .build();

        classUnderTest = ZebraLabel.createLabel()
                                   .forPrinter(printerConfiguration)
                                   .forSize(LABEL_4X6)
                                   .build();

        classUnderTest.validateAndAddElement(defaultFont);
    }

    @Test
    @DisplayName("toZplString generates complete ZPL with all components")
    @Tag("zpl-generation")
    void Given_ConfiguredLabel_When_ToZplString_Then_GeneratesCompleteZpl()
    {
        // Given (classUnderTest is configured with printer and size)

        // When
        String actualZpl = classUnderTest.toZplString();

        // Then
        assertAll("ZPL content validation",
            () -> assertTrue(actualZpl.startsWith("^XA"),
                String.format("ZPL should start with ^XA%nActual ZPL: %s", actualZpl)),
            () -> assertTrue(actualZpl.endsWith("^XZ"),
                String.format("ZPL should end with ^XZ%nActual ZPL: %s", actualZpl)),
            () -> assertTrue(actualZpl.contains("^PW808"),
                String.format("ZPL should contain print width command%nActual ZPL: %s", actualZpl)),
            () -> assertTrue(actualZpl.contains("^LL1215"),
                String.format("ZPL should contain label length command%nActual ZPL: %s", actualZpl))
        );
    }

    @Test
    @DisplayName("addToLabel adds text element with correct positioning")
    @Tag("element-management")
    void Given_TextElement_When_AddToLabel_Then_PositionsCorrectly()
    {
        // Given & When
        Text.createText()
            .withPosition(FIRST_X_POSITION, FIRST_Y_POSITION)
            .withPlainTextContent(EXAMPLE_TEXT_CONTENT)
            .addToLabel(classUnderTest);

        // Then
        String actualZpl = classUnderTest.toZplString();
        int expectedElementCount = 2; // Default font + text element

        assertAll("Text element validation",
            () -> assertEquals(expectedElementCount, classUnderTest.getElements()
                                                                   .size(),
                String.format("Label should contain two elements%nActual elements: %s",
                    classUnderTest.getElements())),
            () -> assertTrue(actualZpl.contains("^FO10,20"),
                String.format("ZPL should contain correct position commands (^FO10,20)%nActual ZPL: %s",
                    actualZpl)),
            () -> assertTrue(actualZpl.contains("^FDExample Text^FS"),
                String.format("ZPL should contain correct text content (^FDExample Text^FS)%nActual ZPL: %s",
                    actualZpl))
        );
    }

    @Test
    @DisplayName("addToLabel handles multiple text elements correctly")
    @Tag("element-management")
    void Given_MultipleTextElements_When_AddToLabel_Then_PositionsAllCorrectly()
    {
        // Given & When
        Text.createText()
            .withPosition(FIRST_X_POSITION, FIRST_Y_POSITION)
            .withPlainTextContent(FIRST_TEXT_CONTENT)
            .addToLabel(classUnderTest);

        Text.createText()
            .withPosition(SECOND_X_POSITION, SECOND_Y_POSITION)
            .withPlainTextContent(SECOND_TEXT_CONTENT)
            .addToLabel(classUnderTest);

        // Then
        String actualZpl = classUnderTest.toZplString();
        int expectedElementCount = 3; // Default font + 2 text elements

        assertAll("Multiple text elements validation",
            () -> assertEquals(expectedElementCount, classUnderTest.getElements()
                                                                   .size(),
                "Label should contain three elements"),
            () -> assertTrue(actualZpl.contains("^FO10,20"),
                "ZPL should contain first position"),
            () -> assertTrue(actualZpl.contains("^FDFirst Text^FS"),
                "ZPL should contain first text"),
            () -> assertTrue(actualZpl.contains("^FO100,200"),
                "ZPL should contain second position"),
            () -> assertTrue(actualZpl.contains("^FDSecond Text^FS"),
                "ZPL should contain second text")
        );
    }

    @Test
    @DisplayName("addToLabel throws exceptions for invalid text positions")
    @Tag("validation")
    void Given_InvalidPositions_When_AddToLabel_Then_ThrowsExceptions()
    {
        // Given
        double labelWidth = LABEL_4X6.getWidthMm();
        double labelHeight = LABEL_4X6.getHeightMm();

        String expectedNegativeXMessage = "X-axis position cannot be negative: -1.00";
        String expectedNegativeYMessage = "Y-axis position cannot be negative: -1.00";
        String expectedExceedsWidthMessage = "X-axis position (102.60 mm) exceeds label width (101.60 mm). The element must be positioned within the label dimensions.";
        String expectedExceedsHeightMessage = "Y-axis position (153.40 mm) exceeds label height (152.40 mm). The element must be positioned within the label dimensions.";

        // When & Then
        assertAll("Position validation",
            () ->
            {
                IllegalStateException actualException = assertThrows(IllegalStateException.class,
                    () -> Text.createText()
                              .withPosition(-1, 20)
                              .withPlainTextContent("Invalid X")
                              .addToLabel(classUnderTest));
                assertEquals(expectedNegativeXMessage, actualException.getMessage(),
                    "Exception message should match for negative X position");
            },

            () ->
            {
                IllegalStateException actualException = assertThrows(IllegalStateException.class,
                    () -> Text.createText()
                              .withPosition(10, -1)
                              .withPlainTextContent("Invalid Y")
                              .addToLabel(classUnderTest));
                assertEquals(expectedNegativeYMessage, actualException.getMessage(),
                    "Exception message should match for negative Y position");
            },

            () ->
            {
                IllegalStateException actualException = assertThrows(IllegalStateException.class,
                    () -> Text.createText()
                              .withPosition(labelWidth + 1, 20)
                              .withPlainTextContent("X too large")
                              .addToLabel(classUnderTest));
                assertEquals(expectedExceedsWidthMessage, actualException.getMessage(),
                    "Exception message should match for X position beyond width");
            },

            () ->
            {
                IllegalStateException actualException = assertThrows(IllegalStateException.class,
                    () -> Text.createText()
                              .withPosition(10, labelHeight + 1)
                              .withPlainTextContent("Y too large")
                              .addToLabel(classUnderTest));
                assertEquals(expectedExceedsHeightMessage, actualException.getMessage(),
                    "Exception message should match for Y position beyond height");
            }
        );
    }

    @Test
    @DisplayName("validateAndAddElement throws exception for null element")
    @Tag("validation")
    void Given_NullElement_When_ValidateAndAddElement_Then_ThrowsException()
    {
        // Given (null element)

        // When & Then
        IllegalArgumentException actualException = assertThrows(IllegalArgumentException.class,
            () -> classUnderTest.validateAndAddElement(null));

        assertAll("Null element validation",
            () -> assertNotNull(actualException, "Exception should not be null"),
            () -> assertEquals(EXPECTED_NULL_ELEMENT_MESSAGE, actualException.getMessage(),
                "Exception message should match expected message")
        );
    }
}
