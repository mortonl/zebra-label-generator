package com.github.mortonl.zebra.formatting;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.mortonl.zebra.formatting.LineColor.BLACK;
import static com.github.mortonl.zebra.formatting.LineColor.WHITE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("LineColor enum functionality")
@Tag("unit")
@Tag("formatting")
class LineColorTest
{

    private static final char BLACK_CODE = 'B';

    private static final char WHITE_CODE = 'W';

    private static final char INVALID_CODE = 'X';

    private static final String EXPECTED_INVALID_CODE_MESSAGE = "Invalid color code: 'X'. Must be 'B' for black or 'W' for white";

    private static final int EXPECTED_ENUM_COUNT = 2;

    private static LineColor classUnderTest;

    @BeforeAll
    static void setUp()
    {
        classUnderTest = BLACK; // Using BLACK as default for stateless enum testing
    }

    @Test
    @DisplayName("getCode returns correct character codes")
    @Tag("enum-methods")
    void Given_LineColorValues_When_GetCode_Then_ReturnsCorrectCodes()
    {
        // Given (enum values)

        // When & Then
        assertAll(
            () -> assertEquals(BLACK_CODE, BLACK.getCode()),
            () -> assertEquals(WHITE_CODE, WHITE.getCode())
        );
    }

    @Test
    @DisplayName("fromCode returns BLACK for valid black code")
    @Tag("enum-methods")
    void Given_BlackCode_When_FromCode_Then_ReturnsBlack()
    {
        // Given (BLACK_CODE constant)

        // When
        LineColor actualColor = LineColor.fromCode(BLACK_CODE);

        // Then
        assertEquals(BLACK, actualColor);
    }

    @Test
    @DisplayName("fromCode returns WHITE for valid white code")
    @Tag("enum-methods")
    void Given_WhiteCode_When_FromCode_Then_ReturnsWhite()
    {
        // Given (WHITE_CODE constant)

        // When
        LineColor actualColor = LineColor.fromCode(WHITE_CODE);

        // Then
        assertEquals(WHITE, actualColor);
    }

    @Test
    @DisplayName("fromCode throws exception for invalid code")
    @Tag("enum-methods")
    void Given_InvalidCode_When_FromCode_Then_ThrowsException()
    {
        // Given (INVALID_CODE constant)

        // When & Then
        IllegalArgumentException actualException = assertThrows(
            IllegalArgumentException.class,
            () -> LineColor.fromCode(INVALID_CODE)
        );

        assertEquals(EXPECTED_INVALID_CODE_MESSAGE, actualException.getMessage());
    }

    @Test
    @DisplayName("values returns all enum constants")
    @Tag("enum-methods")
    void Given_EnumClass_When_Values_Then_ReturnsAllConstants()
    {
        // Given & When
        LineColor[] actualColors = LineColor.values();

        // Then
        assertAll(
            () -> assertEquals(EXPECTED_ENUM_COUNT, actualColors.length),
            () -> assertTrue(contains(actualColors, BLACK)),
            () -> assertTrue(contains(actualColors, WHITE))
        );
    }

    private boolean contains(LineColor[] colors, LineColor target)
    {
        for (LineColor color : colors) {
            if (color == target) {
                return true;
            }
        }
        return false;
    }
}
