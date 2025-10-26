package seedu.contact.model;

import java.util.ArrayList;

import seedu.contact.commons.exceptions.EndOfCommandHistoryException;


/**
 * Represents in-memory Record of Command Usage in order
 */
public class CommandHistory implements ReadOnlyCommandHistory {

    /**
     * Array of stored command strings
     */
    private ArrayList<String> commandHistoryList = new ArrayList<>();

    /**
     * Current Command Index (-1 if not in history) - Max is Size of List
     * Previous Command: +1
     * Next Command = -1
     */
    private int currCommandIndex = -1;

    /**
     * Adds a new command string to commandHistoryList for tracking
     * Commands are added to the end of the array
     */
    public void saveNewCommand(String newCommand) {
        this.commandHistoryList.add(newCommand);
        this.currCommandIndex = -1; // Reset index
        System.out.println("SAVED NEW COMMAND: " + newCommand);
    }

    /**
     * Gets the last command from history
     * @return Last Command String
     */
    public String getLastCommand() {
        int lastIndex = this.commandHistoryList.size() - 1;

        // Out of Bounds ? Return empty
        if (lastIndex < 0) {
            return "";
        }

        return this.commandHistoryList.get(lastIndex);
    }

    /**
     * (Overloaded) Gets the targeted nth command from history
     * @param n (Zero Indexed) Targets Nth Command in history starting from most recent
     * @return target Command String
     */
    public String getLastCommand(int n) {
        assert n >= 0 : "Index n must be non-negative";
        assert this.commandHistoryList != null : "Command History List should not be null";

        int targetIndex = this.commandHistoryList.size() - 1 - n;

        // Out of Bounds ? Return Empty String
        if (targetIndex < 0) {
            return "";
        }

        // If index valid, return command string
        return this.commandHistoryList.get(targetIndex);
    }

    /**
     * Gets the previous command relative to user's position in history
     * @return Previous Command String
     */
    public String getPreviousCommand() throws EndOfCommandHistoryException {
        // Limit to point within available range
        currCommandIndex = Math.min(currCommandIndex + 1, commandHistoryList.size());

        if (currCommandIndex >= commandHistoryList.size()) {
            throw new EndOfCommandHistoryException("End of Command History reached");
        }

        return getLastCommand(currCommandIndex);
    }

    /**
     * Get the Next Command relative to user's position in history
     * @return Next Command String
     */
    public String getNextCommand() {
        // Limit to minimum of -1 currentIndex to represent history not in focus
        currCommandIndex = Math.max(currCommandIndex - 1, -1);

        // If there is no next command
        if (currCommandIndex < 0) {
            return "";
        } else {
            return getLastCommand(currCommandIndex);
        }
    }
}
