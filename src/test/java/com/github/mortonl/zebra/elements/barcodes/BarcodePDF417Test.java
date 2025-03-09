package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BarcodePDF417Test
{
    private static Stream<Arguments> invalidBarcodeParameters()
    {
        return Stream.of(
            // Test case name, data, rowHeight, securityLevel, dataColumns, rows
            Arguments.of("Empty Data", "", 10.0, 0, 1, 3),
            Arguments.of("Excessive Data", new String(new char[3001]).replace('\0', 'a'), 10.0, 0, 1, 3),
            Arguments.of("Invalid Data Columns", "Test", 10.0, 0, 31, 3),
            Arguments.of("Invalid Rows", "Test", 10.0, 0, 1, 91),
            Arguments.of("Invalid Security Level", "Test", 10.0, 9, 1, 3),
            Arguments.of("Negative Row Height", "Test", -1.0, 0, 1, 3)
        );
    }

    /**
     * Test for valid parameters using method source
     */
    private static Stream<Arguments> validBarcodeParameters()
    {
        return Stream.of(
            Arguments.of("Valid Parameters", "Test data", 10.0, 5, 15, 20)
        );
    }

    /**
     * Test for ZPL string generation
     */
    private static Stream<Arguments> zplStringTestParameters()
    {
        return Stream.of(
            Arguments.of(
                "Normal Orientation",
                Orientation.NORMAL,
                0.625,
                10,
                20,
                30,
                "Test123",
                "^FO100,148^B7N,5,10,20,30,N^FDTest123^FS"
            )
        );
    }

    /**
     * Parameterized test for validateInContext method with invalid parameters.
     * Tests various invalid configurations that should throw IllegalStateException.
     *
     * @param testName      name of the test case
     * @param data          barcode data
     * @param rowHeight     height of each row
     * @param securityLevel security level
     * @param dataColumns   number of data columns
     * @param rows          number of rows
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidBarcodeParameters")
    void testValidateInContextWithInvalidParameters(
        String testName,
        String data,
        double rowHeight,
        int securityLevel,
        int dataColumns,
        int rows
    )
    {
        BarcodePDF417 barcode = BarcodePDF417
            .createPDF417Barcode()
            .withOrientation(Orientation.NORMAL)
            .withRowHeightMm(rowHeight)
            .withSecurityLevel(securityLevel)
            .withDataColumns(dataColumns)
            .withRows(rows)
            .withHexadecimalContent(data)
            .build();

        assertThrows(IllegalStateException.class,
            () -> barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validBarcodeParameters")
    void testValidateInContextWithValidParameters(
        String testName,
        String data,
        double rowHeightMm,
        int securityLevel,
        int dataColumns,
        int rows
    )
    {
        BarcodePDF417 barcode = BarcodePDF417
            .createPDF417Barcode()
            .withOrientation(Orientation.NORMAL)
            .withRowHeightMm(rowHeightMm)
            .withSecurityLevel(securityLevel)
            .withDataColumns(dataColumns)
            .withRows(rows)
            .withPlainTextContent(data)
            .build();

        assertDoesNotThrow(() ->
            barcode.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("zplStringTestParameters")
    void testToZplString(
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
        BarcodePDF417 barcode = BarcodePDF417
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

        assertEquals(expectedZpl, barcode.toZplString(PrintDensity.DPI_203));
    }

    /**
     * Test case for validateInContext method when given valid parameters.
     * This test verifies that no exception is thrown when calling validateInContext
     * with valid LabelSize and PrintDensity, and the BarcodePDF417 object has valid parameters.
     */
    @Test
    public void test_validateInContext_withValidParameters()
    {
        BarcodePDF417 barcode = BarcodePDF417
            .createPDF417Barcode()
            .withOrientation(Orientation.NORMAL)
            .withRowHeightMm(10.0)
            .withSecurityLevel(5)
            .withDataColumns(15)
            .withRows(20)
            .withPlainTextContent("Test data")
            .build();

        PrintDensity dpi = PrintDensity.DPI_203;

        assertDoesNotThrow(() -> barcode.validateInContext(LabelSize.LABEL_4X6, dpi));
    }
}
