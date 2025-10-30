package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    public static final String MESSAGE_INVALID_PERSON_NAME = "The person name provided is invalid";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d contacts listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();

        builder.append("Name: ").append(person.getName())
                .append("\nPhone: ").append(person.getPhone());

        if (!person.getEmail().value.equals("unknown@example.com")) {
            builder.append("\nEmail: ").append(person.getEmail());
        }

        if (!person.getCompany().value.equals("N/A")) {
            builder.append("\nCompany: ").append(person.getCompany());
        }

        if (!person.getTags().isEmpty()) {
            builder.append("\nTags: ");
            person.getTags().forEach(tag -> builder.append(tag).append(" "));
        }

        return builder.toString().trim();
    }

}
