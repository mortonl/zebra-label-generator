package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.formatting.FontEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.mortonl.zebra.formatting.FontEncoding.UTF_8;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("InternationalCharacterSet encoding configuration")
@Tag("unit")
@Tag("character-set")
class InternationalCharacterSetTest
{
    private static final String EXPECTED_NULL_ENCODING_MESSAGE = "Encoding cannot be null";

    private InternationalCharacterSet classUnderTest;

    @BeforeEach
    void setUp()
    {
        classUnderTest = InternationalCharacterSet
            .createInternationalCharacterSet()
            .withEncoding(UTF_8)
            .build();
    }

    @Test
    @DisplayName("build creates character set with valid encoding")
    @Tag("builder")
    void Given_ValidEncoding_When_Build_Then_CreatesCharacterSet()
    {
        // Given (classUnderTest is built with UTF_8 encoding)

        // When & Then
        assertNotNull(classUnderTest);
        assertEquals(UTF_8, classUnderTest.getEncoding());
    }

    @Test
    @DisplayName("toZplString generates ZPL with encoding value")
    @Tag("zpl-generation")
    void Given_CharacterSet_When_ToZplString_Then_ContainsEncodingValue()
    {
        // Given (classUnderTest has UTF_8 encoding)

        // When
        String actualResult = classUnderTest.toZplString(DPI_203);

        // Then
        assertTrue(actualResult.contains(UTF_8.getValue()));
    }

    @Test
    @DisplayName("validateInContext accepts valid encoding")
    @Tag("validation")
    void Given_ValidEncoding_When_ValidateInContext_Then_NoException()
    {
        // Given (classUnderTest has valid UTF_8 encoding)

        // When & Then
        assertDoesNotThrow(() -> classUnderTest.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @Test
    @DisplayName("validateInContext throws exception for null encoding")
    @Tag("validation")
    void Given_NullEncoding_When_ValidateInContext_Then_ThrowsException()
    {
        // Given
        InternationalCharacterSet nullEncodingCharSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .withEncoding(null)
            .build();

        // When & Then
        IllegalArgumentException actualException = assertThrows(
            IllegalArgumentException.class,
            () -> nullEncodingCharSet.validateInContext(LABEL_4X6, DPI_203, null)
        );

        assertEquals(EXPECTED_NULL_ENCODING_MESSAGE, actualException.getMessage());
    }

    @Test
    @DisplayName("getEncoding returns configured encoding")
    @Tag("getter")
    void Given_ConfiguredEncoding_When_GetEncoding_Then_ReturnsEncoding()
    {
        // Given (classUnderTest has UTF_8 encoding)

        // When
        FontEncoding actualEncoding = classUnderTest.getEncoding();

        // Then
        assertEquals(UTF_8, actualEncoding);
    }

    @Test
    @DisplayName("build creates character set with null encoding when not specified")
    @Tag("builder")
    void Given_NoEncoding_When_Build_Then_EncodingIsNull()
    {
        // Given & When
        InternationalCharacterSet actualCharSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .build();

        // Then
        assertNull(actualCharSet.getEncoding());
    }
}
