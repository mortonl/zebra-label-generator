package com.github.mortonl.label_tests;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.elements.graphics.CompressionType;
import com.github.mortonl.zebra.elements.graphics.GraphicBox;
import com.github.mortonl.zebra.elements.graphics.GraphicField;
import com.github.mortonl.zebra.elements.text.Text;
import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.label_settings.InternationalCharacterSet;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_300;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("FullLabel Tests")
@Tag("integration")
class BasicFullLabelTest
{
    private static final String EXPECTED_DPI_203_ZPL = """
        ^XA
        ^PW808
        ^LL1215
        ^CI28
        ^FO300,0^GB220,80,80,,0^FS
        ^FO800,800^GFA,11,8000,80,ABCDEFabcdef0123456789^FS
        ^CF0,80,80
        ^FO0,0^FDLarger^FS
        ^CF0,48,48
        ^FO0,0^FDSmaller^FS
        ^XZ""";

    private static final String EXPECTED_DPI_300_ZPL = """
        ^XA
        ^PW1215
        ^LL1824
        ^CI28
        ^FO450,0^GB330,120,120,,0^FS
        ^FO1200,1200^GFA,11,8000,80,ABCDEFabcdef0123456789^FS
        ^CF0,120,120
        ^FO0,0^FDLarger^FS
        ^CF0,72,72
        ^FO0,0^FDSmaller^FS
        ^XZ""";

    private static final String TEST_GRAPHIC_DATA = "ABCDEFabcdef0123456789";

    private static final double BOX_X_POSITION = 37.5;

    private static final double BOX_Y_POSITION = 0.0;

    private static final double BOX_WIDTH = 27.5;

    private static final double BOX_HEIGHT = 10.0;

    private static final double BOX_THICKNESS = 10.0;

    private static final double FIELD_X_POSITION = 100.0;

    private static final double FIELD_Y_POSITION = 100.0;

    private static final int BINARY_BYTE_COUNT = 11;

    private static final int GRAPHIC_FIELD_COUNT = 8000;

    private static final int BYTES_PER_ROW = 80;

    @Test
    @DisplayName("Given configured label with elements, when generating ZPL, then returns correct output")
    @Tag("zpl-generation")
    void givenConfiguredLabelWithElements_whenGeneratingZpl_thenReturnsCorrectOutput()
    {
        // Given
        PrinterConfiguration givenPrinterConfiguration = PrinterConfiguration
            .createPrinterConfiguration()
            .forDpi(DPI_203)
            .forLoadedMedia(LoadedMedia.fromLabelSize(LABEL_4X6))
            .build();

        InternationalCharacterSet givenCharacterSet = InternationalCharacterSet.createInternationalCharacterSet()
                                                                               .withEncoding(FontEncoding.UTF_8)
                                                                               .build();

        ZebraLabel givenLabel = ZebraLabel
            .createLabel()
            .forSize(LABEL_4X6)
            .forPrinter(givenPrinterConfiguration)
            .forInternationalCharacterSet(givenCharacterSet)
            .build();

        GraphicBox.createGraphicBox()
                  .withPosition(BOX_X_POSITION, BOX_Y_POSITION)
                  .withSize(BOX_WIDTH, BOX_HEIGHT)
                  .withThicknessMm(BOX_THICKNESS)
                  .withRoundness(0)
                  .addToLabel(givenLabel);

        GraphicField.createGraphicField()
                    .withPosition(FIELD_X_POSITION, FIELD_Y_POSITION)
                    .withCompressionType(CompressionType.ASCII_HEX)
                    .withBinaryByteCount(BINARY_BYTE_COUNT)
                    .withGraphicFieldCount(GRAPHIC_FIELD_COUNT)
                    .withBytesPerRow(BYTES_PER_ROW)
                    .withData(TEST_GRAPHIC_DATA)
                    .addToLabel(givenLabel);

        DefaultFont.createDefaultFont()
                   .withFontDesignation('0')
                   .withSize(10, 10)
                   .addToLabel(givenLabel);

        Text.createText()
            .withPosition(0, 0)
            .withPlainTextContent("Larger")
            .addToLabel(givenLabel);

        DefaultFont.createDefaultFont()
                   .withFontDesignation('0')
                   .withSize(6, 6)
                   .addToLabel(givenLabel);

        Text.createText()
            .withPosition(0, 0)
            .withPlainTextContent("Smaller")
            .addToLabel(givenLabel);

        // When
        String actualDpi203Zpl = givenLabel.toZplString(DPI_203);
        String actualDpi300Zpl = givenLabel.toZplString(DPI_300);

        // Then
        assertEquals(EXPECTED_DPI_203_ZPL, actualDpi203Zpl);
        assertEquals(EXPECTED_DPI_300_ZPL, actualDpi300Zpl);
    }
}
