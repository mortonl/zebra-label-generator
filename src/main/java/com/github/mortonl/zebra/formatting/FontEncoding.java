package com.github.mortonl.zebra.formatting;

import com.github.mortonl.zebra.ZebraLabel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents font encoding values for the ZPL II ^CI command.
 * These encodings determine how character data is interpreted by the printer.
 *
 * <p>When no encoding is specified, the printer will use its default encoding
 * or the last encoding set by a ^CI command.</p>
 *
 * @see ZebraLabel For usage in label configuration
 */
@Getter
@AllArgsConstructor
public enum FontEncoding
{
    /**
     * United States (single-byte)
     */
    USA_1("0"),

    /**
     * United States (single-byte, alternate)
     */
    USA_2("1"),

    /**
     * United Kingdom (single-byte)
     */
    UK("2"),

    /**
     * Holland (single-byte)
     */
    HOLLAND("3"),

    /**
     * Denmark/Norway (single-byte)
     */
    DENMARK_NORWAY("4"),

    /**
     * Sweden/Finland (single-byte)
     */
    SWEDEN_FINLAND("5"),

    /**
     * Germany (single-byte)
     */
    GERMANY("6"),

    /**
     * France 1 (single-byte)
     */
    FRANCE_1("7"),

    /**
     * France 2 (single-byte)
     */
    FRANCE_2("8"),

    /**
     * Italy (single-byte)
     */
    ITALY("9"),

    /**
     * Spain (single-byte)
     */
    SPAIN("10"),

    /**
     * Miscellaneous (single-byte)
     */
    MISC("11"),

    /**
     * Japan (single-byte)
     */
    JAPAN("12"),

    /**
     * Zebra Code Page 850 (single-byte)
     */
    ZEBRA("13"),

    /**
     * Asian double-byte encodings
     */
    DOUBLE_BYTE_ASIA("14"),

    /**
     * UTF-8 encoding (variable-byte)
     */
    UTF_8("28"),

    /**
     * UTF-16 Big Endian encoding (double-byte)
     */
    UTF_16_BIG_ENDIAN("29"),

    /**
     * UTF-16 Little Endian encoding (double-byte)
     */
    UTF_16_LITTLE_ENDIAN("30");

    /**
     * The ZPL II command value for this encoding.
     * Used as the parameter for the ^CI command.
     */
    private final String value;
}
