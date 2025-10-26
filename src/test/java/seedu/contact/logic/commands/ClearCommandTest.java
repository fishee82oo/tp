package seedu.contact.logic.commands;

import static seedu.contact.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.contact.testutil.TypicalPersons.getTypicalContactBook;

import org.junit.jupiter.api.Test;

import seedu.contact.model.ContactBook;
import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyContactBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyContactBook_success() {
        Model model = new ModelManager(getTypicalContactBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalContactBook(), new UserPrefs());
        expectedModel.setContactBook(new ContactBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
