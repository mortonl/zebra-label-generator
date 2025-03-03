package com.github.mortonl.zebra.elements.fonts;

import com.github.mortonl.zebra.ZplCommand;
import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.SET_FONT;

/**
 * {@inheritDoc}
 *
 * <p>Implements font configuration for ZPL II label elements. Fonts in ZPL are identified
 * by single-character designations and can be scaled and rotated. Supported fonts include
 * both bitmap and scalable varieties.</p>
 *
 * <p>Font characteristics:</p>
 * <ul>
 *     <li>Single character designation (A-Z, 0-9)</li>
 *     <li>Configurable height and width</li>
 *     <li>Optional rotation</li>
 *     <li>Size limits based on printer DPI</li>
 * </ul>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>{@code
 * // Basic font configuration
 * Font.createFont()
 *     .withFontDesignation('A')
 *     .withSize(2.0, 3.0)  // width and height in mm
 *     .withOrientation(Orientation.NORMAL)
 *     .addToLabel(label);
 *
 * // Rotated font with specific dimensions
 * Font.createFont()
 *     .withFontDesignation('0')
 *     .withSize(4.0, 6.0)
 *     .withOrientation(Orientation.ROTATE_90)
 *     .addToLabel(label);
 * }</pre>
 *
 * @see LabelElement The parent class for all label elements
 * @see Orientation Available orientation options
 */
@Getter
@SuperBuilder(builderMethodName = "createFont", setterPrefix = "with")
public class Font extends LabelElement
{
    /**
     * Minimum size in printer dots for font dimensions
     */
    private static final int MIN_DOTS = 10;

    /**
     * Maximum size in printer dots for font dimensions
     */
    private static final int MAX_DOTS = 32000;

    /**
     * Single character designation identifying the font.
     * Valid values are A-Z and 0-9.
     * <p>Common built-in fonts:</p>
     * <ul>
     *     <li>A-H: Bitmap fonts</li>
     *     <li>0: Default scalable font</li>
     *     <li>Other values may represent downloaded fonts</li>
     * </ul>
     */
    private char fontDesignation;

    /**
     * The orientation/rotation of the font.
     * If null, uses printer default orientation.
     *
     * @see Orientation
     */
    private Orientation orientation;

    /**
     * The height of the font in millimeters.
     * Must be between {@code MIN_DOTS/DPI} and {@code MAX_DOTS/DPI} millimeters.
     */
    private double heightMm;

    /**
     * The width of the font in millimeters.
     * Must be between {@code MIN_DOTS/DPI} and {@code MAX_DOTS/DPI} millimeters.
     */
    private double widthMm;

    /**
     * {@inheritDoc}
     *
     * <p>Generates a ZPL II font command including:</p>
     * <ul>
     *     <li>Font designation</li>
     *     <li>Orientation setting</li>
     *     <li>Height in dots</li>
     *     <li>Width in dots</li>
     * </ul>
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        int heightDots = dpi.toDots(heightMm);
        int widthDots = dpi.toDots(widthMm);

        return ZplCommand.generateZplIICommand(
            // Fonts are a special case where the commands first parameter (character designation) is used as part of the command itself
            SET_FONT + fontDesignation,
            orientation != null ? orientation.getValue() : null,
            heightDots,
            widthDots
        );
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates that:</p>
     * <ul>
     *     <li>Font designation is a valid character (A-Z, 0-9)</li>
     *     <li>Height is within valid range for the printer DPI</li>
     *     <li>Width is within valid range for the printer DPI</li>
     * </ul>
     *
     * @throws IllegalStateException if any validation fails
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi) throws IllegalStateException
    {
        validateFontName();
        validateDimension("height", heightMm, dpi);
        validateDimension("width", widthMm, dpi);
    }

    /**
     * Validates that the font designation is a valid character (A-Z, 0-9).
     *
     * @throws IllegalStateException if the font designation is invalid
     */
    private void validateFontName()
    {
        boolean isValidFont = (fontDesignation >= 'A' && fontDesignation <= 'Z') ||
            (fontDesignation >= '0' && fontDesignation <= '9');

        if (!isValidFont) {
            throw new IllegalStateException("Font name must be A-Z or 0-9");
        }
    }

    /**
     * Validates that a font dimension (height or width) is within the valid range for the given DPI.
     *
     * @param dimensionName  The name of the dimension being validated ("height" or "width")
     * @param dimensionValue The value in millimeters to validate
     * @param dpi            The printer density configuration
     * @throws IllegalStateException if the dimension is outside the valid range
     */
    private void validateDimension(String dimensionName, double dimensionValue, PrintDensity dpi)
    {
        int dots = dpi.toDots(dimensionValue);

        if (dots < MIN_DOTS) {
            throw new IllegalStateException(
                String.format("Font %s %.2fmm is too small. Minimum %s is %.2fmm for %d DPI / %s dots per mm",
                    dimensionName, dimensionValue, dimensionName,
                    dpi.toMillimetres(MIN_DOTS),
                    dpi.getDotsPerInch(),
                    dpi.getDotsPerMillimetre()));
        }
        if (dots > MAX_DOTS) {
            throw new IllegalStateException(
                String.format("Font %s %.2fmm is too large. Maximum %s is %.2fmm for %d DPI / %s dots per mm",
                    dimensionName, dimensionValue, dimensionName,
                    dpi.toMillimetres(MAX_DOTS),
                    dpi.getDotsPerInch(),
                    dpi.getDotsPerMillimetre()));
        }
    }

    /**
     * Builder class for Font objects providing a convenient method for setting both
     * width and height simultaneously.
     *
     * @param <C> The type of the Font being built
     * @param <B> The type of the Builder
     */
    public static abstract class FontBuilder<C extends Font, B extends FontBuilder<C, B>>
        extends LabelElementBuilder<C, B>
    {
        /**
         * Sets both width and height of the font in millimeters.
         *
         * @param widthMm  The width in millimeters
         * @param heightMm The height in millimeters
         * @return The builder instance for method chaining
         */
        public B withSize(double widthMm, double heightMm)
        {
            this.widthMm = widthMm;
            this.heightMm = heightMm;
            return self();
        }
    }
}
