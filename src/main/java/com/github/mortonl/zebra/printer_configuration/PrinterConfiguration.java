package com.github.mortonl.zebra.printer_configuration;

import com.github.mortonl.zebra.label_settings.LabelSize;
import lombok.Builder;
import lombok.Data;

/**
 * Configuration settings for a Zebra printer including DPI and loaded media specifications.
 * Used to validate label compatibility and generate appropriate ZPL commands.
 */
@Data
@Builder(builderMethodName = "createPrinterConfiguration", setterPrefix = "for")
public class PrinterConfiguration
{
    private final PrintDensity dpi;

    private final LoadedMedia loadedMedia;

    /**
     * Checks if the printer can print a label of the specified size.
     *
     * @param labelSize the label size to check
     *
     * @return true if the label can be printed, false otherwise
     */
    public boolean canPrintLabel(LabelSize labelSize)
    {
        if (loadedMedia != null) {
            return loadedMedia.canFitLabel(labelSize.getWidthMm(), labelSize.getHeightMm());
        } else {
            throw new IllegalStateException("Loaded media must be specified");
        }
    }
}
