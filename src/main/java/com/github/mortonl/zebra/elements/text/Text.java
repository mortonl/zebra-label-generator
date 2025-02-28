package com.github.mortonl.zebra.elements.text;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.elements.fonts.Font;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.validation.Validator.validateNotEmpty;

@Getter
@SuperBuilder(builderMethodName = "createText", setterPrefix = "with")
public class Text extends PositionedElement
{
    Boolean colorAndBackgroundReversed;
    Font font;
    Field content;

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

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateNotEmpty(content.getData(), "Text");
    }

    public static abstract class TextBuilder<C extends Text, B extends TextBuilder<C, B>>
        extends PositionedElement.PositionedElementBuilder<C, B>
    {
        public B withPlainTextContent(String contents)
        {
            this.content = Field.createField()
                                .withData(contents)
                                .withEnableHexCharacters(false)
                                .build();
            return self();
        }

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
