package com.github.mortonl.zebra.elements.fields;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.FIELD_START;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Field data handling and ZPL generation")
@Tag("unit")
@Tag("field")
class FieldTest
{

    private static final String VALID_TEST_DATA = "Test Data";

    private static final String EXPECTED_BASIC_ZPL = FIELD_START + "Test Data" + FIELD_END;

    private static final String EXPECTED_NULL_DATA_MESSAGE = "Field Data cannot be null";

    private static final String EXPECTED_HEX_ENABLED_ZPL = "^FH^FDtest^FS";

    private static final String EXPECTED_HEX_DISABLED_ZPL = "^FDtest^FS";

    private static final String TEST_HEX_DATA = "test";

    private Field classUnderTest;

    @BeforeEach
    void setUp()
    {
        classUnderTest = Field.createField()
                              .withData(VALID_TEST_DATA)
                              .build();
    }

    @Test
    @DisplayName("toZplString generates correct ZPL for basic field")
    @Tag("zpl-generation")
    void Given_BasicField_When_ToZplString_Then_GeneratesCorrectZpl()
    {
        // Given (classUnderTest is already configured with valid data)

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_BASIC_ZPL, actualZplString);
    }

    @ParameterizedTest(name = "validateInContext accepts data \"{0}\"")
    @ValueSource(strings = {
        "Test Data",
        "123",
        " ",
        "Special Characters !@#$%"
    })
    @DisplayName("validateInContext accepts valid field data")
    @Tag("validation")
    void Given_ValidData_When_ValidateInContext_Then_NoException(String validData)
    {
        // Given
        Field validField = Field.createField()
                                .withData(validData)
                                .build();

        // When & Then
        assertDoesNotThrow(() -> validField.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext rejects {0} data")
    @NullSource
    @DisplayName("validateInContext throws exception for null data")
    @Tag("validation")
    void Given_NullData_When_ValidateInContext_Then_ThrowsException(String invalidData)
    {
        // Given
        Field invalidField = Field.createField()
                                  .withData(invalidData)
                                  .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> invalidField.validateInContext(LABEL_4X6, DPI_203, null)
        );
        assertEquals(EXPECTED_NULL_DATA_MESSAGE, actualException.getMessage());
    }

    @Test
    @DisplayName("toZplString includes hex indicator when hex characters enabled")
    @Tag("zpl-generation")
    void Given_HexEnabled_When_ToZplString_Then_IncludesHexIndicator()
    {
        // Given
        Field hexEnabledField = Field.createField()
                                     .withData(TEST_HEX_DATA)
                                     .withEnableHexCharacters(true)
                                     .build();

        // When
        String actualZplString = hexEnabledField.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_HEX_ENABLED_ZPL, actualZplString);
    }

    @Test
    @DisplayName("toZplString excludes hex indicator when hex characters disabled")
    @Tag("zpl-generation")
    void Given_HexDisabled_When_ToZplString_Then_ExcludesHexIndicator()
    {
        // Given
        Field hexDisabledField = Field.createField()
                                      .withData(TEST_HEX_DATA)
                                      .withEnableHexCharacters(false)
                                      .build();

        // When
        String actualZplString = hexDisabledField.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_HEX_DISABLED_ZPL, actualZplString);
    }

    @Test
    @DisplayName("toZplString excludes hex indicator when hex characters null")
    @Tag("zpl-generation")
    void Given_HexNull_When_ToZplString_Then_ExcludesHexIndicator()
    {
        // Given
        Field hexNullField = Field.createField()
                                  .withData(TEST_HEX_DATA)
                                  .withEnableHexCharacters(null)
                                  .build();

        // When
        String actualZplString = hexNullField.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_HEX_DISABLED_ZPL, actualZplString);
    }
}
