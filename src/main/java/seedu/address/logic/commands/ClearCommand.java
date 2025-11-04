package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the contact book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String COMMAND_CONFIRMATION_WORD = "CONFIRM";
    public static final String MESSAGE_SUCCESS = "Contact book has been cleared!";
    public static final String MESSAGE_EMPTY_ADDRESS_BOOK = "Contact book is already empty.";
    public static final String MESSAGE_UNCONFIRMED = "This will delete all your contact(s) (action irreversible!)"
            + "\nIf you'd like to continue, confirm with input [clear " + COMMAND_CONFIRMATION_WORD + "]";
    public static final String MESSAGE_CLEAR_CANCELLED = "Improper Confirmation. Unchanged.";
    private boolean isConfirmed;

    public ClearCommand() {
        this.isConfirmed = false;
    }

    public ClearCommand(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        // Already Empty List
        if (model.getAddressBook().getPersonList().isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY_ADDRESS_BOOK);
        }

        // Requires Confirmation
        if (!this.isConfirmed) {
            return new CommandResult(MESSAGE_UNCONFIRMED);
        }

        if (model.getAddressBook().getPersonList().isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY_ADDRESS_BOOK);
        }

        model.updateFocusedPerson(-1);
        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
