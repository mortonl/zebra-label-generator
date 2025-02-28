package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.formatting.TextJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_BLOCK;
import static com.github.mortonl.zebra.ZplCommand.FIELD_DATA;
import static com.github.mortonl.zebra.ZplCommand.FIELD_HEXADECIMAL_INDICATOR;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.getMaxDotsPerMillimetre;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

@Getter
@SuperBuilder(builderMethodName = "createTextBlock", setterPrefix = "with")
public class TextBlock extends Text
{
    private Double widthMm;

    private Integer maxLines;

    private Double lineSpacingMm;

    private TextJustification justification;

    private Double hangingIndentMm;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        String textCommand = super.toZplString(dpi);

        String fieldBlockCommand = generateZplIICommand(FIELD_BLOCK,
            widthMm != null ? dpi.toDots(widthMm) : null,
            maxLines,
            lineSpacingMm != null ? dpi.toDots(lineSpacingMm) : null,
            justification != null ? justification.getValue() : null,
            hangingIndentMm != null ? dpi.toDots(hangingIndentMm) : null);

        // Find the position of ^FH and ^FD
        int hexIndex = textCommand.indexOf(FIELD_HEXADECIMAL_INDICATOR);
        int fieldDataIndex = textCommand.indexOf(FIELD_DATA);
        if (fieldDataIndex == -1) {
            throw new IllegalStateException("Field data command (^FD) not found in text command");
        }

        // Insert fieldBlock command before ^FH if it exists, otherwise before ^FD
        int insertPosition = (hexIndex != -1) ? hexIndex : fieldDataIndex;

        return textCommand.substring(0, insertPosition) +
            fieldBlockCommand +
            textCommand.substring(insertPosition);
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateDimensions(size, dpi);
    }

    private void validateDimensions(LabelSize size, PrintDensity dpi)
    {
        // If all parameters are null, that's valid
        if (widthMm == null && maxLines == null && lineSpacingMm == null &&
            justification == null && hangingIndentMm == null)
        {
            return;
        }

        // Validate width if present
        if (widthMm != null) {
            double minWidth = 0;
            double maxWidth = size.getWidthMm();
            validateRange(widthMm, minWidth, maxWidth, "Width");
        }

        // Validate maxLines if present
        if (maxLines != null) {
            validateRange(maxLines, 1, 9999, "Maximum lines");
        }

        // Validate line spacing if present
        if (lineSpacingMm != null) {
            double minSpacing = -9999.0 / getMaxDotsPerMillimetre();
            double maxSpacing = 9999.0 / getMaxDotsPerMillimetre();
            validateRange(lineSpacingMm, minSpacing, maxSpacing, "Line spacing");
        }

        // Validate hanging indent if present
        if (hangingIndentMm != null) {
            double maxIndent = 9999.0 / getMaxDotsPerMillimetre();
            validateRange(hangingIndentMm, 0, maxIndent, "Hanging indent");
        }
    }
}
