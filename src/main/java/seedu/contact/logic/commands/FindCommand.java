package seedu.contact.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.contact.commons.util.ToStringBuilder;
import seedu.contact.logic.Messages;
import seedu.contact.model.Model;
import seedu.contact.model.person.NameOrCompanyPredicate;

/**
 * Finds and lists all persons in address book whose names or companies match the given keywords.
 * Name and company both support partial (substring) search, case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds persons by name and/or company.\n"
            + "Parameters: [n/NAME_KEYWORD] [c/COMPANY_KEYWORD]\n"
            + "Example: " + COMMAND_WORD + " n/Alice\n"
            + "Example: " + COMMAND_WORD + " c/Google\n"
            + "Example: " + COMMAND_WORD + " n/Alice c/NUS";

    private final NameOrCompanyPredicate predicate;

    public FindCommand(NameOrCompanyPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW,
                model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof FindCommand
                && predicate.equals(((FindCommand) other).predicate));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
