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
 * <p>Example usage:</p>
 * <pre>{@code
 * // Basic text block with width constraint
 * TextBlock block = TextBlock.builder()
 *     .withPosition(100, 100)
 *     .withPlainTextContent("This is a long text that will automatically wrap based on the width")
 *     .withWidthMm(50.0)  // Text will wrap at 50mm
 *     .build();
 *
 * // Formatted text block with full configuration
 * TextBlock formattedBlock = TextBlock.builder()
 *     .withPosition(100, 200)
 *     .withFont(Font.builder()
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
 * }</pre>
 *
 * @see Text For basic text capabilities
 * @see TextJustification For available text alignment options
 * @see Font For font configuration options
 */
@Getter
@SuperBuilder(setterPrefix = "with")
public class TextBlock extends Text
{
    /**
     * The width of the text block in millimeters.
     * Controls automatic text wrapping and block boundaries.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When specified: Text wraps automatically at this width</li>
     *     <li>When null: No automatic wrapping occurs</li>
     *     <li>Must be positive when specified</li>
     *     <li>Affects text justification and margin calculations</li>
     * </ul>
     *
     * <p>Note: The actual printed width may vary slightly based on font characteristics
     * and printer resolution.</p>
     *
     * @param widthMm the desired block width in millimeters
     * @return the block width in millimeters
     */
    private Double widthMm;

    /**
     * The maximum number of lines to display in the text block.
     * Controls vertical content limits and truncation behavior.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When specified: Text beyond this number of lines is truncated</li>
     *     <li>When null: All lines are printed</li>
     *     <li>Must be positive when specified</li>
     *     <li>Counts wrapped lines towards the total</li>
     * </ul>
     *
     * <p>Truncation occurs without warning indicators. Consider available space
     * when setting this limit.</p>
     *
     * @param maxLines the maximum number of lines to print
     * @return the maximum line limit
     */
    private Integer maxLines;

    /**
     * The vertical spacing between lines in millimeters.
     * Controls the distance between consecutive lines of text.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When specified: Uses exact spacing in millimeters</li>
     *     <li>When null: Uses printer's default (typically 120% of font height)</li>
     *     <li>Must be positive when specified</li>
     *     <li>Applies uniformly to all lines in the block</li>
     * </ul>
     *
     * <p>Smaller values increase density but may reduce readability.
     * Consider font size when setting spacing.</p>
     *
     * @param lineSpacingMm the desired line spacing in millimeters
     * @return the line spacing in millimeters
     */
    private Double lineSpacingMm;

    /**
     * The text justification within the block.
     * Controls horizontal alignment of text lines.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When specified: Uses selected justification for all lines</li>
     *     <li>When null: Uses printer default (typically LEFT)</li>
     *     <li>Requires block width to be set for CENTER and RIGHT</li>
     *     <li>JUSTIFY may affect word and character spacing</li>
     * </ul>
     *
     * <p>Note: Short lines and trailing spaces may affect visual alignment.
     * Consider text content when selecting justification.</p>
     *
     * @param justification the desired text alignment
     * @return the text justification setting
     * @see TextJustification for available justification options
     */
    private TextJustification justification;

    /**
     * The hanging indent in millimeters for the second and subsequent lines.
     * Controls the left margin offset for wrapped lines.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>When specified: Indents all lines after the first</li>
     *     <li>When null: No indentation is applied</li>
     *     <li>Must be positive when specified</li>
     *     <li>Applied in addition to block margins</li>
     *     <li>Affects wrapped lines and multiple paragraphs</li>
     * </ul>
     *
     * <p>Commonly used for:</p>
     * <ul>
     *     <li>Creating paragraph indents</li>
     *     <li>Formatting lists or citations</li>
     *     <li>Improving readability of multi-line text</li>
     * </ul>
     *
     * @param hangingIndentMm the desired indent in millimeters
     * @return the hanging indent in millimeters
     */
    private Double hangingIndentMm;

    /**
     * {@inheritDoc}
     *
     * <p>Additional ZPL commands included for text blocks:</p>
     * <ul>
     *     <li>Field block (^FB) with width, max lines, line spacing, justification,
     *         and hanging indent parameters</li>
     * </ul>
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
     * <p>Additional validation for text blocks:</p>
     * <ul>
     *     <li>Width must be positive if specified</li>
     *     <li>Line spacing must be positive if specified</li>
     *     <li>Max lines must be positive if specified</li>
     *     <li>Hanging indent must be positive if specified</li>
     *     <li>Width must fit within label boundaries</li>
     * </ul>
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
