package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.validation.Validator.validateNotEmpty;
import static com.github.mortonl.zebra.validation.Validator.validateNotNull;

/**
 * Abstract base class for all barcode elements in Zebra label generation.
 * Provides common functionality and properties shared across different barcode types.
 * Extends {@link PositionedElement} to support placement on the label.
 *
 * <p>All barcode implementations must provide:</p>
 * <ul>
 *     <li>Content to be encoded in the barcode</li>
 *     <li>Specific barcode formatting commands</li>
 *     <li>Type-specific validation rules</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>{@code
 * // Creating a specific barcode type (e.g., Code128)
 * BarcodeCode128 barcode = BarcodeCode128.createCode128Barcode()
 *     .withPlainTextContent("12345")
 *     .withPositionMm(10.0, 20.0)  // inherited from PositionedElement
 *     .addToLabel(label);
 * }</pre>
 *
 * <p><strong>Content Setting:</strong></p>
 * <p>While this class exposes a {@link Field} content property, specific barcode implementations
 * should provide convenience methods for setting content (e.g., {@code withPlainTextContent()}
 * or {@code withHexadecimalContent()}) rather than directly setting the Field instance.</p>
 *
 * @see PositionedElement The parent class providing positioning functionality
 * @see Field The class representing barcode content and its formatting
 * @see ZebraLabel The label class that barcodes can be added to
 */
@Getter
@SuperBuilder(builderMethodName = "createBarcode", setterPrefix = "with")
public abstract class Barcode extends PositionedElement
{
    /**
     * The content to be encoded in the barcode.
     * This field holds both the data and formatting information for the barcode content.
     *
     * <p><strong>Note:</strong> While this field is directly accessible, it's recommended
     * to use the specific content setting methods provided by concrete implementations
     * (such as {@code withPlainTextContent()} or {@code withHexadecimalContent()})
     * rather than setting this field directly.</p>
     *
     * <p>The content field supports both plain text and hexadecimal data formats,
     * depending on the specific requirements of the barcode type and data being encoded.</p>
     *
     * @see Field
     */
    protected Field content;

    /**
     * {@inheritDoc}
     *
     * <p>For barcodes, generates ZPL commands including:</p>
     * <ul>
     *     <li>Position commands (^FO)</li>
     *     <li>Barcode type-specific formatting</li>
     *     <li>Content field data</li>
     * </ul>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        return super.toZplString(dpi);
    }

    /**
     * {@inheritDoc}
     *
     * <p>For barcodes, validates:</p>
     * <ul>
     *     <li>Content is not null or empty</li>
     *     <li>Position coordinates are within label boundaries</li>
     *     <li>Element fits within label dimensions when rotated</li>
     * </ul>
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        super.validateInContext(size, dpi, defaultFont);
        validateNotNull(content, "content");
        validateNotEmpty(content.getData(), "Data");
    }

    /**
     * Builder for creating Barcode instances with type-safe configuration.
     *
     * @param <C> the type of Barcode being built
     * @param <B> the concrete builder type (self-referential for method chaining)
     */
    public abstract static class BarcodeBuilder<C extends Barcode, B extends BarcodeBuilder<C, B>>
        extends PositionedElement.PositionedElementBuilder<C, B>
    {
        /**
         * Exposes the current builder content for subclasses to use in estimation logic (e.g., width).
         * Note: This is read-only; subclasses should not mutate the returned Field.
         */
        protected Field peekContent()
        {
            return this.content;
        }

        /**
         * Sets the barcode content using plain text.
         * This is the recommended method for setting content when using regular text or numbers.
         * Automatically configures a {@link Field} instance with appropriate settings for plain text.
         *
         * <p>Example usage:</p>
         * <pre>{@code
         * .withPlainTextContent("12345")
         * }</pre>
         *
         * @param contents The plain text content to encode in the barcode
         *
         * @return The builder instance for method chaining
         *
         * @see Field
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
         * Sets the barcode content using hexadecimal data.
         * Use this method when the content includes binary data or special characters
         * that need to be represented in hexadecimal format.
         * Automatically configures a {@link Field} instance with appropriate settings for hex data.
         *
         * <p>Example usage:</p>
         * <pre>{@code
         * .withHexadecimalContent("48656C6C6F")  // "Hello" in hex
         * }</pre>
         *
         * @param contents The content in hexadecimal format
         *
         * @return The builder instance for method chaining
         *
         * @see Field
         */
        public B withHexadecimalContent(String contents)
        {
            this.content = Field.createField()
                                .withData(contents)
                                .withEnableHexCharacters(true)
                                .build();
            return self();
        }

        /**
         * Sets the barcode contents using a custom {@link Field} instance.
         * This is for advanced usage only, when direct control over field configuration is needed.
         * For most cases, use {@link #withPlainTextContent(String)} or {@link #withHexadecimalContent(String)} instead.
         *
         * @param contents The custom Field instance
         *
         * @return The builder instance for method chaining
         *
         * @see Field
         */
        public B withContent(Field contents)
        {
            this.content = contents;
            return self();
        }
    }
}
