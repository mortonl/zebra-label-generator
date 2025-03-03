package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.elements.fonts.Font;
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

/**
 * Represents a block of text in ZPL format that supports automatic text wrapping and formatting.
 * Extends {@link Text} to add block-specific features such as width constraints, line spacing,
 * and text justification.
 *
 * <p>When values are not explicitly set, they will not be included in the ZPL command,
 * allowing the printer to use its default values or values from previous default commands.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * // Basic text block with width constraint
 * TextBlock block = TextBlock.createTextBlock()
 *     .withPosition(100, 100)
 *     .withPlainTextContent("This is a long text that will automatically wrap based on the width")
 *     .withWidthMm(50.0)  // Text will wrap at 50mm
 *     .build();
 *
 * // Formatted text block with full configuration
 * TextBlock formattedBlock = TextBlock.createTextBlock()
 *     .withPosition(100, 200)
 *     .withFont(Font.createFont()
 *         .withFontDesignation('A')
 *         .withSize(2.0, 3.0)
 *         .withOrientation(Orientation.NORMAL)
 *         .build())
 *     .withPlainTextContent("This is a justified paragraph with custom line spacing")
 *     .withWidthMm(50.0)
 *     .withMaxLines(3)
 *     .withLineSpacingMm(1.5)
 *     .withJustification(TextJustification.CENTER)
 *     .withHangingIndentMm(2.0)
 *     .build();
 * }</pre></p>
 *
 * @see Text For basic text capabilities
 * @see TextJustification For available text alignment options
 * @see Font For font configuration options
 */
@Getter
@SuperBuilder(builderMethodName = "createTextBlock", setterPrefix = "with")
public class TextBlock extends Text
{
    /**
     * The width of the text block in millimeters.
     * Text will automatically wrap when it reaches this width.
     * When not specified, text will not wrap automatically.
     */
    private Double widthMm;

    /**
     * The maximum number of lines to display.
     * Additional text beyond this limit will be truncated.
     * When not specified, all lines will be displayed.
     */
    private Integer maxLines;

    /**
     * The vertical spacing between lines in millimeters.
     * When not specified, the printer's default line spacing is used.
     */
    private Double lineSpacingMm;

    /**
     * The text justification within the block.
     * When not specified, the printer's default justification (usually left) is used.
     *
     * @see TextJustification For available justification options
     */
    private TextJustification justification;

    /**
     * The hanging indent in millimeters for the second and subsequent lines.
     * When not specified, no hanging indent is applied.
     */
    private Double hangingIndentMm;

    /**
     * {@inheritDoc}
     *
     * <p>Additional ZPL commands included for text blocks:
     * <ul>
     *     <li>Field block (^FB) with width, max lines, line spacing, justification,
     *         and hanging indent parameters</li>
     * </ul></p>
     */
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

    /**
     * {@inheritDoc}
     *
     * <p>Additional validation for text blocks:
     * <ul>
     *     <li>Width must be positive if specified</li>
     *     <li>Line spacing must be positive if specified</li>
     *     <li>Max lines must be positive if specified</li>
     *     <li>Hanging indent must be positive if specified</li>
     *     <li>Width must fit within label boundaries</li>
     * </ul></p>
     *
     * @throws IllegalStateException if any dimension is invalid or exceeds label boundaries
     */
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
