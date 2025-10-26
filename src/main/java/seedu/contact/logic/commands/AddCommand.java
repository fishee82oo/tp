package seedu.contact.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.contact.commons.util.ToStringBuilder;
import seedu.contact.logic.Messages;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.model.Model;
import seedu.contact.model.person.Person;

/**
 * Adds a person to the FastCard contact book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the contact book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_COMPANY + "COMPANY "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@ms.com "
            + PREFIX_COMPANY + "Morgan Stanley "
            + PREFIX_TAG + "competitor"
            + PREFIX_TAG + "delta one sales";

    public static final String MESSAGE_SUCCESS = "New person added: \n%1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the contact book";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand)) {
            return false;
        }

        AddCommand otherAddCommand = (AddCommand) other;
        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
