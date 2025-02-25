package com.github.mortonl.zebra.validation;

import lombok.experimental.UtilityClass;

/**
 * Utility class for validating values against ranges and other constraints.
 */
@UtilityClass
public class Validator
{
    /**
     * Validates that a value falls within an inclusive range.
     *
     * @param value     The value to validate
     * @param min       The minimum allowed value (inclusive)
     * @param max       The maximum allowed value (inclusive)
     * @param fieldName The name of the field being validated (for error messages)
     * @throws IllegalStateException if the value is outside the allowed range
     */
    public static void validateRange(double value, double min, double max, String fieldName)
    {
        if (value < min || value > max) {
            throw new IllegalStateException(
                    String.format("%s must be between %.2f and %.2f, but was %.2f",
                            fieldName, min, max, value)
            );
        }
    }

    /**
     * Validates that an integer value falls within an inclusive range.
     *
     * @param value     The value to validate
     * @param min       The minimum allowed value (inclusive)
     * @param max       The maximum allowed value (inclusive)
     * @param fieldName The name of the field being validated (for error messages)
     * @throws IllegalStateException if the value is outside the allowed range
     */
    public static void validateRange(int value, int min, int max, String fieldName)
    {
        if (value < min || value > max) {
            throw new IllegalStateException(
                    String.format("%s must be between %d and %d, but was %d",
                            fieldName, min, max, value)
            );
        }
    }

    /**
     * Validates that a value is not null.
     *
     * @param value     The value to check for null
     * @param fieldName The name of the field being validated (for error messages)
     * @throws IllegalStateException if the value is null
     */
    public static void validateNotNull(Object value, String fieldName)
    {
        if (value == null) {
            throw new IllegalStateException(fieldName + " cannot be null");
        }
    }

    /**
     * Validates that a string is not null or empty.
     *
     * @param value     The string to validate
     * @param fieldName The name of the field being validated (for error messages)
     * @throws IllegalStateException if the string is null or empty
     */
    public static void validateNotEmpty(String value, String fieldName)
    {
        validateNotNull(value, fieldName);
        if (value
                .trim()
                .isEmpty())
        {
            throw new IllegalStateException(fieldName + " cannot be empty");
        }
    }
}
