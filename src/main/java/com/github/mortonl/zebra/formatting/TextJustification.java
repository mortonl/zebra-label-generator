package com.github.mortonl.zebra.formatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TextJustification
{
    LEFT("L"),
    CENTER("C"),
    RIGHT("R"),
    JUSTIFIED("J");

    private final String value;
}
