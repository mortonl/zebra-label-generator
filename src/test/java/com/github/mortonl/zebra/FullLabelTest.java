package com.github.mortonl.zebra;

import com.github.mortonl.zebra.elements.graphics.CompressionType;
import com.github.mortonl.zebra.elements.graphics.GraphicBox;
import com.github.mortonl.zebra.elements.graphics.GraphicField;
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
            .createGraphicBox()
            .withPosition(37.5, 0)
            .withSize(27.5, 10)
            .withThicknessMm(10.0)
            .withRoundness(0)
            .addToLabel(testLabel);

        GraphicField graphicField = GraphicField
            .createGraphicField()
            .withPosition(100.0, 100.0)
            .withCompressionType(CompressionType.ASCII_HEX)
            .withBinaryByteCount(11)
            .withGraphicFieldCount(8000)
            .withBytesPerRow(80)
//            .withData("::::::::::::K03RFC1IFCN0FC1QF8N07C,K03RFC1IFEN0FC1RFEL01FF,,:::::::::")  // Hex data here
            //TODO: the validation login may be wrong here because i have seen working examples of the GFA command that have no hex chars like above
            .withData("ABCDEFabcdef0123456789")
            .build();

        testLabel.validateAndAddElement(graphicField);

        String DPI203Actual = testLabel.toZplString(DPI_203);

        String DPI203Expected = """
            ^XA
            ^PW812
            ^LL1219
            ^CI28
            ^FO300,0^GB220,80,80,,0^FS
            ^FO800,800^GFA,11,8000,80,ABCDEFabcdef0123456789^FS
            ^XZ""";

        assertEquals(DPI203Expected, DPI203Actual);

        String DPI300Actual = testLabel.toZplString(DPI_300);

        String DPI300Expected = """
            ^XA
            ^PW1219
            ^LL1828
            ^CI28
            ^FO450,0^GB330,120,120,,0^FS
            ^FO1200,1200^GFA,11,8000,80,ABCDEFabcdef0123456789^FS
            ^XZ""";

        assertEquals(DPI300Expected, DPI300Actual);
    }
}
