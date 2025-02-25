package com.github.mortonl.zebra;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.github.mortonl.zebra.ZplCommand.CHANGE_INTERNATIONAL_CHARACTER_SET;
import static com.github.mortonl.zebra.ZplCommand.END_FORMAT;
import static com.github.mortonl.zebra.ZplCommand.LABEL_LENGTH;
import static com.github.mortonl.zebra.ZplCommand.LINE_SEPERATOR;
import static com.github.mortonl.zebra.ZplCommand.PRINT_WIDTH;
import static com.github.mortonl.zebra.ZplCommand.START_FORMAT;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

@Data
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ZebraLabel
{
    private final LabelSize size;
    private final PrinterConfiguration printer;
    private final List<LabelElement> elements;
    private final FontEncoding internationalCharacterSet;

    @Builder
    private static ZebraLabel createWithValidation(LabelSize size, PrinterConfiguration printer, FontEncoding internationalCharacterSet)
    {
        validateConfiguration(size, printer);
        return new ZebraLabel(size, printer, new ArrayList<>(), internationalCharacterSet);
    }

    private static void validateConfiguration(LabelSize size, PrinterConfiguration printer)
    {
        if (size == null) {
            throw new IllegalArgumentException("Label Size must be specified");
        }
        if (printer == null) {
            throw new IllegalArgumentException("Printer Configuration must be specified");
        }
        if (!printer.canPrintLabel(size)) {
            throw new IllegalArgumentException(
                String.format("Label size %.1fx%.1fmm does not fit on the loaded media",
                    size.getWidthMm(),
                    size.getHeightMm()));
        }
    }

    public ZebraLabel validateAndAddElement(LabelElement element)
    {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null elements to Label");
        }
        element.validateInContext(size, printer.getDpi());
        elements.add(element);
        return this;
    }

    public String toZplString()
    {
        return toZplString(printer.getDpi());
    }

    public String toZplString(PrintDensity dpi)
    {
        StringBuilder builder = new StringBuilder()
            .append(START_FORMAT)
            .append(LINE_SEPERATOR);

        addLabelSize(builder, dpi);
        addInternationalCharacterSet(builder);

        elements.forEach(element -> builder
            .append(element.toZplString(dpi))
            .append(LINE_SEPERATOR));

        builder.append(END_FORMAT);

        return builder.toString();
    }

    private void addLabelSize(StringBuilder builder, PrintDensity dpi)
    {
        builder
            .append(PRINT_WIDTH)
            .append(size.getWidthInDots(dpi))
            .append(LINE_SEPERATOR)
            .append(LABEL_LENGTH)
            .append(size.getHeightInDots(dpi))
            .append(LINE_SEPERATOR);
    }

    private void addInternationalCharacterSet(StringBuilder builder)
    {
        if (internationalCharacterSet != null) {
            builder
                .append(generateZplIICommand(CHANGE_INTERNATIONAL_CHARACTER_SET, internationalCharacterSet.getValue()))
                .append(LINE_SEPERATOR);
        }
    }
}
