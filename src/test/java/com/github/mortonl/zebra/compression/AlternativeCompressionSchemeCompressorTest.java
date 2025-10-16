package com.github.mortonl.zebra.compression;

import com.github.mortonl.junit_extensions.StringFileResource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AlternativeCompressionSchemeCompressor data compression")
@Tag("unit")
@Tag("compression")
public class AlternativeCompressionSchemeCompressorTest
{
    private static final int TYPICAL_BYTES_PER_ROW = 23;

    private static DataCompressor classUnderTest;

    @BeforeAll
    static void setUp()
    {
        classUnderTest = new AlternativeCompressionSchemeCompressor();
    }

    @Test
    @DisplayName("compressData compresses ASCII hex data correctly")
    @Tag("compression")
    void Given_HexData_When_CompressData_Then_ReturnsCompressedFormat(
        @StringFileResource("zebra/compression/203/decompressedASCIIHexData.txt") String decompressedHexWithLineBreaks,
        @StringFileResource("zebra/compression/203/compressedASCIIHexData.txt") String expectedCompressedData
    )
    {
        // Given
        String decompressedInput = decompressedHexWithLineBreaks.replaceAll("\n", "");

        // When
        String actualCompressedData = classUnderTest.compressData(decompressedInput, TYPICAL_BYTES_PER_ROW);

        // Then
        assertEquals(formatCompressedDataForComparison(expectedCompressedData), formatCompressedDataForComparison(actualCompressedData));
    }

    public static @NotNull String formatCompressedDataForComparison(final String actualCompressedData)
    {
        return actualCompressedData.replaceAll(",", ",\n")
                                   .replaceAll(":", ":\n");
    }
}
