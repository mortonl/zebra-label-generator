package com.github.mortonl.label_tests;

import com.github.mortonl.junit_extensions.StringFileResource;
import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.elements.barcodes.code_128.Code128Mode;
import com.github.mortonl.zebra.elements.fonts.Font;
import com.github.mortonl.zebra.elements.graphics.symbols.SymbolDesignation;
import com.github.mortonl.zebra.formatting.FontEncoding;
import com.github.mortonl.zebra.formatting.LineColor;
import com.github.mortonl.zebra.formatting.Orientation;
import com.github.mortonl.zebra.formatting.TextJustification;
import com.github.mortonl.zebra.label_settings.InternationalCharacterSet;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static com.github.mortonl.zebra.ZebraLabel.createLabel;
import static com.github.mortonl.zebra.elements.barcodes.BarcodeDefaults.createBarcodeDefaults;
import static com.github.mortonl.zebra.elements.barcodes.code_128.BarcodeCode128.createCode128Barcode;
import static com.github.mortonl.zebra.elements.fields.Comment.createComment;
import static com.github.mortonl.zebra.elements.graphics.GraphicBox.createGraphicBox;
import static com.github.mortonl.zebra.elements.graphics.GraphicBox.horizontalLine;
import static com.github.mortonl.zebra.elements.graphics.GraphicBox.verticalLine;
import static com.github.mortonl.zebra.elements.graphics.symbols.GraphicSymbol.createGraphicSymbol;
import static com.github.mortonl.zebra.elements.text.Text.createText;
import static com.github.mortonl.zebra.elements.text.TextBlock.createTextBlock;
import static com.github.mortonl.zebra.label_settings.InternationalCharacterSet.createInternationalCharacterSet;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static com.github.mortonl.zebra.printer_configuration.PrinterConfiguration.createPrinterConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("USPS Label Test")
@Tag("integration")
public class USPS4X6ShippingLabelTest
{
    public static final InternationalCharacterSet INTERNATIONAL_CHARACTER_SET = createInternationalCharacterSet()
        .withEncoding(FontEncoding.UTF_8)
        .build();

    public static final PrinterConfiguration PRINTER_CONFIGURATION = createPrinterConfiguration()
        .forDpi(DPI_203)
        .forLoadedMedia(LoadedMedia.fromLabelSize(LABEL_4X6))
        .build();

    @Test
    @DisplayName("Given configured label with elements, when generating ZPL, then returns correct output")
    @Tag("zpl-generation")
    void givenConfiguredLabelWithElements_whenGeneratingZpl_thenReturnsCorrectOutput(
        @StringFileResource("test_labels/203/usps_shipping_label_4x6.zpl") String expectedZpl
    )
    {
        // Given
        ZebraLabel uspsStyleLabel = createLabel()
            .forSize(LABEL_4X6)
            .forPrinter(PRINTER_CONFIGURATION)
            .forInternationalCharacterSet(INTERNATIONAL_CHARACTER_SET)
            .build();

        createComment()
            .withContent("ImageFileName: USPS-Shipping")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Set label size to 4\" x 6\" (assumes 8 dpmm)")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Set the Font/Encoding to")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Unicode (UTF-8 encoding) - Unicode Character Set")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Vertical line for service icon block")
            .addToLabel(uspsStyleLabel);
        verticalLine(25.375, 0.375).withPosition(25.4, 0)
                                   .withColor(LineColor.BLACK)
                                   .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Service Block (P Priority Mail)")
            .addToLabel(uspsStyleLabel);
        // Large service letter P centered in a 26 mm block starting at y=2.5 mm.

        final Font serviceBlockFont = Font
            .createFont()
            .withFontDesignation('0')
            .withSize(26.25, 26.25)
            .withOrientation(Orientation.NORMAL)
            .build();

        createTextBlock()
            .withPosition(0, 2.5)
            .withWidthMm(26.0)
            .withMaxLines(1)
            .withFont(serviceBlockFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("P")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Payment Block - 20mm")
            .addToLabel(uspsStyleLabel);
        // Payment information box 28.5 mm x 19 mm with a 0.25 mm border at (66.625, 3.2).
        createGraphicBox()
            .withPosition(66.625, 3.2)
            .withSize(28.5, 19.0)
            .withThicknessMm(0.25)
            .withColor(LineColor.BLACK)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Payment Block Line 1")
            .addToLabel(uspsStyleLabel);

        final Font paymentBlockFont = Font
            .createFont()
            .withFontDesignation('0')
            .withSize(2.5, 2.55)
            .withOrientation(Orientation.NORMAL)
            .build();

        createTextBlock()
            .withPosition(66.625, 7.0)
            .withWidthMm(28.5)
            .withMaxLines(1)
            .withFont(paymentBlockFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("PRIORITY MAIL")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Payment Block Line 2")
            .addToLabel(uspsStyleLabel);
        createTextBlock()
            .withPosition(66.625, 10.0)
            .withWidthMm(28.5)
            .withMaxLines(1)
            .withFont(paymentBlockFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("U.S. POSTAGE PAID")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Payment Block Line 3")
            .addToLabel(uspsStyleLabel);
        createTextBlock()
            .withPosition(66.625, 13.0)
            .withWidthMm(28.5)
            .withMaxLines(1)
            .withFont(paymentBlockFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("ACME WIDGETS")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Payment Block Line 4")
            .addToLabel(uspsStyleLabel);
        createTextBlock()
            .withPosition(66.625, 16.0)
            .withWidthMm(28.5)
            .withMaxLines(1)
            .withFont(paymentBlockFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("eVS")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Top horizontal line")
            .addToLabel(uspsStyleLabel);
        horizontalLine(101.5, 0.5)
            .withPosition(0, 25.4)
            .withColor(LineColor.BLACK)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Service Text")
            .addToLabel(uspsStyleLabel);

        final Font priorityMailFont = Font
            .createFont()
            .withFontDesignation('0')
            .withSize(7.625, 6.5)
            .withOrientation(Orientation.NORMAL)
            .build();

        createTextBlock()
            .withPosition(0, 27.4)
            .withWidthMm(93.75)
            .withMaxLines(1)
            .withFont(priorityMailFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("USPS PRIORITY MAIL")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Registered Trademark symbol")
            .addToLabel(uspsStyleLabel);

        createGraphicSymbol()
            .withPosition(81.25, 26.875)
            .withSize(8.125, 8.125)
            .withSymbol(SymbolDesignation.REGISTERED_TRADEMARK)
            .withOrientation(Orientation.NORMAL)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Bottom horizontal line")
            .addToLabel(uspsStyleLabel);
        horizontalLine(101.5, 0.5)
            .withPosition(0, 33.4)
            .withColor(LineColor.BLACK)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Return Address Line 1")
            .addToLabel(uspsStyleLabel);

        final Font returnAddressFont = Font
            .createFont()
            .withFontDesignation('0')
            .withSize(3, 3)
            .withOrientation(Orientation.NORMAL)
            .build();

        createText()
            .withPosition(1.875, 35.75)
            .withPlainTextContent("INTERNET SALES DEPARTMENT")
            .withFont(returnAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Return Address Line 2")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(1.875, 39.25)
            .withPlainTextContent("FAST AND EFFICIENT SUPPLY CO.")
            .withFont(returnAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Return Address Line 3")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(1.875, 42.75)
            .withPlainTextContent("10474 COMMERCE BVLD DUPLEX B")
            .withFont(returnAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Return Address Line 4")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(1.875, 46.25)
            .withPlainTextContent("SILVER SPRING MD20910-9999")
            .withFont(returnAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Delivery Address Line 1")
            .addToLabel(uspsStyleLabel);

        final Font deliveryAddressFont = Font
            .createFont()
            .withFontDesignation('0')
            .withSize(3.625, 3.625)
            .withOrientation(Orientation.NORMAL)
            .build();

        createText()
            .withPosition(14.0, 66.625)
            .withPlainTextContent("RONALD RECEIVER")
            .withFont(deliveryAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Delivery Address Line 2")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(14.0, 70.75)
            .withPlainTextContent("C/O RICK RECIPIENT")
            .withFont(deliveryAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Delivery Address Line 3")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(14.0, 74.875)
            .withPlainTextContent("INTERNET PURCHASING OFFICE")
            .withFont(deliveryAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Delivery Address Line 4")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(14.0, 79)
            .withPlainTextContent("BIG AND GROWING BUSINESS, CO.")
            .withFont(deliveryAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Delivery Address Line 5")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(14.0, 83.125)
            .withPlainTextContent("8403 LEE HIGHWAY")
            .withFont(deliveryAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Delivery Address Line 6")
            .addToLabel(uspsStyleLabel);
        createText()
            .withPosition(14.0, 87.25)
            .withPlainTextContent("MERRIFIELD VA 22082-9999")
            .withFont(deliveryAddressFont)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("IM Barcode Top Line")
            .addToLabel(uspsStyleLabel);

        horizontalLine(101.5, 1.875)
            .withColor(LineColor.BLACK)
            .withPosition(0, 101.375)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("USPS Tracking Text")
            .addToLabel(uspsStyleLabel);

        final Font trackingFont = Font
            .createFont()
            .withFontDesignation('0')
            .withSize(4, 4)
            .withOrientation(Orientation.NORMAL)
            .build();

        createTextBlock()
            .withPosition(0, 104.625)
            .withWidthMm(101.5)
            .withMaxLines(1)
            .withFont(trackingFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("USPS SIGNATURE TRACKING #")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("IM Barcode")
            .addToLabel(uspsStyleLabel);

        createBarcodeDefaults()
            .withModuleWidthMm(0.375)
            .addToLabel(uspsStyleLabel);

        createCode128Barcode()
            .withPosition(3.125, 111)
            .withOrientation(Orientation.NORMAL)
            .withHeightMm(19.0)
            .withPrintInterpretationLine(false)
            .withPrintInterpretationLineAbove(false)
            .withUccCheckDigitEnabled(false)
            .withMode(Code128Mode.UCC_EAN)
            .withPlainTextContent("4202208<89205591234{id2}0{id3}{id4}01")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("IM Readable Text")
            .addToLabel(uspsStyleLabel);

        createTextBlock()
            .withPosition(0, 133.25)
            .withWidthMm(101.5)
            .withMaxLines(1)
            .withFont(trackingFont)
            .withJustification(TextJustification.CENTER)
            .withLineSpacingMm(0.0)
            .withHangingIndentMm(0.0)
            .withPlainTextContent("9205 5912 34{id2} 0{id3} {id4} 01")
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("IM Barcode Bottom Line")
            .addToLabel(uspsStyleLabel);

        horizontalLine(101.5, 1.875)
            .withColor(LineColor.BLACK)
            .withPosition(0, 137.75)
            .addToLabel(uspsStyleLabel);

        createComment()
            .withContent("Reset")
            .addToLabel(uspsStyleLabel);

        // When
        String actualDpi203Zpl = uspsStyleLabel.toZplString(DPI_203);

        // Then
        assertEquals(expectedZpl, actualDpi203Zpl);
    }
}
