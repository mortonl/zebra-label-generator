# Contributing to zebra-label-generator

Thank you for your interest in contributing to zebra-label-generator! For now, I'm wanting to develop the core of this library by myself and make it a bit more feature complete, and then I'll open it up to the community and start accepting contributions when I feel I've reached a place where I'm happy to do so. When that time comes however this document provides guidelines and requirements for contributing to the project.

## Pull Request General Notes

If you can split a pull-request please do so, I much prefer to review many small PRs rather than fewer larger ones. It also helps if there could be any differences of opinion as some of the stuff can be happily merged while we're bike-shedding.

## Code Style and Standards

### Element Implementation

When creating new label elements:

1. Implement `LabelElement` interface or extend another class that does such as `PositionedElement` class (for elements
   requiring position)
2. Use constants from `ZplCommand` class
3. Use static imports for methods
4. Use Lombok annotations, particularly `@Builder` (without default values)
5. All dimensions must be specified in millimeters and then converted to dots as part of the toZPLString

### Command Generation

- Use `ZplCommand.generateZplIICommand()` static method for generating ZPL commands
- The method takes the command as first parameter followed by any number of parameters
- Parameters are automatically converted to String and comma-separated

### Validation Requirements

1. Implement validation in `validateInContext(LabelSize size, PrintDensity dpi)` method
2. Validate minimum/maximum values against printer DPI capabilities:
    - Use `PrintDensity.getMinDotsPerMillimetre()` and `getMaxDotsPerMillimetre()`
    - Convert millimeter values to dots using `dpi.toDots(mm)`
3. Include label size related validations where appropriate

## Testing Requirements

### Test Setup

- Use JUnit Jupiter with Mockito runner (when mocks are needed)
- Create shared mocks in `@BeforeEach` block only if they are used in each test, otherwise create the mock in the
  individual test i.e. avoid setting up unused mocks
- Use constants instead of mocking enums:

```java
private static final LabelSize DEFAULT_SIZE = LabelSize.LABEL_4X6; // 101.6mm x 152.4mm
private static final PrintDensity DEFAULT_DPI = PrintDensity.DPI_203; // 8 dots per mm
```

### Test Structure

1. Ensure tests are well named and use `@DisplayName` annotations on:
    * Test classes
    * Test scenarios
    * Individual test methods
2. Prefer parameterized tests where applicable
3. Group multiple assertions using assertAll():

```java
assertAll("validation checks",
    () -> assertEquals(expected1, actual1),
    () -> assertEquals(expected2, actual2)
);
```

## Pull Request Process

1. Ensure your code follows all style and testing requirements
2. Update documentation if you're adding new features
3. Add tests for new functionality
4. Ensure all tests pass
5. Update the README.md if necessary

## Development Workflow

1. Fork the repository
2. Create a feature branch
3. Implement your changes following the guidelines
4. Write/update tests
5. Submit a pull request

## Questions or Issues?

If you have questions about implementing these requirements or need clarification, please:

1. Check existing issues
2. Create a new issue with a clear description
3. Tag it appropriately (question/bug/enhancement)

## Code Review Process

All submissions require review. We strive to review pull requests within a reasonable time frame.

Your pull request should:

* Follow all code style guidelines
* Include appropriate tests
* Have passing CI checks - To be implemented
* Include documentation updates if needed

