package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.elements.fonts.Font;
import com.github.mortonl.zebra.formatting.TextJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_BLOCK;
import static com.github.mortonl.zebra.ZplCommand.FIELD_DATA;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.getMaxDotsPerMillimetre;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

@Getter
@SuperBuilder(setterPrefix = "with")
public class TextBlock extends Text
{
    private double widthMm;

    private int maxLines;

    private double lineSpacingMm;

    private TextJustification justification;

    private double hangingIndentMm;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        String textCommand = super.toZplString(dpi);

        String fieldBlockCommand = generateZplIICommand(FIELD_BLOCK,
                dpi.toDots(widthMm),
                maxLines,
                dpi.toDots(lineSpacingMm),
                justification.getValue(),
                dpi.toDots(hangingIndentMm));

    // Find the position of ^FD
    int fieldDataIndex = textCommand.indexOf(FIELD_DATA);
    if (fieldDataIndex == -1) {
        throw new IllegalStateException("Field data command (^FD) not found in text command");
    }

    // Insert fieldBlock command before ^FD
    return textCommand.substring(0, fieldDataIndex) +
           fieldBlockCommand +
           textCommand.substring(fieldDataIndex);
}


    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateDimensions(size, dpi);
    }

    private void validateDimensions(LabelSize size, PrintDensity dpi)
    {
        // Validate width
        double minWidth = 0;
        double maxWidth = size.getWidthMm();
        validateRange(widthMm, minWidth, maxWidth, "Width");

        // Validate maxLines
        validateRange(maxLines, 1, 9999, "Maximum lines");

        // Validate line spacing
        double minSpacing = -9999.0 / getMaxDotsPerMillimetre();
        double maxSpacing = 9999.0 / getMaxDotsPerMillimetre();
        validateRange(lineSpacingMm, minSpacing, maxSpacing, "Line spacing");

        // Validate hanging indent
        double maxIndent = 9999.0 / getMaxDotsPerMillimetre();
        validateRange(hangingIndentMm, 0, maxIndent, "Hanging indent");
    }
}
