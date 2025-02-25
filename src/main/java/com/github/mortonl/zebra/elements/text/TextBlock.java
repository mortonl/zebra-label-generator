package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.formatting.TextJustification;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_BLOCK;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.getMaxDotsPerMillimetre;
import static com.github.mortonl.zebra.validation.Validator.validateNotEmpty;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

@Getter
@SuperBuilder
public class TextBlock extends PositionedElement
{
    @Builder.Default
    private double widthMm = 0;

    @Builder.Default
    private int maxLines = 1;

    @Builder.Default
    private double lineSpacingMm = 0;

    @Builder.Default
    private TextJustification justification = TextJustification.LEFT;

    @Builder.Default
    private double hangingIndentMm = 0;

    private Field text;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        String elementPositionCommand = super.toZplString(dpi);

        String fieldBlockCommand = generateZplIICommand(FIELD_BLOCK,
            dpi.toDots(widthMm),
            maxLines,
            dpi.toDots(lineSpacingMm),
            justification.getValue(),
            dpi.toDots(hangingIndentMm));

        return elementPositionCommand + fieldBlockCommand + text.toZplString(dpi);
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        validateNotEmpty(text.getData(), "Text");
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
