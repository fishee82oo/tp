package seedu.contact.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.contact.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.contact.model.Model;

/**
 * Lists all persons in the contact book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
