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
    @Test
    void testBuilder_ValidEncoding()
    {
        InternationalCharacterSet charSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .withEncoding(FontEncoding.UTF_8)
            .build();

        assertNotNull(charSet);
        assertEquals(FontEncoding.UTF_8, charSet.getEncoding());
    }

    @Test
    void testToZplString()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .withEncoding(FontEncoding.UTF_8)
            .build();

        // Act
        String result = charSet.toZplString(PrintDensity.DPI_203);

        // Assert
        assertTrue(result.contains(FontEncoding.UTF_8.getValue()));
    }

    @Test
    void testValidateInContext_ValidEncoding()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .withEncoding(FontEncoding.UTF_8)
            .build();

        // Act & Assert
        assertDoesNotThrow(() -> charSet.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @Test
    void testValidateInContext_NullEncoding()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .withEncoding(null)
            .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> charSet.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203)
        );

        assertEquals("Encoding cannot be null", exception.getMessage());
    }

    @Test
    void testGetEncoding()
    {
        // Arrange
        InternationalCharacterSet charSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .withEncoding(FontEncoding.UTF_8)
            .build();

        // Act & Assert
        assertEquals(FontEncoding.UTF_8, charSet.getEncoding());
    }

    @Test
    void testBuilder_BuildWithoutEncoding()
    {
        // Act
        InternationalCharacterSet charSet = InternationalCharacterSet
            .createInternationalCharacterSet()
            .build();

        // Assert
        assertNull(charSet.getEncoding());
    }
}
