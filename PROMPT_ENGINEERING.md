Generate a class that fulfils the following requirements

- implements LabelElement or extends PositionedElement (if the element would require a position)
- uses constants from ZplCommand
- use static imports for methods
- uses lombok annotations particularly Builder with defaults
- dimensions should be specified in millimetres and in the toZplString(
  com.github.mortonl.zebra.printer_configuration.PrintDensity dpi) method use dpi.toDots(mm) to calculate the dots
- commands should be generated using the static method from ZplCommand generateZplIICommand which takes the command as
  the first parameter and then any number of Objects as parameters after and calls string valueOf and comma separates
  them automatically.
- minimum and maximum values in the spec should be validated against the minimum and maximum dots per millimetre, i.e. a
  minimum in millimetres should when multiplied by the minimum dots per millimetre for validation, and the maximum value
  calculated from the maximum dots per millimetre. the values can be retrieved from the static methods
  getMinDotsPerMillimetre and getMaxDotsPerMillimetre on the PrintDensity class
- validation should always occur when instantiating a class
- for label size related validations
- fulfils this spec:
