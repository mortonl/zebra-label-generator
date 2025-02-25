package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.awt.Font;

@Getter
@SuperBuilder
public class Text extends PositionedElement
{
    Boolean isReversed;
    Boolean usesHexadecimals;
    Font font;
    String text;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        return "";
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {

    }
}
