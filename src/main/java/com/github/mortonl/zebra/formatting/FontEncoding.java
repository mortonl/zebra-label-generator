package com.github.mortonl.zebra.formatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Font/encoding values for the ^CI command
 */
@Getter
@AllArgsConstructor
public enum FontEncoding
{
    USA_1("0"),
    USA_2("1"),
    UK("2"),
    HOLLAND("3"),
    DENMARK_NORWAY("4"),
    SWEDEN_FINLAND("5"),
    GERMANY("6"),
    FRANCE_1("7"),
    FRANCE_2("8"),
    ITALY("9"),
    SPAIN("10"),
    MISC("11"),
    JAPAN("12"),
    ZEBRA("13"),
    DOUBLE_BYTE_ASIA("14"),
    UTF_8("28"),
    UTF_16_BIG_ENDIAN("29"),
    UTF_16_LITTLE_ENDIAN("30");

    private final String value;
}