package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Data;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.FIELD_START;

@Data
public class Field implements LabelElement
{
    private final String data;

    public String toZplString(PrintDensity dpi)
    {
        return FIELD_START + data + FIELD_END;
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        if (data == null) {
            throw new IllegalArgumentException("Field Data cannot be null");
        }
    }
}
