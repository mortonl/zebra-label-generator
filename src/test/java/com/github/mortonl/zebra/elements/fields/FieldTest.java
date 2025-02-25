package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.FIELD_START;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldTest
{
    private final PrintDensity testDpi = PrintDensity.DPI_203;
    private final LabelSize testSize = LabelSize.LABEL_4X6;

    @Test
    void testToZplString()
    {
        Field field = new Field("Test Data");
        String expected = FIELD_START + "Test Data" + FIELD_END;
        assertEquals(expected, field.toZplString(testDpi));
    }

    @ParameterizedTest(name = "Field with data \"{0}\" should be valid")
    @ValueSource(strings = {
        "Test Data",
        "123",
        " ",
        "Special Characters !@#$%"
    })
    void testValidateInContext_ValidData(String validData)
    {
        Field field = new Field(validData);
        assertDoesNotThrow(() -> field.validateInContext(testSize, testDpi));
    }

    @ParameterizedTest(name = "Field with {0} data should be rejected")
    @NullSource
    void testValidateInContext_InvalidData(String invalidData)
    {
        Field field = new Field(invalidData);
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> field.validateInContext(testSize, testDpi)
        );
        assertEquals("Field Data cannot be null", exception.getMessage());
    }
}
