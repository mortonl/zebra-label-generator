package com.github.mortonl.zebra;

import com.github.mortonl.zebra.elements.graphics.GraphicBox;
import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import org.junit.jupiter.api.Test;

import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_300;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FullLabelTest
{
    @Test
    void testGeneratingAFullyConfiguredLabelWithManyElements()
    {
        LabelSize labelSize = LABEL_4X6;

        ZebraLabel testLabel = ZebraLabel
            .builder()
            .size(labelSize)
            .printer(PrinterConfiguration
                .builder()
                .dpi(DPI_203)
                .loadedMedia(LoadedMedia.fromLabelSize(labelSize))
                .build())
            .internationalCharacterSet(FontEncoding.UTF_8)
            .build();

        GraphicBox topBox = GraphicBox
            .builder()
            .at(37.5, 0)
            .ofSize(27.5, 10)
            .thicknessMm(10.0)
            .roundness(0)
            .build();

        testLabel.validateAndAddElement(topBox);

        String DPI203Actual = testLabel.toZplString(DPI_203);

        String DPI203Expected = """
            ^XA
            ^PW813
            ^LL1219
            ^CI28
            ^FO300,0^GB220,80,80,,0^FS
            ^XZ""";

        assertEquals(DPI203Expected, DPI203Actual);

        String DPI300Actual = testLabel.toZplString(DPI_300);

        String DPI300Expected = """
            ^XA
            ^PW1219
            ^LL1829
            ^CI28
            ^FO450,0^GB330,120,120,,0^FS
            ^XZ""";

        assertEquals(DPI300Expected, DPI300Actual);
    }
}
