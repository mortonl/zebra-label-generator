package com.github.mortonl.zebra.formatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Orientation
{
    NORMAL("N"),
    ROTATED("R"),
    INVERTED("I"),
    BOTTOM_UP("B");

    private final String value;
}
