package com.github.mortonl.zebra.elements.graphics;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Image element sizing and rendering")
@Tag("unit")
@Tag("graphics")
class ImageElementTest
{
    public static final PrinterConfiguration PRINTER_CONFIGURATION = PrinterConfiguration
        .createPrinterConfiguration()
        .forDpi(PrintDensity.DPI_203)
        .forLoadedMedia(LoadedMedia.fromLabelSize(LabelSize.LABEL_4X6))
        .build();

    private BufferedImage makeImage(int w, int h, Color color)
    {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(color);
            g.fillRect(0, 0, w, h);
        } finally {
            g.dispose();
        }
        return img;
    }

    @Test
    @DisplayName("Resizes to requested width in mm while preserving aspect ratio")
    void Given_WidthOnly_When_Render_Then_BytesPerRowMatchesWidthDots()
    {
        // Given: source image 20x10 px (aspect 2:1), width=50mm at 203dpi => ~400 dots wide, height ~200 dots
        BufferedImage src = makeImage(20, 10, Color.BLACK);
        ImageElement imageElement = ImageElement.createImage()
                                                .withImage(src)
                                                .withPosition(0.0, 0.0)
                                                .withWidthMm(50.0)
                                                .build();

        ZebraLabel label = ZebraLabel.createLabel()
                                     .forSize(LabelSize.LABEL_4X6)
                                     .forPrinter(PRINTER_CONFIGURATION)
                                     .build();
        label.validateAndAddElement(imageElement);

        // When
        String zpl = label.toZplString(DPI_203);

        // Then: parse ^GF parameters: ^GFa,b,c,d,
        int idx = zpl.indexOf("^GF");
        assertTrue(idx >= 0, "ZPL should contain ^GF");
        String params = zpl.substring(idx + 3);
        // format like A,binary,field,bytesPerRow,
        String[] parts = params.split(",", 5);
        int bytesPerRow = Integer.parseInt(parts[3]);
        int binaryCount = Integer.parseInt(parts[1]);
        int fieldCount = Integer.parseInt(parts[2]);

        int expectedWidthDots = DPI_203.toDots(50.0);
        int expectedBytesPerRow = (expectedWidthDots + 7) / 8;
        int expectedHeightDots = expectedWidthDots / 2; // aspect 2:1

        assertEquals(expectedBytesPerRow, bytesPerRow, "bytesPerRow should match width in dots");
        assertEquals(expectedBytesPerRow * expectedHeightDots, fieldCount, "graphicFieldCount should match bytesPerRow*rows");
        assertEquals(fieldCount, binaryCount, "binaryByteCount should equal field count for ASCII_HEX");
    }

    @Test
    @DisplayName("Renders to exact requested width and height in mm when both are provided")
    void Given_BoxSize_When_Render_Then_UsesExactDimensions()
    {
        // Given 100x50 px (2:1), target box 30mm x 30mm at 203dpi -> exact 30x30mm regardless of source aspect
        BufferedImage src = makeImage(100, 50, Color.BLACK);
        ImageElement imageElement = ImageElement.createImage()
                                                .withImage(src)
                                                .withPosition(0.0, 0.0)
                                                .withSize(30.0, 30.0)
                                                .build();

        ZebraLabel label = ZebraLabel.createLabel()
                                     .forSize(LabelSize.LABEL_4X6)
                                     .forPrinter(PRINTER_CONFIGURATION)
                                     .build();
        label.validateAndAddElement(imageElement);

        String zpl = label.toZplString(DPI_203);
        int idx = zpl.indexOf("^GF");
        assertTrue(idx >= 0, "ZPL should contain ^GF");
        String[] parts = zpl.substring(idx + 3)
                            .split(",", 5);
        int bytesPerRow = Integer.parseInt(parts[3]);
        int fieldCount = Integer.parseInt(parts[2]);

        int dots = DPI_203.toDots(30.0);
        int expectedWidthDots = dots;
        int expectedHeightDots = dots;
        int expectedBytesPerRow = (expectedWidthDots + 7) / 8;
        assertEquals(expectedBytesPerRow, bytesPerRow);
        assertEquals(expectedBytesPerRow * expectedHeightDots, fieldCount);
    }

    // --- Migrated unit tests from ImageElementToGraphicDataConverterTest ---
    @ParameterizedTest
    @CsvSource({
        "1, 1",
        "8, 1",
        "9, 2",
        "16, 2",
        "17, 3"
    })
    @DisplayName("Given image width, when calculating bytes per row, then returns correct byte count")
    void givenImageWidth_whenCalculatingBytesPerRow_thenReturnsCorrectByteCount(int imageWidth, int expectedBytes)
    {
        int actualBytes = ImageElement.calculateBytesPerRow(imageWidth);
        assertEquals(expectedBytes, actualBytes);
    }

    @Test
    @DisplayName("Given byte array, when converting to hex, then returns hex string")
    void givenByteArray_whenConvertingToHex_thenReturnsHexString()
    {
        byte[] givenData = new byte[]{(byte) 0xFF, 0x00, (byte) 0xAB};
        String actualHex = ImageElement.bytesToHex(givenData);
        assertEquals("FF00AB", actualHex);
    }

    @Test
    @DisplayName("Given white pixel image, when converting to monochrome, then does not set bit")
    void givenWhitePixelImage_whenConvertingToMonochrome_thenDoesNotSetBit()
    {
        BufferedImage givenImage = makeImage(8, 1, Color.WHITE);
        byte[] actualResult = ImageElement.convertToMonochrome(givenImage);
        assertEquals(1, actualResult.length);
        assertEquals(0x00, actualResult[0]);
    }

    @Test
    @DisplayName("Given black image, when converting to hex data, then returns hex string")
    void givenBlackImage_whenConvertingToHexData_thenReturnsHexString()
    {
        BufferedImage givenImage = makeImage(8, 1, Color.BLACK);
        String actualResult = ImageElement.convertToHexData(givenImage);
        assertEquals("FF", actualResult);
    }

    @Test
    @DisplayName("Given image, when calculating total bytes, then returns correct count")
    void givenImage_whenCalculatingTotalBytes_thenReturnsCorrectCount()
    {
        BufferedImage givenImage = new BufferedImage(16, 2, BufferedImage.TYPE_INT_RGB);
        int actualResult = ImageElement.calculateTotalBytes(givenImage);
        assertEquals(4, actualResult);
    }

    @Test
    @DisplayName("Given unsupported compression, when converting data, then throws exception")
    void givenUnsupportedCompression_whenConvertingData_thenThrowsException()
    {
        BufferedImage givenImage = new BufferedImage(8, 1, BufferedImage.TYPE_INT_RGB);
        Assertions.assertThrows(UnsupportedOperationException.class,
            () -> ImageElement.convertToData(givenImage, CompressionType.BINARY));
    }
}

