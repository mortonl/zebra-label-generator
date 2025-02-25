package com.github.mortonl.zebra.formatting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LineColor
{
    BLACK('B'),
    WHITE('W');

    private final char code;

    /**
     * Find LineColor by ZPL code
     *
     * @param code the ZPL code ('B' or 'W')
     * @return the matching LineColor
     * @throws IllegalArgumentException if code is invalid
     */
    public static LineColor fromCode(char code)
    {
        for (LineColor color : values()) {
            if (color.code == code) {
                return color;
            }
        }
        throw new IllegalArgumentException(
            "Invalid color code: '" + code + "'. Must be 'B' for black or 'W' for white"
        );
    }
}
