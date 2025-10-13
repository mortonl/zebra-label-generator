package com.github.mortonl.zebra;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class containing ZPL II (Zebra Programming Language) command constants and helper methods.
 * This class provides a centralized repository of ZPL commands and control characters used in
 * label generation for Zebra printers.
 *
 * <p>Example of generating a complete label:</p>
 * <pre>{@code
 * StringBuilder label = new StringBuilder()
 *     .append(ZplCommand.START_FORMAT)                                    // ^XA
 *     .append(ZplCommand.generateZplIICommand(FIELD_ORIGIN, 50, 50))     // ^FO,50,50
 *     .append(ZplCommand.generateZplIICommand(SET_FONT + "0",
 *             Orientation.NORMAL.getValue(), 30, 20))                     // ^A0,N,30,20
 *     .append(ZplCommand.generateZplIICommand(FIELD_DATA, "Hello"))      // ^FD,Hello
 *     .append(ZplCommand.generateZplIICommand(FIELD_SEPARATOR))          // ^FS
 *     .append(ZplCommand.END_FORMAT);                                    // ^XZ
 * }</pre>
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ZplCommand
{

    public static final String CONTROL_CHARACTER_1 = "^";

    public static final String CONTROL_CHARACTER_2 = "~";

    public static final String LINE_SEPERATOR = "\n";

    public static final List<String> CONTROL_CHARACTERS = List.of(CONTROL_CHARACTER_1, CONTROL_CHARACTER_2);

    // Start/End Commands
    public static final String START_FORMAT = "^XA";

    public static final String END_FORMAT = "^XZ";

    // Font Commands
    public static final String SET_FONT = "^A";

    public static final String FONT_LINKING = "^FL";

    // Barcode Commands
    public static final String BARCODE_AZTEC = "^B0";

    public static final String BARCODE_CODE_11 = "^B1";

    public static final String BARCODE_INTERLEAVED_2_OF_5 = "^B2";

    public static final String BARCODE_CODE_39 = "^B3";

    public static final String BARCODE_CODE_49 = "^B4";

    public static final String BARCODE_PLANET_CODE = "^B5";

    public static final String BARCODE_PDF_417 = "^B7";

    public static final String BARCODE_EAN_8 = "^B8";

    public static final String BARCODE_UPC_E = "^B9";

    public static final String BARCODE_CODE_93 = "^BA";

    public static final String BARCODE_CODABLOCK = "^BB";

    public static final String BARCODE_CODE_128 = "^BC";

    public static final String BARCODE_UPS_MAXICODE = "^BD";

    public static final String BARCODE_EAN_13 = "^BE";

    public static final String BARCODE_MICRO_PDF417 = "^BF";

    public static final String BARCODE_INDUSTRIAL_2_OF_5 = "^BI";

    public static final String BARCODE_STANDARD_2_OF_5 = "^BJ";

    public static final String BARCODE_ANSI_CODABAR = "^BK";

    public static final String BARCODE_LOGMARS = "^BL";

    public static final String BARCODE_MSI = "^BM";

    public static final String BARCODE_AZTEC_ALT = "^BO";

    public static final String BARCODE_PLESSEY = "^BP";

    public static final String BARCODE_QR = "^BQ";

    public static final String BARCODE_GS1_DATABAR = "^BR";

    public static final String BARCODE_UPC_EAN_EXTENSION = "^BS";

    public static final String BARCODE_TLC39 = "^BT";

    public static final String BARCODE_UPC_A = "^BU";

    public static final String BARCODE_DATA_MATRIX = "^BX";

    public static final String BARCODE_POSTAL = "^BZ";

    public static final String BARCODE_DEFAULTS = "^BY";

    public static final String QR_CODE = BARCODE_QR;

    // Advanced Settings
    public static final String CHANGE_CARET = "^CC";

    public static final String CHANGE_TILDE = "^CT";

    public static final String CHANGE_DELIMITER = "^CD";

    public static final String CHANGE_ALPHANUMERIC_DEFAULT_FONT = "^CF";

    public static final String CHANGE_INTERNATIONAL_CHARACTER_SET = "^CI";

    public static final String CHANGE_BARCODE_VALIDATION = "^CV";

    public static final String CHANGE_FONT_IDENTIFIER = "^CW";

    // Perform Downloads
    public static final String DOWNLOAD_FORMAT = "^DF";

    public static final String DOWNLOAD_GRAPHIC = "^DG";

    public static final String ABORT_DOWNLOAD_GRAPHIC = "^DN";

    public static final String DOWNLOAD_SCALABLE_FONT = "^DS";

    public static final String DOWNLOAD_BOUNDED_TRUE_TYPE_FONT = "^DT";

    public static final String DOWNLOAD_UNBOUNDED_TRUE_TYPE_FONT = "^DU";

    public static final String DOWNLOAD_OBJECT = "^DY";

    // Remove Downloads
    public static final String ERASE_DOWNLOAD_GRAPHIC = "^EG";

    // Field Commands
    public static final String FIELD_BLOCK = "^FB";

    public static final String FIELD_CLOCK = "^FC";

    public static final String FIELD_DATA = "^FD";

    public static final String FIELD_START = FIELD_DATA;

    public static final String FIELD_HEXADECIMAL_INDICATOR = "^FH";

    public static final String MULTIPLE_FIELD_ORIGIN = "^FM";

    public static final String FIELD_NUMBER = "^FN";

    public static final String FIELD_ORIGIN = "^FO";

    public static final String FIELD_PARAMETER = "^FP";

    public static final String FIELD_REVERSE_PRINT = "^FR";

    public static final String FIELD_SEPARATOR = "^FS";

    public static final String FIELD_END = FIELD_SEPARATOR;

    public static final String FIELD_TYPESET = "^FT";

    public static final String FIELD_VARIABLE = "^FV";

    public static final String FIELD_ORIENTATION = "^FW";

    public static final String FIELD_COMMENT = "^FX";

    public static final String COMMENT = FIELD_COMMENT;

    // Graphic Commands
    public static final String GRAPHIC_BOX = "^GB";

    public static final String GRAPHIC_CIRCLE = "^GC";

    public static final String GRAPHIC_DIAGONAL_LINE = "^GD";

    public static final String GRAPHIC_ELLIPSE = "^GE";

    public static final String GRAPHIC_FIELD = "^GF";

    public static final String GRAPHIC_SYMBOL = "^GS";

    // Host / Hardware Commands
    public static final String BATTERY_STATUS = "^HB";

    public static final String HEAD_DIAGNOSTIC = "^HD";

    public static final String HOST_FORMAT = "^HF";

    public static final String HOST_GRAPHIC = "^HG";

    public static final String RETURN_CONFIGURATION = "^HH";

    public static final String HOST_IDENTIFICATION = "^HI";

    public static final String HOST_RAM_STATUS = "^HM";

    public static final String HOST_QUERY = "^HQ";

    public static final String HOST_STATUS_RETURN = "^HS";

    public static final String HOST_LINKED_FONTS_LIST = "^HT";

    public static final String RETURN_ZEBRANET_ALERT_CONFIGURATION = "^HU";

    public static final String HOST_VERIFICATION = "^HV";

    public static final String HOST_DIRECTORY_LIST = "^HW";

    public static final String UPLOAD_GRAPHICS = "^HY";

    public static final String DISPLAY_DESCRIPTION_INFORMATION = "^HZ";

    // Image Commands
    public static final String DELETE_OBJECT = "^ID";

    public static final String IMAGE_LOAD = "^IL";

    public static final String IMAGE_MOVE = "^IM";

    public static final String IMAGE_SAVE = "^IS";

    // Job / Advanced Commands
    public static final String CANCEL_ALL = "~JA";

    public static final String INITIALIZE_MEMORY = "^JB";

    public static final String RESET_OPTIONAL_MEMORY = "~JB";

    public static final String SET_MEDIA_SENSOR_CALIBRATION = "~JC";

    public static final String ENABLE_COMMUNICATIONS_DIAGNOSTICS = "~JD";

    public static final String DISABLE_COMMUNICATIONS_DIAGNOSTICS = "~JE";

    public static final String SET_PAUSE_ON_LOW_VOLTAGE = "~JF";

    public static final String PRINT_MEDIA_SENSOR_GRAPH = "~JG";

    public static final String CONFIGURE_EARLY_WARNINGS = "^JH";

    public static final String START_ZEBRA_BASIC_INTERPRETER = "^JI";

    public static final String SET_AUXILIARY_PORT = "^JJ";

    public static final String SET_LABEL_LENGTH = "~JL";

    public static final String SET_DOTS_PER_MM = "^JM";

    public static final String ENABLE_HEAD_TEST_FATAL = "~JN";

    public static final String ENABLE_HEAD_TEST_PAUSE = "~JO";

    public static final String PAUSE_AND_CANCEL_FORMAT = "~JP";

    public static final String END_ZEBRA_BASIC_INTERPRETER = "~JQ";

    public static final String PERFORM_POWERED_ON_RESET = "~JR";

    public static final String SELECT_SENSOR = "^JS";

    public static final String CHANGE_BACKFEED_SEQUENCE = "~JS";

    public static final String SET_HEAD_TEST_INTERVAL = "^JT";

    public static final String PERFORM_PRINTER_CONFIGURATION_UPDATE = "^JU";

    public static final String SET_RIBBON_TENSION = "^JW";

    public static final String CANCEL_CURRENT_FORMAT = "~JX";

    public static final String SET_REPRINT_AFTER_ERROR = "^JZ";


    public static final String RECALL_FORMAT = "^XF";

    public static final String MAP_OBJECT = "^MP";

    public static final String SET_INTERVAL = "^SI";

    public static final String SET_VARIABLE = "^SA";

    public static final String MIRROR_IMAGE = "^MI";

    public static final String ZEBRANET_ALERT = "^SX";

    public static final String PRINT_ENERGY = "^SE";

    // Print Settings
    public static final String PRINT_SPEED = "^PR";

    public static final String PRINT_WIDTH = "^PW";

    public static final String PRINT_MODE = "^PM";

    public static final String LABEL_LENGTH = "^LL";

    public static final String DARKNESS = "^MD";

    public static final String PRINT_METHOD = "^MT";

    public static final String PRINT_QUANTITY = "^PQ";

    public static final String PRINT_ANGLE = "^PA";

    public static final String CUT_NOW = "^CN";

    public static final String REMOVE_LABEL = "^CP";

    // Media Settings
    public static final String MEDIA_TYPE = "^MN";

    public static final String MEDIA_SETTINGS = "^MF";

    public static final String MEMORY_CARD = "^MM";

    public static final String SET_MEDIA_TRACKING = "^MN";

    // Position Settings
    public static final String HOME_POSITION = "^LH";

    public static final String LABEL_TOP = "^LT";

    public static final String LEFT_POSITION = "^LS";

    public static final String LABEL_ORIGIN = "^LO";

    // Communication Settings
    public static final String SERIAL_COMM = "^SC";

    public static final String STROKE_WIDTH = "^CS";

    // Clear Printer
    public static final String CLEAR_BUFFER = "^CL";

    /**
     * Generates a properly formatted ZPL II command string with the specified parameters.
     * The command will be prefixed with the control character (^) and parameters will be
     * comma-separated.
     *
     * <p>Examples:</p>
     * <pre>{@code
     * // Basic field positioning
     * generateZplIICommand(FIELD_ORIGIN, 100, 200)      // ^FO,100,200
     *
     * // Field data with text
     * generateZplIICommand(FIELD_DATA, "Sample Text")    // ^FD,Sample Text
     *
     * // Barcode generation
     * generateZplIICommand(BARCODE_CODE_128, 2, 50, "Y") // ^BC,2,50,Y
     *
     * // Print width specification
     * generateZplIICommand(PRINT_WIDTH, 600)             // ^PW,600
     * }</pre>
     *
     * @param command    The ZPL II command without the control character
     * @param parameters Optional parameters for the command
     *
     * @return A properly formatted ZPL II command string ending with a newline
     *
     * @throws IllegalArgumentException if command is null or empty
     */
    public static String generateZplIICommand(String command, Object... parameters)
    {
        if (parameters == null || parameters.length == 0) {
            return command;
        }
        return command + Stream
            .of(parameters)
            .map(param -> param == null ? "" : String.valueOf(param))
            .collect(Collectors.joining(","));
    }
}
