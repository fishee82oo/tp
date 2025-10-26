package seedu.contact.ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.contact.logic.commands.CommandResult;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private final CommandHistoryGetter historyGetter;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor, CommandHistoryGetter historyGetter) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.historyGetter = historyGetter;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();

        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles any Key Press while CommandBox in Focus
     * Handles specifically the Up and Down arrow Key Press
     */
    @FXML
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            String prevCommand = historyGetter.navigateCommandHistory(-1);
            commandTextField.setText(prevCommand);
            Platform.runLater(() -> commandTextField.end()); // Set cursor to end of line
        } else if (event.getCode() == KeyCode.DOWN) {
            String nextCommand = historyGetter.navigateCommandHistory(1);
            commandTextField.setText(nextCommand);
            Platform.runLater(() -> commandTextField.end()); // Set cursor to end of line
        }


    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.contact.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Represents a function that gets Commands from History
     * Navigates command history
     * (-1): Previous
     * (1): Next
     */
    @FunctionalInterface
    public interface CommandHistoryGetter {
        String navigateCommandHistory(int historyDirection);
    }

}
