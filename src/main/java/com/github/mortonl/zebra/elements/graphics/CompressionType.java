package com.github.mortonl.zebra.elements.graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the supported compression types for graphic field data.
 */
@Getter
@AllArgsConstructor
public enum CompressionType
{
    /**
     * ASCII hexadecimal format, following the format for other download commands.
     */
    ASCII_HEX("A"),

    /**
     * Binary format, where data is sent as strictly binary.
     */
    BINARY("B"),

    /**
     * Compressed binary format, where data is compressed using Zebra's compression algorithm
     * and decompressed by the printer.
     */
    COMPRESSED_BINARY("C");

    private final String value;
}
