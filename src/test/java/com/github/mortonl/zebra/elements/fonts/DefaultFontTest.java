package com.github.mortonl.zebra.elements.fonts;

import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("DefaultFont Tests")
@Tag("unit")
@Tag("font")
class DefaultFontTest
{

    private static final char VALID_FONT_DESIGNATION = 'A';

    private static final double VALID_HEIGHT_MM = 5.0;

    private static final double VALID_WIDTH_MM = 3.0;

    private static final String EXPECTED_ZPL_COMMAND_PREFIX = "^CF";

    private static final String DUPLICATE_DEFAULT_ERROR = "Font specification matches current default font - For efficiency don't specify the font on this element";

    private final Random random = new Random(42);

    private DefaultFont classUnderTest;

    private PrintDensity testDpi;

    private LabelSize testLabelSize;

    @BeforeEach
    void setUp()
    {
        testDpi       = PrintDensity.DPI_203;
        testLabelSize = LabelSize.LABEL_4X6;

        classUnderTest = DefaultFont.createDefaultFont()
                                    .withFontDesignation(VALID_FONT_DESIGNATION)
                                    .withHeightMm(VALID_HEIGHT_MM)
                                    .withWidthMm(VALID_WIDTH_MM)
                                    .build();
    }

    @Test
    @DisplayName("toZplString generates correct ZPL command with valid parameters")
    @Tag("zpl-generation")
    void givenValidFont_whenToZplString_thenGeneratesCorrectCommand()
    {
        // Given
        int    expectedHeightDots = testDpi.toDots(VALID_HEIGHT_MM);
        int    expectedWidthDots  = testDpi.toDots(VALID_WIDTH_MM);
        String expectedZplCommand = EXPECTED_ZPL_COMMAND_PREFIX + VALID_FONT_DESIGNATION + "," + expectedHeightDots + "," + expectedWidthDots;

        // When
        String actualZplCommand = classUnderTest.toZplString(testDpi);

        // Then
        assertEquals(expectedZplCommand, actualZplCommand);
    }

    @ParameterizedTest
    @DisplayName("toZplString handles different DPI values correctly")
    @Tag("zpl-generation")
    @CsvSource({
        "DPI_152, 152, 6",
        "DPI_203, 203, 8",
        "DPI_300, 300, 12",
        "DPI_600, 600, 24"
    })
    void givenDifferentDpi_whenToZplString_thenConvertsCorrectly(String dpiName, int dotsPerInch, int dotsPerMm)
    {
        // Given
        PrintDensity dpi                = PrintDensity.valueOf(dpiName);
        int          expectedHeightDots = dpi.toDots(VALID_HEIGHT_MM);
        int          expectedWidthDots  = dpi.toDots(VALID_WIDTH_MM);
        String       expectedZplCommand = EXPECTED_ZPL_COMMAND_PREFIX + VALID_FONT_DESIGNATION + "," + expectedHeightDots + "," + expectedWidthDots;

        // When
        String actualZplCommand = classUnderTest.toZplString(dpi);

        // Then
        assertEquals(expectedZplCommand, actualZplCommand);
    }

    @ParameterizedTest
    @DisplayName("toZplString handles different font designations correctly")
    @Tag("zpl-generation")
    @ValueSource(chars = {'A',
        'B',
        'Z',
        '0',
        '1',
        '9'})
    void givenDifferentFontDesignations_whenToZplString_thenIncludesCorrectDesignation(char fontDesignation)
    {
        // Given
        DefaultFont font = DefaultFont.createDefaultFont()
                                      .withFontDesignation(fontDesignation)
                                      .withHeightMm(VALID_HEIGHT_MM)
                                      .withWidthMm(VALID_WIDTH_MM)
                                      .build();

        // When
        String actualZplCommand = font.toZplString(testDpi);

        // Then
        assertEquals(EXPECTED_ZPL_COMMAND_PREFIX + fontDesignation + "," + testDpi.toDots(VALID_HEIGHT_MM) + "," + testDpi.toDots(VALID_WIDTH_MM), actualZplCommand);
    }

    @Test
    @DisplayName("toZplString handles random valid dimensions correctly")
    @Tag("zpl-generation")
    void givenRandomValidDimensions_whenToZplString_thenGeneratesValidCommand()
    {
        // Given
        double randomHeight = 1.0 + random.nextDouble() * 10.0;
        double randomWidth  = 1.0 + random.nextDouble() * 10.0;
        DefaultFont font = DefaultFont.createDefaultFont()
                                      .withFontDesignation(VALID_FONT_DESIGNATION)
                                      .withHeightMm(randomHeight)
                                      .withWidthMm(randomWidth)
                                      .build();

        int    expectedHeightDots = testDpi.toDots(randomHeight);
        int    expectedWidthDots  = testDpi.toDots(randomWidth);
        String expectedZplCommand = EXPECTED_ZPL_COMMAND_PREFIX + VALID_FONT_DESIGNATION + "," + expectedHeightDots + "," + expectedWidthDots;

        // When
        String actualZplCommand = font.toZplString(testDpi);

        // Then
        assertEquals(expectedZplCommand, actualZplCommand);
    }

    @Test
    @DisplayName("validateInContext passes with valid font and no existing default")
    @Tag("validation")
    void givenValidFont_whenValidateInContext_thenNoException()
    {
        // Given - valid font setup in setUp()

        // When & Then
        classUnderTest.validateInContext(testLabelSize, testDpi, null);
    }

    @Test
    @DisplayName("validateInContext passes with valid font and different existing default")
    @Tag("validation")
    void givenValidFontAndDifferentDefault_whenValidateInContext_thenNoException()
    {
        // Given
        DefaultFont existingDefault = DefaultFont.createDefaultFont()
                                                 .withFontDesignation('B')
                                                 .withHeightMm(VALID_HEIGHT_MM)
                                                 .withWidthMm(VALID_WIDTH_MM)
                                                 .build();

        // When & Then
        classUnderTest.validateInContext(testLabelSize, testDpi, existingDefault);
    }

    @Test
    @DisplayName("validateInContext throws exception when identical to existing default")
    @Tag("validation")
    void givenIdenticalToExistingDefault_whenValidateInContext_thenThrowsException()
    {
        // Given
        DefaultFont existingDefault = DefaultFont.createDefaultFont()
                                                 .withFontDesignation(VALID_FONT_DESIGNATION)
                                                 .withHeightMm(VALID_HEIGHT_MM)
                                                 .withWidthMm(VALID_WIDTH_MM)
                                                 .build();

        // When
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> classUnderTest.validateInContext(testLabelSize, testDpi, existingDefault));

        // Then
        assertEquals(DUPLICATE_DEFAULT_ERROR, actualException.getMessage());
    }

    @ParameterizedTest
    @DisplayName("validateInContext throws exception for invalid font designations")
    @Tag("validation")
    @ValueSource(chars = {'a',
        'z',
        '!',
        '@',
        '#',
        ' '})
    void givenInvalidFontDesignation_whenValidateInContext_thenThrowsException(char invalidDesignation)
    {
        // Given
        DefaultFont font = DefaultFont.createDefaultFont()
                                      .withFontDesignation(invalidDesignation)
                                      .withHeightMm(VALID_HEIGHT_MM)
                                      .withWidthMm(VALID_WIDTH_MM)
                                      .build();

        // When
        IllegalStateException actualException = assertThrows(IllegalStateException.class,
            () -> font.validateInContext(testLabelSize, testDpi, null));

        // Then
        assertEquals("Font name must be A-Z or 0-9", actualException.getMessage());
    }

    @Test
    @DisplayName("validateInContext throws exception for too small dimensions")
    @Tag("validation")
    void givenTooSmallDimensions_whenValidateInContext_thenThrowsException()
    {
        // Given
        double tooSmallSize = 0.1;
        DefaultFont font = DefaultFont.createDefaultFont()
                                      .withFontDesignation(VALID_FONT_DESIGNATION)
                                      .withHeightMm(tooSmallSize)
                                      .withWidthMm(tooSmallSize)
                                      .build();

        // When & Then
        assertThrows(IllegalStateException.class,
            () -> font.validateInContext(testLabelSize, testDpi, null));
    }

    @Test
    @DisplayName("validateInContext throws exception for too large dimensions")
    @Tag("validation")
    void givenTooLargeDimensions_whenValidateInContext_thenThrowsException()
    {
        // Given
        double tooLargeSize = 5000.0;
        DefaultFont font = DefaultFont.createDefaultFont()
                                      .withFontDesignation(VALID_FONT_DESIGNATION)
                                      .withHeightMm(tooLargeSize)
                                      .withWidthMm(tooLargeSize)
                                      .build();

        // When & Then
        assertThrows(IllegalStateException.class,
            () -> font.validateInContext(testLabelSize, testDpi, null));
    }

    @Test
    @DisplayName("builder creates font with all properties set correctly")
    @Tag("builder")
    void givenBuilderWithAllProperties_whenBuild_thenCreatesCorrectFont()
    {
        // Given
        char   expectedDesignation = 'Z';
        double expectedHeight      = 7.5;
        double expectedWidth       = 4.2;

        // When
        DefaultFont actualFont = DefaultFont.createDefaultFont()
                                            .withFontDesignation(expectedDesignation)
                                            .withHeightMm(expectedHeight)
                                            .withWidthMm(expectedWidth)
                                            .build();

        // Then
        assertAll(
            () -> assertEquals(expectedDesignation, actualFont.getFontDesignation()),
            () -> assertEquals(expectedHeight, actualFont.getHeightMm()),
            () -> assertEquals(expectedWidth, actualFont.getWidthMm())
        );
    }

    @Test
    @DisplayName("builder withSize method sets both dimensions correctly")
    @Tag("builder")
    void givenBuilderWithSize_whenBuild_thenSetsBothDimensions()
    {
        // Given
        double expectedWidth  = 6.0;
        double expectedHeight = 8.0;

        // When
        DefaultFont actualFont = DefaultFont.createDefaultFont()
                                            .withFontDesignation(VALID_FONT_DESIGNATION)
                                            .withSize(expectedWidth, expectedHeight)
                                            .build();

        // Then
        assertAll(
            () -> assertEquals(expectedWidth, actualFont.getWidthMm()),
            () -> assertEquals(expectedHeight, actualFont.getHeightMm())
        );
    }
}
