# LoadedMedia Usage Example

The new `LoadedMedia` support allows for both fixed-size and dynamic-length label media. Here are some examples:

```java
// Create printer configuration with fixed-size media
PrinterConfiguration fixedConfig = PrinterConfiguration.builder()
                                                       .loadedMedia(LoadedMedia.builder()
                                                                               .widthMm(100.0)
                                                                               .fixedLengthMm(150.0)
                                                                               .build())
                                                       .build();

// Create printer configuration with dynamic-length media
PrinterConfiguration dynamicConfig = PrinterConfiguration.builder()
                                                         .loadedMedia(LoadedMedia.builder()
                                                                                 .widthMm(100.0)
                                                                                 .fixedLengthMm(null) // null indicates dynamic length
                                                                                 .build())
                                                         .build();

// Create a label - this will validate against the media size
ZebraLabel label = ZebraLabel.forPrinter(fixedConfig)
                             .widthMillimetres(90.0)  // Must be <= media width
                             .heightMillimetres(140.0) // Must be <= media length for fixed, or within valid range for dynamic
                             .build();
```

The loaded media will enforce these constraints:

- Width must be positive
- For fixed-length media, length must be between 6.35mm and 991mm
- For dynamic-length media, each label's height must be between 6.35mm and 991mm
- Labels must not exceed the media width
- Labels must not exceed the fixed media length (if using fixed-length media)