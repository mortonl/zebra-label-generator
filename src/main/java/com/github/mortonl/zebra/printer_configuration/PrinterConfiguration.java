package com.github.mortonl.zebra.printer_configuration;

import com.github.mortonl.zebra.label_settings.InternationalCharacterSet;
import com.github.mortonl.zebra.label_settings.LabelSize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrinterConfiguration
{
    private final PrintDensity dpi;
    private final LoadedMedia loadedMedia;
    private final InternationalCharacterSet internationalCharacterSet;

    public boolean canPrintLabel(LabelSize labelSize)
    {
        if (loadedMedia != null) {
            return loadedMedia.canFitLabel(labelSize.getWidthMm(), labelSize.getHeightMm());
        } else {
            throw new IllegalStateException("Loaded media must be specified");
        }
    }
}
