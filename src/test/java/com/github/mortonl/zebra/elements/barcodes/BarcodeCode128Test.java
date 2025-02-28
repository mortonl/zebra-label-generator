package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Code 128 Barcode Tests")
class BarcodeCode128Test
{
    private static final LabelSize DEFAULT_SIZE = LabelSize.LABEL_4X6; // 101.6mm x 152.4mm
    private static final PrintDensity DEFAULT_DPI = PrintDensity.DPI_203; // 8 dots per mm
    private static final float VALID_HEIGHT_MM = 10.0f;

    private static Stream<Arguments> validBarcodeParameters()
    {
        return Stream.of(
            Arguments.of(10.0f, Orientation.NORMAL, true, false, false,
                "^BCN,80,Y,N,N"),
            Arguments.of(15.0f, Orientation.ROTATED, false, true, true,
                "^BCR,120,N,Y,Y"),
            Arguments.of(20.0f, Orientation.INVERTED, true, true, false,
                "^BCI,160,Y,Y,N")
        );
    }

    private static Stream<Arguments> validHeightRanges()
    {
        return Stream.of(
            Arguments.of(PrintDensity.DPI_203, 10.0f),
            Arguments.of(PrintDensity.DPI_300, 15.0f),
            Arguments.of(PrintDensity.DPI_600, 20.0f)
        );
    }

    @Test
    @DisplayName("Should create barcode with default values")
    void shouldCreateBarcodeWithDefaultValues()
    {
        BarcodeCode128 barcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(VALID_HEIGHT_MM)
            .withOrientation(Orientation.NORMAL)
            .build();

        assertAll(
            () -> assertEquals(VALID_HEIGHT_MM, barcode.getHeightMm()),
            () -> assertEquals(Orientation.NORMAL, barcode.getOrientation()),
            () -> assertFalse(barcode.isUccCheckDigitEnabled()),
            () -> assertFalse(barcode.isPrintInterpretationLineDesired()),
            () -> assertFalse(barcode.isPrintInterpretationLineAboveDesired())
        );
    }

    @ParameterizedTest
    @MethodSource("validBarcodeParameters")
    @DisplayName("Should generate correct ZPL command for different configurations")
    void shouldGenerateCorrectZplCommand(
        float heightMm, Orientation orientation,
        boolean interpretationLine, boolean interpretationLineAbove,
        boolean uccCheckDigit, String expected
    )
    {
        BarcodeCode128 barcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(heightMm)
            .withOrientation(orientation)
            .withPrintInterpretationLineDesired(interpretationLine)
            .withPrintInterpretationLineAboveDesired(interpretationLineAbove)
            .withUccCheckDigitEnabled(uccCheckDigit)
            .build();

        String zplCommand = barcode.toZplString(DEFAULT_DPI);
        assertEquals(expected, zplCommand);
    }

    @ParameterizedTest
    @ValueSource(floats = {0.0f, -1.0f, 4500.0f})
    @DisplayName("Should throw exception for invalid heights")
    void shouldThrowExceptionForInvalidHeights(float invalidHeight)
    {
        BarcodeCode128 barcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(invalidHeight)
            .withOrientation(Orientation.NORMAL)
            .build();

        assertThrows(IllegalStateException.class,
            () -> barcode.validateInContext(DEFAULT_SIZE, DEFAULT_DPI));
    }

    @Test
    @DisplayName("Should throw exception when orientation is null")
    void shouldThrowExceptionWhenOrientationIsNull()
    {
        BarcodeCode128 barcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(VALID_HEIGHT_MM)
            .withOrientation(null)
            .build();

        assertThrows(IllegalStateException.class,
            () -> barcode.validateInContext(DEFAULT_SIZE, DEFAULT_DPI));
    }

    @ParameterizedTest
    @MethodSource("validHeightRanges")
    @DisplayName("Should accept valid heights for different DPIs")
    void shouldAcceptValidHeightsForDifferentDpis(PrintDensity dpi, float heightMm)
    {
        BarcodeCode128 barcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(heightMm)
            .withOrientation(Orientation.NORMAL)
            .withPlainTextContent("123456")
            .build();

        assertDoesNotThrow(() -> barcode.validateInContext(DEFAULT_SIZE, dpi));
    }
}
