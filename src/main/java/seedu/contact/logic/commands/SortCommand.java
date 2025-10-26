package seedu.contact.logic.commands;

import static seedu.contact.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Comparator;
import java.util.logging.Logger;

import seedu.contact.commons.core.LogsCenter;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.model.Model;
import seedu.contact.model.person.Person;

/**
 * Sorts all persons in the contact book for the user.
 */
public class SortCommand extends Command {

    public static final String COMMAND_WORD = "sort";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": sorts all persons in contact book "
            + "based on the sorting comparator passed in.\n"
            + "Parameters: f/[FIELD] o/[ORDER]\n"
            + "Example: " + COMMAND_WORD + " f/name o/asc\n"
            + "Valid fields: name, tag\n"
            + "Valid orderings: asc, desc";

    public static final String MESSAGE_SUCCESS = "Sorted all persons by %s in %s order";

    private static final Logger logger = LogsCenter.getLogger(SortCommand.class);

    private String field;
    private String order;

    /**
     * Creates a SortCommand object with field and order
     * initialized.
     *
     * @param field Comparator to be used for sorting
     * @param order Order in which to sort contacts
     */
    public SortCommand(String field, String order) {
        requireAllNonNull(field, order);
        this.field = field;
        this.order = order;
        logger.fine("SortCommand created with field: " + field + ", order: " + order);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        logger.info("Executing sort command with field: " + field + ", order: " + order);

        Comparator<Person> comparator = getComparator();
        model.sortPersons(comparator);

        logger.info("Sort command executed successfully. "
                + "Sorted by " + field + " in " + getOrderFullFormat() + " order");
        return new CommandResult(String.format(MESSAGE_SUCCESS, field, getOrderFullFormat()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SortCommand)) {
            return false;
        }

        SortCommand other = (SortCommand) o;

        return this.field.equals(other.field) && this.order.equals(other.order);
    }

    private Comparator<Person> getComparator() throws CommandException {
        Comparator<Person> comparator;

        switch (field.toLowerCase()) {
        case "name":
            logger.fine("Creating name comparator");
            comparator = Comparator.comparing(person -> person.getName().toString(), String.CASE_INSENSITIVE_ORDER);
            break;
        case "tag":
            logger.fine("Creating tag comparator");
            comparator = Comparator.comparing(person ->
                    person.getTags().stream()
                          .map(tag -> tag.tagName.toLowerCase())
                          .sorted()
                          .findFirst()
                          .orElse(""));
            break;
        default:
            logger.warning("Invalid field specified: " + field);
            throw new CommandException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        switch (order.toLowerCase()) {
        case "asc":
        case "ascending":
            logger.fine("Applying ascending order");
            return comparator;
        case "desc":
        case "descending":
            logger.fine("Applying descending order");
            return comparator.reversed();
        default:
            logger.warning("Invalid order specified: " + order);
            throw new CommandException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
    }

    /**
     * Returns the full format of a specified order
     * Precondition: Order is valid and is either an abbreviation or the full format
     * @return String containing the full format of the order
     */
    private String getOrderFullFormat() {
        switch (order) {
        case "asc":
            return "ascending";
        case "desc":
            return "descending";
        default:
            return order;
        }
    }
}
