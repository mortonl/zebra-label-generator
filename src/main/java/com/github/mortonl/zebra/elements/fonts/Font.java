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
     * Minimum size in printer dots for font dimensions.
     * Used to validate both height and width measurements after conversion from millimeters.
     * This ensures fonts remain readable and within printer capabilities.
     */
    private static final int MIN_DOTS = 10;

    /**
     * Maximum size in printer dots for font dimensions.
     * Used to validate both height and width measurements after conversion from millimeters.
     * This limit is based on printer hardware constraints.
     */
    private static final int MAX_DOTS = 32000;

    /**
     * Single character designation identifying the font.
     * Valid values are A-Z and 0-9.
     *
     * <p>Built-in font types:</p>
     * <ul>
     *     <li>A-H: Bitmap fonts (fixed-size, device-specific)</li>
     *     <li>0: Default scalable font (vector-based, resolution independent)</li>
     *     <li>1-9: Additional scalable fonts when available</li>
     *     <li>I-Z: Reserved for downloaded fonts</li>
     * </ul>
     *
     * <p>Font characteristics:</p>
     * <ul>
     *     <li>Bitmap fonts (A-H) offer fastest printing but limited scaling</li>
     *     <li>Scalable fonts (0-9) provide smooth scaling but slower printing</li>
     *     <li>Downloaded fonts require prior installation on the printer</li>
     * </ul>
     *
     * @param fontDesignation single character identifying the font
     * @return the font designation character
     */
    private char fontDesignation;

    /**
     * The orientation/rotation of the font relative to the label.
     * Controls how text is rotated when printed.
     *
     * <p>If null, the printer's default orientation is used (typically NORMAL).
     * Rotation is applied clockwise from the normal position.</p>
     *
     * @param orientation the desired text rotation, or null for printer default
     * @return the current font orientation
     * @see Orientation for available rotation values
     */
    private Orientation orientation;

    /**
     * The height of the font in millimeters.
     * Determines the vertical size of the printed characters.
     *
     * <p>Constraints:</p>
     * <ul>
     *     <li>Minimum: {@value MIN_DOTS}/{@code DPI} millimeters</li>
     *     <li>Maximum: {@value MAX_DOTS}/{@code DPI} millimeters</li>
     *     <li>For bitmap fonts (A-H): Some sizes may not scale smoothly</li>
     *     <li>For scalable fonts: Any size within range is supported</li>
     * </ul>
     *
     * @param heightMm the desired font height in millimeters
     * @return the font height in millimeters
     */
    private double heightMm;

    /**
     * The width of the font in millimeters.
     * Determines the horizontal size of the printed characters.
     *
     * <p>Constraints:</p>
     * <ul>
     *     <li>Minimum: {@value MIN_DOTS}/{@code DPI} millimeters</li>
     *     <li>Maximum: {@value MAX_DOTS}/{@code DPI} millimeters</li>
     *     <li>For bitmap fonts (A-H): Some sizes may not scale smoothly</li>
     *     <li>For scalable fonts: Any size within range is supported</li>
     * </ul>
     *
     * <p>Setting width to 0 enables automatic proportional spacing based on height.</p>
     *
     * @param widthMm the desired font width in millimeters, or 0 for proportional
     * @return the font width in millimeters
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
