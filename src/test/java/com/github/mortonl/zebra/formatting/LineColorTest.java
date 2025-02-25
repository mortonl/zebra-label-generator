package com.github.mortonl.zebra.formatting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LineColorTest
{

    @Test
    void testGetCode()
    {
        assertEquals('B', LineColor.BLACK.getCode());
        assertEquals('W', LineColor.WHITE.getCode());
    }

    @Test
    void testFromCode_ValidBlack()
    {
        assertEquals(LineColor.BLACK, LineColor.fromCode('B'));
    }

    @Test
    void testFromCode_ValidWhite()
    {
        assertEquals(LineColor.WHITE, LineColor.fromCode('W'));
    }

    @Test
    void testFromCode_InvalidCode()
    {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> LineColor.fromCode('X')
        );

        assertEquals(
            "Invalid color code: 'X'. Must be 'B' for black or 'W' for white",
            exception.getMessage()
        );
    }

    @Test
    void testEnumValues()
    {
        LineColor[] colors = LineColor.values();
        assertEquals(2, colors.length);
        assertTrue(contains(colors, LineColor.BLACK));
        assertTrue(contains(colors, LineColor.WHITE));
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
