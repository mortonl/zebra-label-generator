package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.elements.barcodes.interleaved_2OF5.BarcodeInterleaved2of5;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.formatting.Orientation.NORMAL;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("BarcodeInterleaved2of5 barcode creation and validation")
@Tag("unit")
@Tag("barcode")
class BarcodeInterleaved2of5Test
{
    private static final double VALID_HEIGHT_MM = 10.0;

    private static final String VALID_EVEN_DATA = "1234";

    private static final String VALID_ODD_DATA = "12345";

    private static final String EXPECTED_NULL_ORIENTATION_MESSAGE = "Orientation cannot be null";

    private static final String EXPECTED_ZPL_OUTPUT = "^FO0,0^B2N,80,Y,N,N^FD1234^FS";

    private BarcodeInterleaved2of5 classUnderTest;

    private static Stream<Arguments> validLengthCasesForValidateInContext()
    {
        return Stream.of(
            Arguments.of("1234", false),  // even length, no check digit
            Arguments.of("12345", true)   // odd length, with check digit
        );
    }

    @BeforeEach
    void setUp()
    {
        classUnderTest = BarcodeInterleaved2of5
            .createInterleaved2of5Barcode()
            .withPlainTextContent(VALID_EVEN_DATA)
            .withOrientation(NORMAL)
            .withHeightMm(VALID_HEIGHT_MM)
            .build();
    }

    @Test
    @DisplayName("validateInContext throws exception when orientation is null")
    @Tag("validation")
    void Given_NullOrientation_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        BarcodeInterleaved2of5 nullOrientationBarcode = BarcodeInterleaved2of5
            .createInterleaved2of5Barcode()
            .withPlainTextContent(VALID_EVEN_DATA)
            .withHeightMm(VALID_HEIGHT_MM)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> nullOrientationBarcode.validateInContext(LABEL_4X6, DPI_203, null));
        assertEquals(EXPECTED_NULL_ORIENTATION_MESSAGE, actualException.getMessage());
    }

    @ParameterizedTest(name = "validateInContext throws exception for height {0}mm")
    @ValueSource(doubles = {-1.0,
        0.0,
        200.0})
    @DisplayName("validateInContext throws exception for invalid heights")
    @Tag("validation")
    void Given_InvalidHeight_When_ValidateInContext_Then_ThrowsException(double invalidHeight)
    {
        // Given
        BarcodeInterleaved2of5 invalidHeightBarcode = BarcodeInterleaved2of5
            .createInterleaved2of5Barcode()
            .withPlainTextContent(VALID_EVEN_DATA)
            .withOrientation(NORMAL)
            .withHeightMm(invalidHeight)
            .build();

        // When & Then
        assertThrows(IllegalStateException.class,
            () -> invalidHeightBarcode.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext throws exception for data '{0}'")
    @ValueSource(strings = {"12A34",
        "123",
        "12.34",
        ""})
    @DisplayName("validateInContext throws exception for invalid data")
    @Tag("validation")
    void Given_InvalidData_When_ValidateInContext_Then_ThrowsException(String invalidData)
    {
        // Given
        BarcodeInterleaved2of5 invalidDataBarcode = BarcodeInterleaved2of5
            .createInterleaved2of5Barcode()
            .withPlainTextContent(invalidData)
            .withOrientation(NORMAL)
            .withHeightMm(VALID_HEIGHT_MM)
            .build();

        // When & Then
        assertThrows(IllegalStateException.class,
            () -> invalidDataBarcode.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext accepts data length {0} with checkDigit={1}")
    @MethodSource("validLengthCasesForValidateInContext")
    @DisplayName("validateInContext accepts valid data length based on check digit")
    @Tag("validation")
    void Given_ValidLength_When_ValidateInContext_Then_NoException(String data, boolean useCheckDigit)
    {
        // Given
        BarcodeInterleaved2of5 validLengthBarcode = BarcodeInterleaved2of5
            .createInterleaved2of5Barcode()
            .withPlainTextContent(data)
            .withOrientation(NORMAL)
            .withHeightMm(VALID_HEIGHT_MM)
            .withCalculateAndPrintMod10CheckDigit(useCheckDigit)
            .build();

        // When & Then
        assertDoesNotThrow(() -> validLengthBarcode.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @Test
    @DisplayName("toZplString generates correct ZPL command")
    @Tag("zpl-generation")
    void Given_ConfiguredBarcode_When_ToZplString_Then_GeneratesCorrectZpl()
    {
        // Given
        BarcodeInterleaved2of5 configuredBarcode = BarcodeInterleaved2of5
            .createInterleaved2of5Barcode()
            .withPlainTextContent(VALID_EVEN_DATA)
            .withOrientation(NORMAL)
            .withHeightMm(VALID_HEIGHT_MM)
            .withPrintInterpretationLine(true)
            .withPrintInterpretationLineAbove(false)
            .withCalculateAndPrintMod10CheckDigit(false)
            .build();

        // When
        String actualZplString = configuredBarcode.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_ZPL_OUTPUT, actualZplString);
    }
}
