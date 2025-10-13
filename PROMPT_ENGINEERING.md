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
- don't mock enums instead use them directly LabelSize.LABEL_4X6; // 101.6mm x 152.4mm and PrintDensity.DPI_203; // 8 dots per mm
- Parameterized tests are preferred and DisplayNames are required on classes, scenarios and tests
- group multiple assertions in tests with assertAll to get all the failures in the result
- ideally builder methods would be static imported but due to a compilation and annotation processing order issue with
  javac we cannot

Update this test class to follow these rules and conventions:

#### Imports
- Do **not** use wildcard imports.
- Prefer **static imports** for functions and constants.

#### Test Structure
- Test **only one public method** of the class per test method.
- Use **@DisplayName** on test classes and test methods to describe their purpose clearly.
- Use **@Tag** annotations on test classes and methods to support test grouping and filtering.
- Name test methods using the **Given_When_Then** format to express the scenario being tested clearly.
    - Method names must be **64 characters or fewer** to maintain readability and avoid tooling issues.
    - If the name exceeds the limit, shorten it by removing redundant words or abbreviating context while preserving clarity.
- Structure test code into **Given**, **When**, and **Then** blocks:
    - **Given**: Create test objects, mock dependencies, and arrange input.
    - **When**: Perform the action or invoke the method under test.
    - **Then**: Assert the output and verify expected behaviour.
    - Always verify that there were **no unexpected interactions** or side effects.
- Instantiate the class under test using a clearly named variable, such as `classUnderTest` or `classInstanceUnderTest`.
    - If the class is **stateless** and safe to reuse, instantiate it once in a `@BeforeAll` or as a static final field.
    - If the class is **mutable** or its state could be polluted between tests, instantiate it in a `@BeforeEach` method to ensure test isolation.

#### Parameterised Testing
- If testing a method with multiple input variations, use a **parameterised test**.
- Use **@CsvSource** when variations are short and readable.
- Use **@MethodSource** when variations are complex or lengthy.

#### Assertions
- Use **`assertAll`** when asserting multiple properties of a single result to report all failures together.
- Prefix variables used in assertions with `actual` and `expected` to improve readability.
- Assert only **hard-coded expected values** against actual results.
- Use **constants** to follow the DRY principle:
    - Name constants using **UPPER_SNAKE_CASE**.
    - If constants are reused across multiple test classes, place them in a shared constants class in the nearest common package.

- For **multi-line strings or CSV expectations**:
    - Use **inline strings** only if they are short and readable.
    - For longer or complex expectations, load them from clearly named files in the `test/resources` directory. Place them in an obvious and relevant location to keep test code clean and maintainable.

#### Mocking
- Mock any dependency whose functionality is **outside the scope** of the class under test.
- Use **Mockito.verify** to confirm all expected interactions with mocks.
- Use **verifyNoMoreInteractions** to ensure no unexpected interactions occurred.
- If the **order of interactions matters** (i.e. it would affect the outcomes), use **`InOrder`** to verify the sequence.

#### Random Values
- Use **random values** to increase input diversity and uncover edge cases.
- Ensure **reproducibility** by:
    - Using a fixed seed (e.g. `new Random(42)`) or logging the seed used.
    - Avoiding flaky tests—random values should **not cause intermittent failures**. If a test fails due to randomness, it must be reproducible and explainable.
- Use random values **only where meaningful**, such as numeric ranges, strings, or dates, and avoid them where deterministic behaviour is required.

#### Edge Case Testing
- Always include tests for edge cases:
    - Empty strings
    - Null values
    - Boundary conditions (e.g. min/max values, zero, negative numbers)
    - Unexpected but valid inputs

#### Test Hygiene & Maintainability
- Never implement **production logic** in a test case just to pass assertions.
- Write tests with **realistic scenarios** in mind to ensure meaningful coverage.
- Create **helper functions** to generate commonly used objects and mock data to improve readability and maintainability.
- Use JUnit lifecycle annotations (e.g. `@BeforeEach`, `@AfterEach`) to prepare and clean up test data, ensuring **test isolation**.
- Aim to cover **at least 80% of the code** with unit tests. More coverage is better until diminishing returns are reached.
- Whenever possible, **write tests before implementation** to follow **Test-Driven Development (TDD)**. This ensures testable production code, easier refactoring, and fewer regressions.

Generate Javadocs that:

- include references to other classes
- usage examples when relevant etc.
- fields should have javadocs that include the param and the return type so that lombok auto generation will copy it to
  the methods
- NOTE: when a value isn't provided the library will not include any value in the zpl command, this is because providing
  a value would prevent the effective use of default commands. so the default values aren't actually included in the
  output command but are important to note as the printer will use that value or the value from the last relevant
  default command.
- insteadOf withContent users should make use of withPlainTextContent(String contents) or withHexadecimalContent(String
  contents) as these shortcut the construction the relevant Field instance with the correct data and settings
- If the overriding method's behavior is identical to the base method (including parameter meanings, return values, and
  exceptions), then no additional JavaDoc is needed. The @Override annotation is sufficient, however document
- inheritDoc can only be used on methods
  differences while avoiding redundant documentation by using inheritDoc if the overriding method:
    - Has different/additional behavior
    - Has stronger/weaker constraints
    - Has covariant return types
    - Has different exception conditions
    - Has specific implementation details that are important for users
- fulfils this spec:
