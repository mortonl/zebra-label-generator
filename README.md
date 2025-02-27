# zebra-label-generator

A Java library that simplifies the generation of Zebra Printer Label (ZPL) commands, supporting barcodes and printer settings.

## Overview

This library provides a programmatic way to create ZPL label format commands, with a focus on DPI-agnostic label generation. It's designed to make working with Zebra printers more straightforward and maintainable in Java applications.

## Features

### Current Features
- Comprehensive near-complete set of constants for Zebra ZPL II commands
- Clean and type-safe Java API for label generation
- Support for standard ZPL printer settings
- DPI-Agnostic Label Creation (This sparked the idea for the initial project)

### Planned Features
- Elements for more Commands including:
  - More Barcode Types
  - More graphic types
    - Circle
    - Diagonal Lines
    - Ellipses
    - Symbols
- Graphic field validation
  - I currently dont understand some of the examples i've seen of graphic fields being used where non-hex characters were used in a hex command, when i can learn more about how this functionality is supposed to work i will add better validation.
- Setting defaults for the label
  - Warning when setting an item the same as a default setting?
- Explicit mode as an alternative to using defaults?
  - This would no longer print empty parts of the Zebra command and instead use the default values for that command where possible or the latest relevant default set?
- Image helpers to convert to ASCII hex and binary?
- Template vs complete zpl generation?
  - Currently designed around passing actual values into elements like text and barcodes etc. we could allow field parameters to be provided separately and this would allow the template to be permanently stored on the printer?
- Native command support? - will there be any cases where this would be needed, would like to avoid this as it goes against desired Java API.


## Getting Started

### Prerequisites
- Java 21 or higher
- Maven or Gradle for dependency management

### Installation

#### Maven
Add the following dependency to your `pom.xml` make sure to look up the value of the latest release:
```xml
<dependency>
    <groupId>io.github.mortonl</groupId>
    <artifactId>zebra-label-generator</artifactId>
    <version>1.0.3</version>
</dependency>
```

#### Gradle
Add the following to your `build.gradle` make sure to look up the value of the latest release:
```
implementation 'io.github.mortonl:zebra-label-generator:1.0.3'
```

## Usage

### Best Practices

#### Measurements and Positioning
- Sizes are never specified in dots when using this library; always use millimeters (mm) as doubles
- The library automatically handles conversion from mm to dots based on the DPI/DPMM when generating ZPL
- Use the provided helper methods for positioning and sizing:
```java
// Instead of:
.withWidthMm(27.5).withHeightMm(10.0)

// Use:
.withSize(27.5, 10.0)

// For positioning:
.withPosition(37.5, 0)
```
#### Builder Pattern Usage

- All elements use the Builder pattern for intuitive creation and configuration
- Take advantage of helper methods in builders to simplify common operations
- Builder method names are designed for readability and may evolve to become more intuitive
- Chain builder methods for cleaner, more readable code:

```java
GraphicBox box = GraphicBox.builder()
.withPosition(37.5, 0)
.withSize(27.5, 10)
.withThicknessMm(10.0)
.build();
```

#### Label Construction
- Use validateAndAddElement() to ensure elements are properly validated before being added to the label
- Add comments to document complex label layouts
- Use text blocks for formatted text with specific width constraints
- Use font configurations to maintain consistent typography across labels
- Consider grouping related elements logically in your code
- Test your labels using the Labelary Online ZPL Viewer before printing

#### DPI Independence
- Design your labels using millimeter measurements to ensure consistency across different printer DPIs
- Let the library handle the conversion between mm and dots for different printer resolutions
- Test your labels with different DPI settings to ensure proper scaling

### Basic Label Creation

Create a label with specific size and printer configuration:

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

### Generating ZPL Output

The library supports different DPI outputs from the same label definition.

NOTE: in this example output we have also added a graphic box and graphic field

For 203 DPI:
```java
String zpl203 = label.toZplString(DPI_203);
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

### Getting the DPI

In my use case we send the `~HI` command to get the printer information this returns the dots per mm i.e. one of
- `6`
- `8`
- `12`
- `24`

Once you have the dots per mm you can then use the built in functionality to get the appropriate `PrintDensity` for your use case:
```java
PrintDensity.fromDotsPerMillimetre(6);
```

Or if you somehow know the dpi but not the dots per mm you can instead use:

```java
PrintDensity.fromDotsPerInch(203);
```

### Adding and Validating Elements

### Text Elements

#### Font Configuration

Create a custom font configuration:

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
Text text = Text.builder()
                .withPosition(3.125, 56.625)
                .withFont(customFont)
                .withColorAndBackgroundReversed(false)
                .withPlainTextContent("Destination")
                .build();

label.validateAndAddElement(text);
```

#### Text Block with Formatting

Create a text block with specific formatting and positioning:

```java
TextBlock textBlock = TextBlock.builder()
.withPosition(37.5, 2.5)
.withHexadecimalContent("ABABABAB")  // Variable placeholder
.withFont(customFont)
.withWidthMm(27.5)
.withMaxLines(1)
.withJustification(CENTER)
.build();

label.validateAndAddElement(textBlock);
```

#### Comments

Add comments to your label for documentation purposes:

```java
Comment comment = Comment.builder()
    .comment("THIS IS A COMMENT")
    .build();

label.validateAndAddElement(comment);
```

#### Adding Graphics

##### Graphic Boxes
Add a box to the label:
```java
GraphicBox box = GraphicBox.builder()
    .withPosition(37.5, 0)
    .withSize(27.5, 10)
    .withThicknessMm(10.0)
    .withRoundness(0)
    .build();

label.validateAndAddElement(box);
```

##### Adding Graphic Images/Fields

```java
GraphicField graphic = GraphicField.builder()
    .withPosition(100.0, 100.0)
    .withCompressionType(CompressionType.ASCII_HEX)
    .withBinaryByteCount(11)
    .withGraphicFieldCount(8000)
    .withBytesPerRow(80)
    .withData("ABCDEFabcdef0123456789")
    .build();

label.validateAndAddElement(graphic);

```

## Reference Documentation
Official Zebra Programming Guide 2018 documentation is used as reference for implementation

- [Zebra Programming Guide 2018](https://support.zebra.com/cpws/docs/zpl/zpl-zbi2-pm-en.pdf)

## Inspirations
This project was created as an alternative to existing solutions, with a specific focus on:
- DPI-agnostic label generation
- More Comprehensive Validation

These projects in particular were
- [W3 Blog France / Zebra ZPL Project](https://github.com/w3blogfr/zebra-zpl)
  I was originally going to make use of this project but for many reasons i chose not to use it or fork it. In
  particular i found i was having to rewrite a lot of the logic for my own use cases, and it wouldn't solve my initial
  wishes for DPI agnostic programmatic label creation.

## Testing Labels

You can test the ZPL output generated by this library using
the [Labelary Online ZPL Viewer](https://labelary.com/viewer.html).
Simply copy the generated ZPL code and paste it into the viewer to preview how your labels will look when printed.

The Labelary Viewer allows you to:

- Visualize labels before printing
- Verify proper placement of elements
- Check barcode formatting
- Test different label sizes and dpi configurations
- Validate ZPL syntax

This is particularly useful during development to ensure your labels are formatted correctly without
needing physical access to a Zebra printer.

## Project Status
This project is currently under active development. While the core functionality is being implemented, contributions and feedback are welcome.

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

The Apache License 2.0 is a permissive license that allows you to:

- Use the software for any purpose
- Modify and distribute the software
- Use patent claims of contributors to the code
- Distribute modified versions under different terms

The license requires you to:

- Include a copy of the license
- Include any significant notices in the source files
- State significant changes made to the software
- Include any existing NOTICE file when distributing

## Acknowledgments
W3 Blog France / Zebra-ZPL project for inspiration
