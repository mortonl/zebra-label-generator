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
    @Test
    void testToZplString()
    {
        Field field = Field
            .builder()
            .data("Test Data")
            .build();
        String expected = FIELD_START + "Test Data" + FIELD_END;
        assertEquals(expected, field.toZplString(PrintDensity.DPI_203));
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
        Field field = Field
            .builder()
            .data(validData)
            .build();
        assertDoesNotThrow(() -> field.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "Field with {0} data should be rejected")
    @NullSource
    void testValidateInContext_InvalidData(String invalidData)
    {
        Field field = Field
            .builder()
            .data(invalidData)
            .build();
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> field.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203)
        );
        assertEquals("Field Data cannot be null", exception.getMessage());
    }

    @Test
    void testHexadecimalCharactersEnabled()
    {
        Field field = Field
            .builder()
            .data("test")
            .enableHexCharacters(true)
            .build();

        assertEquals("^FH^FDtest^FS", field.toZplString(PrintDensity.DPI_203));
    }

    @Test
    void testHexadecimalCharactersDisabled()
    {
        Field field = Field
            .builder()
            .data("test")
            .enableHexCharacters(false)
            .build();

        assertEquals("^FDtest^FS", field.toZplString(PrintDensity.DPI_203));
    }

    @Test
    void testHexadecimalCharactersNull()
    {
        Field field = Field
            .builder()
            .data("test")
            .enableHexCharacters(null)
            .build();

        assertEquals("^FDtest^FS", field.toZplString(PrintDensity.DPI_203));
    }
}
