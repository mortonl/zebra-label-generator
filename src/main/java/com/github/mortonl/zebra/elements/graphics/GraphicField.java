package com.github.mortonl.zebra.elements.graphics;

import com.github.mortonl.zebra.elements.PositionedElement;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.GRAPHIC_FIELD;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.validation.Validator.validateNotEmpty;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

/**
 * Represents a graphic field in ZPL format (^GF command).
 * This class handles the creation and validation of graphic data with various compression types.
 *
 * <p>The graphic field can be created using one of three formats:</p>
 * <ul>
 *     <li>ASCII hexadecimal (default)</li>
 *     <li>Binary</li>
 *     <li>Compressed binary</li>
 * </ul>
 *
 * <p><strong>Note:</strong> When values are not explicitly set, they will not be included in the ZPL command.
 * This allows the printer to use its default values or maintain values from previous default commands.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Creating a graphic field with ASCII hex data
 * GraphicField field = GraphicField.createGraphicField()
 *     .withPosition(100, 100)
 *     .withHexadecimalContent("48656C6C6F", 5)  // "Hello" in hex, 5 bytes per row
 *     .build();
 *
 * // Creating a graphic field with binary data
 * GraphicField field = GraphicField.createGraphicField()
 *     .withPosition(100, 100)
 *     .withBinaryContent(imageData, 100)  // 100 bytes per row
 *     .build();
 * }</pre>
 *
 * @see CompressionType For supported compression formats
 * @see PositionedElement For positioning capabilities
 */
@Getter
@SuperBuilder(builderMethodName = "createGraphicField", setterPrefix = "with")
public class GraphicField extends PositionedElement
{
    /**
     * The minimum allowed value for byte-related fields.
     */
    private static final int MIN_BYTE_VALUE = 1;

    /**
     * The maximum allowed value for byte-related fields.
     */
    private static final int MAX_BYTE_VALUE = 99999;

    /**
     * The type of compression used for the graphic data.
     *
     * <p>Determines how the graphic data is encoded in the ZPL command.
     * When not specified, the printer will use its default value (ASCII_HEX)
     * or maintain the last used compression type from a previous default command.</p>
     *
     * @param compressionType the compression type to use for encoding graphic data
     * @return the compression type used for the graphic data
     * @see CompressionType#ASCII_HEX For standard hexadecimal format
     * @see CompressionType#BINARY For raw binary data (B64)
     * @see CompressionType#COMPRESSED_BINARY For Zebra's compressed format (Z64)
     */
    private final CompressionType compressionType;

    /**
     * The total number of bytes to be transmitted for the total image.
     * For ASCII download, this should match the graphic field count.
     * Must be between {@value MIN_BYTE_VALUE} and {@value MAX_BYTE_VALUE}.
     *
     * @param binaryByteCount the total number of bytes in the image data
     * @return the total number of bytes in the image data
     */
    private final int binaryByteCount;

    /**
     * The total number of bytes comprising the graphic format (width x height).
     * When divided by bytes per row, gives the number of lines in the image.
     * Must be between {@value MIN_BYTE_VALUE} and {@value MAX_BYTE_VALUE}.
     *
     * @param graphicFieldCount the total byte count of the graphic format
     * @return the total byte count of the graphic format
     */
    private final int graphicFieldCount;

    /**
     * The number of bytes in the downloaded data that comprise one row of the image.
     * Must be between {@value MIN_BYTE_VALUE} and {@value MAX_BYTE_VALUE}.
     *
     * @param bytesPerRow the number of bytes per image row
     * @return the number of bytes per image row
     */
    private final int bytesPerRow;

    /**
     * The graphic data in the format specified by the compression type.
     * Format varies by compression type:
     * <ul>
     *     <li>ASCII_HEX: hexadecimal characters (0-9, A-F)</li>
     *     <li>BINARY: B64 encoded format (:B64:encoded_data:crc)</li>
     *     <li>COMPRESSED_BINARY: Z64 encoded format (:Z64:encoded_data:crc)</li>
     * </ul>
     *
     * @param data the encoded graphic data string
     * @return the encoded graphic data string
     */
    @NonNull
    private final String data;

    /**
     * Converts this graphic field to its ZPL representation.
     *
     * <p>The command format follows: ^GFa,b,c,d,data where:</p>
     * <ul>
     *     <li>a = compression type (A/B/C)</li>
     *     <li>b = binary byte count</li>
     *     <li>c = graphic field count</li>
     *     <li>d = bytes per row</li>
     * </ul>
     *
     * @param dpi the print density to use for converting measurements
     * @return the complete ZPL command string
     * @see PrintDensity For supported DPI values
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        StringBuilder zplString = new StringBuilder(super.toZplString(dpi));

        zplString.append(generateZplIICommand(
            GRAPHIC_FIELD,
            compressionType != null ? compressionType.getValue() : null,
            binaryByteCount,
            graphicFieldCount,
            bytesPerRow,
            data
        ));

        zplString.append(FIELD_END);

        return zplString.toString();
    }

    /**
     * Validates this graphic field in the context of a specific label size and print density.
     * Performs comprehensive validation of all field values and data format.
     *
     * <p>Validation includes:</p>
     * <ul>
     *     <li>Position validation (inherited from PositionedElement)</li>
     *     <li>Data presence and format</li>
     *     <li>Numeric range checks for byte counts and row size</li>
     *     <li>Hex data format validation when using ASCII_HEX compression</li>
     * </ul>
     *
     * @param size the label size context for validation
     * @param dpi  the print density context for validation
     * @throws IllegalStateException if any validation fails
     * @see LabelSize For label dimension constraints
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        super.validateInContext(size, dpi);

        validateNotEmpty(data, "Graphic data");

        validateRange(binaryByteCount, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "Binary byte count");
        validateRange(graphicFieldCount, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "Graphic field count");
        validateRange(bytesPerRow, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "Bytes per row");

        if (compressionType == CompressionType.ASCII_HEX) {
            validateHexData();
        }
    }

    /**
     * Validates ASCII hexadecimal data format and length.
     *
     * <p>Validation includes:</p>
     * <ul>
     *     <li>Removing whitespace and commas from the data</li>
     *     <li>Checking for valid hexadecimal characters (0-9, A-F, a-f)</li>
     *     <li>Verifying the data length matches the specified binary byte count</li>
     * </ul>
     *
     * @throws IllegalStateException if the hex data is invalid or length doesn't match
     */
    private void validateHexData()
    {
        // Remove whitespace and commas for validation
        String cleanData = data.replaceAll("[\\s,]+", "");

        // Check if data contains valid hex characters
        if (!cleanData.matches("[0-9A-Fa-f]+")) {
            throw new IllegalStateException(
                "ASCII hex data must contain only valid hexadecimal characters"
            );
        }

        // Check if data length matches the specified byte count
        if (cleanData.length() / 2 != binaryByteCount) {
            throw new IllegalStateException(
                String.format("ASCII hex data length (%d bytes) does not match the specified binary byte count (%d)",
                    cleanData.length() / 2, binaryByteCount)
            );
        }
    }

    /**
     * Builder for creating GraphicField instances with type-safe configuration.
     * Provides convenient methods for setting graphic data with different compression types.
     *
     * @param <C> The type of the GraphicField being built
     * @param <B> The type of the builder itself
     */
    public static abstract class GraphicFieldBuilder<C extends GraphicField, B extends GraphicFieldBuilder<C, B>>
        extends PositionedElementBuilder<C, B>
    {
        /**
         * Sets the graphic data using ASCII hexadecimal format.
         * Automatically configures all related fields for hex data handling.
         *
         * <p>This method:
         * <ul>
         *     <li>Sets compression type to ASCII_HEX</li>
         *     <li>Converts data to uppercase hexadecimal format</li>
         *     <li>Calculates and sets both binary byte count and graphic field count</li>
         * </ul></p>
         *
         * <p>The hexadecimal data should represent the binary image data where each byte
         * is represented by two hexadecimal characters.</p>
         *
         * @param hexData     Hexadecimal string containing the graphic data (0-9, A-F)
         * @param bytesPerRow The number of bytes that comprise one row of the image
         * @return The builder instance for method chaining
         * @throws IllegalArgumentException if hexData is not a valid hexadecimal string
         *                                  or bytesPerRow is not between 1 and 99999
         * @see CompressionType#ASCII_HEX
         * @see #withBinaryContent(String, int, int) (byte[], int)
         * @see #withCompressedBinaryContent(String, int, int, int)
         */
        public B withHexadecimalContent(String hexData, int bytesPerRow)
        {
            validateHexString(hexData);
            validateRange(bytesPerRow, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "bytesPerRow");

            // Each hex character represents half a byte, so divide by 2 to get byte count
            int byteCount = hexData.length() / 2;

            this.compressionType = CompressionType.ASCII_HEX;
            this.data = hexData.toUpperCase();
            this.bytesPerRow = bytesPerRow;
            this.binaryByteCount = byteCount;
            this.graphicFieldCount = byteCount;

            return self();
        }

        /**
         * Sets the graphic data using binary format with B64 encoding.
         * Automatically configures all related fields for binary data handling.
         *
         * <p>This method:
         * <ul>
         *     <li>Sets compression type to BINARY</li>
         *     <li>Expects data to already be Base64 encoded</li>
         *     <li>Adds required header and CRC checksum</li>
         *     <li>Sets binary byte count and graphic field count to the original data length</li>
         * </ul></p>
         *
         * <p>The data is encoded in the format: :B64:encoded_data:crc
         * where:
         * <ul>
         *     <li>:B64: - identifies Base64 encoding</li>
         *     <li>encoded_data - pre-encoded Base64 data</li>
         *     <li>:crc - 4-digit hexadecimal CRC checksum of the encoded data</li>
         * </ul></p>
         *
         * <p><strong>Note:</strong> For small amounts of data, the B64 header and CRC
         * may negate any gains compared to ASCII hexadecimal encoding.</p>
         *
         * @param encodedData       Pre-encoded Base64 data string
         * @param originalByteCount The total number of bytes in the original binary data
         * @param bytesPerRow       The number of bytes that comprise one row of the image
         * @return The builder instance for method chaining
         * @throws IllegalArgumentException if encodedData is empty or any numeric parameter
         *                                  is not between 1 and 99999
         * @see CompressionType#BINARY
         * @see #withHexadecimalContent(String, int)
         * @see #withCompressedBinaryContent(String, int, int, int)
         */
        public B withBinaryContent(@NonNull String encodedData, int originalByteCount, int bytesPerRow)
        {
            validateNotEmpty(encodedData, "encodedData");
            validateRange(originalByteCount, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "originalByteCount");
            validateRange(bytesPerRow, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "bytesPerRow");

            String crc = calculateCRC(encodedData);

            this.compressionType = CompressionType.BINARY;
            this.data = String.format(":B64:%s:%s", encodedData, crc);
            this.bytesPerRow = bytesPerRow;
            this.binaryByteCount = originalByteCount;
            this.graphicFieldCount = originalByteCount;

            return self();
        }

        /**
         * Sets the graphic data using compressed binary format with Z64 encoding.
         * Automatically configures all related fields for compressed data handling.
         *
         * <p>This method:
         * <ul>
         *     <li>Sets compression type to COMPRESSED_BINARY</li>
         *     <li>Expects data to be pre-compressed using LZ77 algorithm and Base64 encoded</li>
         *     <li>Adds required header and CRC checksum</li>
         *     <li>Sets binary byte count to the compressed data length</li>
         *     <li>Sets graphic field count to the original (uncompressed) data length</li>
         * </ul></p>
         *
         * <p>The data is encoded in the format: :Z64:encoded_data:crc
         * where:
         * <ul>
         *     <li>:Z64: - identifies LZ77 compressed and Base64 encoded data</li>
         *     <li>encoded_data - pre-encoded Base64 data of compressed content</li>
         *     <li>:crc - 4-digit hexadecimal CRC checksum of the encoded data</li>
         * </ul></p>
         *
         * <p>The graphicFieldCount parameter must represent the final uncompressed size
         * that will be placed in the printer's memory, not the size of the downloaded data.</p>
         *
         * @param encodedData         Pre-compressed (LZ77) and Base64 encoded data string
         * @param compressedByteCount The total number of bytes in the compressed data
         * @param originalByteCount   The total number of bytes in the original (uncompressed) image
         * @param bytesPerRow         The number of bytes that comprise one row of the image
         * @return The builder instance for method chaining
         * @throws IllegalArgumentException if encodedData is empty or any numeric parameter
         *                                  is not between 1 and 99999
         * @see CompressionType#COMPRESSED_BINARY
         * @see #withHexadecimalContent(String, int)
         * @see #withBinaryContent(String, int, int)
         */
        public B withCompressedBinaryContent(
            @NonNull String encodedData, int compressedByteCount,
            int originalByteCount, int bytesPerRow
        )
        {
            validateNotEmpty(encodedData, "encodedData");
            validateRange(compressedByteCount, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "compressedByteCount");
            validateRange(originalByteCount, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "originalByteCount");
            validateRange(bytesPerRow, MIN_BYTE_VALUE, MAX_BYTE_VALUE, "bytesPerRow");

            String crc = calculateCRC(encodedData);

            this.compressionType = CompressionType.COMPRESSED_BINARY;
            this.data = String.format(":Z64:%s:%s", encodedData, crc);
            this.bytesPerRow = bytesPerRow;
            this.binaryByteCount = compressedByteCount;
            this.graphicFieldCount = originalByteCount;

            return self();
        }

        /**
         * Validates that the input string contains only valid hexadecimal characters
         * and has an even length (as each byte requires two hex characters).
         *
         * @param hexData The hexadecimal string to validate
         * @throws IllegalArgumentException if the string is empty, has an odd length,
         *                                  or contains non-hexadecimal characters
         */
        private void validateHexString(String hexData)
        {
            validateNotEmpty(hexData, "hexData");
            if (hexData.length() % 2 != 0) {
                throw new IllegalArgumentException("Hex data length must be even");
            }
            if (!hexData.matches("[0-9A-Fa-f]+")) {
                throw new IllegalArgumentException("Invalid hex string");
            }
        }

        /**
         * Calculates a 4-digit hexadecimal CRC checksum for the encoded data using CRC-16-CCITT.
         * This implementation uses the same algorithm as Zebra printers for ZB64 encoding validation.
         *
         * <p>The CRC-16-CCITT specifications:
         * <ul>
         *     <li>Width: 16 bits</li>
         *     <li>Polynomial: 0x1021 (x^16 + x^12 + x^5 + 1)</li>
         *     <li>Initial value: 0xFFFF</li>
         *     <li>No final XOR</li>
         * </ul></p>
         *
         * @param encodedData The Base64 encoded data to calculate CRC for
         * @return A 4-digit hexadecimal CRC checksum
         */
        private String calculateCRC(String encodedData)
        {
            int crc = 0xFFFF; // Initialize with 0xFFFF
            byte[] bytes = encodedData.getBytes();

            for (byte b : bytes) {
                crc ^= (b & 0xFF) << 8; // XOR byte into MSB of crc

                // Process each bit
                for (int i = 0; i < 8; i++) {
                    if ((crc & 0x8000) != 0) { // If MSB is set
                        crc = (crc << 1) ^ 0x1021; // Shift left and XOR with polynomial
                    } else {
                        crc <<= 1; // Just shift left
                    }
                    crc &= 0xFFFF; // Ensure 16-bit value
                }
            }

            // Format as 4-digit hexadecimal, uppercase
            return String.format("%04X", crc);
        }
    }
}
