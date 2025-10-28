package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.regex.Pattern;

/**
 * Represents a Tag in the address book.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final int MAX_LENGTH = 30;
    public static final String MESSAGE_CONSTRAINTS =
            "Tag names should only contain alphanumeric characters, spaces and must be at most "
                    + MAX_LENGTH + " characters after trimming extra spaces.";
    public static final String VALIDATION_REGEX = "[\\p{Alnum}]+(?: [\\p{Alnum}]+)*";

    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile(" +");

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        String normalizedTagName = normalizeSpacing(tagName);
        checkArgument(isValidTagName(normalizedTagName), MESSAGE_CONSTRAINTS);
        this.tagName = normalizedTagName;
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        requireNonNull(test);
        String normalizedTagName = normalizeSpacing(test);

        if (normalizedTagName.isEmpty()) {
            return false;
        }

        Boolean withinMaxLength = normalizedTagName.length() <= MAX_LENGTH;
        Boolean matchesPattern = normalizedTagName.matches(VALIDATION_REGEX);

        return withinMaxLength && matchesPattern;
    }

    /**
     * Collapses repeated spaces between words into a single space and trims leading and trailing whitespace.
     */
    public static String normalizeSpacing(String tagName) {
        requireNonNull(tagName);
        String trimmedTagName = tagName.trim();
        return MULTIPLE_SPACES_PATTERN.matcher(trimmedTagName).replaceAll(" ");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag)) {
            return false;
        }

        Tag otherTag = (Tag) other;
        return tagName.equalsIgnoreCase(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.toLowerCase().hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
