package com.github.mortonl.zebra.elements.graphics;

import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.elements.graphics.ImageElement.ImageElementBuilder;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static com.github.mortonl.zebra.compression.AlternativeCompressionSchemeCompressorTest.formatCompressedDataForComparison;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_300;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Image element integration with ZebraLabel")
@Tag("integration")
class ImageElementIntegrationTest
{
    private static final LoadedMedia LOADED_MEDIA_4X6 = LoadedMedia.fromLabelSize(LabelSize.LABEL_4X6);

    // Simple caches to avoid repeated IO during parameterized runs
    private static final Map<String, String> RESOURCE_TEXT_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, BufferedImage> IMAGE_CACHE = new ConcurrentHashMap<>();

    private static Stream<TestCase> provideScenarios()
    {
        return Stream.of(
            // 203 dpi scenarios
            TestCase.builder()
                    .displayName("[203dpi] Given PNG with alt compression, original dimensions set")
                    .dpi(DPI_203)
                    .sourceImageResource("/zebra/image_encoding/203/evri-logo.png")
                    .enableAltCompression(true)
                    .widthMm(23.0)
                    .heightMm(7.25)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/203/logoImageCommand-compressed.zpl")
                    .expectedBytesPerRow(23)
                    .expectedHexDataResource("/zebra/compression/203/compressedASCIIHexData.txt")
                    .build(),
            TestCase.builder()
                    .displayName("[203dpi] Given PNG no alt compression, dimensions not set")
                    .dpi(DPI_203)
                    .sourceImageResource("/zebra/image_encoding/203/evri-logo.png")
                    .enableAltCompression(false)
                    .widthMm(null)
                    .heightMm(null)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/203/logoImageCommand-uncompressed.zpl")
                    .expectedBytesPerRow(23)
                    .expectedHexDataResource("/zebra/compression/203/decompressedASCIIHexData.txt")
                    .build(),
            TestCase.builder()
                    .displayName("[203dpi] Given PNG with alt compression, dimensions not set")
                    .dpi(DPI_203)
                    .sourceImageResource("/zebra/image_encoding/203/evri-logo.png")
                    .enableAltCompression(true)
                    .widthMm(null)
                    .heightMm(null)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/203/logoImageCommand-compressed.zpl")
                    .expectedBytesPerRow(23)
                    .expectedHexDataResource("/zebra/compression/203/compressedASCIIHexData.txt")
                    .build(),
            TestCase.builder()
                    .displayName("[203dpi] Given PNG no alt compression, original dimensions set")
                    .dpi(DPI_203)
                    .sourceImageResource("/zebra/image_encoding/203/evri-logo.png")
                    .enableAltCompression(false)
                    .widthMm(23.0)
                    .heightMm(7.25)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/203/logoImageCommand-uncompressed.zpl")
                    .expectedBytesPerRow(23)
                    .expectedHexDataResource("/zebra/compression/203/decompressedASCIIHexData.txt")
                    .build(),
            // 300 dpi scenarios
            TestCase.builder()
                    .displayName("[300dpi] Given PNG with alt compression, original dimensions set")
                    .dpi(DPI_300)
                    .sourceImageResource("/zebra/image_encoding/300/evri-logo.png")
                    .enableAltCompression(true)
                    .widthMm(21.33)
                    .heightMm(7.08)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/300/logoImageCommand-compressed.zpl")
                    .expectedBytesPerRow(32)
                    .expectedHexDataResource("/zebra/compression/300/compressedASCIIHexData.txt")
                    .build(),
            TestCase.builder()
                    .displayName("[300dpi] Given PNG no alt compression, dimensions not set")
                    .dpi(DPI_300)
                    .sourceImageResource("/zebra/image_encoding/300/evri-logo.png")
                    .enableAltCompression(false)
                    .widthMm(null)
                    .heightMm(null)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/300/logoImageCommand-uncompressed.zpl")
                    .expectedBytesPerRow(32)
                    .expectedHexDataResource("/zebra/compression/300/decompressedASCIIHexData.txt")
                    .build(),
            TestCase.builder()
                    .displayName("[300dpi] Given PNG with alt compression, dimensions not set")
                    .dpi(DPI_300)
                    .sourceImageResource("/zebra/image_encoding/300/evri-logo.png")
                    .enableAltCompression(true)
                    .widthMm(null)
                    .heightMm(null)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/300/logoImageCommand-compressed.zpl")
                    .expectedBytesPerRow(32)
                    .expectedHexDataResource("/zebra/compression/300/compressedASCIIHexData.txt")
                    .build(),
            TestCase.builder()
                    .displayName("[300dpi] Given PNG no alt compression, original dimensions set")
                    .dpi(DPI_300)
                    .sourceImageResource("/zebra/image_encoding/300/evri-logo.png")
                    .enableAltCompression(false)
                    .widthMm(21.33)
                    .heightMm(7.08)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/300/logoImageCommand-uncompressed.zpl")
                    .expectedBytesPerRow(32)
                    .expectedHexDataResource("/zebra/compression/300/decompressedASCIIHexData.txt")
                    .build(),
            // Cross-DPI scenarios: 300dpi source rendered at 203dpi (resized)
            TestCase.builder()
                    .displayName("[203dpi<-300src] Given 300dpi PNG with alt compression, resized to 203dpi size")
                    .dpi(DPI_203)
                    .sourceImageResource("/zebra/image_encoding/300/evri-logo.png")
                    .enableAltCompression(true)
                    .widthMm(23.0)
                    .heightMm(7.25)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/300_to_203/logoImageCommand-compressed.zpl")
                    .expectedBytesPerRow(23)
                    .expectedHexDataResource("/zebra/compression/300_to_203/compressedASCIIHexData.txt")
                    .build(),
            TestCase.builder()
                    .displayName("[203dpi<-300src] Given 300dpi PNG no alt compression, resized to 203dpi size")
                    .dpi(DPI_203)
                    .sourceImageResource("/zebra/image_encoding/300/evri-logo.png")
                    .enableAltCompression(false)
                    .widthMm(23.0)
                    .heightMm(7.25)
                    .xPosMm(75.625)
                    .yPosMm(1.875)
                    .expectedZPLResource("/zebra/image_encoding/300_to_203/logoImageCommand-uncompressed.zpl")
                    .expectedBytesPerRow(23)
                    .expectedHexDataResource("/zebra/compression/300_to_203/decompressedASCIIHexData.txt")
                    .build()
        );
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("provideScenarios")
    @DisplayName("Given PNG, When toZplString variations, Then outputs match")
    @Tag("image-toZplString")
    void Given_Png_When_ToZplString_Variations_Then_OutputsMatch(TestCase testCase) throws Exception
    {
        // Given
        ZebraLabel label = createLabelForTestCase(testCase);
        ImageElement imageElement = createImageElementFromTestCase(testCase);

        // When
        label.validateAndAddElement(imageElement);
        String zpl = label.toZplString(testCase.dpi);

        // Then
        assertTrue(zpl.contains("^GF"), "Generated ZPL must contain ^GF");

        String expectedZPLString = testCase.expectedZPLResource != null ?
            readResourceAsString(testCase.expectedZPLResource) : null;
        ExtractedCommands expected = expectedZPLString != null ? extractCommandsFromZpl(expectedZPLString) : null;

        // Use the same parser for actual output to reduce duplication and fragility
        ExtractedCommands actual = extractCommandsFromZpl(zpl);

        int actualBytesPerRow = requireNonNull(actual.bytesPerRow, "bytesPerRow should be parsed from ^GF");

        String actualDataFormatted = testCase.enableAltCompression
            ? formatCompressedDataForComparison(actual.data)
            : formatDataForComparison(actual.data, actualBytesPerRow);

        String expectedHex = testCase.expectedHexDataResource != null ? readResourceAsString(testCase.expectedHexDataResource) : null;
        String expectedDataFormatted = null;
        if (expectedHex != null) {
            expectedDataFormatted = testCase.enableAltCompression
                ? formatCompressedDataForComparison(expectedHex)
                : formatDataForComparison(expectedHex, testCase.expectedBytesPerRow);
        }
        final String expectedDataFormattedFinal = expectedDataFormatted;

        assertAll(
            // Optional: only assert bytesPerRow if provided
            () ->
            {
                if (testCase.expectedBytesPerRow != null) {
                    assertEquals((int) testCase.expectedBytesPerRow, actualBytesPerRow, "bytesPerRow should match expected");
                }
            },
            // Optional: only assert hex if provided
            () ->
            {
                if (expectedDataFormattedFinal != null) {
                    assertEquals(expectedDataFormattedFinal, actualDataFormatted, "ASCII hex data should match reference file");
                }
            },
            // Validate ^FO command separately if present
            () ->
            {
                if (expected != null && expected.foCommand != null) {
                    assertEquals(expected.foCommand, actual.foCommand, "^FO position command should match expected");
                }
            },
            // Validate ^GF command separately
            () ->
            {
                if (expected != null && expected.gfCommand != null) {
                    assertEquals(expected.gfCommand, actual.gfCommand, "^GF graphic command should match expected");
                }
            },
            // Validate whole command against file (optional)
            () ->
            {
                if (expectedZPLString != null && actual.gfCommand != null) {
                    String actualWhole = (actual.foCommand != null ? actual.foCommand : "") + actual.gfCommand;
                    assertEquals(expectedZPLString, actualWhole, "Exact ^FO...^GF...^FS command should match expected file");
                }
            }
        );
    }

    public static @NotNull String formatDataForComparison(final String actualData, final int bytesPerRow)
    {
        if (actualData == null) {
            return "";
        }
        if (bytesPerRow <= 0) {
            // Fallback: return input as-is if bytesPerRow is invalid
            return actualData;
        }
        final int charactersPerLine = bytesPerRow * 2;
        // Normalize and remove any existing line breaks before formatting
        final String data = actualData.replace("\r\n", "\n")
                                      .replace("\r", "\n")
                                      .replace("\n", "");
        final int len = data.length();
        if (len == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(len + (len / charactersPerLine) + 8);
        int i = 0;
        while (i < len) {
            int end = Math.min(i + charactersPerLine, len);
            sb.append(data, i, end);
            if (end < len) {
                sb.append('\n');
            }
            i = end;
        }
        return sb.toString();
    }

    private ImageElement createImageElementFromTestCase(final TestCase testCase) throws IOException
    {
        BufferedImage source = IMAGE_CACHE.computeIfAbsent(testCase.sourceImageResource, key ->
        {
            try {
                return ImageIO.read(requireNonNull(getClass().getResourceAsStream(key)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ImageElementBuilder<?, ?> builder = ImageElement
            .createImage()
            .withImage(source)
            .withPosition(testCase.xPosMm, testCase.yPosMm)
            .withEnableAlternativeDataCompression(testCase.enableAltCompression);

        if (testCase.widthMm != null || testCase.heightMm != null) {
            if (testCase.widthMm != null && testCase.heightMm != null) {
                builder.withSize(testCase.widthMm, testCase.heightMm);
            } else if (testCase.widthMm != null) {
                builder.withWidthMm(testCase.widthMm);
            } else {
                builder.withHeightMm(testCase.heightMm);
            }
        }

        return builder.build();
    }

    private static ZebraLabel createLabelForTestCase(final TestCase testCase)
    {
        PrinterConfiguration printer = PrinterConfiguration
            .createPrinterConfiguration()
            .forDpi(testCase.dpi)
            .forLoadedMedia(LOADED_MEDIA_4X6)
            .build();

        ZebraLabel label = ZebraLabel
            .createLabel()
            .forSize(LabelSize.LABEL_4X6)
            .forPrinter(printer)
            .build();

        return label;
    }

    private ExtractedCommands extractCommandsFromZpl(String zpl)
    {
        if (zpl == null) {
            return null;
        }
        String s = zpl.replace("\r\n", "\n")
                      .replace("\r", "\n");

        int gfIndex = s.indexOf("^GF");
        if (gfIndex < 0) {
            return new ExtractedCommands(null, null, null, null);
        }

        int fsIndex = s.indexOf("^FS", gfIndex);
        String gfCommand = fsIndex >= 0 ? s.substring(gfIndex, fsIndex + 3) : s.substring(gfIndex);

        int foIndex = s.lastIndexOf("^FO", gfIndex);
        String foCommand = null;
        if (foIndex >= 0) {
            int nextCaret = s.indexOf("^", foIndex + 1);
            if (nextCaret == -1 || nextCaret > gfIndex) {
                nextCaret = gfIndex;
            }
            foCommand = s.substring(foIndex, nextCaret);
        }

        // Parse bytesPerRow and data
        Integer bytesPerRow = null;
        String hexData = null;
        try {
            String afterGf = s.substring(gfIndex + 3);
            String[] parts = afterGf.split(",", 5);
            if (parts.length >= 4) {
                String candidate = parts[3].trim();
                try {
                    bytesPerRow = Integer.parseInt(candidate);
                } catch (NumberFormatException ignored) {
                }
            }
            int first = s.indexOf(',', gfIndex + 3);
            int second = s.indexOf(',', first + 1);
            int third = s.indexOf(',', second + 1);
            int fourth = s.indexOf(',', third + 1);
            int dataStart = fourth >= 0 ? fourth + 1 : -1;
            if (dataStart > 0 && fsIndex > dataStart) {
                hexData = s.substring(dataStart, fsIndex)
                           .replace("\n", "");
            }
        } catch (Exception e) {
            // ignore
        }

        return new ExtractedCommands(foCommand, gfCommand, bytesPerRow, hexData);
    }

    private String readResourceAsString(String resourcePath) throws Exception
    {
        String cached = RESOURCE_TEXT_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }
        byte[] bytes = requireNonNull(getClass().getResourceAsStream(resourcePath)).readAllBytes();
        String value = new String(bytes, StandardCharsets.UTF_8);
        RESOURCE_TEXT_CACHE.put(resourcePath, value);
        return value;
    }

    @AllArgsConstructor
    private static class ExtractedCommands
    {
        final String foCommand;      // for example ^FOx,y

        final String gfCommand;      // full ^GF…^FS block

        final Integer bytesPerRow;   // parsed from ^GF params (4th param)

        final String data;        // data between last comma and ^FS
    }

    @Builder
    private static class TestCase
    {
        final String displayName;

        // Which printer density to use for this scenario
        final PrintDensity dpi;

        // Source image to load for this scenario
        final String sourceImageResource;

        final boolean enableAltCompression;

        final Double widthMm;

        final Double heightMm;

        final double xPosMm;

        final double yPosMm;

        final String expectedZPLResource;

        final Integer expectedBytesPerRow;

        final String expectedHexDataResource;

        @Override
        public String toString()
        {
            return displayName != null ? displayName : "TestCase";
        }
    }
}
