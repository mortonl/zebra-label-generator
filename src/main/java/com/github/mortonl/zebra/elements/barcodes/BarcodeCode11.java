//package com.github.mortonl.zebra.elements.barcodes;
//
//import com.github.mortonl.zebra.ZplCommand;
//import com.github.mortonl.zebra.elements.PositionedElement;
//import com.github.mortonl.zebra.formatting.Orientation;
//import lombok.Builder;
//import lombok.Data;
//
//import java.util.regex.Pattern;
//
//import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
//import static com.github.mortonl.zebra.ZplCommand.FIELD_START;
//import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;
//
//@Builder
//@Data
//public class BarcodeCode11 extends PositionedElement
//{
//    private static final Pattern CODE11_PATTERN = Pattern.compile("^[0-9-]+$");
//    private static final int MIN_HEIGHT = 1;
//    private static final int MAX_HEIGHT = 32000;
//
//    @Builder.Default
//    private final Orientation orientation = Orientation.NORMAL;
//    @Builder.Default
//    private final boolean singleCheckDigit = false;
//    @Builder.Default
//    private final int height = 10;
//    @Builder.Default
//    private final boolean printInterpretationLine = true;
//    @Builder.Default
//    private final boolean interpretationLineAboveCode = false;
//    private final String data;
//
//    /**
//     * Creates a Code 11 barcode with default settings
//     *
//     * @param data The data to encode in the barcode
//     */
//    public BarcodeCode11(String data, Position position)
//    {
//        this(Orientation.NORMAL, false, 10, true, false, data, position);
//    }
//
//    /**
//     * Creates a Code 11 barcode with full parameter control
//     *
//     * @param orientation                 Barcode orientation
//     * @param singleCheckDigit            True for single check digit, false for double check digit
//     * @param height                      Height of barcode in dots (1-32000)
//     * @param printInterpretationLine     Whether to print interpretation line
//     * @param interpretationLineAboveCode Whether to print interpretation line above code
//     * @param data                        The data to encode in the barcode
//     */
//    public BarcodeCode11(
//            Orientation orientation,
//            boolean singleCheckDigit,
//            int height,
//            boolean printInterpretationLine,
//            boolean interpretationLineAboveCode,
//            String data
//    )
//    {
//        super(position.getXAxisLocationDots(), position.getYAxisLocationDots(), position.getZJustification());
//        validateParameters(height, data);
//        this.orientation = orientation;
//        this.singleCheckDigit = singleCheckDigit;
//        this.height = height;
//        this.printInterpretationLine = printInterpretationLine;
//        this.interpretationLineAboveCode = interpretationLineAboveCode;
//        this.data = data;
//    }
//
//    private void validateParameters(int height, String data)
//    {
//        if (height < MIN_HEIGHT || height > MAX_HEIGHT) {
//            throw new IllegalStateException(
//                    String.format("Height must be between %d and %d dots", MIN_HEIGHT, MAX_HEIGHT)
//            );
//        }
//
//        if (data == null || data.isEmpty()) {
//            throw new IllegalStateException("Data cannot be null or empty");
//        }
//
//        if (!CODE11_PATTERN.matcher(data)
//                           .matches())
//        {
//            throw new IllegalStateException("Code 11 only supports digits and hyphens");
//        }
//    }
//
//    /**
//     * Converts the barcode to its ZPL representation
//     *
//     * @return ZPL command string
//     */
//    public String toZplString()
//    {
//        StringBuilder zpl = new StringBuilder();
//
//        zpl.append(
//                generateZplIICommand(
//                        ZplCommand.BARCODE_CODE_11,
//                        orientation.getValue(),
//                        singleCheckDigit ? "Y" : "N",
//                        height,
//                        printInterpretationLine ? "Y" : "N",
//                        interpretationLineAboveCode ? "Y" : "N"
//                )
//        );
//
//        // Add field data
//        zpl.append(FIELD_START)
//           .append(data)
//           .append(FIELD_END);
//
//        return zpl.toString();
//    }
//}
