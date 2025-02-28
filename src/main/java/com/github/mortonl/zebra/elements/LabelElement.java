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
     * Converts the label element to its ZPL (Zebra Programming Language) string representation.
     *
     * @param dpi the print density configuration for the target printer
     * @return a String containing the ZPL commands representing this element
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

    protected static abstract class LabelElementBuilder<C extends LabelElement, B extends LabelElementBuilder<C, B>> {
        public C addToLabel(ZebraLabel label) throws IllegalStateException {
            C element = build();

            label.validateAndAddElement(element);
            return element;
        }
    }
}
