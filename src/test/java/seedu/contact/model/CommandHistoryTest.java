package seedu.contact.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.contact.commons.exceptions.EndOfCommandHistoryException;

public class CommandHistoryTest {
    @Test
    public void saveNewCommand_withValidCommandString_savesCommand() {
        // getLastCommand(n) is also simultaneously tested due to usage

        CommandHistory commandHistory = new CommandHistory();
        // saves command testN in reverse order as getLastCommand indexes backwards
        commandHistory.saveNewCommand("test2");
        commandHistory.saveNewCommand("test1");
        commandHistory.saveNewCommand("test0"); // Latest Command

        assertEquals("test1", commandHistory.getLastCommand(1));
        assertEquals("test0", commandHistory.getLastCommand(0));
        assertEquals("test2", commandHistory.getLastCommand(2));
        assertEquals("test0", commandHistory.getLastCommand());
    }


    @Test
    public void getLastCommand_emptyOrOutOfBounds_returnsEmptyString() {
        CommandHistory commandHistory = new CommandHistory();

        String emptyHistoryGetResult1 = commandHistory.getLastCommand();
        String emptyHistoryGetResult2 = commandHistory.getLastCommand(0);
        assertEquals("", emptyHistoryGetResult1);
        assertEquals("", emptyHistoryGetResult2);


        // Add temporary commands for out of bounds test
        commandHistory.saveNewCommand("test1");
        commandHistory.saveNewCommand("test0");

        String outOfBoundsHistoryGetResult = commandHistory.getLastCommand(2);
        assertEquals("", outOfBoundsHistoryGetResult);
    }

    @Test
    public void getPreviousAndNextCommands_validHistory_returnsRespectiveCommands() {
        CommandHistory commandHistory = new CommandHistory();

        // saves command testN in reverse order as getLastCommand indexes backwards
        commandHistory.saveNewCommand("test2");
        commandHistory.saveNewCommand("test1");
        commandHistory.saveNewCommand("test0"); // Latest Command

        // Check if previous accurately returns in order
        for (int i = 0; i <= 4; i++) {
            try {
                // For first 3 commands expect respective strings
                String result = commandHistory.getPreviousCommand();
                assertEquals("test" + i, result);

            } catch (EndOfCommandHistoryException e) {
                // For 4th and 5th commands (Out of Bounds) expects EndOfCommandHistoryException
                assertEquals("End of Command History reached", e.getMessage());
            }
        }

        // After reaching end of history, check getNextCommands method
        for (int i = 2; i >= 0; i--) {
            // Returns in backwards order
            String result = commandHistory.getNextCommand();
            assertEquals("test" + i, result);
        }
        // Finally check if calling next after reaching end (expects empty strings)
        for (int i = 0; i < 2; i++) {
            String result = commandHistory.getNextCommand();
            assertEquals("", result);
        }

    }
}
