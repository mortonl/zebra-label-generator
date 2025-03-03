package com.github.mortonl.zebra.printer_configuration;

import com.github.mortonl.zebra.label_settings.LabelSize;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents the physical media (labels) loaded in a Zebra printer.
 * Supports both fixed-length labels and variable-length (dynamic) media configurations.
 *
 * <p>The media configuration can be either:</p>
 * <ul>
 *     <li>Fixed length - for pre-cut labels of specific dimensions</li>
 *     <li>Dynamic length - for continuous media that can be cut to variable lengths</li>
 * </ul>
 *
 * <p>Example usage for fixed-length labels:</p>
 * <pre>{@code
 * LoadedMedia media = LoadedMedia.fromLabelSize(LabelSize.LABEL_4X6);
 * }</pre>
 *
 * <p>Example usage for dynamic-length labels:</p>
 * <pre>{@code
 * LoadedMedia media = LoadedMedia.fromLabelSizeDynamic(LabelSize.LABEL_4X6, 200.0);
 * }</pre>
 */
@Getter
public class LoadedMedia
{
    /**
     * Minimum allowed length for any label in millimeters
     */
    private static final double MIN_LENGTH_MM = 6.35;

    /**
     * Maximum allowed length for any label in millimeters
     */
    private static final double MAX_LENGTH_MM = 991.0;

    /**
     * Width of the loaded media in millimeters.
     * Specifies the physical width of the label media currently loaded in the printer.
     *
     * @param widthMm the width of the media in millimeters
     * @return the width of the loaded media in millimeters
     */
    private final double widthMm;

    /**
     * Fixed length of the labels in millimeters (null for dynamic length).
     * When set, defines a constant length for all labels. When null, indicates
     * that labels can have variable lengths up to maxDynamicLengthMm.
     *
     * @param fixedLengthMm the fixed length in millimeters, or null for dynamic length
     * @return the fixed length of labels in millimeters, or null if dynamic
     */
    private final Double fixedLengthMm;

    /**
     * Maximum allowed length for dynamic labels in millimeters.
     * Only applicable when fixedLengthMm is null. Must be between
     * {@value MIN_LENGTH_MM} and {@value MAX_LENGTH_MM} millimeters.
     *
     * @param maxDynamicLengthMm the maximum allowed length for dynamic labels in millimeters
     * @return the maximum allowed length for dynamic labels in millimeters
     */
    private final Double maxDynamicLengthMm;

    /**
     * Creates a new LoadedMedia instance.
     * Either fixedLengthMm or maxDynamicLengthMm must be specified, but not both.
     *
     * @param widthMm            Width of the media in millimeters
     * @param fixedLengthMm      Fixed length of the labels in millimeters (null for dynamic length)
     * @param maxDynamicLengthMm Maximum length for dynamic labels in millimeters (null for fixed length)
     * @throws IllegalArgumentException if dimensions are invalid or both lengths are specified/omitted
     */
    @Builder
    private LoadedMedia(double widthMm, Double fixedLengthMm, Double maxDynamicLengthMm)
    {
        validateDimensions(widthMm, fixedLengthMm, maxDynamicLengthMm);
        this.widthMm = widthMm;
        this.fixedLengthMm = fixedLengthMm;
        this.maxDynamicLengthMm = maxDynamicLengthMm;
    }

    /**
     * Creates a LoadedMedia instance that exactly matches the given label size.
     * This creates a fixed-length media configuration.
     *
     * @param labelSize The label size to create media for
     * @return A LoadedMedia instance configured for the exact label dimensions
     * @throws IllegalArgumentException if the label dimensions are invalid
     */
    public static LoadedMedia fromLabelSize(LabelSize labelSize)
    {
        return LoadedMedia
            .builder()
            .widthMm(labelSize.getWidthMm())
            .fixedLengthMm(labelSize.getHeightMm())
            .build();
    }

    /**
     * Creates a LoadedMedia instance that can accommodate the given label size
     * with additional tolerance for dynamic length labels.
     *
     * @param labelSize   The minimum label size to support
     * @param maxLengthMm The maximum length to allow for dynamic labels
     * @return A LoadedMedia instance configured for dynamic labels
     * @throws IllegalArgumentException if the dimensions are invalid
     */
    public static LoadedMedia fromLabelSizeDynamic(LabelSize labelSize, double maxLengthMm)
    {
        return LoadedMedia
            .builder()
            .widthMm(labelSize.getWidthMm())
            .maxDynamicLengthMm(maxLengthMm)
            .build();
    }

    /**
     * Checks if this media configuration supports dynamic length labels.
     *
     * @return true if the media supports dynamic lengths, false for fixed-length labels
     */
    public boolean isDynamicLength()
    {
        return fixedLengthMm == null;
    }

    /**
     * Checks if a label with the given dimensions can fit on this media.
     *
     * @param labelWidthMm  The width of the label in millimeters
     * @param labelHeightMm The height of the label in millimeters
     * @return true if the label can fit on this media, false otherwise
     */
    public boolean canFitLabel(double labelWidthMm, double labelHeightMm)
    {
        if (labelWidthMm > widthMm) {
            return false;
        }

        if (isDynamicLength()) {
            return labelHeightMm >= MIN_LENGTH_MM && labelHeightMm <= maxDynamicLengthMm;
        } else {
            return labelHeightMm <= fixedLengthMm;
        }
    }

    /**
     * Validates the dimensions provided for the media configuration.
     *
     * @param widthMm            Width of the media in millimeters
     * @param fixedLengthMm      Fixed length of the labels in millimeters
     * @param maxDynamicLengthMm Maximum length for dynamic labels in millimeters
     * @throws IllegalArgumentException if the dimensions are invalid
     */
    private void validateDimensions(double widthMm, Double fixedLengthMm, Double maxDynamicLengthMm)
    {
        if (widthMm <= 0) {
            throw new IllegalArgumentException("Width must be positive");
        }

        if (fixedLengthMm != null && maxDynamicLengthMm != null) {
            throw new IllegalArgumentException("Cannot specify both fixed length and maximum dynamic length");
        }

        if (fixedLengthMm == null && maxDynamicLengthMm == null) {
            throw new IllegalArgumentException("Must specify either fixed length or maximum dynamic length");
        }

        validateLength("Length", fixedLengthMm);
        validateLength("Maximum Dynamic Length", maxDynamicLengthMm);
    }

    /**
     * Validates a length value against the minimum and maximum allowed dimensions.
     *
     * @param lengthType Description of the length being validated
     * @param length     The length to validate in millimeters
     * @throws IllegalArgumentException if the length is invalid
     */
    private void validateLength(String lengthType, Double length)
    {
        if (length != null) {
            if (length < MIN_LENGTH_MM || length > MAX_LENGTH_MM) {
                throw new IllegalArgumentException(String.format("%s must be between %.2f and %.2f mm",
                    lengthType, MIN_LENGTH_MM, MAX_LENGTH_MM));
            }
        }
    }
}
