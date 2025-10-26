package seedu.contact.logic;

import java.nio.file.Path;

import javafx.collections.ObservableList;
import seedu.contact.commons.core.GuiSettings;
import seedu.contact.commons.exceptions.EndOfCommandHistoryException;
import seedu.contact.logic.commands.CommandResult;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.logic.parser.exceptions.ParseException;
import seedu.contact.model.ReadOnlyContactBook;
import seedu.contact.model.person.Person;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Returns the ContactBook.
     *
     * @see seedu.contact.model.Model#getContactBook()
     */
    ReadOnlyContactBook getContactBook();

    /** Returns an unmodifiable view of the filtered list of persons */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Returns the user prefs' contact book file path.
     */
    Path getContactBookFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Saves command into history
     */
    void saveNewCommand(String newCommand);

    /**
     * Returns the String of the previous command (relative to previous command index user is on)
     */
    String getPreviousCommand() throws EndOfCommandHistoryException;

    /**
     * Returns the String of the next command (relative to previous command index user is on)
     */
    String getNextCommand();

}
