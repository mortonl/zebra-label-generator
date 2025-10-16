package com.github.mortonl.zebra.elements.barcodes;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.BARCODE_DEFAULTS;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
import static com.github.mortonl.zebra.validation.Validator.validateRange;

/**
 * Represents the ZPL II ^BY (Bar Code Field Default) command, which sets default
 * values for barcode module width (w), wide-to-narrow ratio (r), and height (h).
 * These defaults apply to subsequent barcode fields until another ^BY is issued.
 *
 * <p>Parameters:</p>
 * <ul>
 *     <li>w (module width, in dots): 1..10</li>
 *     <li>r (wide:narrow ratio): 2.0..3.0 (0.1 increments on the device)</li>
 *     <li>h (height, in dots): 1..32000 (specified here in millimetres and converted to dots)</li>
 * </ul>
 */
@Getter
@SuperBuilder(builderMethodName = "createBarcodeDefaults", setterPrefix = "with")
public class BarcodeDefaults extends LabelElement
{
    /** Module width (X dimension) in millimetres. Converted to dots using the provided DPI. Nullable: omitted if null. */
    private final Double moduleWidthMm;

    /** Wide-to-narrow ratio for applicable symbologies. Nullable: omitted if null. */
    private final Double wideToNarrowRatio;

    /**
     * Default barcode height in millimetres. Converted to dots using the provided DPI.
     * Nullable: omitted if null.
     */
    private final Double heightMm;

    @Override
    public String toZplString(PrintDensity dpi)
    {
        Integer moduleWidthDots = moduleWidthMm == null ? null : dpi.toDots(moduleWidthMm);
        Integer heightDots = heightMm == null ? null : dpi.toDots(heightMm);
        return generateZplIICommand(BARCODE_DEFAULTS, moduleWidthDots, wideToNarrowRatio, heightDots);
    }

    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        // Validate ranges when provided
        if (moduleWidthMm != null) {
            validateRange(dpi.toDots(moduleWidthMm), 1, 10, "Module width (dots)");
        }
        if (wideToNarrowRatio != null) {
            // Range check per ZPL spec
            validateRange(wideToNarrowRatio, 2.0, 3.0, "Wide-to-narrow ratio");

            // Ensure the ratio is specified in 0.1 increments (for example 2.0, 2.1, ..., 3.0)
            double scaled = Math.round(wideToNarrowRatio * 10.0);
            double reconstructed = scaled / 10.0;
            double delta = Math.abs(wideToNarrowRatio - reconstructed);
            if (delta > 1e-9) {
                throw new IllegalStateException(
                    String.format("Wide-to-narrow ratio must be in 0.1 increments between 2.0 and 3.0, but was %.3f", wideToNarrowRatio)
                );
            }
        }
        if (heightMm != null) {
            // Convert to dots and validate within ZPL limits
            int heightDots = dpi.toDots(heightMm);
            validateRange(heightDots, 1, 32000, "Bar code height (dots)");

            // Additional contextual validation: height should not exceed label length
            int maxLabelHeightDots = size.getHeightInDots(dpi);
            if (heightDots > maxLabelHeightDots) {
                throw new IllegalStateException(
                    String.format(
                        "Bar code height (dots) must not exceed label height (%d dots), but was %d",
                        maxLabelHeightDots, heightDots
                    )
                );
            }
        }
    }
}
