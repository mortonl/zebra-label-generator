package com.github.mortonl.zebra.elements.graphics.symbols;

import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SymbolDesignation
{
    REGISTERED_TRADEMARK("A"),
    COPYRIGHT("B"),
    TRADEMARK("C"),
    UNDERWRITERS_LABORATORIES_APPROVAL("D"),
    CANADIAN_STANDARDS_ASSOCIATION_APPROVAL("E");

    /**
     * The character value used in the ZPL command to represent a character symbol.
     * This value is automatically included in the generated ZPL command
     * when the mode is specified.
     *
     * @see GraphicSymbol#toZplString(PrintDensity)
     */
    private final String value;
}
