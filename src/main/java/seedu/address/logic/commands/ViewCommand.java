package seedu.address.logic.commands;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_INDEX_NOT_FOUND = "Index provided invalid";
    public static final String MESSAGE_SHOWING = "Showing Details for Index ";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Toggles Detail Pane \n"
            + "Parameters: [CONTACT_INDEX]\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String TOGGLE_ACKNOWLEDGEMENT = "Toggling Detail Pane";

    private Integer targetIndex;

    public ViewCommand() {
        this.targetIndex = null;
    }

    public ViewCommand(Integer targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (targetIndex == null) {
            // Only Toggle when no target index provided
            return new CommandResult(TOGGLE_ACKNOWLEDGEMENT, false, false, true, -1);
        } else {
            // Pass index to update focused Person using their index
            try {
                if (targetIndex < 1) {
                    throw new IndexOutOfBoundsException();
                }
                model.updateFocusedPerson(targetIndex - 1);
            } catch (IndexOutOfBoundsException e) {
                throw new CommandException(MESSAGE_INDEX_NOT_FOUND);
            }
            return new CommandResult(MESSAGE_SHOWING + this.targetIndex, false, false, false, targetIndex);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ViewCommand)) {
            return false;
        }

        ViewCommand otherViewCommand = (ViewCommand) other;

        if (targetIndex == null) {
            return otherViewCommand.targetIndex == null;
        } else {
            return targetIndex.equals(otherViewCommand.targetIndex);
        }
    }
}
