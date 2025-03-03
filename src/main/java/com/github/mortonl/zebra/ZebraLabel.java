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

/**
 * Represents a printable Zebra label with configurable size, printer settings, and content elements.
 * This class uses the Builder pattern for construction and validation of label configurations.
 *
 * <p>A label consists of:</p>
 * <ul>
 *     <li>Physical dimensions (width and height)</li>
 *     <li>Printer configuration (DPI, media settings)</li>
 *     <li>Content elements (text, barcodes, etc.)</li>
 *     <li>Optional international character set support</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ZebraLabel label = ZebraLabel.builder()
 *     .withSize(new LabelSize(100, 50))           // 100x50mm label
 *     .withPrinter(printerConfig)
 *     .withInternationalCharacterSet(CharSet.UTF8)
 *     .build();
 *
 * label.validateAndAddElement(new TextElement("Hello World"))
 *      .validateAndAddElement(new BarcodeElement("12345"));
 *
 * String zplCode = label.toZplString();
 * }</pre>
 */
@Data
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ZebraLabel
{
    /**
     * Physical width and height dimensions of the label media.
     *
     * @param size the label dimensions
     * @return the current label size configuration
     */
    private final LabelSize size;

    /**
     * Print density and media handling settings.
     *
     * @param printer the printer settings
     * @return the current printer configuration
     */
    private final PrinterConfiguration printer;

    /**
     * Ordered collection of printable label components.
     *
     * @param elements the list of label elements
     * @return the current list of elements
     */
    private final List<LabelElement> elements;

    /**
     * Character encoding for non-ASCII text content.
     *
     * @param internationalCharacterSet the character encoding
     * @return the current font encoding
     */
    private final FontEncoding internationalCharacterSet;

    /**
     * Creates a new label instance with validation of core configuration parameters.
     *
     * @param size                      The physical dimensions of the label
     * @param printer                   The printer configuration
     * @param internationalCharacterSet Optional character set for international text
     * @return A new ZebraLabel instance
     * @throws IllegalArgumentException if size or printer is null, or if label size exceeds printer capabilities
     */
    @Builder
    private static ZebraLabel createWithValidation(LabelSize size, PrinterConfiguration printer, FontEncoding internationalCharacterSet)
    {
        validateConfiguration(size, printer);
        return new ZebraLabel(size, printer, new ArrayList<>(), internationalCharacterSet);
    }

    /**
     * Validates that the label configuration is valid for the specified printer.
     *
     * @param size    The label dimensions to validate
     * @param printer The printer configuration to validate against
     * @throws IllegalArgumentException if size or printer is null, or if label size exceeds printer capabilities
     */
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

    /**
     * Adds a new element to the label after validating it against the label's constraints.
     * Elements are printed in the order they are added.
     *
     * <p>Example:
     * <pre>{@code
     * label.validateAndAddElement(new TextElement("Hello"))
     *      .validateAndAddElement(new BarcodeElement("12345"));
     * }</pre>
     *
     * @param element The element to add to the label
     * @return This label instance for method chaining
     * @throws IllegalArgumentException if element is null or invalid for the label's constraints
     */
    public ZebraLabel validateAndAddElement(LabelElement element)
    {
        if (element == null) {
            throw new IllegalArgumentException("Cannot add null elements to Label");
        }
        element.validateInContext(size, printer.getDpi());
        elements.add(element);
        return this;
    }

    /**
     * Generates the ZPL II code for this label using the printer's configured DPI.
     *
     * @return The complete ZPL II code string for the label
     */
    public String toZplString()
    {
        return toZplString(printer.getDpi());
    }

    /**
     * Generates the ZPL II code for this label using the specified DPI.
     * This method allows for preview generation at different print densities.
     *
     * @param dpi The print density to use for generating the ZPL code
     * @return The complete ZPL II code string for the label
     */
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

    /**
     * Adds the label size commands to the ZPL II output.
     */
    private void addLabelSize(StringBuilder builder, PrintDensity dpi)
    {
        builder
            .append(generateZplIICommand(PRINT_WIDTH, size.getWidthInDots(dpi)))
            .append(LINE_SEPERATOR)
            .append(generateZplIICommand(LABEL_LENGTH, size.getHeightInDots(dpi)))
            .append(LINE_SEPERATOR);
    }

    /**
     * Adds the international character set configuration to the ZPL II output if specified.
     */
    private void addInternationalCharacterSet(StringBuilder builder)
    {
        if (internationalCharacterSet != null) {
            builder
                .append(generateZplIICommand(CHANGE_INTERNATIONAL_CHARACTER_SET, internationalCharacterSet.getValue()))
                .append(LINE_SEPERATOR);
        }
    }
}
