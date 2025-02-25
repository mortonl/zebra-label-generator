package com.github.mortonl.zebra.printer_configuration;

import com.github.mortonl.zebra.label_settings.LabelSize;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoadedMedia
{
    private static final double MIN_LENGTH_MM = 6.35;
    private static final double MAX_LENGTH_MM = 991.0;

    private final double widthMm;
    private final Double fixedLengthMm; // null means dynamic length
    private final Double maxDynamicLengthMm; // maximum length for dynamic labels

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

    public boolean isDynamicLength()
    {
        return fixedLengthMm == null;
    }

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
