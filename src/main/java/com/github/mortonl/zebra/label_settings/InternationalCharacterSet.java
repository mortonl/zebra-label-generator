package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.CHANGE_INTERNATIONAL_CHARACTER_SET;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

@Getter
@SuperBuilder(builderMethodName = "createInternationalCharacterSet", setterPrefix = "with")
public class InternationalCharacterSet extends LabelElement
{
    private final FontEncoding encoding;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        return generateZplIICommand(CHANGE_INTERNATIONAL_CHARACTER_SET, encoding.getValue());
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalArgumentException
    {
        if (encoding == null) {
            throw new IllegalArgumentException("Encoding cannot be null");
        }
    }
}
