package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

@Getter
@SuperBuilder(setterPrefix = "with")
public class BarcodePDF417 extends Barcode
{
    /* Nullable */
    private final Orientation orientation;
    /* Nullable */
    private final int rowHeight;           // 1 to label height
    /* Nullable */
    private final int securityLevel;       // 0 to 8
    /* Nullable */
    private final int dataColumns;         // 1 to 30
    /* Nullable */
    private final int rows;                // 3 to 90

    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        zplCommand
            .append(super.toZplString(dpi))
            .append(generateZplIICommand(
                ZplCommand.BARCODE_PDF_417,
                orientation.getValue(),
                rowHeight,
                securityLevel,
                dataColumns,
                rows))
            .append(data.toZplString(dpi));

        return zplCommand.toString();
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateParameters();
    }

    private void validateParameters()
    {
        if (data.getData().length() > 3000) {
            throw new IllegalStateException("Field data is limited to 3K characters");
        }
        if (rowHeight < 0) {
            throw new IllegalStateException("Row height must be greater than 0");
        }
        if (securityLevel < 0 || securityLevel > 8) {
            throw new IllegalStateException("Security level must be between 0 and 8");
        }
        if (dataColumns < 0 || dataColumns > 30) {
            throw new IllegalStateException("Number of data columns must be between 1 and 30");
        }
        if (rows < 0 || rows > 90) {
            throw new IllegalStateException("Number of rows must be between 3 and 90");
        }
    }
}
