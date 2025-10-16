package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BarcodeDefaults (^BY) Tests")
@Tag("unit")
@Tag("barcode")
class BarcodeDefaultsTest
{
    @Test
    @DisplayName("toZplString generates correct ^BY with all parameters")
    void Given_AllParams_When_ToZplString_Then_GeneratesCorrectCommand()
    {
        // Given
        PrintDensity dpi = PrintDensity.DPI_203; // 8 dots/mm
        BarcodeDefaults defaults = BarcodeDefaults.createBarcodeDefaults()
            .withModuleWidthMm(1.125)
            .withWideToNarrowRatio(2.4)
            .withHeightMm(10.0) // 10mm -> 80 dots at 203 dpi
            .build();

        // When
        String zpl = defaults.toZplString(dpi);

        // Then
        assertEquals("^BY9,2.4,80", zpl);
    }

    @Test
    @DisplayName("toZplString includes commas for omitted parameters per generator convention")
    void Given_SomeParamsOmitted_When_ToZplString_Then_EmitsPlaceholders()
    {
        // Given
        PrintDensity dpi = PrintDensity.DPI_203;
        BarcodeDefaults defaults = BarcodeDefaults.createBarcodeDefaults()
            .withModuleWidthMm(0.625)
            .build();

        // When
        String zpl = defaults.toZplString(dpi);

        // Then: generator keeps placeholders for nulls
        assertEquals("^BY5,,", zpl);
    }

    @Test
    @DisplayName("validateInContext enforces parameter ranges when provided")
    void Given_OutOfRangeValues_When_Validate_Then_Throws()
    {
        PrintDensity dpi = PrintDensity.DPI_203;

        // Invalid module width
        BarcodeDefaults invalidW = BarcodeDefaults.createBarcodeDefaults()
            .withModuleWidthMm(0.0)
            .build();
        assertThrows(IllegalStateException.class, () -> invalidW.validateInContext(LabelSize.LABEL_4X6, dpi, null));

        // Invalid ratio
        BarcodeDefaults invalidR = BarcodeDefaults.createBarcodeDefaults()
            .withWideToNarrowRatio(1.9)
            .build();
        assertThrows(IllegalStateException.class, () -> invalidR.validateInContext(LabelSize.LABEL_4X6, dpi, null));

        // Invalid height: convert to dots and exceed 32000
        BarcodeDefaults invalidH = BarcodeDefaults.createBarcodeDefaults()
            .withHeightMm(5000.0)
            .build();
        assertThrows(IllegalStateException.class, () -> invalidH.validateInContext(LabelSize.LABEL_4X6, dpi, null));
    }

    @Test
    @DisplayName("BarcodeDefaults can be added to label and appears in output")
    void Given_BarcodeDefaults_When_AddedToLabel_Then_IncludedInZpl()
    {
        // Given
        PrinterConfiguration printer = PrinterConfiguration.createPrinterConfiguration()
            .forDpi(PrintDensity.DPI_203)
            .forLoadedMedia(LoadedMedia.fromLabelSize(LabelSize.LABEL_4X6))
            .build();
        ZebraLabel label = ZebraLabel.createLabel()
            .forSize(LabelSize.LABEL_4X6)
            .forPrinter(printer)
            .build();

        BarcodeDefaults defaults = BarcodeDefaults.createBarcodeDefaults()
            .withModuleWidthMm(0.25)
            .withWideToNarrowRatio(3.0)
            .build();

        // When/Then
        assertDoesNotThrow(() -> label.validateAndAddElement(defaults));
        String zpl = label.toZplString();
        assertTrue(zpl.contains("^BY2,3.0"));
    }

    @Test
    @DisplayName("validateInContext enforces 0.1 increment for ratio")
    void Given_RatioNotPointOneIncrement_When_Validate_Then_Throws()
    {
        PrintDensity dpi = PrintDensity.DPI_203;
        BarcodeDefaults invalid = BarcodeDefaults.createBarcodeDefaults()
            .withWideToNarrowRatio(2.05)
            .build();
        assertThrows(IllegalStateException.class,
            () -> invalid.validateInContext(LabelSize.LABEL_4X6, dpi, null));
    }

    @Test
    @DisplayName("validateInContext allows ratio in 0.1 increments")
    void Given_RatioAtPointOneIncrement_When_Validate_Then_DoesNotThrow()
    {
        PrintDensity dpi = PrintDensity.DPI_203;
        double[] validValues = {2.0, 2.1, 2.5, 3.0};
        for (double r : validValues) {
            BarcodeDefaults bd = BarcodeDefaults.createBarcodeDefaults()
                .withWideToNarrowRatio(r)
                .build();
            assertDoesNotThrow(() -> bd.validateInContext(LabelSize.LABEL_4X6, dpi, null));
        }
    }

    @Test
    @DisplayName("validateInContext rejects height that exceeds label height")
    void Given_HeightExceedsLabel_When_Validate_Then_Throws()
    {
        PrintDensity dpi = PrintDensity.DPI_203;
        int maxDots = LabelSize.LABEL_4X6.getHeightInDots(dpi);
        double mmJustOver = dpi.toMillimetres(maxDots + 1);
        BarcodeDefaults tooTall = BarcodeDefaults.createBarcodeDefaults()
            .withHeightMm(mmJustOver)
            .build();
        assertThrows(IllegalStateException.class,
            () -> tooTall.validateInContext(LabelSize.LABEL_4X6, dpi, null));
    }

    @Test
    @DisplayName("validateInContext allows height equal to label height")
    void Given_HeightEqualsLabel_When_Validate_Then_DoesNotThrow()
    {
        PrintDensity dpi = PrintDensity.DPI_203;
        int maxDots = LabelSize.LABEL_4X6.getHeightInDots(dpi);
        double mmAtMax = dpi.toMillimetres(maxDots);
        BarcodeDefaults okTall = BarcodeDefaults.createBarcodeDefaults()
            .withHeightMm(mmAtMax)
            .build();
        assertDoesNotThrow(() -> okTall.validateInContext(LabelSize.LABEL_4X6, dpi, null));
    }
}
