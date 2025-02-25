package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Builder;
import lombok.Data;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.FIELD_HEXADECIMAL_INDICATOR;
import static com.github.mortonl.zebra.ZplCommand.FIELD_START;

@Data
@Builder
public class Field implements LabelElement
{
    private final String data;

    private Boolean enableHexCharacters;

    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplCommand = new StringBuilder();

        if (Boolean.TRUE.equals(enableHexCharacters)) {
            zplCommand.append(FIELD_HEXADECIMAL_INDICATOR);
        }

        zplCommand.append(FIELD_START)
                 .append(data)
                 .append(FIELD_END);

        return zplCommand.toString();
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        if (data == null) {
            throw new IllegalArgumentException("Field Data cannot be null");
        }
    }
}
