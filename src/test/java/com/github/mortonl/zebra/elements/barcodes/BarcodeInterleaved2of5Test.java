package com.github.mortonl.zebra.elements.barcodes;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Interleaved 2 of 5 Barcode Tests")
class BarcodeInterleaved2of5Test
{
    private static final double MIN_HEIGHT_MM = 1.0 / PrintDensity.getMaxDotsPerMillimetre();
    private static final double MAX_HEIGHT_MM = 32000.0 / PrintDensity.getMinDotsPerMillimetre();

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests
    {
        private static Stream<Arguments> provideLengthTestCases()
        {
            return Stream.of(
                Arguments.of("1234", false),  // even length, no check digit
                Arguments.of("12345", true)   // odd length, with check digit
            );
        }

        @Test
        @DisplayName("Should throw exception when orientation is null")
        void shouldThrowExceptionWhenOrientationIsNull()
        {
            BarcodeInterleaved2of5 barcode = BarcodeInterleaved2of5
                .createInterleaved2of5Barcode()
                .withPlainTextContent("1234")
                .withHeightMm(10)
                .build();

            IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
            assertEquals("Orientation cannot be null", exception.getMessage());
        }

        @ParameterizedTest(name = "Height {0}mm should throw exception")
        @ValueSource(doubles = {-1.0, 0.0, 200.0})
        @DisplayName("Should throw exception for invalid heights")
        void shouldThrowExceptionForInvalidHeights(double height)
        {
            BarcodeInterleaved2of5 barcode = BarcodeInterleaved2of5
                .createInterleaved2of5Barcode()
                .withPlainTextContent("1234")
                .withOrientation(Orientation.NORMAL)
                .withHeightMm(height)
                .build();

            assertThrows(IllegalStateException.class,
                () -> barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        }

        @ParameterizedTest(name = "Data '{0}' should throw exception")
        @ValueSource(strings = {"12A34", "123", "12.34", ""})
        @DisplayName("Should throw exception for invalid data")
        void shouldThrowExceptionForInvalidData(String data)
        {
            BarcodeInterleaved2of5 barcode = BarcodeInterleaved2of5
                .createInterleaved2of5Barcode()
                .withPlainTextContent(data)
                .withOrientation(Orientation.NORMAL)
                .withHeightMm(10)
                .build();

            assertThrows(IllegalStateException.class,
                () -> barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        }

        @ParameterizedTest(name = "Data length {0} with checkDigit={1}")
        @MethodSource("provideLengthTestCases")
        @DisplayName("Should validate data length based on check digit setting")
        void shouldValidateDataLengthBasedOnCheckDigit(String data, boolean useCheckDigit)
        {
            BarcodeInterleaved2of5 barcode = BarcodeInterleaved2of5
                .createInterleaved2of5Barcode()
                .withPlainTextContent(data)
                .withOrientation(Orientation.NORMAL)
                .withHeightMm(10)
                .withCalculateAndPrintMod10CheckDigit(useCheckDigit)
                .build();

            assertDoesNotThrow(() -> barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        }
    }

    @Nested
    @DisplayName("ZPL Generation Tests")
    class ZplGenerationTests
    {
        @Test
        @DisplayName("Should generate correct ZPL string")
        void shouldGenerateCorrectZplString()
        {
            BarcodeInterleaved2of5 barcode = BarcodeInterleaved2of5
                .createInterleaved2of5Barcode()
                .withPlainTextContent("1234")
                .withOrientation(Orientation.NORMAL)
                .withHeightMm(10)
                .withPrintInterpretationLine(true)
                .withPrintInterpretationLineAbove(false)
                .withCalculateAndPrintMod10CheckDigit(false)
                .build();

            String expected = "^FO0,0^B2N,80,Y,N,N^FD1234^FS";

            assertEquals(expected, barcode.toZplString(PrintDensity.DPI_203));
        }
    }
}
