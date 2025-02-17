package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZplCommandCodes;
import com.github.mortonl.zebra.elements.shared.ZebraOrientation;
import lombok.Data;

@Data
public class BarcodePDF417 {
    private final ZebraOrientation orientation;
    private final int rowHeight;           // 1 to label height
    private final int securityLevel;       // 0 to 8
    private final int dataColumns;         // 1 to 30
    private final int rows;                // 3 to 90
    private final String data;

    public BarcodePDF417(String data) {
        this(ZebraOrientation.NORMAL, 0, 0, 0, 0, data);
    }

    public BarcodePDF417(ZebraOrientation orientation, String data) {
        this(orientation, 0, 0, 0, 0, data);
    }

    public BarcodePDF417(ZebraOrientation orientation, int rowHeight, String data) {
        this(orientation, rowHeight, 0, 0, 0, data);
    }

    public BarcodePDF417(ZebraOrientation orientation, int rowHeight, int securityLevel,
                         int dataColumns, int rows, String data) {
        this.orientation = orientation != null ? orientation : ZebraOrientation.NORMAL;
        this.rowHeight = rowHeight;
        this.securityLevel = securityLevel;
        this.dataColumns = dataColumns;
        this.rows = rows;
        this.data = data;
        validateParameters();
    }

    public String toZplString() {
        StringBuilder command = new StringBuilder();

        command.append(ZplCommandCodes.BARCODE_PDF_417);

        // Add orientation
        command.append(orientation.getValue());

        // Add other parameters
        command.append(",").append(rowHeight > 0 ? rowHeight : "");
        command.append(",").append(securityLevel);
        command.append(",").append(dataColumns > 0 ? dataColumns : "");
        command.append(",").append(rows > 0 ? rows : "");

        // Add field data
        command.append(ZplCommandCodes.FIELD_START).append(data).append(ZplCommandCodes.FIELD_END_SEPARATOR);

        return command.toString();
    }

    private void validateParameters() {
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("Data cannot be null or empty");
        }
        if (data.length() > 3000) {
            throw new IllegalArgumentException("Field data is limited to 3K characters");
        }
        if (rowHeight < 0) {
            throw new IllegalArgumentException("Row height must be greater than 0");
        }
        if (securityLevel < 0 || securityLevel > 8) {
            throw new IllegalArgumentException("Security level must be between 0 and 8");
        }
        if (dataColumns < 0 || dataColumns > 30) {
            throw new IllegalArgumentException("Number of data columns must be between 1 and 30");
        }
        if (rows < 0 || rows > 90) {
            throw new IllegalArgumentException("Number of rows must be between 3 and 90");
        }
    }
}
