package seedu.contact.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.contact.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.contact.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.contact.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.contact.testutil.TypicalPersons.getTypicalContactBook;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.contact.commons.core.index.Index;
import seedu.contact.logic.Messages;
import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.UserPrefs;
import seedu.contact.model.person.Name;
import seedu.contact.model.person.Person;
import seedu.contact.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalContactBook(), new UserPrefs());

    @Test
    public void execute_validNameUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(0);
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getName());
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getContactBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameNotInContactBook_throwsCommandException() {
        Name nameNotInContactBook = new Name("NONAME");
        DeleteCommand deleteCommand = new DeleteCommand(nameNotInContactBook);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_NAME);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(0);
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getContactBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_duplicateNames_showsListWithoutDeleting() {
        Person basePerson = model.getFilteredPersonList().get(0);
        Person duplicatePerson = new PersonBuilder(basePerson)
                .withPhone("00000001")
                .withEmail("duplicate@example.com")
                .build();
        model.addPerson(duplicatePerson);

        DeleteCommand deleteCommand = new DeleteCommand(basePerson.getName());
        String expectedMessage = String.format(DeleteCommand.MESSAGE_MULTIPLE_PERSONS_WITH_NAME, basePerson.getName())
                + System.lineSeparator()
                + String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 2);

        Model expectedModel = new ModelManager(model.getContactBook(), new UserPrefs());
        Predicate<Person> nameMatchesPredicate =
                person -> person.getName().fullName.equalsIgnoreCase(basePerson.getName().fullName);
        expectedModel.updateFilteredPersonList(nameMatchesPredicate);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void equals() {
        Name firstName = model.getFilteredPersonList().get(0).getName();
        Name secondName = model.getFilteredPersonList().get(1).getName();
        Index firstIndex = INDEX_FIRST_PERSON;
        Index secondIndex = INDEX_SECOND_PERSON;

        DeleteCommand deleteFirstByNameCommand = new DeleteCommand(firstName);
        DeleteCommand deleteSecondByNameCommand = new DeleteCommand(secondName);
        DeleteCommand deleteFirstByIndexCommand = new DeleteCommand(firstIndex);
        DeleteCommand deleteFirstByIndexCommandCopy = new DeleteCommand(firstIndex);

        // same object -> returns true
        assertTrue(deleteFirstByNameCommand.equals(deleteFirstByNameCommand));

        // same values -> returns true
        DeleteCommand deleteFirstByNameCommandCopy = new DeleteCommand(firstName);
        assertTrue(deleteFirstByNameCommand.equals(deleteFirstByNameCommandCopy));
        assertTrue(deleteFirstByIndexCommand.equals(deleteFirstByIndexCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstByNameCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstByNameCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstByNameCommand.equals(deleteSecondByNameCommand));
        assertFalse(deleteFirstByNameCommand.equals(deleteFirstByIndexCommand));
        assertFalse(deleteFirstByIndexCommand.equals(new DeleteCommand(secondIndex)));
    }

    @Test
    public void toStringMethod() {
        Name targetName = new Name("Alice Pauline");
        DeleteCommand deleteCommand = new DeleteCommand(targetName);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetName=" + targetName + "}";

        assertEquals(expected, deleteCommand.toString());
    }

    @Test
    public void toStringMethod_indexTarget() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";

        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show none.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
