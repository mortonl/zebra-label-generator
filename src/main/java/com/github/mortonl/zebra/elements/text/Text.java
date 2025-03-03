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
 * <p>Example usage:</p>
 * <pre>{@code
 * // Basic text with defaults
 * Text text = Text.builder()
 *     .withPosition(100, 100)
 *     .withPlainTextContent("Hello World")
 *     .build();
 *
 * // Formatted text with specific font
 * Text formattedText = Text.builder()
 *     .withPosition(100, 200)
 *     .withFont(Font.builder()
 *         .withFontDesignation('A')
 *         .withSize(2.0, 3.0)  // width and height in mm
 *         .withOrientation(Orientation.NORMAL)
 *         .build())
 *     .withPlainTextContent("Hello World")
 *     .withColorAndBackgroundReversed(true)
 *     .build();
 *
 * // Text with hexadecimal content
 * Text hexText = Text.builder()
 *     .withPosition(100, 300)
 *     .withHexadecimalContent("48656C6C6F") // "Hello" in hex
 *     .build();
 * }</pre>
 *
 * @see PositionedElement For positioning capabilities
 * @see Font For font configuration options
 * @see Field For field data handling
 * @see Orientation For text orientation options
 */
@Getter
@SuperBuilder(setterPrefix = "with")
public class Text extends PositionedElement
{
    /**
     * Controls color inversion of the text field.
     *
     * @param colorAndBackgroundReversed true for white on black, false for standard
     * @return the current color reversal state
     * @see ZplCommand#FIELD_REVERSE_PRINT
     */
    Boolean colorAndBackgroundReversed;

    /**
     * Defines the text appearance characteristics.
     *
     * @param font the font configuration to use
     * @return the current font configuration
     * @see Font for configuration options
     */
    Font font;

    /**
     * The content of the text element.
     * Should not be set directly - use {@link TextBuilder#withPlainTextContent(String)}
     * or {@link TextBuilder#withHexadecimalContent(String)} instead.
     *
     * @param content the field data container
     * @return the current field content
     * @see Field For field data configuration
     * @see TextBuilder#withPlainTextContent(String)
     * @see TextBuilder#withHexadecimalContent(String)
     */
    Field content;

    /**
     * {@inheritDoc}
     *
     * <p>Additional ZPL commands included for text elements:</p>
     * <ul>
     *     <li>Field reversal (^FR) if color reversal is specified</li>
     *     <li>Font settings (^A) if font is specified</li>
     *     <li>Field data (^FD)</li>
     * </ul>
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
     * <p>Additional validation for text elements:</p>
     * <ul>
     *     <li>Content must not be empty</li>
     * </ul>
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
     * @param <C> the type of Text being built
     * @param <B> the concrete builder type (self-referential for method chaining)
     */
    public static abstract class TextBuilder<C extends Text, B extends TextBuilder<C, B>>
        extends PositionedElement.PositionedElementBuilder<C, B>
    {
        /**
         * Sets the content of the text element using plain text.
         * This method automatically configures the underlying {@link Field} with the
         * correct settings for plain text handling.
         *
         * <p>Example usage:</p>
         * <pre>{@code
         * Text text = Text.builder()
         *     .withPlainTextContent("Hello World")
         *     .build();
         * }</pre>
         *
         * @param contents The text content to display
         * @return the builder instance for method chaining
         * @throws IllegalArgumentException if contents is null
         * @see Field.FieldBuilder#data(String)
         * @see #withHexadecimalContent(String)
         */
        public B withPlainTextContent(String contents)
        {
            this.content = Field
                .builder()
                .data(contents)
                .enableHexCharacters(false)
                .build();
            return self();
        }

        /**
         * Sets the content of the text element using hexadecimal data.
         * This method automatically configures the underlying {@link Field} with the
         * correct settings for hexadecimal character handling.
         *
         * <p>Example usage:</p>
         * <pre>{@code
         * Text text = Text.builder()
         *     .withHexadecimalContent("48656C6C6F") // "Hello" in hex
         *     .build();
         * }</pre>
         *
         * @param contents The hexadecimal string representing the text content
         * @return the builder instance for method chaining
         * @throws IllegalArgumentException if contents is null or not valid hexadecimal
         * @see Field.FieldBuilder#data(String)
         * @see #withPlainTextContent(String)
         */
        public B withHexadecimalContent(String contents)
        {
            this.content = Field
                .builder()
                .data(contents)
                .enableHexCharacters(true)
                .build();
            return self();
        }
    }
}
