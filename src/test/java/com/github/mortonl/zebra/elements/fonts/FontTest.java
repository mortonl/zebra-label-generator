package com.github.mortonl.zebra.elements.fonts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.mortonl.zebra.formatting.Orientation.NORMAL;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Font creation and validation")
@Tag("unit")
@Tag("font")
class FontTest
{
    private static final char VALID_FONT_DESIGNATION = 'A';

    private static final double VALID_WIDTH_MM = 4.0;

    private static final double VALID_HEIGHT_MM = 3.0;

    private static final String EXPECTED_ZPL_OUTPUT = "^AAN,24,32";

    private static final String EXPECTED_INVALID_FONT_MESSAGE = "Font name must be A-Z or 0-9";

    private Font classUnderTest;

    @BeforeEach
    void setUp()
    {
        classUnderTest = Font.createFont()
                             .withFontDesignation(VALID_FONT_DESIGNATION)
                             .withOrientation(NORMAL)
                             .withSize(VALID_WIDTH_MM, VALID_HEIGHT_MM)
                             .build();
    }

    @Test
    @DisplayName("toZplString generates correct ZPL command")
    @Tag("zpl-generation")
    void Given_ValidFont_When_ToZplString_Then_GeneratesCorrectZpl()
    {
        // Given (classUnderTest is already configured)

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_ZPL_OUTPUT, actualZplString);
    }

    @Test
    @DisplayName("validateInContext accepts valid font")
    @Tag("validation")
    void Given_ValidFont_When_ValidateInContext_Then_NoException()
    {
        // Given (classUnderTest is already configured with valid font)

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext rejects font designation {0}")
    @ValueSource(chars = {'a',
        'z',
        '#',
        '$',
        ' ',
        'ñ'})
    @DisplayName("validateInContext throws exception for invalid font designation")
    @Tag("validation")
    void Given_InvalidDesignation_When_ValidateInContext_Then_ThrowsException(char invalidDesignation)
    {
        // Given
        Font invalidFont = Font.createFont()
                               .withFontDesignation(invalidDesignation)
                               .withSize(VALID_WIDTH_MM, VALID_HEIGHT_MM)
                               .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> invalidFont.validateInContext(LABEL_4X6, DPI_203, null)
        );
        assertEquals(EXPECTED_INVALID_FONT_MESSAGE, actualException.getMessage());
    }

    @ParameterizedTest(name = "validateInContext accepts font designation {0}")
    @ValueSource(chars = {'A',
        'Z',
        '0',
        '9',
        'M',
        '5'})
    @DisplayName("validateInContext accepts valid font designations")
    @Tag("validation")
    void Given_ValidDesignation_When_ValidateInContext_Then_NoException(char validDesignation)
    {
        // Given
        Font validFont = Font.createFont()
                             .withFontDesignation(validDesignation)
                             .withSize(VALID_WIDTH_MM, VALID_HEIGHT_MM)
                             .build();

        // When & Then
        assertDoesNotThrow(() -> validFont.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext rejects {2}")
    @CsvSource({
        "height,0.1,Font height below minimum (0.10mm) should be rejected,Font height 0.10mm is too small. Minimum height is 1.25mm for 203 DPI / 8 dots per mm",
        "height,4001.0,Font height above maximum (4001mm) should be rejected,Font height 4001.00mm is too large. Maximum height is 4000.00mm for 203 DPI / 8 dots per mm",
        "width,0.1,Font width below minimum (0.10mm) should be rejected,Font width 0.10mm is too small. Minimum width is 1.25mm for 203 DPI / 8 dots per mm",
        "width,4001.0,Font width above maximum (4001mm) should be rejected,Font width 4001.00mm is too large. Maximum width is 4000.00mm for 203 DPI / 8 dots per mm"
    })
    @DisplayName("validateInContext throws exception for invalid dimensions")
    @Tag("validation")
    void Given_InvalidDimensions_When_ValidateInContext_Then_ThrowsException(
        String dimension,
        double invalidValue,
        String testDescription,
        String expectedMessage
    )
    {
        // Given
        Font invalidFont = Font.createFont()
                               .withFontDesignation(VALID_FONT_DESIGNATION)
                               .withSize("width".equals(dimension) ? invalidValue : 2.0, "height".equals(dimension) ? invalidValue : 2.0)
                               .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> invalidFont.validateInContext(LABEL_4X6, DPI_203, null),
            testDescription
        );

        assertEquals(expectedMessage, actualException.getMessage());
    }

    @ParameterizedTest(name = "validateInContext accepts {2}")
    @CsvSource({
        "height,1.25,Minimum allowed font height (1.25mm) should be valid",
        "height,4000.0,Maximum allowed font height (4000mm) should be valid",
        "width,1.25,Minimum allowed font width (1.25mm) should be valid",
        "width,4000.0,Maximum allowed font width (4000mm) should be valid"
    })
    @DisplayName("validateInContext accepts valid boundary dimensions")
    @Tag("validation")
    void Given_ValidBoundaryDimensions_When_ValidateInContext_Then_NoException(
        String dimension,
        double boundaryValue,
        String testDescription
    )
    {
        // Given
        Font validFont = Font.createFont()
                             .withFontDesignation(VALID_FONT_DESIGNATION)
                             .withSize("width".equals(dimension) ? boundaryValue : 2.0, "height".equals(dimension) ? boundaryValue : 2.0)
                             .build();

        // When & Then
        assertDoesNotThrow(
            () -> validFont.validateInContext(LABEL_4X6, DPI_203, null),
            testDescription
        );
    }
}
