package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.regex.Pattern;
/**
 * Represents a Person's company in the contact book.
 * Guarantees: immutable; is valid as declared in {@link #isValidCompany(String)}
 */
public class Company {

    public static final int MAX_LENGTH = 100;
    public static final String MESSAGE_CONSTRAINTS =
            "Company can take any values, should not be blank, and must be at most "
                    + MAX_LENGTH + " characters after trimming extra spaces";

    /*
     * The first character of the company must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[^\\s].*";

    private static final Pattern MULTIPLE_WHITESPACE_PATTERN = Pattern.compile("\\s+");

    public final String value;

    /**
     * Constructs a {@code Company}.
     *
     * @param company A valid company.
     */
    public Company(String company) {
        requireNonNull(company);
        String normalizedCompany = normalizeSpacing(company);
        checkArgument(isValidNormalizedCompany(normalizedCompany), MESSAGE_CONSTRAINTS);
        value = normalizedCompany;
    }

    /**
     * Returns true if a given string is a valid company value.
     */
    public static boolean isValidCompany(String test) {
        requireNonNull(test);
        return isValidNormalizedCompany(normalizeSpacing(test));
    }

    private static String normalizeSpacing(String value) {
        String trimmed = value.trim();
        return MULTIPLE_WHITESPACE_PATTERN.matcher(trimmed).replaceAll(" ");
    }

    private static boolean isValidNormalizedCompany(String normalizedCompany) {
        Boolean withinLength = normalizedCompany.length() <= MAX_LENGTH;
        Boolean isMatch = normalizedCompany.matches(VALIDATION_REGEX);
        return withinLength && isMatch;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Company)) {
            return false;
        }

        Company otherCompany = (Company) other;
        return value.equals(otherCompany.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
