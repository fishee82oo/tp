package seedu.contact.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.contact.commons.core.GuiSettings;
import seedu.contact.commons.core.LogsCenter;
import seedu.contact.commons.exceptions.EndOfCommandHistoryException;
import seedu.contact.logic.commands.Command;
import seedu.contact.logic.commands.CommandResult;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.logic.parser.ContactBookParser;
import seedu.contact.logic.parser.exceptions.ParseException;
import seedu.contact.model.Model;
import seedu.contact.model.ReadOnlyContactBook;
import seedu.contact.model.person.Person;
import seedu.contact.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final ContactBookParser contactBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        contactBookParser = new ContactBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = contactBookParser.parseCommand(commandText);
        commandResult = command.execute(model);

        try {
            storage.saveContactBook(model.getContactBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyContactBook getContactBook() {
        return model.getContactBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public Path getContactBookFilePath() {
        return model.getContactBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public void saveNewCommand(String newCommand) {
        model.saveNewCommand(newCommand);
    }

    @Override
    public String getPreviousCommand() throws EndOfCommandHistoryException {
        return model.getPreviousCommand();
    }

    @Override
    public String getNextCommand() {
        return model.getNextCommand();
    }
}
