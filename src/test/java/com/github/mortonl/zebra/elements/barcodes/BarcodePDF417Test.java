package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZplCommandCodes;
import com.github.mortonl.zebra.elements.shared.ZebraOrientation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class BarcodePDF417Test {

    /**
     * Tests the PDF417Barcode constructor with only data parameter.
     * Verifies that default values are properly set for other parameters.
     */
    @Test
    public void testPDF417BarcodeConstructorWithDataOnly() {
        // Arrange
        String testData = "Test123";

        // Act
        BarcodePDF417 barcode = new BarcodePDF417(testData);

        // Assert
        assertNotNull(barcode);
        assertEquals(ZebraOrientation.NORMAL, barcode.getOrientation());
        assertEquals(0, barcode.getRowHeight());
        assertEquals(0, barcode.getSecurityLevel());
        assertEquals(0, barcode.getDataColumns());
        assertEquals(0, barcode.getRows());
        assertEquals(testData, barcode.getData());
    }

    @Test
    public void testPDF417BarcodeConstructorWithEmptyData() {
        assertThrows(IllegalStateException.class, () -> new BarcodePDF417(""));
    }

    @Test
    public void testPDF417BarcodeConstructorWithNullData() {
        assertThrows(IllegalStateException.class, () -> new BarcodePDF417(null));
    }

    /**
     * Tests the PDF417Barcode constructor with orientation and data parameters.
     * Verifies that the barcode is created with the specified orientation and data.
     */
    @Test
    public void testPDF417BarcodeConstructorWithOrientationAndData() {
        ZebraOrientation orientation = ZebraOrientation.ROTATED;
        String data = "Test123";
        
        BarcodePDF417 barcode = new BarcodePDF417(orientation, data);
        
        assertEquals(orientation, barcode.getOrientation(), "Orientation should match the provided value");
        assertEquals(data, barcode.getData(), "Data should match the provided value");
        assertEquals(0, barcode.getRowHeight(), "Row height should be 0 by default");
        assertEquals(0, barcode.getSecurityLevel(), "Security level should be 0 by default");
        assertEquals(0, barcode.getDataColumns(), "Data columns should be 0 by default");
        assertEquals(0, barcode.getRows(), "Rows should be 0 by default");
    }

    /**
     * Tests the PDF417Barcode constructor with orientation, row height, and data parameters.
     * Verifies that the ZPL output contains the correct formatting and data.
     */
    @Test
    public void testPDF417BarcodeConstructorWithOrientationRowHeightAndData() {
        ZebraOrientation orientation = ZebraOrientation.ROTATED;
        int rowHeight = 10;
        String data = "Test123";

        BarcodePDF417 barcode = new BarcodePDF417(orientation, rowHeight, data);

        String zplString = barcode.toZplString();
        assertTrue(zplString.contains("^B7R,10,0,,"));
        assertTrue(zplString.contains("^FD" + data + "^FS"));
    }

    /**
     * Tests the PDF417Barcode constructor with data exceeding maximum length.
     * Verifies that appropriate validation is performed for data length limits.
     */
    @Test
    public void testPDF417BarcodeConstructorWithTooLongData() {
        String longData = "a".repeat(3001);
        assertThrows(IllegalArgumentException.class, () -> new BarcodePDF417(longData));
    }

    /**
     * Tests the PDF417Barcode constructor with a complete set of valid parameters.
     * Verifies that all parameters are correctly set in the constructed barcode.
     */
    @Test
    public void testPDF417BarcodeConstructorWithValidParameters() {
        // Arrange
        ZebraOrientation orientation = ZebraOrientation.NORMAL;
        int rowHeight = 10;
        int securityLevel = 5;
        int dataColumns = 15;
        int rows = 30;
        String data = "Test123";

        // Act
        BarcodePDF417 barcode = new BarcodePDF417(orientation, rowHeight, securityLevel, dataColumns, rows, data);

        // Assert
        assertNotNull(barcode);
        assertEquals("^B7N,10,5,15,30^FDTest123^FS", barcode.toZplString());
    }

    /**
     * Tests the PDF417Barcode behavior when constructed with empty data.
     * Verifies that appropriate validation is performed.
     */
    /**
     * Tests creating a PDF417Barcode with empty data.
     * Verifies that an IllegalStateException is thrown for empty data.
     */
    @Test
    public void testPDF417BarcodeWithEmptyData() {
        assertThrows(IllegalStateException.class, () -> new BarcodePDF417(""));
    }

    /**
     * Tests creating a PDF417Barcode with empty data using orientation constructor.
     * Verifies that an IllegalStateException is thrown.
     */
    @Test
    public void testPDF417BarcodeWithEmptyData_2() {
        assertThrows(IllegalStateException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, "");
        });
    }

    /**
     * Tests creating a PDF417Barcode with empty data using orientation and height constructor.
     * Verifies that an IllegalStateException is thrown.
     */
    @Test
    public void testPDF417BarcodeWithEmptyData_3() {
        assertThrows(IllegalStateException.class, () -> new BarcodePDF417(ZebraOrientation.NORMAL, 10, ""));
    }

    /**
     * Tests creating a PDF417Barcode with empty data using all parameters constructor.
     * Verifies that an IllegalStateException is thrown.
     */
    @Test
    public void testPDF417BarcodeWithEmptyData_4() {
        assertThrows(IllegalStateException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 2, 10, 20, "");
        });
    }

    /**
     * Tests creating a PDF417Barcode with data exceeding maximum length.
     * Verifies that an IllegalArgumentException is thrown for data over 3000 characters.
     */
    @Test
    public void testPDF417BarcodeWithExcessiveDataLength() {
        String longData = "a".repeat(3001);
        assertThrows(IllegalArgumentException.class, () -> new BarcodePDF417(longData));
    }

    /**
     * Tests creating a PDF417Barcode with excessive data length using orientation constructor.
     * Verifies that an IllegalArgumentException is thrown for data over 3000 characters.
     */
    @Test
    public void testPDF417BarcodeWithExcessiveDataLength_2() {
        String excessiveData = "a".repeat(3001);
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, excessiveData);
        });
    }

    /**
     * Tests creating a PDF417Barcode with excessive data length using orientation and height constructor.
     * Verifies that an IllegalArgumentException is thrown for data over 3000 characters.
     */
    @Test
    public void testPDF417BarcodeWithExcessiveDataLength_3() {
        String longData = "a".repeat(3001);
        assertThrows(IllegalArgumentException.class, () -> new BarcodePDF417(ZebraOrientation.NORMAL, 10, longData));
    }

    /**
     * Tests creating a PDF417Barcode with excessive data length using all parameters constructor.
     * Verifies that an IllegalArgumentException is thrown for data over 3000 characters.
     */
    @Test
    public void testPDF417BarcodeWithExcessiveDataLength_4() {
        String longData = "a".repeat(3001);
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 2, 10, 20, longData);
        });
    }

    @Test
    public void testPDF417BarcodeWithInvalidDataColumns() {
        /**
         * Test creating a PDF417Barcode with data columns outside the valid range.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> 
            new BarcodePDF417(ZebraOrientation.NORMAL, 1, 0, 31, 0, "test"));
    }

    @Test
    public void testPDF417BarcodeWithInvalidDataColumns_2() {
        /**
         * Test creating a PDF417Barcode with invalid number of data columns.
         * This should throw an IllegalArgumentException.
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 1, 0, 31, 0, "Test Data");
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 31})
    public void testPDF417BarcodeWithInvalidDataColumns_3(int dataColumns) {
        /**
         * Test creating a PDF417Barcode with invalid data columns
         */
        assertThrows(IllegalArgumentException.class, () -> new BarcodePDF417(ZebraOrientation.NORMAL, 10, 0, dataColumns, 0, "test data"));
    }

    @Test
    public void testPDF417BarcodeWithInvalidDataColumns_4() {
        /**
         * Test creating a PDF417Barcode with invalid number of data columns.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 2, 31, 20, "test data");
        });
    }

    @Test
    public void testPDF417BarcodeWithInvalidRowHeight() {
        /**
         * Test creating a PDF417Barcode with invalid row height.
         * This should throw an IllegalArgumentException.
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, -1, 0, 0, 0, "Test Data");
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -10})
    public void testPDF417BarcodeWithInvalidRowHeight_2(int rowHeight) {
        /**
         * Test creating a PDF417Barcode with invalid row height
         */
        assertThrows(IllegalArgumentException.class, () -> new BarcodePDF417(ZebraOrientation.NORMAL, rowHeight, "test data"));
    }

    @Test
    public void testPDF417BarcodeWithInvalidRows() {
        /**
         * Test creating a PDF417Barcode with rows outside the valid range.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> 
            new BarcodePDF417(ZebraOrientation.NORMAL, 1, 0, 0, 91, "test"));
    }

    @Test
    public void testPDF417BarcodeWithInvalidRows_2() {
        /**
         * Test creating a PDF417Barcode with invalid number of rows.
         * This should throw an IllegalArgumentException.
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 1, 0, 0, 91, "Test Data");
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 91})
    public void testPDF417BarcodeWithInvalidRows_3(int rows) {
        /**
         * Test creating a PDF417Barcode with invalid number of rows
         */
        assertThrows(IllegalArgumentException.class, () -> new BarcodePDF417(ZebraOrientation.NORMAL, 10, 0, 0, rows, "test data"));
    }

    @Test
    public void testPDF417BarcodeWithInvalidRows_4() {
        /**
         * Test creating a PDF417Barcode with invalid number of rows.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 2, 10, 91, "test data");
        });
    }

    @Test
    public void testPDF417BarcodeWithInvalidSecurityLevel() {
        /**
         * Test creating a PDF417Barcode with security level outside the valid range.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> 
            new BarcodePDF417(ZebraOrientation.NORMAL, 1, 9, 0, 0, "test"));
    }

    @Test
    public void testPDF417BarcodeWithInvalidSecurityLevel_2() {
        /**
         * Test creating a PDF417Barcode with invalid security level.
         * This should throw an IllegalArgumentException.
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 1, 9, 0, 0, "Test Data");
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 9})
    public void testPDF417BarcodeWithInvalidSecurityLevel_3(int securityLevel) {
        /**
         * Test creating a PDF417Barcode with invalid security level
         */
        assertThrows(IllegalArgumentException.class, () -> new BarcodePDF417(ZebraOrientation.NORMAL, 10, securityLevel, 0, 0, "test data"));
    }

    @Test
    public void testPDF417BarcodeWithInvalidSecurityLevel_4() {
        /**
         * Test creating a PDF417Barcode with invalid security level.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 9, 10, 20, "test data");
        });
    }

    @Test
    public void testPDF417BarcodeWithNegativeRowHeight() {
        /**
         * Test creating a PDF417Barcode with negative row height.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> 
            new BarcodePDF417(ZebraOrientation.NORMAL, -1, 0, 0, 0, "test"));
    }

    @Test
    public void testPDF417BarcodeWithNegativeRowHeight_2() {
        /**
         * Test creating a PDF417Barcode with negative row height.
         * Expected: IllegalArgumentException
         */
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, -1, 2, 10, 20, "test data");
        });
    }

    /**
     * Tests the PDF417Barcode behavior when constructed with null data.
     * Verifies that appropriate validation is performed.
     */
    /**
     * Tests creating a PDF417Barcode with null data.
     * Verifies that an IllegalStateException is thrown for null data.
     */
    @Test
    public void testPDF417BarcodeWithNullData() {
        assertThrows(IllegalStateException.class, () -> new BarcodePDF417(null));
    }

    /**
     * Tests creating a PDF417Barcode with null data using orientation constructor.
     * Verifies that an IllegalStateException is thrown.
     */
    /**
     * Tests creating a PDF417Barcode with null data using orientation constructor.
     * Verifies that an IllegalStateException is thrown.
     */
    @Test
    public void testPDF417BarcodeWithNullData_2() {
        assertThrows(IllegalStateException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, null);
        });
    }

    /**
     * Tests creating a PDF417Barcode with null data using orientation and height constructor.
     * Verifies that an IllegalStateException is thrown.
     */
    @Test
    public void testPDF417BarcodeWithNullData_3() {
        assertThrows(IllegalStateException.class, () -> new BarcodePDF417(ZebraOrientation.NORMAL, 10, null));
    }

    /**
     * Tests creating a PDF417Barcode with null data using full constructor.
     * Verifies that an IllegalStateException is thrown.
     */
    @Test
    public void testPDF417BarcodeWithNullData_4() {
        assertThrows(IllegalStateException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 2, 10, 20, null);
        });
    }

    @Test
    public void testPDF417BarcodeWithNullOrientation() {
        /**
         * Test creating a PDF417Barcode with null orientation.
         * Expected: No exception (default to NORMAL)
         */
        assertDoesNotThrow(() -> new BarcodePDF417(null, "test"));
    }

    @Test
    public void testPDF417BarcodeWithNullOrientation_2() {
        /**
         * Test creating a PDF417Barcode with null orientation.
         * This should not throw an exception and use the default orientation.
         */
        BarcodePDF417 barcode = new BarcodePDF417(null, "Test Data");
        assertEquals(ZebraOrientation.NORMAL, barcode.getOrientation());
    }

    @Test
    public void testPDF417BarcodeWithNullOrientation_3() {
        /**
         * Test creating a PDF417Barcode with null orientation
         */
        BarcodePDF417 barcode = new BarcodePDF417(null, 10, "test data");
        assertEquals(ZebraOrientation.NORMAL, barcode.getOrientation());
    }

    @Test
    public void testPDF417BarcodeWithNullOrientation_4() {
        /**
         * Test creating a PDF417Barcode with null orientation.
         * Expected: No exception (should default to NORMAL)
         */
        assertDoesNotThrow(() -> {
            new BarcodePDF417(null, 10, 2, 10, 20, "test data");
        });
    }

    /**
     * Tests the toZplString method with default parameters.
     * Verifies that the generated ZPL string contains the correct command codes and formatting.
     */
    @Test
    public void testToZplStringWithDefaultParameters() {
        String data = "Test123";
        BarcodePDF417 barcode = new BarcodePDF417(data);
        
        String expected = ZplCommandCodes.BARCODE_PDF_417 + 
                          ZebraOrientation.NORMAL.getValue() + 
                          ",,0,," + 
                          ZplCommandCodes.FIELD_START + 
                          data + 
                          ZplCommandCodes.FIELD_END_SEPARATOR;
        
        assertEquals(expected, barcode.toZplString());
    }

    /**
     * Tests toZplString behavior with data exceeding length limits.
     * Verifies proper handling of oversized data input.
     */
    /**
     * Tests creating a PDF417Barcode with data longer than 3000 characters.
     * Verifies that an IllegalArgumentException is thrown for excessive data length.
     */
    @Test
    public void testToZplString_DataTooLong() {
        String longData = "a".repeat(3001);
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(longData);
        });
    }

    /**
     * Tests toZplString behavior with empty data.
     * Verifies proper error handling for empty input.
     */
    /**
     * Tests creating a PDF417Barcode with empty data.
     * Verifies that an IllegalStateException is thrown when empty data is provided.
     */
    @Test
    public void testToZplString_EmptyData() {
        assertThrows(IllegalStateException.class, () -> {
            new BarcodePDF417("");
        });
    }

    /**
     * Tests creating a PDF417Barcode with an invalid number of data columns.
     * Verifies that an IllegalArgumentException is thrown for invalid column count.
     */
    @Test
    public void testToZplString_InvalidDataColumns() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 0, 31, 3, "test");
        });
    }

    /**
     * Tests creating a PDF417Barcode with a negative row height.
     * Verifies that an IllegalArgumentException is thrown for invalid height.
     */
    @Test
    public void testToZplString_InvalidRowHeight() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, -1, "test");
        });
    }

    /**
     * Tests creating a PDF417Barcode with an invalid number of rows.
     * Verifies that an IllegalArgumentException is thrown for invalid row count.
     */
    @Test
    public void testToZplString_InvalidRows() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 0, 1, 91, "test");
        });
    }

    /**
     * Tests creating a PDF417Barcode with an invalid security level.
     * Verifies that an IllegalArgumentException is thrown for invalid security level.
     */
    @Test
    public void testToZplString_InvalidSecurityLevel() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BarcodePDF417(ZebraOrientation.NORMAL, 10, 9, 1, 3, "test");
        });
    }

    /**
     * Tests creating a PDF417Barcode with null data.
     * Verifies that an IllegalStateException is thrown for null data input.
     */
    @Test
    public void testToZplString_NullData() {
        assertThrows(IllegalStateException.class, () -> {
            new BarcodePDF417(null);
        });
    }

    /**
     * Tests creating a PDF417Barcode with null orientation.
     * Verifies that the default NORMAL orientation is used when null is provided.
     */
    @Test
    public void testToZplString_NullOrientation() {
        BarcodePDF417 barcode = new BarcodePDF417(null, "test");
        String zplString = barcode.toZplString();
        assertTrue(zplString.startsWith("^B7N"));
    }

}