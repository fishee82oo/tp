package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.commons.core.index.Index;
import seedu.address.model.person.Name;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.Assert;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validNameUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(0);
        DeleteCommand deleteCommand = new DeleteCommand(personToDelete.getName());
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_validNameDifferentCase_success() {
        Model localModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Person personToDelete = localModel.getFilteredPersonList().get(0);
        Name lowerCasedInput = new Name(personToDelete.getName().fullName.toLowerCase());
        DeleteCommand deleteCommand = new DeleteCommand(lowerCasedInput);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(localModel.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, localModel, expectedMessage, expectedModel);
    }

    @Test
    public void execute_nameNotInAddressBook_throwsCommandException() {
        Name missingName = new Name("Non Existent Person");
        DeleteCommand deleteCommand = new DeleteCommand(missingName);

        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Person> expectedFilteredList = new ArrayList<>(model.getFilteredPersonList());

        Assert.assertThrows(CommandException.class,
                String.format(DeleteCommand.MESSAGE_NO_PERSON_FOUND, missingName),
                () -> deleteCommand.execute(model));

        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredList, model.getFilteredPersonList());
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(0);
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateName_throwsCommandException() {
        Person originalPerson = model.getFilteredPersonList().get(0);
        Person duplicatePerson = new PersonBuilder(originalPerson).withPhone("99999999").build();
        model.addPerson(duplicatePerson);

        DeleteCommand deleteCommand = new DeleteCommand(originalPerson.getName());

        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        NameContainsKeywordsPredicate expectedPredicate = new NameContainsKeywordsPredicate(
                Arrays.asList(originalPerson.getName().fullName.split("\\s+")));
        long expectedMatches = model.getAddressBook().getPersonList().stream()
                .filter(expectedPredicate)
                .count();

        Assert.assertThrows(CommandException.class,
                String.format(DeleteCommand.MESSAGE_MULTIPLE_PERSONS_FOUND, originalPerson.getName()),
                () -> deleteCommand.execute(model));
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedMatches, model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(originalPerson));
        assertTrue(model.getFilteredPersonList().contains(duplicatePerson));
    }

    @Test
    public void equals() {
        Name firstName = model.getFilteredPersonList().get(0).getName();
        Name secondName = model.getFilteredPersonList().get(1).getName();

            DeleteCommand deleteFirstCommand = new DeleteCommand(firstName);
            DeleteCommand deleteSecondCommand = new DeleteCommand(secondName);
            DeleteCommand deleteByIndexCommand = new DeleteCommand(Index.fromOneBased(1));

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(firstName);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

            // different target type -> returns false
            assertFalse(deleteFirstCommand.equals(deleteByIndexCommand));
    }

    @Test
    public void toStringMethod() {
        Name targetName = new Name("Alice Pauline");
        DeleteCommand deleteCommand = new DeleteCommand(targetName);
        String expected = DeleteCommand.class.getCanonicalName()
                    + "{targetName=" + targetName + ", targetIndex=null}";

        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
