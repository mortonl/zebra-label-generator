# Usage Guide

This guide provides detailed instructions and examples on how to use the Zebra Label Generator library.

---

## Table of Contents

- [Best Practices](#best-practices)
    - [Measurements and Positioning](#measurements-and-positioning)
    - [Builder Pattern Usage](#builder-pattern-usage)
    - [Label Construction](#label-construction)
    - [DPI Independence](#dpi-independence)
- [Basic Label Creation](#basic-label-creation)
- [Generating ZPL Output](#generating-zpl-output)
- [Adding Elements to the Label](#adding-elements-to-the-label)
    - [Fluid Interface with `addToLabel()`](#fluid-interface-with-addtolabel)
    - [Text Elements](#text-elements)
    - [Barcode Elements](#barcode-elements)
    - [Adding Graphics](#adding-graphics)
    - [Comments](#comments)
- [Getting the DPI](#getting-the-dpi)
- [Additional Examples](#additional-examples)

---

## Best Practices

### Measurements and Positioning

- **Use Millimeters (mm):** Always specify sizes and positions in millimeters as doubles.
- **DPI Handling:** The library automatically converts mm to dots based on the printer's DPI/DPMM.
- **Helper Methods:** Utilize provided methods for positioning and sizing:

  ```java
  .withSize(27.5, 10.0)      // Width and height
  .withPosition(37.5, 0)     // X and Y coordinates
  ```

### Builder Pattern Usage

- **Fluent Interface:** Chain builder methods for cleaner, more readable code.
- **Custom `with` Methods:** Use intuitive methods to set properties.
- **Element Addition:** Use `.addToLabel(Label label)` to build and add elements in one step.

### Label Construction

- **Validation Levels:** Set `ValidationLevel` per element to control validation strictness.
- **Grouping Elements:** Organize related elements logically in your code.
- **Testing:** Use tools like the [Labelary Online ZPL Viewer](http://labelary.com/viewer.html) to preview labels.

### DPI Independence

- **Design in mm:** Create labels using millimeters for consistency across printer resolutions.
- **DPI Conversion:** Let the library handle conversions; specify the printer's DPI when generating ZPL.

---

## Basic Label Creation

Create a label with a specific size and printer configuration:

```java
import com.github.mortonl.zebra.ZebraLabel;
import com.github.mortonl.zebra.printer_configuration.LoadedMedia;
import com.github.mortonl.zebra.printer_configuration.PrinterConfiguration;

import static com.github.mortonl.zebra.formatting.FontEncoding.UTF_8;
import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
```

```java
ZebraLabel label = ZebraLabel.builder()
                             .size(LABEL_4X6)
                             .printer(PrinterConfiguration.builder()
                                                          .dpi(DPI_203)
                                                          .loadedMedia(LoadedMedia.fromLabelSize(LABEL_4X6))
                                                          .build())
                             .internationalCharacterSet(UTF_8)
                             .build();
```

---

## Generating ZPL Output

The library supports generating ZPL for different DPIs from the same label definition.

NOTE: in this example output we have also added a graphic box and graphic field

For 203 DPI:

```java
String zpl203 = label.toZplString(); // Uses the DPI specified in the label
```

This will output:

```text
^XA
^PW813
^LL1219
^CI28
^FO300,0^GB220,80,80,,0^FS
^FO800,800^GFA,11,8000,80,ABCDEFabcdef0123456789^FS
^XZ
```

For 300 DPI:

```java
String zpl300 = label.toZplString(DPI_300);
```

This will output:

```text
^XA
^PW1219
^LL1829
^CI28
^FO450,0^GB330,120,120,,0^FS
^FO1200,1200^GFA,11,8000,80,ABCDEFabcdef0123456789^FS
^XZ
```

---

## Adding Elements to the Label

### Fluid Interface with `addToLabel()`

Create and add a `GraphicBox` to the label:

```java
import com.github.mortonl.zebra.elements.graphics.GraphicBox;
import com.github.mortonl.zebra.validation.ValidationException;
import com.github.mortonl.zebra.validation.ValidationLevel;

try{
    GraphicBox.createGraphicBox()
        .

withPosition(10,20)
        .

withSize(70,50)
        .

withThicknessMm(2.0)
        .

withRoundness(5)
        .

withValidationLevel(ValidationLevel.NORMAL)
        .

addToLabel(label);
}catch(
ValidationException e){
    System.err.

println("Validation failed: "+e.getMessage());
    }
```

---

### Text Elements

#### Font Configuration

Create a custom font configuration:

```java
import com.github.mortonl.zebra.elements.text.Font;
import com.github.mortonl.zebra.elements.orientation.Orientation;
```

```java
Font customFont = Font.builder()
                      .withFontDesignation('0')
                      .withSize(5.0, 5.0)
                      .withOrientation(Orientation.NORMAL)
                      .build();
```

#### Simple Text

Add basic text to a label:

```java
import com.github.mortonl.zebra.elements.text.Text;
```

```java
Text.createText()
    .

withPosition(10,50)
    .

withFont(customFont)
    .

withPlainTextContent("Hello, Zebra!")
    .

addToLabel(label);
```

#### Text Block with Formatting

Create a text block with specific formatting and positioning:

```java
import com.github.mortonl.zebra.elements.text.TextBlock;
```

```java
TextBlock.createTextBlock()
         .

withPosition(37.5,2.5)
         .

withHexadecimalContent("ABABABAB")  // Variable placeholder
         .

withFont(customFont)
         .

withWidthMm(27.5)
         .

withMaxLines(1)
         .

withJustification(CENTER)
         .

addToLabel(label);
```

---

### Barcode Elements

#### Code 128 Barcode

Create and add a `BarcodeCode128` element:

```java
import com.github.mortonl.zebra.elements.barcode.BarcodeCode128;
```

```java
BarcodeCode128.createBarcodeCode128()
    .

withPosition(20.0,70.0)
    .

withData("ABC123")
    .

withHeightInMillimetres(15.0)
    .

withPrintInterpretationLine(true)
    .

withValidationLevel(ValidationLevel.STRICT)
    .

addToLabel(label);
```

#### Interleaved 2 of 5 Barcode

Create and add an `Interleaved2of5` barcode:

```java
import com.github.mortonl.zebra.elements.barcode.BarcodeInterleaved2of5;
```

```java
BarcodeInterleaved2of5.createBarcodeInterleaved2of5()
    .

withPosition(30.0,90.0)
    .

withData("12345678") // Must be even length
    .

withHeightInMillimetres(15.0)
    .

withPrintInterpretationLine(true)
    .

withValidationLevel(ValidationLevel.NORMAL)
    .

addToLabel(label);
```

---

### Adding Graphics

#### Graphic Fields (Images)

Create and add a `GraphicField` element:

```java
import com.github.mortonl.zebra.elements.graphics.GraphicField;
import com.github.mortonl.zebra.elements.graphics.CompressionType;
```

```java
GraphicField.createGraphicField()
    .

withPosition(100.0,100.0)
    .

withCompressionType(CompressionType.ASCII_HEX)
    .

withBinaryByteCount(11)
    .

withGraphicFieldCount(8000)
    .

withBytesPerRow(80)
    .

withData("ABCDEFabcdef0123456789")
    .

addToLabel(label);
```

---

### Comments

Add a `Comment` to the label:

```java
import com.github.mortonl.zebra.elements.miscellaneous.Comment;
```

```java
Comment.createComment()
    .

withComment("THIS IS A COMMENT")
    .

addToLabel(label);
```

---

## Getting the DPI

Retrieve the printer's DPI using the `~HI` command to get the printer information this returns the dots per mm i.e. one
of `6`,`8`,`12`,`24`

Once you have the dots per mm you can then use the built-in functionality to get the appropriate `PrintDensity` for your
use case:

```java
PrintDensity.fromDotsPerMillimetre(6);
```

Or if you somehow know the dpi but not the dots per mm you can instead use:

```java
PrintDensity.fromDotsPerInch(203);
```

---

## Additional Examples

### Text Block with Formatting

Create a text block with specific formatting and positioning:

```java
import com.github.mortonl.zebra.elements.text.TextBlock;
import com.github.mortonl.zebra.elements.text.TextJustification;
```

```java
TextBlock.createTextBlock()
    .

withPosition(37.5,2.5)
    .

withHexadecimalContent("ABABABAB")
    .

withFont(customFont)
    .

withWidthMm(27.5)
    .

withMaxLines(1)
    .

withJustification(TextJustification.CENTER)
    .

addToLabel(label);
```

### Horizontal and Vertical Lines

Add horizontal and vertical lines using pre-configured builders:

```java
// Horizontal line
GraphicBox.horizontalLine(80.0,2.0)
    .

withPosition(10.0,30.0)
    .

addToLabel(label);

// Vertical line
GraphicBox.

verticalLine(50.0,2.0)
    .

withPosition(20.0,40.0)
    .

addToLabel(label);
```

---

## Notes

- **Testing Labels:** Before printing, test your labels using online tools or printer simulators.
- **Error Handling:** Always handle `ValidationException` to catch issues with element configurations.
- **Label Preview:** Visualize your label's layout to ensure elements are correctly positioned.

---

For any questions or issues, please refer to the [Contact](README.md#contact) section in the main README.
