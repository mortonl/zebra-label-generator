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

@Getter
@SuperBuilder(setterPrefix = "with")
public class GraphicField extends PositionedElement
{
    private static final int MIN_BYTE_VALUE = 1;
    private static final int MAX_BYTE_VALUE = 99999;

    /**
     * The type of compression used for the graphic data.
     */
    private final CompressionType compressionType;

    /**
     * The total number of bytes to be transmitted for the total image.
     * For ASCII download, this should match the graphic field count.
     * Must be between 1 and 99999.
     */
    private final int binaryByteCount;

    /**
     * The total number of bytes comprising the graphic format (width x height).
     * When divided by bytes per row, gives the number of lines in the image.
     * Must be between 1 and 99999.
     */
    private final int graphicFieldCount;

    /**
     * The number of bytes in the downloaded data that comprise one row of the image.
     * Must be between 1 and 99999.
     */
    private final int bytesPerRow;

    /**
     * The graphic data in the format specified by the compression type.
     * For ASCII_HEX, this should be hexadecimal characters (0-9, A-F).
     */
    @NonNull
    private final String data;

    /**
     * Converts this graphic field to its ZPL representation.
     *
     * @param dpi the print density to use for converting measurements
     * @return the ZPL command string
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
     * Checks all field values are within their allowed ranges and the data format is correct.
     *
     * @param size the label size context for validation
     * @param dpi  the print density context for validation
     * @throws IllegalStateException if any validation fails
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
     * Checks that the data contains only valid hexadecimal characters and
     * that the length matches the specified binary byte count.
     *
     * @throws IllegalStateException if the hex data is invalid
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
}
