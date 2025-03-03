package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.elements.fonts.Font;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.validation.Validator.validateNotEmpty;

/**
 * Represents a text element in ZPL format that can be positioned on a label.
 * Supports configurable fonts, field data, and color/background reversal.
 *
 * <p>When values are not explicitly set, they will not be included in the ZPL command,
 * allowing the printer to use its default values or values from previous default commands.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * // Basic text with defaults
 * Text text = Text.createText()
 *     .withPosition(100, 100)
 *     .withPlainTextContent("Hello World")
 *     .build();
 *
 * // Formatted text with specific font
 * Text formattedText = Text.createText()
 *     .withPosition(100, 200)
 *     .withFont(Font.createFont()
 *         .withFontDesignation('A')
 *         .withSize(2.0, 3.0)  // width and height in mm
 *         .withOrientation(Orientation.NORMAL)
 *         .build())
 *     .withPlainTextContent("Hello World")
 *     .withColorAndBackgroundReversed(true)
 *     .build();
 *
 * // Text with hexadecimal content
 * Text hexText = Text.createText()
 *     .withPosition(100, 300)
 *     .withHexadecimalContent("48656C6C6F") // "Hello" in hex
 *     .build();
 * }</pre></p>
 *
 * @see PositionedElement For positioning capabilities
 * @see Font For font configuration options
 * @see Field For field data handling
 * @see Orientation For text orientation options
 */
@Getter
@SuperBuilder(builderMethodName = "createText", setterPrefix = "with")
public class Text extends PositionedElement
{
    /**
     * Indicates whether the text colors should be reversed.
     * When true, text will be white on black background.
     * When not specified, the printer's default color settings are used.
     *
     * @see ZplCommand#FIELD_REVERSE_PRINT
     */
    Boolean colorAndBackgroundReversed;

    /**
     * The font configuration for this text element.
     * When not specified, the printer's default font settings are used.
     *
     * @see Font For available font options
     */
    Font font;

    /**
     * The content of the text element.
     * Should not be set directly - use {@link TextBuilder#withPlainTextContent(String)}
     * or {@link TextBuilder#withHexadecimalContent(String)} instead.
     *
     * @see Field For field data configuration
     * @see TextBuilder#withPlainTextContent(String)
     * @see TextBuilder#withHexadecimalContent(String)
     */
    Field content;

    /**
     * {@inheritDoc}
     *
     * <p>Additional ZPL commands included for text elements:
     * <ul>
     *     <li>Field reversal (^FR) if color reversal is specified</li>
     *     <li>Font settings (^A) if font is specified</li>
     *     <li>Field data (^FD)</li>
     * </ul></p>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();
        zplCommand.append(super.toZplString(dpi));

        if (Boolean.TRUE.equals(colorAndBackgroundReversed)) {
            zplCommand.append(ZplCommand.FIELD_REVERSE_PRINT);
        }

        if (font != null) {
            zplCommand.append(font.toZplString(dpi));
        }

        zplCommand.append(content.toZplString(dpi));

        return zplCommand.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Additional validation for text elements:
     * <ul>
     *     <li>Content must not be empty</li>
     * </ul></p>
     *
     * @throws IllegalStateException if the text content is empty
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateNotEmpty(content.getData(), "Text");
    }

    /**
     * Builder for creating Text instances with type-safe configuration.
     *
     * @param <C> {@inheritDoc}
     * @param <B> {@inheritDoc}
     */
    public static abstract class TextBuilder<C extends Text, B extends TextBuilder<C, B>>
        extends PositionedElement.PositionedElementBuilder<C, B>
    {
        /**
         * Sets the content of the text element using plain text.
         * This method automatically configures the underlying {@link Field} with the
         * correct settings for plain text handling.
         *
         * <p>Example usage:
         * <pre>{@code
         * Text text = Text.createText()
         *     .withPlainTextContent("Hello World")
         *     .build();
         * }</pre></p>
         *
         * @param contents The text content to display
         * @return {@inheritDoc}
         * @throws IllegalArgumentException if contents is null
         * @see Field.FieldBuilder#withData(String) (String)
         * @see #withHexadecimalContent(String)
         */
        public B withPlainTextContent(String contents)
        {
            this.content = Field.createField()
                                .withData(contents)
                                .withEnableHexCharacters(false)
                                .build();
            return self();
        }

        /**
         * Sets the content of the text element using hexadecimal data.
         * This method automatically configures the underlying {@link Field} with the
         * correct settings for hexadecimal character handling.
         *
         * <p>Example usage:
         * <pre>{@code
         * Text text = Text.createText()
         *     .withHexadecimalContent("48656C6C6F") // "Hello" in hex
         *     .build();
         * }</pre></p>
         *
         * @param contents The hexadecimal string representing the text content
         * @return {@inheritDoc}
         * @throws IllegalArgumentException if contents is null or not valid hexadecimal
         * @see Field.FieldBuilder#withData(String) (String)
         * @see #withPlainTextContent(String)
         */
        public B withHexadecimalContent(String contents)
        {
            this.content = Field.createField()
                                .withData(contents)
                                .withEnableHexCharacters(true)
                                .build();
            return self();
        }
    }
}
