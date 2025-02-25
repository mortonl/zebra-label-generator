package com.github.mortonl.zebra.label_settings;

import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InternationalCharacterSetTest
{
    private final FontEncoding testEncoding = FontEncoding.UTF_8;
    private final PrintDensity testDpi = PrintDensity.DPI_203;
    private final LabelSize testSize = LabelSize.LABEL_4X6;

    @Test
    void testBuilder_ValidEncoding()
    {
        InternationalCharacterSet charSet = InternationalCharacterSet
                .builder()
                .encoding(testEncoding)
                .build();

        assertNotNull(charSet);
        assertEquals(testEncoding, charSet.getEncoding());
    }

    @Test
    void testToZplString()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
                .builder()
                .encoding(testEncoding)
                .build();

        // Act
        String result = charSet.toZplString(testDpi);

        // Assert
        assertTrue(result.contains(testEncoding.getValue()));
    }

    @Test
    void testValidateInContext_ValidEncoding()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
                .builder()
                .encoding(testEncoding)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> charSet.validateInContext(testSize, testDpi));
    }

    @Test
    void testValidateInContext_NullEncoding()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
                .builder()
                .encoding(null)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> charSet.validateInContext(testSize, testDpi)
        );

        assertEquals("Encoding cannot be null", exception.getMessage());
    }

    @Test
    void testGetEncoding()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
                .builder()
                .encoding(testEncoding)
                .build();

        // Act & Assert
        assertEquals(testEncoding, charSet.getEncoding());
    }

    @Test
    void testBuilder_BuildWithoutEncoding()
    {
        // Act
        InternationalCharacterSet charSet = InternationalCharacterSet
                .builder()
                .build();

        // Assert
        assertNull(charSet.getEncoding());
    }
}
