package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.validation.Validator.validateNotNull;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

@Getter
@SuperBuilder(builderMethodName = "createInterleaved2of5Barcode", setterPrefix = "with")
public class BarcodeInterleaved2of5 extends Barcode
{
    private final Orientation orientation;
    private final double heightInMillimetres;
    private final boolean printInterpretationLine;
    private final boolean printInterpretationLineAbove;
    private final boolean calculateAndPrintMod10CheckDigit;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        zplCommand
            .append(super.toZplString(dpi))
            .append(generateZplIICommand(
                ZplCommand.BARCODE_INTERLEAVED_2_OF_5,
                orientation.getValue(),
                dpi.toDots(heightInMillimetres),
                printInterpretationLine ? "Y" : "N",
                printInterpretationLineAbove ? "Y" : "N",
                calculateAndPrintMod10CheckDigit ? "Y" : "N"))
            .append(getData().toZplString(dpi));

        return zplCommand.toString();
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateParameters(size, dpi);
    }

    private void validateParameters(LabelSize size, PrintDensity dpi)
    {
        validateNotNull(orientation, "Orientation");

        // Calculate height limits in millimeters
        double minHeightMm = 1.0 / PrintDensity.getMaxDotsPerMillimetre();
        double maxHeightMm = 32000.0 / PrintDensity.getMinDotsPerMillimetre();

        // Validate height range
        validateRange(heightInMillimetres, minHeightMm, maxHeightMm, "Bar code height");

        // Validate height fits within label
        validateRange(heightInMillimetres, 0, size.getHeightMm(), "Bar code height");

        // Validate data
        String data = getData().getData();
        validateNotNull(data, "Barcode data");

        if (!data.matches("\\d+")) {
            throw new IllegalStateException("Interleaved 2 of 5 bar code only accepts numeric data");
        }

        if (calculateAndPrintMod10CheckDigit && data.length() % 2 != 1) {
            throw new IllegalStateException("When using check digit, data length must be odd " +
                "to result in even total length after check digit is added");
        }

        if (!calculateAndPrintMod10CheckDigit && data.length() % 2 != 0) {
            throw new IllegalStateException("Data length must be even when not using check digit");
        }
    }
}
