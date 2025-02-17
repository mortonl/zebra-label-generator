package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZplCommandCodes;
import com.github.mortonl.zebra.elements.shared.ZebraOrientation;
import lombok.Data;

@Data
public class BarcodeCode11 {
    private final ZebraOrientation orientation;
    private final boolean singleCheckDigit;
    private final int height;
    private final boolean printInterpretationLine;
    private final boolean interpretationLineAboveCode;
    private final String data;

    /**
     * Creates a Code 11 barcode with default settings
     * @param data The data to encode in the barcode
     */
    public BarcodeCode11(String data) {
        this(ZebraOrientation.NORMAL, false, 10, true, false, data);
    }

    /**
     * Creates a Code 11 barcode with full parameter control
     * @param orientation Barcode orientation
     * @param singleCheckDigit True for single check digit, false for double check digit
     * @param height Height of barcode in dots (1-32000)
     * @param printInterpretationLine Whether to print interpretation line
     * @param interpretationLineAboveCode Whether to print interpretation line above code
     * @param data The data to encode in the barcode
     */
    public BarcodeCode11(ZebraOrientation orientation,
                         boolean singleCheckDigit,
                         int height,
                         boolean printInterpretationLine,
                         boolean interpretationLineAboveCode,
                         String data) {
        validateParameters(height, data);

        this.orientation = orientation;
        this.singleCheckDigit = singleCheckDigit;
        this.height = height;
        this.printInterpretationLine = printInterpretationLine;
        this.interpretationLineAboveCode = interpretationLineAboveCode;
        this.data = data;
    }

    private void validateParameters(int height, String data) {
        if (height < 1 || height > 32000) {
            throw new IllegalArgumentException("Height must be between 1 and 32000 dots");
        }

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Data cannot be null or empty");
        }

        // Code 11 only allows digits and hyphens
        if (!data.matches("^[0-9-]+$")) {
            throw new IllegalArgumentException("Code 11 only supports digits and hyphens");
        }
    }

    /**
     * Converts the barcode to its ZPL representation
     * @return ZPL command string
     */
    public String toZplString() {
        StringBuilder zpl = new StringBuilder();

        // Start with the B1 command
        zpl.append(ZplCommandCodes.BARCODE_CODE_11);

        // Add orientation
        zpl.append(orientation.getValue());

        // Add check digit parameter
        zpl.append(singleCheckDigit ? "Y" : "N");

        // Add height
        zpl.append(",").append(height);

        // Add interpretation line parameters
        zpl.append(",").append(printInterpretationLine ? "Y" : "N");
        zpl.append(",").append(interpretationLineAboveCode ? "Y" : "N");

        // Add field data
        zpl.append(ZplCommandCodes.FIELD_START).append(data).append(ZplCommandCodes.FIELD_END_SEPARATOR);

        return zpl.toString();
    }
}
