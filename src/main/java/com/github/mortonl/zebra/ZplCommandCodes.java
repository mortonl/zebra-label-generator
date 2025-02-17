package com.github.mortonl.zebra;

public class ZplCommandCodes {
    // Prevent instantiation
    private ZplCommandCodes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Start/End Commands
    public static final String START_FORMAT = "^XA";
    public static final String END_FORMAT = "^XZ";

    // Field Commands
    public static final String FIELD_START = "^FD";
    public static final String FIELD_END_SEPARATOR = "^FS";

    // Text Commands
    public static final String FIELD_ORIGIN = "^FO"; // Field Origin
    public static final String FIELD_TYPE = "^FT";   // Field Type/Position
    public static final String FIELD_BLOCK = "^FB";  // Field Block

    // Font Commands
    public static final String FONT_SIZE = "^A";

    // Barcode Commands
    public static final String BARCODE_CODE_11 = "^B1";
    public static final String BARCODE_39 = "^B3";
    public static final String BARCODE_128 = "^BC";
    public static final String BARCODE_PDF_417 = "^B7";
    public static final String QR_CODE = "^BQ";

    // Graphic Commands
    public static final String GRAPHIC_BOX = "^GB";
    public static final String GRAPHIC_CIRCLE = "^GC";

    // Print Settings
    public static final String PRINT_WIDTH = "^PW";
    public static final String PRINT_RATE = "^PR";
    public static final String DARKNESS = "^MD";

    // Label Home Position
    public static final String HOME_POSITION = "^LH";

    // Clear Printer
    public static final String CLEAR_BUFFER = "^CL";
}
