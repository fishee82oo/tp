package seedu.contact.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.contact.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in the contact book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 50;
    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters and spaces, and it should not be blank"
                    + " (maximum " + MAX_LENGTH + " characters)";

    /*
     * The first character of the contact must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        String normalized = name.trim().replaceAll("\\s+", " ");
        checkArgument(isValidName(normalized), MESSAGE_CONSTRAINTS);
        fullName = normalized;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        Boolean bool1 = test.length() <= MAX_LENGTH;
        Boolean bool2 = test.matches(VALIDATION_REGEX);

        return bool1 && bool2;
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name)) {
            return false;
        }

        Name otherName = (Name) other;
        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
