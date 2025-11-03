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
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The contact index provided is invalid.";
    public static final String MESSAGE_INVALID_PERSON_INFO = "The contact information provided is invalid. Confirm you are deleting one contact at a time. \nPlease make sure NAME (case insensitive) in <delete NAME> is full name. " +
            "\nWhen there are multiple contacts of that name shown, you should <delete INDEX>. \nPlease make sure the index is a valid integer.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d contact(s) listed!";
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

        if (!person.getDetail().isEmpty()) {
            builder.append("\nDetail: ").append(person.getDetail());
        }

        if (!person.getTags().isEmpty()) {
            builder.append("\nTags: ");
            person.getTags().forEach(tag -> builder.append(tag).append(" "));
        }

        return builder.toString().trim();
    }

}
