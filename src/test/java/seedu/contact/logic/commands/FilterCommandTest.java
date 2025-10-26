package seedu.contact.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.contact.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.contact.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.UserPrefs;
import seedu.contact.model.tag.Tag;
import seedu.contact.model.tag.TagsContainTagPredicate;

public class FilterCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        TagsContainTagPredicate firstPredicate =
                new TagsContainTagPredicate(Arrays.asList(new Tag("friend")));
        TagsContainTagPredicate secondPredicate =
                new TagsContainTagPredicate(Arrays.asList(new Tag("colleague")));

        FilterCommand filterFirstCommand = new FilterCommand(firstPredicate);
        FilterCommand filterSecondCommand = new FilterCommand(secondPredicate);

        // same object -> returns true
        assertTrue(filterFirstCommand.equals(filterFirstCommand));

        // same values -> returns true
        FilterCommand filterFirstCommandCopy = new FilterCommand(firstPredicate);
        assertTrue(filterFirstCommand.equals(filterFirstCommandCopy));

        // different types -> returns false
        assertFalse(filterFirstCommand.equals(1));

        // null -> returns false
        assertFalse(filterFirstCommand.equals(null));

        // different list -> returns false
        assertFalse(filterFirstCommand.equals(filterSecondCommand));
    }

    @Test
    public void execute_notPresentTag_noPersonDisplayed() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        TagsContainTagPredicate predicate =
                new TagsContainTagPredicate(Arrays.asList(new Tag("a")));
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_onePresentTag_somePersonsDisplayed() {
        TagsContainTagPredicate predicate =
                new TagsContainTagPredicate(Arrays.asList(new Tag("oWEsmoney")));

        // Expected number of people are found manually in TypicalPersons.
        // Please update this value accordingly if TypicalPersons is updated.
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_allPresentTags_morePersonsDisplayed() {
        TagsContainTagPredicate predicate =
                new TagsContainTagPredicate(Arrays.asList(new Tag("owesMoney"), new Tag("FRIENDS")));

        // Expected number of people are found manually in TypicalPersons.
        // Please update this value accordingly if TypicalPersons is updated.
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        FilterCommand command = new FilterCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }
}
