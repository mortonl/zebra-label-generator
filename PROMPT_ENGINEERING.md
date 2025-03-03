Generate a class that fulfils the following requirements

- implements LabelElement or extends PositionedElement (if the element would require a position)
- uses constants from ZplCommand
- use static imports for methods
- Orientation has a class defined already in the package com.github.mortonl.zebra.formatting
- uses lombok annotations particularly Builder but don't default values
- dimensions should be specified in millimetres and in the toZplString(
  com.github.mortonl.zebra.printer_configuration.PrintDensity dpi) method use dpi.toDots(mm) to calculate the dots
- commands should be generated using the static method from ZplCommand generateZplIICommand which takes the command as
  the first parameter and then any number of Objects as parameters after and calls string valueOf and comma separates
  them automatically.
- minimum and maximum values in the spec should be validated against the minimum and maximum dots per millimetre, i.e. a
  minimum in millimetres should when multiplied by the minimum dots per millimetre for validation, and the maximum value
  calculated from the maximum dots per millimetre. the values can be retrieved from the static methods
  getMinDotsPerMillimetre and getMaxDotsPerMillimetre on the PrintDensity class
- validation should be performed in the validateInContext(LabelSize size, PrintDensity dpi) method as this is called
  when elements are added to labels
- the Validator class has validation helpers such as
    - validateRange(double value, double min, double max, String fieldName)
    - validateRange(int value, int min, int max, String fieldName)
    - validateNotNull(Object value, String fieldName)
    - validateNotEmpty(String value, String fieldName)
- for label size related validations
- fulfils this spec:

Generate unit tests meeting these criteria:
- use junit jupiter with the mockito runner (if mocks are required)
- only mocks used in all tests should be created in the before each block
- don't mock enums instead use constants for label size and dpi e.g.
  private static final com.github.mortonl.zebra.label_settings.LabelSize DEFAULT_SIZE = LabelSize.LABEL_4X6; // 101.6mm
  x 152.4mm
  private static final com.github.mortonl.zebra.printer_configuration.PrintDensity DEFAULT_DPI =
  PrintDensity.DPI_203; // 8 dots per mm
- Parameterized tests are preferred and DisplayNames are required on classes, scenarios and tests
- group multiple assertions in tests with assertAll to get all the failures in the result
- ideally builder methods would be static imported but due to a compilation and annotation processing order issue with
  javac we cannot

Generate Javadocs that:
- include references to other classes
- usage examples when relevant etc.
- NOTE: when a value isn't provided the library will not include any value in the zpl command, this is because providing a value would prevent the effective use of default commands. so the default values aren't actually included in the output command but are important to note as the printer will use that value or the value from the last relevant default command.
- insteadOf withContent users should make use of withPlainTextContent(String contents) or withHexadecimalContent(String contents) as these shortcut the construction the relevant Field instance with the correct data and settings
- If the overriding method's behavior is identical to the base method (including parameter meanings, return values, and exceptions), then no additional JavaDoc is needed. The @Override annotation is sufficient, however document differences while avoiding redundant documentation by using inheritDoc if the overriding method:
  - Has different/additional behavior
  - Has stronger/weaker constraints
  - Has covariant return types
  - Has different exception conditions
  - Has specific implementation details that are important for users
- fulfils this spec:
