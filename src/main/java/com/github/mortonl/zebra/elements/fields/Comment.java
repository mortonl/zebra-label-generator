package com.github.mortonl.zebra.elements.fields;

import com.github.mortonl.zebra.elements.LabelElement;
import com.github.mortonl.zebra.elements.fonts.DefaultFont;
import com.github.mortonl.zebra.label_settings.LabelSize;
import com.github.mortonl.zebra.printer_configuration.PrintDensity;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import static com.github.mortonl.zebra.ZplCommand.COMMENT;
import static com.github.mortonl.zebra.ZplCommand.CONTROL_CHARACTERS;
import static com.github.mortonl.zebra.ZplCommand.FIELD_END;
import static com.github.mortonl.zebra.ZplCommand.generateZplIICommand;

/**
 * <p>Implements a comment element in ZPL II, which allows adding non-printing
 * documentation within the label format. Comments are useful for:</p>
 * <ul>
 *     <li>Documenting label layouts</li>
 *     <li>Adding version information</li>
 *     <li>Including author attribution</li>
 *     <li>Explaining complex label configurations</li>
 * </ul>
 *
 * <p>Comments are ignored by the printer during label generation but are preserved
 * in the ZPL code for documentation purposes.</p>
 *
 * <p><strong>Usage example:</strong></p>
 * <pre>{@code
 * Comment.createComment()
 *     .withContent("Label template v1.2 - Created by Luke Morton")
 *     .addToLabel(label);
 *
 * Comment.createComment()
 *     .withContent("Configuration: High-resolution shipping label")
 *     .addToLabel(label);
 * }</pre>
 *
 * @see LabelElement The parent class for all label elements
 */
@Getter
@SuperBuilder(builderMethodName = "createComment", setterPrefix = "with")
public class Comment extends LabelElement
{
    /**
     * The text content of the comment.
     * Must not contain any ZPL control characters that could prematurely terminate the comment.
     *
     * <p>Restrictions:</p>
     * <ul>
     *     <li>Cannot be null (enforced by {@code @NonNull})</li>
     *     <li>Cannot contain ZPL control characters (e.g., '~', '^')</li>
     *     <li>No length restrictions other than practical memory limits</li>
     * </ul>
     *
     * @param content the text content for the comment, must not contain ZPL control characters
     * @return the text content of the comment
     */
    @NonNull
    private final String content;

    /**
     * {@inheritDoc}
     *
     * <p>Generates a ZPL II comment command using the specified content.
     * The comment is enclosed in appropriate ZPL command delimiters.</p>
     *
     * @return The ZPL II command string representing this comment
     */
    @Override
    public String toZplString(PrintDensity dpi)
    {
        return generateZplIICommand(COMMENT, this.content) + FIELD_END;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Validates that the comment content does not contain any ZPL control characters
     * that could cause parsing issues. These characters include command delimiters
     * that would prematurely terminate the comment.</p>
     *
     * @throws IllegalStateException if the content contains any ZPL control characters
     */
    @Override
    public void validateInContext(LabelSize size, PrintDensity dpi, final DefaultFont defaultFont) throws IllegalStateException
    {
        if (CONTROL_CHARACTERS
            .stream()
            .anyMatch(content::contains)) {
            throw new IllegalStateException(
                "Comments cannot contain the special characters " +
                    String.join(" or ", CONTROL_CHARACTERS) +
                    " as these end the comment early"
            );
        }
    }
}
