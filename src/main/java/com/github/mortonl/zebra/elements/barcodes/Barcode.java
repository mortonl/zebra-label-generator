package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.elements.fields.Field;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.validation.Validator.validateNotEmpty;
import static com.github.mortonl.zebra.validation.Validator.validateNotNull;

@Getter
@SuperBuilder(setterPrefix = "with")
public class Barcode extends PositionedElement
{
    Field data;

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);
        validateNotNull(data, "Data");
        validateNotEmpty(data.getData(), "Data");
    }

    public static abstract class BarcodeBuilder<C extends Barcode, B extends BarcodeBuilder<C, B>>
        extends PositionedElement.PositionedElementBuilder<C, B>
    {
        public B withPlainTextContent(String contents)
        {
            this.data = Field
                .builder()
                .data(contents)
                .enableHexCharacters(false)
                .build();
            return self();
        }

        public B withHexadecimalContent(String contents)
        {
            this.data = Field
                .builder()
                .data(contents)
                .enableHexCharacters(true)
                .build();
            return self();
        }
    }
}
