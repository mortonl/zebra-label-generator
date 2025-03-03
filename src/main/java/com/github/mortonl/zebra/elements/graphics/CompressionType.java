package com.github.mortonl.zebra.elements.graphics;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines the supported compression types for graphic field data in ZPL commands.
 *
 * <p>When no compression type is specified in a command, the printer will use
 * its default value ('A' - ASCII_HEX) or maintain the last used compression type
 * from a previous default command.</p>
 *
 * <p>This enum is primarily used with {@link GraphicField} and {@link DownloadGraphic}
 * commands to specify how the graphic data should be encoded when sent to the printer.</p>
 *
 * @see GraphicField
 * @see DownloadGraphic
 *
 * @example <pre>
 * // Using ASCII hexadecimal compression (default)
 * GraphicField.builder()
 *     .compressionType(CompressionType.ASCII_HEX)
 *     .withHexadecimalContent("48656C6C6F")
 *     .build();
 *
 * // Using compressed binary
 * GraphicField.builder()
 *     .compressionType(CompressionType.COMPRESSED_BINARY)
 *     .withBinaryContent(binaryData)
 *     .build();
 * </pre>
 */
@Getter
@AllArgsConstructor
public enum CompressionType
{
    /**
     * ASCII hexadecimal format, following the format for other download commands.
     * This is the printer's default compression type.
     *
     * <p>When using this compression type, graphic data should be provided using
     * {@code withHexadecimalContent()} rather than {@code withContent()}.</p>
     */
    ASCII_HEX("A"),

    /**
     * Binary format, where data is sent as strictly binary.
     *
     * <p>When using this compression type, graphic data should be provided using
     * {@code withBinaryContent()} rather than {@code withContent()}.</p>
     */
    BINARY("B"),

    /**
     * Compressed binary format, where data is compressed using Zebra's compression algorithm
     * and decompressed by the printer.
     *
     * <p>This format typically results in smaller data transfers but requires
     * additional processing on the host side to compress the data. When using this
     * compression type, data should be provided using {@code withBinaryContent()}
     * after applying Zebra's compression algorithm.</p>
     */
    COMPRESSED_BINARY("C");

    /**
     * The ZPL command value representing this compression type.
     */
    private final String value;
}
