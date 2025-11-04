package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified using their name or the index shown in the last person listing.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the name or by their displayed index.\n"
            + "Parameters: NAME | INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " Alice Pauline\n"
            + "Example: " + COMMAND_WORD + " 1\n"
            + "If multiple contacts share the same name, they will be listed automatically."
            + " Delete the intended contact using its index.";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Contact: \n%1$s";
    public static final String MESSAGE_MULTIPLE_PERSONS_WITH_NAME =
            "Multiple contacts named %1$s found. Please specify the index to delete, e.g. delete 1";

    private final Name targetName;
    private final Index targetIndex;

    /**
     * Creates a {@code DeleteCommand} that deletes based on {@code targetName}.
     */
    public DeleteCommand(Name targetName) {
        this.targetName = requireNonNull(targetName);
        this.targetIndex = null;
    }

    /**
     * Creates a {@code DeleteCommand} that deletes based on {@code targetIndex} of the last shown list.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
        this.targetName = null;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (isIndexTarget()) {
            return executeDeleteByIndex(model);
        }
        return executeDeleteByName(model);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        if (isIndexTarget()) {
            return otherDeleteCommand.isIndexTarget()
                    && targetIndex.equals(otherDeleteCommand.targetIndex);
        }
        return !otherDeleteCommand.isIndexTarget()
                && targetName.equals(otherDeleteCommand.targetName);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        if (isIndexTarget()) {
            builder.add("targetIndex", targetIndex);
        } else {
            builder.add("targetName", targetName);
        }
        return builder.toString();
    }

    private boolean isIndexTarget() {
        return targetIndex != null;
    }

    private CommandResult executeDeleteByIndex(Model model) throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
        if (model.getFocusedPerson().get() != null && model.getFocusedPerson().get().equals(personToDelete)) {
            model.updateFocusedPerson(-1);
        }
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    private CommandResult executeDeleteByName(Model model) throws CommandException {
        List<Person> matchingPersons = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().fullName.equalsIgnoreCase(targetName.fullName))
                .collect(Collectors.toList());

        if (matchingPersons.isEmpty()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_INFO);
        }

        if (matchingPersons.size() > 1) {
            model.updateFilteredPersonList(person ->
                    person.getName().fullName.equalsIgnoreCase(targetName.fullName));
            String feedback = MESSAGE_MULTIPLE_PERSONS_WITH_NAME.formatted(targetName)
                    + System.lineSeparator()
                    + Messages.MESSAGE_PERSONS_LISTED_OVERVIEW.formatted(matchingPersons.size());
            return new CommandResult(feedback);
        }

        Person personToDelete = matchingPersons.get(0);
        model.deletePerson(personToDelete);
        if (model.getFocusedPerson().get() != null && model.getFocusedPerson().get().equals(personToDelete)) {
            model.updateFocusedPerson(-1);
        }
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }
}
