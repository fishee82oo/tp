package seedu.contact.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.logging.Logger;

import seedu.contact.commons.util.ToStringBuilder;
import seedu.contact.logic.Messages;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.model.Model;
import seedu.contact.model.person.Person;

/**
 * Adds a person with only name and phone to the contact book.
 */
public class AddCommandBasic extends Command {

    public static final String COMMAND_WORD = "addbasic";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to contact book "
            + "with only name and phone. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Jadon "
            + PREFIX_PHONE + "88880000";

    public static final String MESSAGE_SUCCESS = "You have successfully added this contact: \n%1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the contact book";

    private static final Logger logger = Logger.getLogger(AddCommandBasic.class.getName());
    private final Person toAdd;

    /**
     * Creates an AddCommandBasic to add the specified {@code Person}
     */
    public AddCommandBasic(Person person) {
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
        logger.info("Adding a new person with basic information: " + toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddCommandBasic)) {
            return false;
        }

        AddCommandBasic otherAddCommandBasic = (AddCommandBasic) other;
        return toAdd.equals(otherAddCommandBasic.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
