package seedu.contact.logic.commands;

import static seedu.contact.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.contact.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.contact.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.contact.testutil.TypicalPersons.getTypicalContactBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalContactBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getContactBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
