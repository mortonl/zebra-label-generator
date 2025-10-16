package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.elements.barcodes.pdf_417.BarcodePDF417;
import com.github.mortonl.zebra.formatting.Orientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.formatting.Orientation.NORMAL;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("BarcodePDF417 barcode creation and validation")
@Tag("unit")
@Tag("barcode")
class BarcodePDF417Test
{
    private static final double VALID_ROW_HEIGHT_MM = 10.0;

    private static final int VALID_SECURITY_LEVEL = 5;

    private static final int VALID_DATA_COLUMNS = 15;

    private static final int VALID_ROWS = 20;

    private static final String VALID_TEST_DATA = "Test data";

    private static final String EXCESSIVE_DATA = new String(new char[3001]).replace('\0', 'a');

    private static final String EXPECTED_ZPL_OUTPUT = "^FO100,148^B7N,5,10,20,30,N^FDTest123^FS";

    private BarcodePDF417 classUnderTest;

    private static Stream<Arguments> invalidParametersForValidateInContext()
    {
        return Stream.of(
            Arguments.of("Empty Data", "", 10.0, 0, 1, 3),
            Arguments.of("Excessive Data", new String(new char[3001]).replace('\0', 'a'), 10.0, 0, 1, 3),
            Arguments.of("Invalid Data Columns", "Test", 10.0, 0, 31, 3),
            Arguments.of("Invalid Rows", "Test", 10.0, 0, 1, 91),
            Arguments.of("Invalid Security Level", "Test", 10.0, 9, 1, 3),
            Arguments.of("Negative Row Height", "Test", -1.0, 0, 1, 3)
        );
    }

    private static Stream<Arguments> validParametersForValidateInContext()
    {
        return Stream.of(
            Arguments.of("Valid Parameters", "Test data", 10.0, 5, 15, 20)
        );
    }

    private static Stream<Arguments> parametersForToZplString()
    {
        return Stream.of(
            Arguments.of(
                "Normal Orientation",
                NORMAL,
                0.625,
                10,
                20,
                30,
                "Test123",
                "^FO100,148^B7N,5,10,20,30,N^FDTest123^FS"
            )
        );
    }

    @BeforeEach
    void setUp()
    {
        classUnderTest = BarcodePDF417
            .createPDF417Barcode()
            .withOrientation(NORMAL)
            .withRowHeightMm(VALID_ROW_HEIGHT_MM)
            .withSecurityLevel(VALID_SECURITY_LEVEL)
            .withDataColumns(VALID_DATA_COLUMNS)
            .withRows(VALID_ROWS)
            .withPlainTextContent(VALID_TEST_DATA)
            .build();
    }

    @ParameterizedTest(name = "validateInContext throws exception for {0}")
    @MethodSource("invalidParametersForValidateInContext")
    @DisplayName("validateInContext throws exception for invalid parameters")
    @Tag("validation")
    void Given_InvalidParams_When_ValidateInContext_Then_ThrowsException(
        String testName,
        String data,
        double rowHeight,
        int securityLevel,
        int dataColumns,
        int rows
    )
    {
        // Given
        BarcodePDF417 invalidBarcode = BarcodePDF417
            .createPDF417Barcode()
            .withOrientation(NORMAL)
            .withRowHeightMm(rowHeight)
            .withSecurityLevel(securityLevel)
            .withDataColumns(dataColumns)
            .withRows(rows)
            .withHexadecimalContent(data)
            .build();

        // When & Then
        assertThrows(IllegalStateException.class,
            () -> invalidBarcode.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext accepts {0}")
    @MethodSource("validParametersForValidateInContext")
    @DisplayName("validateInContext accepts valid parameters")
    @Tag("validation")
    void Given_ValidParams_When_ValidateInContext_Then_NoException(
        String testName,
        String data,
        double rowHeightMm,
        int securityLevel,
        int dataColumns,
        int rows
    )
    {
        // Given
        BarcodePDF417 validBarcode = BarcodePDF417
            .createPDF417Barcode()
            .withOrientation(NORMAL)
            .withRowHeightMm(rowHeightMm)
            .withSecurityLevel(securityLevel)
            .withDataColumns(dataColumns)
            .withRows(rows)
            .withPlainTextContent(data)
            .build();

        // When & Then
        assertDoesNotThrow(() ->
            validBarcode.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "toZplString generates correct ZPL for {0}")
    @MethodSource("parametersForToZplString")
    @DisplayName("toZplString generates correct ZPL command")
    @Tag("zpl-generation")
    void Given_ConfiguredBarcode_When_ToZplString_Then_GeneratesCorrectZpl(
        String testName,
        Orientation orientation,
        double rowHeight,
        int securityLevel,
        int dataColumns,
        int rows,
        String data,
        String expectedZpl
    )
    {
        // Given
        BarcodePDF417 configuredBarcode = BarcodePDF417
            .createPDF417Barcode()
            .withPosition(12.5, 18.5)
            .withOrientation(orientation)
            .withRowHeightMm(rowHeight)
            .withSecurityLevel(securityLevel)
            .withDataColumns(dataColumns)
            .withRows(rows)
            .withEnableRightSideTruncation(false)
            .withPlainTextContent(data)
            .build();

        // When
        String actualZplString = configuredBarcode.toZplString(DPI_203);

        // Then
        assertEquals(expectedZpl, actualZplString);
    }

    @Test
    @DisplayName("validateInContext accepts valid default configuration")
    @Tag("validation")
    void Given_ValidDefaultConfig_When_ValidateInContext_Then_NoException()
    {
        // Given (classUnderTest is already configured with valid parameters)

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, null));
    }
}
