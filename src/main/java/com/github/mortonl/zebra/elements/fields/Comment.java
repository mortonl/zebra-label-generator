package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Data;
import lombok.NonNull;

import static com.github.mortonl.zebra.ZplCommand.COMMENT;
import static com.github.mortonl.zebra.ZplCommand.CONTROL_CHARACTERS;
import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

@Data
public class Comment implements LabelElement
{
    @NonNull
    private final String comment;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        return generateZplIICommand(COMMENT, this.comment) + FIELD_END;
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        if (CONTROL_CHARACTERS
            .stream()
            .anyMatch(comment::contains))
        {
            throw new IllegalStateException(
                "Comments cannot contain the special characters " +
                    String.join(" or ", CONTROL_CHARACTERS) +
                    " as these end the comment early"
            );
        }
    }
}
