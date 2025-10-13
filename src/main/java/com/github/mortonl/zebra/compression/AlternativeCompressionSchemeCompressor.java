package com.github.mortonl.zebra.compression;

import com.github.mortonl.zebra.elements.graphics.CompressionType;
import com.github.mortonl.zebra.elements.graphics.GraphicField;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;

/**
 * Implements Zebra's alternative data compression scheme for ASCII hexadecimal graphic data.
 * This compression scheme reduces the number of data bytes and download time for graphic
 * images and bitmapped fonts used with ~DG and ~DB commands.
 *
 * <p>The compression uses a run-length encoding system with special symbols:</p>
 * <ul>
 *     <li><strong>Repeat counts 1-19:</strong> G-Z (G=1, H=2, ..., Z=19)</li>
 *     <li><strong>Repeat counts 20-400:</strong> g-z (g=20, h=40, ..., z=400)</li>
 *     <li><strong>Line filling:</strong> "," fills line with zeros, "!" fills with ones</li>
 *     <li><strong>Line repetition:</strong> ":" repeats the previous line</li>
 * </ul>
 *
 * <p>Examples of compression:</p>
 * <ul>
 *     <li>{@code FFFFFFF} → {@code GF} (7 consecutive F's)</li>
 *     <li>{@code 000...000} (full line) → {@code ,} (line of zeros)</li>
 *     <li>{@code 111...111} (full line) → {@code !} (line of ones)</li>
 *     <li>Identical line → {@code :} (repeat previous)</li>
 * </ul>
 *
 * <p><strong>Note:</strong> This compressor is automatically used by
 * {@link GraphicField} when
 * {@code enableAlternativeDataCompression} is set to {@code true}.</p>
 *
 * @see GraphicField#getEnableAlternativeDataCompression()
 * @see CompressionType#ASCII_HEX
 */
public class AlternativeCompressionSchemeCompressor extends DataCompressor
{
    /** Symbol used to fill an entire line with zeros. */
    public static final String FILL_LINE_WITH_ZEROS = ",";

    /** Symbol used to fill an entire line with ones. */
    public static final String FILL_LINE_WITH_ONES = "!";


    private static boolean thereIsNoDataProvided(final String data)
    {
        return data == null || data.isEmpty();
    }

    /**
     * Compresses ASCII hexadecimal data using Zebra's alternative compression scheme.
     *
     * <p>The compression process:</p>
     * <ol>
     *     <li>Splits data into lines based on bytesPerRow * 2 characters</li>
     *     <li>Detects repeated lines and uses ":" symbol</li>
     *     <li>Compresses individual lines using run-length encoding</li>
     *     <li>Uses special symbols for line filling ("," and "!")</li>
     * </ol>
     *
     * @param data        the ASCII hexadecimal data to compress
     * @param bytesPerRow the number of bytes per row (characters per line = bytesPerRow * 2)
     *
     * @return the compressed data string
     *
     * @see GraphicField#toZplString(PrintDensity)
     */
    @Override
    public String compressData(final String data, final int bytesPerRow)
    {
        if (thereIsNoDataProvided(data)) {
            return data;
        }

        final int           charactersPerLine = bytesPerRow * 2;
        final StringBuilder compressedResult  = new StringBuilder();
        String              previousLine      = null;

        for (int position = 0; position < data.length(); position += charactersPerLine) {
            final String currentLine = extractLine(data, position, charactersPerLine);

            if (theLinesAreTheSame(currentLine, previousLine)) {
                compressedResult.append(":");
            } else {
                compressedResult.append(compressLine(currentLine, charactersPerLine));
                previousLine = currentLine;
            }
        }

        return compressedResult.toString();
    }

    private String extractLine(final String data, final int startPosition, final int charactersPerLine)
    {
        final int endPosition = Math.min(startPosition + charactersPerLine, data.length());
        return data.substring(startPosition, endPosition);
    }

    private boolean theLinesAreTheSame(final String currentLine, final String previousLine)
    {
        return previousLine != null && currentLine.equals(previousLine);
    }

    private String compressLine(final String line, final int charactersPerLine)
    {
        if (line.isEmpty()) {
            return line;
        }

        final String specialCompression = trySpecialCompression(line, charactersPerLine);
        if (specialCompression != null) {
            return specialCompression;
        }

        return compressWithRunLengthEncoding(line, charactersPerLine);
    }

    private String trySpecialCompression(final String line, final int charactersPerLine)
    {
        if (line.length() != charactersPerLine) {
            return null;
        }

        if (isAllZeros(line)) {
            return FILL_LINE_WITH_ZEROS;
        }
        if (isAllOnes(line)) {
            return FILL_LINE_WITH_ONES;
        }
        return null;
    }

    private boolean isAllZeros(final String line)
    {
        return line.chars()
                   .allMatch(c -> c == '0');
    }

    private boolean isAllOnes(final String line)
    {
        return line.chars()
                   .allMatch(c -> c == '1');
    }

    private String compressWithRunLengthEncoding(final String line, final int charactersPerLine)
    {
        final StringBuilder compressed = new StringBuilder();
        int                 position   = 0;

        while (position < line.length()) {
            final char currentCharacter = line.charAt(position);
            final int  consecutiveCount = countConsecutiveCharacters(line, position, currentCharacter);

            if (isTrailingFillCharacter(line, position, consecutiveCount, charactersPerLine, currentCharacter)) {
                compressed.append(getLineFillCharacterSymbol(currentCharacter));
                break;
            }

            appendCompressedSequence(compressed, currentCharacter, consecutiveCount);
            position += consecutiveCount;
        }

        return compressed.toString();
    }

    private int countConsecutiveCharacters(final String line, final int startPosition, final char character)
    {
        int count = 1;
        while (startPosition + count < line.length() && line.charAt(startPosition + count) == character) {
            count++;
        }
        return count;
    }

    private boolean isTrailingFillCharacter(final String line, final int position, final int count, final int charactersPerLine, final char character)
    {
        final boolean isAtEndOfLine           = position + count == line.length();
        final boolean isFullLine              = line.length() == charactersPerLine;
        final boolean isLineFillableCharacter = character == '0' || character == '1';

        return isAtEndOfLine && isFullLine && isLineFillableCharacter;
    }

    private String getLineFillCharacterSymbol(final char character)
    {
        return character == '0' ? FILL_LINE_WITH_ZEROS : FILL_LINE_WITH_ONES;
    }

    private void appendCompressedSequence(final StringBuilder compressed, final char character, final int count)
    {
        if (count == 1) {
            compressed.append(character);
        } else if (count == 2) {
            compressed.append(character)
                      .append(character);
        } else {
            compressed.append(determineRepeatSymbolForHexCharacterCount(count))
                      .append(character);
        }
    }

    private String determineRepeatSymbolForHexCharacterCount(int count)
    {
        final StringBuilder symbol = new StringBuilder();

        while (count >= 20) {
            final int multiplesOf20 = Math.min(count / 20, 20);
            symbol.append((char) ('g' + multiplesOf20 - 1));
            count -= multiplesOf20 * 20;
        }

        if (count > 0) {
            symbol.append((char) ('G' + count - 1));
        }

        return symbol.toString();
    }
}
