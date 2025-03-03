package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentTest
{
    private static final LabelSize DEFAULT_SIZE = LabelSize.LABEL_4X6;
    private static final PrintDensity DEFAULT_DPI = PrintDensity.DPI_203;

    private static Stream<Arguments> validComments()
    {
        return Stream.of(
            Arguments.of("Simple comment", "This is a test comment"),
            Arguments.of("Numbers and special chars", "Test123!@#$%&*()"),
            Arguments.of("Multiple spaces", "Comment with    spaces"),
            Arguments.of("Unicode characters", "Test コメント 测试 τεστ")
        );
    }

    private static Stream<Arguments> invalidComments()
    {
        return Stream.of(
            Arguments.of("Comment with ~", "Test~Comment"),
            Arguments.of("Comment with ^", "Test^Comment"),
            Arguments.of("Multiple control characters", "Test~^Comment"),
            Arguments.of("Control character at start", "~TestComment"),
            Arguments.of("Control character at end", "TestComment^")
        );
    }

    @Test
    void testToZplString()
    {
        Comment comment = Comment
            .builder()
            .content("Test Comment")
            .build();
        String expected = "^FX" + "Test Comment" + "^FS";
        assertEquals(expected, comment.toZplString(DEFAULT_DPI));
    }

    @Test
    void testToZplStringWithEmptyComment()
    {
        Comment comment = new Comment("");
        String expected = "^FX" + "^FS";
        assertEquals(expected, comment.toZplString(DEFAULT_DPI));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validComments")
    void testValidateInContextWithValidComments(String testName, String commentText)
    {
        Comment comment = new Comment(commentText);
        assertDoesNotThrow(() -> comment.validateInContext(DEFAULT_SIZE, DEFAULT_DPI));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidComments")
    void testValidateInContextWithInvalidComments(String testName, String commentText)
    {
        Comment comment = new Comment(commentText);
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> comment.validateInContext(DEFAULT_SIZE, DEFAULT_DPI)
        );

        assertTrue(exception
            .getMessage()
            .contains("Comments cannot contain the special characters"));
    }

    @Test
    void testNullComment()
    {
        assertThrows(NullPointerException.class, () -> new Comment(null));
    }
}
