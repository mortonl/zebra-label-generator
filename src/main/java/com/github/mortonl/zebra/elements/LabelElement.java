package com.github.mortonl.zebra.elements;

import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;

/**
 * Represents an element that can be placed on a Zebra label.
 * This interface defines the contract for elements that can be converted to ZPL format
 * and validated within the context of a label's specifications.
 */
public interface LabelElement
{
    /**
     * Converts the label element to its ZPL (Zebra Programming Language) string representation.
     *
     * @param dpi the print density configuration for the target printer
     * @return a String containing the ZPL commands representing this element
     */
    public String toZplString(PrintDensity dpi);

    /**
     * Validates that this element's properties are valid within the context of the given label specifications.
     *
     * @param size the dimensions and specifications of the label
     * @param dpi  the print density configuration for the target printer
     * @throws IllegalStateException if the element's properties are invalid for the given context
     */
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException;
}
