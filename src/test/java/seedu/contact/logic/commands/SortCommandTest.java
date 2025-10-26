package seedu.contact.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.contact.testutil.TypicalPersons.getTypicalContactBook;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.UserPrefs;
import seedu.contact.model.person.Person;

public class SortCommandTest {

    private static final String NAME_FIELD = "name";
    private static final String TAGS_FIELD = "tag";
    private static final String INVALID_FIELD = "invalid";


    private static final String ASCENDING_ORDER = "asc";
    private static final String DESCENDING_ORDER = "desc";
    private static final String ASCENDING_ORDER_FULL_FORMAT = "ascending";
    private static final String DESCENDING_ORDER_FULL_FORMAT = "descending";
    private static final String INVALID_ORDER = "invalid";

    private static final Comparator<Person> NAME_ASCENDING_COMPARATOR =
            Comparator.comparing(person -> person.getName().toString(), String.CASE_INSENSITIVE_ORDER);

    private static final Comparator<Person> NAME_DESCENDING_COMPARATOR = NAME_ASCENDING_COMPARATOR.reversed();

    private static final Comparator<Person> TAGS_ASCENDING_COMPARATOR =
            Comparator.comparing(person -> person.getTags().stream()
                                                 .map(tag -> tag.tagName.toLowerCase())
                                                 .sorted()
                                                 .findFirst()
                                                 .orElse(""));
    private static final Comparator<Person> TAGS_DESCENDING_COMPARATOR = TAGS_ASCENDING_COMPARATOR.reversed();

    private Model model = new ModelManager(getTypicalContactBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(model.getContactBook(), new UserPrefs());

    @Test
    public void execute_fullOrderFormatArg_success() {
        SortCommand command = new SortCommand(NAME_FIELD, ASCENDING_ORDER_FULL_FORMAT);
        String expectedMessage = String.format(SortCommand.MESSAGE_SUCCESS, NAME_FIELD, ASCENDING_ORDER_FULL_FORMAT);
        expectedModel.sortPersons(NAME_ASCENDING_COMPARATOR);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);

        // Repeat for descending order full format
        command = new SortCommand(NAME_FIELD, DESCENDING_ORDER_FULL_FORMAT);
        expectedMessage = String.format(SortCommand.MESSAGE_SUCCESS, NAME_FIELD, DESCENDING_ORDER_FULL_FORMAT);
        expectedModel.sortPersons(NAME_DESCENDING_COMPARATOR);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sortNamesAscending_success() {
        SortCommand command = new SortCommand(NAME_FIELD, ASCENDING_ORDER);
        String expectedMessage = String.format(SortCommand.MESSAGE_SUCCESS, NAME_FIELD, ASCENDING_ORDER_FULL_FORMAT);
        expectedModel.sortPersons(NAME_ASCENDING_COMPARATOR);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sortNamesDescending_success() {
        SortCommand command = new SortCommand(NAME_FIELD, DESCENDING_ORDER);
        String expectedMessage = String.format(SortCommand.MESSAGE_SUCCESS, NAME_FIELD, DESCENDING_ORDER_FULL_FORMAT);
        expectedModel.sortPersons(NAME_DESCENDING_COMPARATOR);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sortTagsAscending_success() {
        SortCommand command = new SortCommand(TAGS_FIELD, ASCENDING_ORDER);
        String expectedMessage = String.format(SortCommand.MESSAGE_SUCCESS, TAGS_FIELD, ASCENDING_ORDER_FULL_FORMAT);
        expectedModel.sortPersons(TAGS_ASCENDING_COMPARATOR);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sortTagsDescending_success() {
        SortCommand command = new SortCommand(TAGS_FIELD, DESCENDING_ORDER);
        String expectedMessage = String.format(SortCommand.MESSAGE_SUCCESS, TAGS_FIELD, DESCENDING_ORDER_FULL_FORMAT);
        expectedModel.sortPersons(TAGS_DESCENDING_COMPARATOR);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sortInvalidField_throwsCommandException() {
        SortCommand command = new SortCommand(INVALID_FIELD, ASCENDING_ORDER);
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void execute_sortInvalidOrder_throwsCommandException() {
        SortCommand command = new SortCommand(NAME_FIELD, INVALID_ORDER);
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void execute_sortInvalidArgs_throwsCommandException() {
        SortCommand command = new SortCommand(INVALID_FIELD, INVALID_ORDER);
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void equals() {
        final SortCommand standardCommand = new SortCommand("name", "asc");
        SortCommand commandWithSameValues = new SortCommand("name", "asc");

        assertTrue(standardCommand.equals(commandWithSameValues));
        assertTrue(standardCommand.equals(standardCommand));
        assertFalse(standardCommand.equals(null));
        assertFalse(standardCommand.equals(new ClearCommand()));
        assertFalse(standardCommand.equals(new SortCommand("name", "desc")));
        assertFalse(standardCommand.equals(new SortCommand("tag", "asc")));

    }
}
