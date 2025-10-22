package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.elements.barcodes.code_128.BarcodeCode128;
import com.github.mortonl.zebra.elements.barcodes.code_128.Code128Mode;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.elements.barcodes.code_128.Code128Mode.AUTOMATIC;
import static com.github.mortonl.zebra.elements.barcodes.code_128.Code128Mode.UCC_CASE;
import static com.github.mortonl.zebra.formatting.Orientation.INVERTED;
import static com.github.mortonl.zebra.formatting.Orientation.NORMAL;
import static com.github.mortonl.zebra.formatting.Orientation.ROTATED;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_300;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_600;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("BarcodeCode128 barcode creation and validation")
@Tag("unit")
@Tag("barcode")
class BarcodeCode128Test
{
    private static final double VALID_HEIGHT_MM = 10.0;

    private static final String TEST_CONTENT = "123456";

    private static final String LONG_TEST_CONTENT = "123456789";

    private static Stream<Arguments> validParametersForToZplString()
    {
        return Stream.of(
            Arguments.of(10.0, NORMAL, true, false, false,
                "^FO0,0^BCN,80,Y,N,N,A^FD123456789^FS"),
            Arguments.of(15.0, ROTATED, false, true, true,
                "^FO0,0^BCR,120,N,Y,Y,A^FD123456789^FS"),
            Arguments.of(20.0, INVERTED, true, true, false,
                "^FO0,0^BCI,160,Y,Y,N,A^FD123456789^FS")
        );
    }

    private static Stream<Arguments> validHeightRangesForValidateInContext()
    {
        return Stream.of(
            Arguments.of(DPI_203, 10.0),
            Arguments.of(DPI_300, 15.0),
            Arguments.of(DPI_600, 20.0)
        );
    }

    private static Stream<Arguments> invalidUccCaseDataForValidateInContext()
    {
        return Stream.of(
            Arguments.of("12345678901234567890", "UCC Case Mode cannot handle more than 19 digits"),
            Arguments.of(null, "Data cannot be null when using UCC Case Mode")
        );
    }

    @Test
    @DisplayName("build creates barcode with all properties set correctly")
    @Tag("builder")
    void Given_AllProperties_When_Build_Then_CreatesCorrectBarcode()
    {
        // Given
        double expectedHeight = VALID_HEIGHT_MM;
        Orientation expectedOrientation = NORMAL;
        boolean expectedUccCheckDigit = true;
        boolean expectedInterpretationLine = true;
        boolean expectedInterpretationLineAbove = true;
        Code128Mode expectedMode = UCC_CASE;
        String expectedContent = TEST_CONTENT;

        // When
        BarcodeCode128 actualBarcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(expectedHeight)
            .withOrientation(expectedOrientation)
            .withUccCheckDigitEnabled(expectedUccCheckDigit)
            .withPrintInterpretationLine(expectedInterpretationLine)
            .withPrintInterpretationLineAbove(expectedInterpretationLineAbove)
            .withMode(expectedMode)
            .withPlainTextContent(expectedContent)
            .build();

        // Then
        assertAll(
            () -> assertEquals(expectedHeight, actualBarcode.getHeightMm()),
            () -> assertEquals(expectedOrientation, actualBarcode.getOrientation()),
            () -> assertTrue(actualBarcode.getUccCheckDigitEnabled()),
            () -> assertTrue(actualBarcode.getPrintInterpretationLine()),
            () -> assertTrue(actualBarcode.getPrintInterpretationLineAbove()),
            () -> assertEquals(expectedMode, actualBarcode.getMode()),
            () -> assertEquals(expectedContent, actualBarcode.getContent()
                                                             .getData())
        );
    }

    @ParameterizedTest
    @MethodSource("validParametersForToZplString")
    @DisplayName("toZplString generates correct ZPL for configurations")
    @Tag("zpl-generation")
    void Given_Config_When_ToZplString_Then_GeneratesCorrectZpl(
        double heightMm, Orientation orientation,
        boolean interpretationLine, boolean interpretationLineAbove,
        boolean uccCheckDigit, String expectedZpl
    )
    {
        // Given
        BarcodeCode128 configuredBarcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(heightMm)
            .withOrientation(orientation)
            .withPrintInterpretationLine(interpretationLine)
            .withPrintInterpretationLineAbove(interpretationLineAbove)
            .withUccCheckDigitEnabled(uccCheckDigit)
            .withPlainTextContent(LONG_TEST_CONTENT)
            .withMode(AUTOMATIC)
            .build();

        // When
        String actualZplCommand = configuredBarcode.toZplString(DPI_203);

        // Then
        assertEquals(expectedZpl, actualZplCommand);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0,
        -1.0,
        5334.0})
    @DisplayName("validateInContext throws exception for invalid heights")
    @Tag("validation")
    void Given_InvalidHeight_When_ValidateInContext_Then_ThrowsException(double invalidHeight)
    {
        // Given
        BarcodeCode128 invalidBarcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(invalidHeight)
            .withOrientation(NORMAL)
            .withPlainTextContent(TEST_CONTENT)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> invalidBarcode.validateInContext(LABEL_4X6, DPI_203, null));
        assertTrue(actualException.getMessage()
                                  .contains("height"));
    }

    @ParameterizedTest
    @MethodSource("validHeightRangesForValidateInContext")
    @DisplayName("validateInContext accepts valid heights for different DPIs")
    @Tag("validation")
    void Given_ValidHeight_When_ValidateInContext_Then_NoException(PrintDensity dpi, double heightMm)
    {
        // Given
        BarcodeCode128 validBarcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(heightMm)
            .withOrientation(NORMAL)
            .withPlainTextContent(TEST_CONTENT)
            .build();

        // When & Then
        assertDoesNotThrow(() -> validBarcode.validateInContext(LABEL_4X6, dpi, null));
    }

    @ParameterizedTest
    @MethodSource("invalidUccCaseDataForValidateInContext")
    @DisplayName("validateInContext throws exception for invalid UCC CASE data")
    @Tag("validation")
    void Given_InvalidUccData_When_ValidateInContext_Then_ThrowsException(String data, String expectedError)
    {
        // Given
        BarcodeCode128 invalidUccBarcode = BarcodeCode128
            .createCode128Barcode()
            .withHeightMm(VALID_HEIGHT_MM)
            .withOrientation(NORMAL)
            .withMode(UCC_CASE)
            .withPlainTextContent(data)
            .build();

        // When & Then
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> invalidUccBarcode.validateInContext(LABEL_4X6, DPI_203, null));
        assertEquals(expectedError, actualException.getMessage());
    }
}
