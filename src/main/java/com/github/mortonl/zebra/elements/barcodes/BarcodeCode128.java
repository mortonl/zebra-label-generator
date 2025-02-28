package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.validation.Validator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.BARCODE_CODE_128;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

@Getter
@SuperBuilder(builderMethodName = "createCode128Barcode", setterPrefix = "with")
public class BarcodeCode128 extends Barcode
{
    private final float heightMm;
    private final Orientation orientation;
    private final boolean printInterpretationLineDesired;
    private final boolean printInterpretationLineAboveDesired;
    private final boolean uccCheckDigitEnabled;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        return generateZplIICommand(BARCODE_CODE_128,
            orientation.getValue(),
            dpi.toDots(heightMm),
            printInterpretationLineDesired ? "Y" : "N",
            printInterpretationLineAboveDesired ? "Y" : "N",
            uccCheckDigitEnabled ? "Y" : "N"
        );
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi)
    {
        float minValidHeightMm = 1.0f / dpi.getMaxDotsPerMillimetre();
        float maxValidHeightMm = 32000.0f / dpi.getMinDotsPerMillimetre();

        Validator.validateRange(heightMm,
            minValidHeightMm,
            maxValidHeightMm,
            "Barcode height (mm)");

        // Additional size validations can be added here if needed
        super.validateInContext(size, dpi);
    }
}
