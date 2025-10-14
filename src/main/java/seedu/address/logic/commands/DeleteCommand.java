package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.Name;

/**
 * Deletes a person identified using their name or index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the name or index.\n"
            + "Parameters: NAME | INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " Alice Pauline\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_NO_PERSON_FOUND = "No person named %1$s found.";
    public static final String MESSAGE_MULTIPLE_PERSONS_FOUND =
            "Multiple persons named %1$s found. Showing matching results; delete the desired entry by its index.";

    private final Name targetName;
    private final Index targetIndex;

    public DeleteCommand(Name targetName) {
        requireNonNull(targetName);
        this.targetName = targetName;
        this.targetIndex = null;
    }

    public DeleteCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.targetName = null;
    }

        @Override
        public CommandResult execute (Model model) throws CommandException {
            requireNonNull(model);
            if (targetName != null) {
                return deleteByName(model);
            }
            return deleteByIndex(model);
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
        return java.util.Objects.equals(targetName, otherDeleteCommand.targetName)
                && java.util.Objects.equals(targetIndex, otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .add("targetIndex", targetIndex)
                .toString();
    }

    private CommandResult deleteByName(Model model) throws CommandException {
        List<Person> matches = model.getAddressBook().getPersonList().stream()
                .filter(person -> person.getName().equals(targetName))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_PERSON_FOUND, targetName));
        }

        if (matches.size() > 1) {
            showMatchingPersons(model);
            throw new CommandException(String.format(MESSAGE_MULTIPLE_PERSONS_FOUND, targetName));
        }

        Person personToDelete = matches.get(0);
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    private CommandResult deleteByIndex(Model model) throws CommandException {
        requireAllNonNull(targetIndex);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    private void showMatchingPersons(Model model) {
        requireNonNull(model);
        requireNonNull(targetName);
        new FindCommand(new NameContainsKeywordsPredicate(keywordsFrom(targetName))).execute(model);
    }

    private static List<String> keywordsFrom(Name name) {
        requireNonNull(name);
        return Arrays.asList(name.fullName.split("\\s+"));
    }
}
