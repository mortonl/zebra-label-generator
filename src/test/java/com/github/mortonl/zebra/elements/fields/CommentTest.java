package com.github.mortonl.zebra.elements.fields;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.github.mortonl.zebra.label_settings.LabelSize.LABEL_4X6;
import static com.github.mortonl.zebra.printer_configuration.PrintDensity.DPI_203;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Comment field creation and validation")
@Tag("unit")
@Tag("comment")
class CommentTest
{

    private static final String VALID_COMMENT_TEXT = "Test Comment";

    private static final String EMPTY_COMMENT_TEXT = "";

    private static final String EXPECTED_VALID_ZPL = "^FXTest Comment^FS";

    private static final String EXPECTED_EMPTY_ZPL = "^FX^FS";

    private static final String EXPECTED_ERROR_MESSAGE = "Comments cannot contain the special characters";

    private Comment classUnderTest;

    private static Stream<Arguments> validCommentsForValidateInContext()
    {
        return Stream.of(
            Arguments.of("Simple comment", "This is a test comment"),
            Arguments.of("Numbers and special chars", "Test123!@#$%&*()"),
            Arguments.of("Multiple spaces", "Comment with    spaces"),
            Arguments.of("Unicode characters", "Test コメント 测试 τεστ")
        );
    }

    private static Stream<Arguments> invalidCommentsForValidateInContext()
    {
        return Stream.of(
            Arguments.of("Comment with ~", "Test~Comment"),
            Arguments.of("Comment with ^", "Test^Comment"),
            Arguments.of("Multiple control characters", "Test~^Comment"),
            Arguments.of("Control character at start", "~TestComment"),
            Arguments.of("Control character at end", "TestComment^")
        );
    }

    @BeforeEach
    void setUp()
    {
        classUnderTest = Comment
            .createComment()
            .withContent(VALID_COMMENT_TEXT)
            .build();
    }

    @Test
    @DisplayName("toZplString generates correct ZPL for valid comment")
    @Tag("zpl-generation")
    void Given_ValidComment_When_ToZplString_Then_GeneratesCorrectZpl()
    {
        // Given (classUnderTest is already configured with valid comment)

        // When
        String actualZplString = classUnderTest.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_VALID_ZPL, actualZplString);
    }

    @Test
    @DisplayName("toZplString generates correct ZPL for empty comment")
    @Tag("zpl-generation")
    void Given_EmptyComment_When_ToZplString_Then_GeneratesCorrectZpl()
    {
        // Given
        Comment emptyComment = Comment.createComment()
                                      .withContent(EMPTY_COMMENT_TEXT)
                                      .build();

        // When
        String actualZplString = emptyComment.toZplString(DPI_203);

        // Then
        assertEquals(EXPECTED_EMPTY_ZPL, actualZplString);
    }

    @ParameterizedTest(name = "validateInContext accepts {0}")
    @MethodSource("validCommentsForValidateInContext")
    @DisplayName("validateInContext accepts valid comments")
    @Tag("validation")
    void Given_ValidComment_When_ValidateInContext_Then_NoException(String testName, String commentText)
    {
        // Given
        Comment validComment = Comment.createComment()
                                      .withContent(commentText)
                                      .build();

        // When & Then
        assertDoesNotThrow(() -> validComment.validateInContext(LABEL_4X6, DPI_203, null));
    }

    @ParameterizedTest(name = "validateInContext rejects {0}")
    @MethodSource("invalidCommentsForValidateInContext")
    @DisplayName("validateInContext throws exception for invalid comments")
    @Tag("validation")
    void Given_InvalidComment_When_ValidateInContext_Then_ThrowsException(String testName, String commentText)
    {
        // Given
        Comment invalidComment = Comment.createComment()
                                        .withContent(commentText)
                                        .build();

        // When & Then
        IllegalStateException actualException = assertThrows(
            IllegalStateException.class,
            () -> invalidComment.validateInContext(LABEL_4X6, DPI_203, null)
        );

        assertTrue(actualException.getMessage()
                                  .contains(EXPECTED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("build throws exception for null comment content")
    @Tag("validation")
    void Given_NullContent_When_Build_Then_ThrowsException()
    {
        // Given, When & Then
        assertThrows(NullPointerException.class, () ->
            Comment.createComment()
                   .withContent(null)
                   .build()
        );
    }
}
