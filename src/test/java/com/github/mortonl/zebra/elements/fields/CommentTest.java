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
            .createComment()
            .withContent("Test Comment")
            .build();
        String expected = "^FX" + "Test Comment" + "^FS";
        assertEquals(expected, comment.toZplString(PrintDensity.DPI_203));
    }

    @Test
    void testToZplStringWithEmptyComment()
    {
        Comment comment = Comment.createComment().withContent("").build();
        String expected = "^FX" + "^FS";
        assertEquals(expected, comment.toZplString(PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validComments")
    void testValidateInContextWithValidComments(String testName, String commentText)
    {
        Comment comment = Comment.createComment().withContent(commentText).build();
        assertDoesNotThrow(() -> comment.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidComments")
    void testValidateInContextWithInvalidComments(String testName, String commentText)
    {
        Comment comment = Comment.createComment().withContent(commentText).build();
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> comment.validateInContext(LabelSize.LABEL_4X6, PrintDensity.DPI_203)
        );

        assertTrue(exception
            .getMessage()
            .contains("Comments cannot contain the special characters"));
    }

    @Test
    void testNullComment()
    {
        assertThrows(NullPointerException.class, () ->
            Comment.createComment().withContent(null).build()
        );
    }
}
