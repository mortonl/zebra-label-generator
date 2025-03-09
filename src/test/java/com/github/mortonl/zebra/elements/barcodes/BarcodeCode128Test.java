package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.elements.barcodes.code_128.Code128Mode;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Code 128 Barcode Tests")
class BarcodeCode128Test
{
    private static final double VALID_HEIGHT_MM = 10.0;

    @Nested
    @DisplayName("Basic Barcode Creation Tests")
    class BasicBarcodeCreationTests
    {
        @Test
        @DisplayName("Should create barcode with all values set")
        void shouldCreateBarcodeWithAllValuesSet()
        {
            BarcodeCode128 barcode = BarcodeCode128
                .createCode128Barcode()
                .withHeightMm(VALID_HEIGHT_MM)
                .withOrientation(Orientation.NORMAL)
                .withUccCheckDigitEnabled(true)
                .withPrintInterpretationLine(true)
                .withPrintInterpretationLineAbove(true)
                .withMode(Code128Mode.UCC_CASE)
                .withPlainTextContent("123456")
                .build();

            assertAll(
                () -> assertEquals(VALID_HEIGHT_MM, barcode.getHeightMm()),
                () -> assertEquals(Orientation.NORMAL, barcode.getOrientation()),
                () -> assertTrue(barcode.getUccCheckDigitEnabled()),
                () -> assertTrue(barcode.getPrintInterpretationLine()),
                () -> assertTrue(barcode.getPrintInterpretationLineAbove()),
                () -> assertEquals(Code128Mode.UCC_CASE, barcode.getMode()),
                () -> assertEquals("123456", barcode.getContent().getData())
            );
        }
    }

    @Nested
    @DisplayName("ZPL Command Generation Tests")
    class ZplCommandGenerationTests
    {
        private static Stream<Arguments> validBarcodeParameters()
        {
            return Stream.of(
                Arguments.of(10.0, Orientation.NORMAL, true, false, false,
                    "^FO0,0^BCN,80,Y,N,N,A^FD123456789^FS"),
                Arguments.of(15.0, Orientation.ROTATED, false, true, true,
                    "^FO0,0^BCR,120,N,Y,Y,A^FD123456789^FS"),
                Arguments.of(20.0, Orientation.INVERTED, true, true, false,
                    "^FO0,0^BCI,160,Y,Y,N,A^FD123456789^FS")
            );
        }

        @ParameterizedTest
        @MethodSource("validBarcodeParameters")
        @DisplayName("Should generate correct ZPL command for different configurations")
        void shouldGenerateCorrectZplCommand(
            double heightMm, Orientation orientation,
            boolean interpretationLine, boolean interpretationLineAbove,
            boolean uccCheckDigit, String expected
        )
        {
            BarcodeCode128 barcode = BarcodeCode128
                .createCode128Barcode()
                .withHeightMm(heightMm)
                .withOrientation(orientation)
                .withPrintInterpretationLine(interpretationLine)
                .withPrintInterpretationLineAbove(interpretationLineAbove)
                .withUccCheckDigitEnabled(uccCheckDigit)
                .withPlainTextContent("123456789")
                .withMode(Code128Mode.AUTOMATIC)
                .build();

            String zplCommand = barcode.toZplString(PrintDensity.DPI_203);
            assertEquals(expected, zplCommand);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests
    {
        private static Stream<Arguments> validHeightRanges()
        {
            return Stream.of(
                Arguments.of(PrintDensity.DPI_203, 10.0),
                Arguments.of(PrintDensity.DPI_300, 15.0),
                Arguments.of(PrintDensity.DPI_600, 20.0)
            );
        }

        private static Stream<Arguments> invalidUccCaseData()
        {
            return Stream.of(
                Arguments.of("12345678901234567890", "UCC Case Mode cannot handle more than 19 digits"),
                Arguments.of(null, "Data cannot be null when using UCC Case Mode")
            );
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, -1.0, 5334.0})
        @DisplayName("Should throw exception for invalid heights")
        void shouldThrowExceptionForInvalidHeights(double invalidHeight)
        {
            BarcodeCode128 barcode = BarcodeCode128
                .createCode128Barcode()
                .withHeightMm(invalidHeight)
                .withOrientation(Orientation.NORMAL)
                .withPlainTextContent("123456")
                .build();

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
            assertTrue(exception.getMessage().contains("height"));
        }

        @ParameterizedTest
        @MethodSource("validHeightRanges")
        @DisplayName("Should accept valid heights for different DPIs")
        void shouldAcceptValidHeightsForDifferentDpis(PrintDensity dpi, double heightMm)
        {
            BarcodeCode128 barcode = BarcodeCode128
                .createCode128Barcode()
                .withHeightMm(heightMm)
                .withOrientation(Orientation.NORMAL)
                .withPlainTextContent("123456")
                .build();

            assertDoesNotThrow(() -> barcode.validateInContext(LabelSize.LABEL_4X6, dpi));
        }

        @ParameterizedTest
        @MethodSource("invalidUccCaseData")
        @DisplayName("Should throw exception for invalid UCC CASE mode data")
        void shouldThrowExceptionForInvalidUccCaseData(String data, String expectedError)
        {
            BarcodeCode128 barcode = BarcodeCode128
                .createCode128Barcode()
                .withHeightMm(VALID_HEIGHT_MM)
                .withOrientation(Orientation.NORMAL)
                .withMode(Code128Mode.UCC_CASE)
                .withPlainTextContent(data)
                .build();

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
            assertEquals(expectedError, exception.getMessage());
        }
    }
}
