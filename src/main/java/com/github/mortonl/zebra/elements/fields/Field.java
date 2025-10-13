package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.FIELD_HEXADECIMAL_INDICATOR;
import static com.github.mortonl.zebra.ZplCommand.FIELD_START;

/**
 * <p>Implements a basic field element in ZPL II, representing a fundamental data container
 * that can hold both plain text and hexadecimal data. Fields are the basic building blocks
 * for most label content including:</p>
 * <ul>
 *     <li>Text content</li>
 *     <li>Barcode data</li>
 *     <li>Special characters</li>
 *     <li>Binary data (when using hex mode)</li>
 * </ul>
 *
 * <p><strong>Usage examples:</strong></p>
 * <pre>{@code
 * // Basic text field
 * Field.createField()
 *     .withData("Sample Text")
 *     .addToLabel(label);
 *
 * // Hexadecimal field for special characters or binary data
 * Field.createField()
 *     .withData("A5B2C3")
 *     .withEnableHexCharacters(true)
 *     .addToLabel(label);
 * }</pre>
 *
 * @see LabelElement The parent class for all label elements
 */
@Getter
@SuperBuilder(builderMethodName = "createField", setterPrefix = "with")
public class Field extends LabelElement
{

    /**
     * The content data for this field.
     * Can contain plain text or hexadecimal data depending on the {@code enableHexCharacters} setting.
     *
     * <p>When using plain text:</p>
     * <ul>
     *     <li>Any printable ASCII characters are allowed</li>
     *     <li>Special characters may need escaping</li>
     * </ul>
     *
     * <p>When using hexadecimal mode:</p>
     * <ul>
     *     <li>Must contain valid hexadecimal characters (0-9, A-F)</li>
     *     <li>Each byte requires two hex characters</li>
     * </ul>
     *
     * @param data the content to be printed, either as plain text or hex values
     * @return the field content as either plain text or hex values
     * @see #enableHexCharacters for controlling the data interpretation mode
     */
    private final String data;

    /**
     * Controls whether the field data should be interpreted as hexadecimal values.
     * This setting affects how the printer interprets the {@link #data} content.
     *
     * <p>When set to:</p>
     * <ul>
     *     <li>{@code true} - Data is treated as hexadecimal values</li>
     *     <li>{@code false} - Data is treated as plain text</li>
     *     <li>{@code null} - Uses printer default (plain text)</li>
     * </ul>
     *
     * @param enableHexCharacters true for hex mode, false for text mode, null for printer default
     * @return the current hex interpretation mode
     * @see #data for the content affected by this setting
     */
    private Boolean enableHexCharacters;

    /**
     * {@inheritDoc}
     *
     * <p>Generates a ZPL II field command, including:</p>
     * <ul>
     *     <li>Optional hexadecimal indicator if enabled</li>
     *     <li>Field start delimiter</li>
     *     <li>Field data</li>
     *     <li>Field end delimiter</li>
     * </ul>
     *
     * @return The ZPL II command string representing this field
     */
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        if (Boolean.TRUE.equals(enableHexCharacters)) {
            zplCommand.append(FIELD_HEXADECIMAL_INDICATOR);
        }

        zplCommand
            .append(FIELD_START)
            .append(data)
            .append(FIELD_END);

        return zplCommand.toString();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates that:</p>
     * <ul>
     *     <li>Field data is not null</li>
     *     <li>When hex mode is enabled, data contains valid hex characters (validated at runtime)</li>
     * </ul>
     *
     * @throws IllegalStateException if the field data is null
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        if (data == null) {
            throw new IllegalStateException("Field Data cannot be null");
        }
    }
}
