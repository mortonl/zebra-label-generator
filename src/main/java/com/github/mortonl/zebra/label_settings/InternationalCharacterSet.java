package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.CHANGE_INTERNATIONAL_CHARACTER_SET;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

/**
 * Represents an International Character Set configuration element for ZPL labels.
 * Controls how the printer interprets character encodings for text elements.
 *
 * <p>This element generates the ^CI command to set the international font encoding
 * used for subsequent text fields on the label. The encoding remains in effect
 * until changed by another ^CI command.</p>
 */
@Getter
@SuperBuilder(builderMethodName = "createInternationalCharacterSet", setterPrefix = "with")
public class InternationalCharacterSet extends LabelElement
{
    /**
     * The font encoding to be used for text interpretation.
     * Determines how character bytes are mapped to printed symbols.
     *
     * @param encoding the font encoding setting
     * @return the current font encoding
     */
    private final FontEncoding encoding;

    /**
     * Converts this character set configuration to a ZPL II command string.
     * Generates a ^CI command with the appropriate encoding parameter.
     *
     * @param dpi the print density of the target printer
     * @return the ZPL II command string for setting the character encoding
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        return generateZplIICommand(CHANGE_INTERNATIONAL_CHARACTER_SET, encoding.getValue());
    }

    /**
     * Validates the character set configuration in the context of a specific label size and print density.
     * Ensures that required parameters are properly set.
     *
     * @param size the dimensions of the target label
     * @param dpi  the print density of the target printer
     * @throws IllegalArgumentException if the encoding is null
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalArgumentException
    {
        if (encoding == null) {
            throw new IllegalArgumentException("Encoding cannot be null");
        }
    }
}
