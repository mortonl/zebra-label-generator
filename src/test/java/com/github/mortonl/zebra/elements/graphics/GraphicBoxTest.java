package com.github.mortonl.zebra.elements.graphics;

import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.github.mortonl.zebra.formatting.LineColor.BLACK;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphicBoxTest
{
    @Test
    void shouldValidateCompleteGraphicBox()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withWidthMm(50.0)
            .withHeightMm(75.0)
            .withThicknessMm(1.0)
            .withRoundness(4)
            .build();

        assertDoesNotThrow(() -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @Test
    void shouldAllowAllNullParameters()
    {
        GraphicBox box = GraphicBox
            .builder()
            .build();
        assertDoesNotThrow(() -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 9})
    void shouldRejectInvalidRoundness(int invalidRoundness)
    {
        GraphicBox box = GraphicBox
            .builder()
            .withRoundness(invalidRoundness)
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        assertTrue(exception
            .getMessage()
            .contains("Roundness"));
    }

    @Test
    void shouldRejectThicknessBelowMinimum()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withThicknessMm(0.03)
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        assertTrue(exception
            .getMessage()
            .contains("Thickness"));
    }

    @Test
    void shouldRejectThicknessAboveMaximum()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withThicknessMm(1400.0)
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        assertTrue(exception
            .getMessage()
            .contains("Thickness"));
    }

    @Test
    void shouldRejectWidthExceedingLabelWidth()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withWidthMm(102.0) // Exceeds LABEL_4X6 width (101.6mm)
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        assertTrue(exception
            .getMessage()
            .contains("Width"));
    }

    @Test
    void shouldRejectHeightExceedingLabelHeight()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withHeightMm(153.0) // Exceeds LABEL_4X6 height (152.4mm)
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        assertTrue(exception
            .getMessage()
            .contains("Height"));
    }

    @Test
    void shouldRejectWidthLessThanThickness()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withWidthMm(1.0)
            .withThicknessMm(2.0)
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        assertTrue(exception
            .getMessage()
            .contains("Width"));
    }

    @Test
    void shouldRejectHeightLessThanThickness()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withHeightMm(1.0)
            .withThicknessMm(2.0)
            .build();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> box.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
        assertTrue(exception
            .getMessage()
            .contains("Height"));
    }

    @Test
    void shouldCreateHorizontalLine()
    {
        GraphicBox horizontalLine = GraphicBox
            .horizontalLine(100.0, 1.0)
            .build();

        assertEquals(100.0, horizontalLine.getWidthMm());
        assertEquals(1.0, horizontalLine.getHeightMm());
        assertEquals(1.0, horizontalLine.getThicknessMm());
    }

    @Test
    void shouldCreateVerticalLine()
    {
        GraphicBox verticalLine = GraphicBox
            .verticalLine(100.0, 1.0)
            .build();

        assertEquals(1.0, verticalLine.getWidthMm());
        assertEquals(100.0, verticalLine.getHeightMm());
        assertEquals(1.0, verticalLine.getThicknessMm());
    }

    @Test
    void shouldGenerateCompleteZplString()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withWidthMm(50.0)
            .withHeightMm(75.0)
            .withThicknessMm(1.0)
            .withColor(BLACK)
            .withRoundness(4)
            .build();

        String zplString = box.toZplString(PrintDensity.DPI_203);

        assertTrue(zplString.contains("^GB"));
        assertTrue(zplString.contains("400"));  // 50mm * 8 dots/mm = 400 dots
        assertTrue(zplString.contains("600"));  // 75mm * 8 dots/mm = 600 dots
        assertTrue(zplString.contains("8"));    // 1mm * 8 dots/mm = 8 dots
        assertTrue(zplString.contains("B"));    // color code
        assertTrue(zplString.contains("4"));    // roundness
    }

    @Test
    void shouldGenerateZplStringWithNullValues()
    {
        GraphicBox box = GraphicBox
            .builder()
            .withWidthMm(50.0)
            .withHeightMm(75.0)
            .build();

        String zplString = box.toZplString(PrintDensity.DPI_203);

        // Check the complete command structure
        assertTrue(zplString.startsWith("^FO"));
        assertTrue(zplString.endsWith("^FS"));

        // Extract the GB command parameters
        String gbCommand = zplString.substring(
            zplString.indexOf("^GB") + 3,
            zplString.indexOf("^FS")
        );
        String[] parameters = gbCommand.split(",", -1); // Use -1 to keep empty tokens

        // Verify each parameter
        assertEquals("400", parameters[0]); // width
        assertEquals("600", parameters[1]); // height
        assertEquals("", parameters[2]);    // thickness (null)
        assertEquals("", parameters[3]);    // color (null)
        assertEquals("", parameters[4]);    // roundness (null)
    }
}
