package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.FIELD_START;
import static com.github.mortonl.zebra.elements.fields.Field.createField;
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
        Field field = createField()
            .withData("Test Data")
            .build();
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
        Field field = createField()
            .withData(validData)
            .build();
        assertDoesNotThrow(() -> field.validateInContext(testSize, testDpi));
    }

    @ParameterizedTest(name = "Field with {0} data should be rejected")
    @NullSource
    void testValidateInContext_InvalidData(String invalidData)
    {
        Field field = createField()
            .withData(invalidData)
            .build();
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> field.validateInContext(testSize, testDpi)
        );
        assertEquals("Field Data cannot be null", exception.getMessage());
    }

    @Test
    void testHexadecimalCharactersEnabled()
    {
        Field field = createField()
            .withData("test")
            .withEnableHexCharacters(true)
            .build();

        assertEquals("^FH^FDtest^FS", field.toZplString(testDpi));
    }

    @Test
    void testHexadecimalCharactersDisabled()
    {
        Field field = createField()
            .withData("test")
            .withEnableHexCharacters(false)
            .build();

        assertEquals("^FDtest^FS", field.toZplString(testDpi));
    }

    @Test
    void testHexadecimalCharactersNull()
    {
        Field field = createField()
            .withData("test")
            .withEnableHexCharacters(null)
            .build();

        assertEquals("^FDtest^FS", field.toZplString(testDpi));
    }
}
