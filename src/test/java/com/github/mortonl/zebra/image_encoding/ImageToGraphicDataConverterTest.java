package com.github.mortonl.zebra.image_encoding;

import com.github.mortonl.junit_extensions.StringFileResource;
import com.github.mortonl.zebra.elements.graphics.CompressionType;
import com.github.mortonl.zebra.elements.graphics.GraphicField;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static com.github.mortonl.zebra.elements.graphics.GraphicField.createGraphicField;
import static com.github.mortonl.zebra.image_encoding.ImageToGraphicDataConverter.bytesToHex;
import static com.github.mortonl.zebra.image_encoding.ImageToGraphicDataConverter.calculateBytesPerRow;
import static com.github.mortonl.zebra.image_encoding.ImageToGraphicDataConverter.calculateTotalBytes;
import static com.github.mortonl.zebra.image_encoding.ImageToGraphicDataConverter.convertToData;
import static com.github.mortonl.zebra.image_encoding.ImageToGraphicDataConverter.convertToHexData;
import static com.github.mortonl.zebra.image_encoding.ImageToGraphicDataConverter.convertToMonochrome;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ImageToGraphicDataConverter Tests")
@Tag("unit")
class ImageToGraphicDataConverterTest
{

    private static final byte EXPECTED_WHITE_PIXEL_BYTE = 0x00;

    private static final String EXPECTED_HEX_FOR_ALL_BLACK = "FF";

    private static final String EXPECTED_HEX_FOR_MIXED_BYTES = "FF00AB";

    private static final int EXPECTED_BYTES_PER_ROW = 23;

    private static final int IMAGE_WIDTH_8 = 8;

    private static final int IMAGE_HEIGHT_1 = 1;

    private static final int IMAGE_WIDTH_16 = 16;

    private static final int IMAGE_HEIGHT_2 = 2;

    private static final int EXPECTED_TOTAL_BYTES = 4;

    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "8, 1",
        "9, 2",
        "16, 2",
        "17, 3"
    })
    @DisplayName("Given image width, when calculating bytes per row, then returns correct byte count")
    @Tag("calculation")
    void givenImageWidth_whenCalculatingBytesPerRow_thenReturnsCorrectByteCount(int imageWidth, int expectedBytes)
    {
        // When
        int actualBytes = calculateBytesPerRow(imageWidth);

        // Then
        assertEquals(expectedBytes, actualBytes);
    }

    @Test
    @DisplayName("Given byte array, when converting to hex, then returns hex string")
    @Tag("conversion")
    void givenByteArray_whenConvertingToHex_thenReturnsHexString()
    {
        // Given
        byte[] givenData = {(byte) 0xFF,
            0x00,
            (byte) 0xAB};

        // When
        String actualHex = bytesToHex(givenData);

        // Then
        assertEquals(EXPECTED_HEX_FOR_MIXED_BYTES, actualHex);
    }

    @Test
    @DisplayName("Given white pixel image, when converting to monochrome, then does not set bit")
    @Tag("conversion")
    void givenWhitePixelImage_whenConvertingToMonochrome_thenDoesNotSetBit()
    {
        // Given
        BufferedImage givenImage = createFullRowImage(Color.WHITE);

        // When
        byte[] actualResult = convertToMonochrome(givenImage);

        // Then
        assertAll(
            () -> assertEquals(1, actualResult.length),
            () -> assertEquals(EXPECTED_WHITE_PIXEL_BYTE, actualResult[0])
        );
    }

    @Test
    @DisplayName("Given black image, when converting to hex data, then returns hex string")
    @Tag("conversion")
    void givenBlackImage_whenConvertingToHexData_thenReturnsHexString()
    {
        // Given
        BufferedImage givenImage = createFullRowImage(Color.BLACK);

        // When
        String actualResult = convertToHexData(givenImage);

        // Then
        assertEquals(EXPECTED_HEX_FOR_ALL_BLACK, actualResult);
    }

    @Test
    @DisplayName("Given image, when calculating total bytes, then returns correct count")
    @Tag("calculation")
    void givenImage_whenCalculatingTotalBytes_thenReturnsCorrectCount()
    {
        // Given
        BufferedImage givenImage = new BufferedImage(IMAGE_WIDTH_16, IMAGE_HEIGHT_2, BufferedImage.TYPE_INT_RGB);

        // When
        int actualResult = calculateTotalBytes(givenImage, CompressionType.ASCII_HEX);

        // Then
        assertEquals(EXPECTED_TOTAL_BYTES, actualResult);
    }

    @Test
    @DisplayName("Given unsupported compression, when converting data, then throws exception")
    @Tag("error")
    void givenUnsupportedCompression_whenConvertingData_thenThrowsException()
    {
        // Given
        BufferedImage givenImage = new BufferedImage(IMAGE_WIDTH_8, IMAGE_HEIGHT_1, BufferedImage.TYPE_INT_RGB);

        // When & Then
        assertThrows(UnsupportedOperationException.class,
            () -> convertToData(givenImage, CompressionType.BINARY));
    }

    @Test
    @DisplayName("Given PNG file, when creating graphic field, then returns valid field")
    @Tag("integration")
    void givenPngFile_whenCreatingGraphicField_thenReturnsValidField(@StringFileResource("zebra/image_encoding/logoImageCommand-203-dpi.zpl") String expectedZplCommand, @StringFileResource("zebra/compression/decompressedASCIIHexData.txt") String decompressedHexWithLineBreaks) throws Exception
    {

        // Given
        BufferedImage givenImage = ImageIO.read(
            Objects.requireNonNull(getClass().getResourceAsStream("/zebra/image_encoding/evri-logo-203-dpi.png"))
        );

        // When
        GraphicField actual = createGraphicField()
            .withPosition(75.625, 1.875)
            .fromImage(givenImage)
            .withEnableAlternativeDataCompression(true)
            .build();

        String       actualDataWithLineBreaks      = addLineBreaks(actual.getData(), actual.getBytesPerRow());
        final String decompressedWithoutLineBreaks = decompressedHexWithLineBreaks.replaceAll("\n", "");

        // Then
        assertAll(
            () -> assertEquals(CompressionType.ASCII_HEX, actual.getCompressionType()),
            () -> assertEquals(EXPECTED_BYTES_PER_ROW, actual.getBytesPerRow()),
            () -> assertEquals(actual.getBinaryByteCount(), actual.getGraphicFieldCount()),
            () -> assertEquals(decompressedWithoutLineBreaks, actual.getData()),
            () -> assertEquals(decompressedHexWithLineBreaks, actualDataWithLineBreaks),
            () -> assertEquals(expectedZplCommand, actual.toZplString(PrintDensity.DPI_203))
        );
    }


    private BufferedImage createFullRowImage(Color color)
    {
        BufferedImage image    = new BufferedImage(IMAGE_WIDTH_8, IMAGE_HEIGHT_1, BufferedImage.TYPE_INT_RGB);
        Graphics2D    graphics = image.createGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, IMAGE_WIDTH_8, IMAGE_HEIGHT_1);
        graphics.dispose();
        return image;
    }

    private String addLineBreaks(String data, int bytesPerRow)
    {
        int           charactersPerLine = bytesPerRow * 2;
        StringBuilder result            = new StringBuilder();

        for (int i = 0; i < data.length(); i += charactersPerLine) {
            if (i > 0) {
                result.append("\n");
            }
            int endIndex = Math.min(i + charactersPerLine, data.length());
            result.append(data.substring(i, endIndex));
        }

        return result.toString();
    }
}
