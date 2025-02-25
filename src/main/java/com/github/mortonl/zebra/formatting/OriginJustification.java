package com.github.mortonl.zebra.formatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OriginJustification
{
    LEFT(0),
    RIGHT(1),
    AUTO(2);

    private final Integer value;
}
