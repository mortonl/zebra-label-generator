package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.validation.ValidationLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * Represents an element that can be placed on a Zebra label.
 * This interface defines the contract for elements that can be converted to ZPL format
 * and validated within the context of a label's specifications.
 */
@Getter
@SuperBuilder(builderMethodName = "createLabelElement", setterPrefix = "with")
@AllArgsConstructor
public abstract class LabelElement
{
    @Builder.Default
    protected final ValidationLevel validationLevel = ValidationLevel.NORMAL;

    /**
     * Generates the ZPL II commands for this label element.
     *
     * <p>The generated commands should include all necessary positioning and formatting
     * instructions for the element. Only specified (non-null) parameters should be included
     * in the generated command to allow printer defaults to take effect.</p>
     *
     * @param dpi The print density configuration for the target printer
     * @return A String containing the ZPL commands representing this element
     * @see PrintDensity
     */
    public abstract String toZplString(PrintDensity dpi);

    /**
     * Validates that this element's properties are valid within the context of the given label specifications.
     *
     * @param size the dimensions and specifications of the label
     * @param dpi  the print density configuration for the target printer
     * @throws IllegalStateException if the element's properties are invalid for the given context
     */
    public abstract void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException;

    protected static abstract class LabelElementBuilder<C extends LabelElement, B extends LabelElementBuilder<C, B>>
    {
        /**
         * Validates and adds this element to the specified label in one step.
         * This is the recommended way to add an element to a label as it ensures
         * immediate validation against the label's constraints.
         *
         * <p>Example usage:</p>
         * <pre>{@code
         * BarcodeCode128.createCode128Barcode()
         *     .withHeightMm(15.0)
         *     .withContent(new BarcodeContent("12345"))
         *     .addToLabel(label);
         * }</pre>
         *
         * @param label The {@link ZebraLabel} to add this barcode to
         * @throws IllegalStateException if the barcode configuration is invalid
         *         for the given label's size and printer density settings
         * @see ZebraLabel#validateAndAddElement(LabelElement)
         */
        public C addToLabel(ZebraLabel label) throws IllegalStateException
        {
            C element = build();

            label.validateAndAddElement(element);
            return element;
        }
    }
}
