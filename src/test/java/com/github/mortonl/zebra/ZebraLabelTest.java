package com.github.mortonl.zebra;

import com.github.mortonl.zebra.elements.text.Text;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("Zebra Label Tests")
class ZebraLabelTest
{
    private ZebraLabel label;

    @BeforeEach
    void setUp()
    {
        PrinterConfiguration printerConfiguration = PrinterConfiguration
                .createPrinterConfiguration()
                .forDpi(PrintDensity.DPI_203)
                .forLoadedMedia(LoadedMedia.fromLabelSize(LabelSize.LABEL_4X6))
                .build();

        label = ZebraLabel.createLabel().forPrinter(printerConfiguration)
                          .forSize(LabelSize.LABEL_4X6).build();
    }

    @Test
    @DisplayName("Generate ZPL should include all components")
    void testGenerateZpl()
    {
        String zpl = label.toZplString();

        assertAll("ZPL content validation",
                () -> assertTrue(zpl.startsWith("^XA"),
                        String.format("ZPL should start with ^XA%nActual ZPL: %s", zpl)),
                () -> assertTrue(zpl.endsWith("^XZ"),
                        String.format("ZPL should end with ^XZ%nActual ZPL: %s", zpl)),
                () -> assertTrue(zpl.contains("^PW812"),
                        String.format("ZPL should contain print width command%nActual ZPL: %s", zpl)),
                () -> assertTrue(zpl.contains("^LL1219"),
                        String.format("ZPL should contain label length command%nActual ZPL: %s", zpl))
        );
    }

    @Test
    @DisplayName("Add text element using builder should position correctly")
    void testAddTextElement()
    {
        Text.createText()
            .withPosition(1.25, 2.5)
            .withPlainTextContent("Example Text")
            .addToLabel(label);

        String zpl = label.toZplString();

        assertAll("Text element validation",
                () -> assertEquals(1, label.getElements().size(),
                    String.format("Label should contain one element%nActual elements: %s",
                        label.getElements())),
                () -> assertTrue(zpl.contains("^FO10,20"),
                    String.format("ZPL should contain correct position commands (^FO10,20)%nActual ZPL: %s",
                        zpl)),
                () -> assertTrue(zpl.contains("^FDExample Text^FS"),
                    String.format("ZPL should contain correct text content (^FDExample Text^FS)%nActual ZPL: %s",
                        zpl))
        );
    }

    @Test
    @DisplayName("Multiple text elements should be positioned correctly")
    void testMultipleTextElements()
    {
        Text.createText()
            .withPosition(1.25, 2.5)
            .withPlainTextContent("First Text")
            .addToLabel(label);

        Text.createText()
            .withPosition(12.5, 25)
            .withPlainTextContent("Second Text")
            .addToLabel(label);

        String zpl = label.toZplString();

        assertAll("Multiple text elements validation",
                () -> assertEquals(2, label.getElements().size(),
                        "Label should contain two elements"),
                () -> assertTrue(zpl.contains("^FO10,20"),
                        "ZPL should contain first position"),
                () -> assertTrue(zpl.contains("^FDFirst Text^FS"),
                        "ZPL should contain first text"),
                () -> assertTrue(zpl.contains("^FO100,200"),
                        "ZPL should contain second position"),
                () -> assertTrue(zpl.contains("^FDSecond Text^FS"),
                        "ZPL should contain second text")
        );
    }

    @Test
    @DisplayName("Text element with invalid position should throw IllegalStateException with correct messages")
    void testInvalidTextPosition()
    {
        int labelWidth = LabelSize.LABEL_4X6.getWidthInDots(PrintDensity.DPI_203);
        int labelHeight = LabelSize.LABEL_4X6.getHeightInDots(PrintDensity.DPI_203);

        assertAll("Position validation",
                () -> {
                    IllegalStateException exception = assertThrows(IllegalStateException.class,
                            () -> Text.createText()
                                      .withPosition(-1, 20)
                                      .withPlainTextContent("Invalid X")
                                      .addToLabel(label));
                    assertEquals("X-axis position cannot be negative: -1.00",
                            exception.getMessage(),
                            "Exception message should match for negative X position");
                },

                () -> {
                    IllegalStateException exception = assertThrows(IllegalStateException.class,
                            () -> Text.createText()
                                      .withPosition(10, -1)
                                      .withPlainTextContent("Invalid Y")
                                      .addToLabel(label));
                    assertEquals("Y-axis position cannot be negative: -1.00",
                            exception.getMessage(),
                            "Exception message should match for negative Y position");
                },

                () -> {
                    IllegalStateException exception = assertThrows(IllegalStateException.class,
                            () -> Text.createText()
                                      .withPosition(labelWidth + 1, 20)
                                      .withPlainTextContent("X too large")
                                      .addToLabel(label));
                    assertEquals("X-axis position (813.00 mm) exceeds label width (101.60 mm). The element must be positioned within the label dimensions.",
                            exception.getMessage(),
                            "Exception message should match for X position beyond width");
                },

                () -> {
                    IllegalStateException exception = assertThrows(IllegalStateException.class,
                            () -> Text.createText()
                                      .withPosition(10, labelHeight + 1)
                                      .withPlainTextContent("Y too large")
                                      .addToLabel(label));
                    assertEquals("Y-axis position (1220.00 mm) exceeds label height (152.40 mm). The element must be positioned within the label dimensions.",
                            exception.getMessage(),
                            "Exception message should match for Y position beyond height");
                }
        );
    }

    @Test
    @DisplayName("Adding null element should throw IllegalArgumentException with correct message")
    void testAddNullElement()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> label.validateAndAddElement(null));

        assertAll("Null element validation",
                () -> assertNotNull(exception,
                        "Exception should not be null"),
                () -> assertEquals("Cannot add null elements to Label", exception.getMessage(),
                        "Exception message should match expected message")
        );
    }
}
