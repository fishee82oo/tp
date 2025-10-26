package seedu.contact.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.contact.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.contact.testutil.Assert.assertThrows;
import static seedu.contact.testutil.TypicalPersons.ALICE;
import static seedu.contact.testutil.TypicalPersons.BENSON;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.contact.commons.core.GuiSettings;
import seedu.contact.commons.exceptions.EndOfCommandHistoryException;
import seedu.contact.model.person.NameContainsKeywordsPredicate;
import seedu.contact.model.person.Person;
import seedu.contact.model.person.exceptions.PersonNotFoundException;
import seedu.contact.testutil.ContactBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new ContactBook(), new ContactBook(modelManager.getContactBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setContactBookFilePath(Paths.get("contact/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setContactBookFilePath(Paths.get("new/contact/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setContactBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setContactBookFilePath(null));
    }

    @Test
    public void setContactBookFilePath_validPath_setsContactBookFilePath() {
        Path path = Paths.get("contact/book/file/path");
        modelManager.setContactBookFilePath(path);
        assertEquals(path, modelManager.getContactBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInContactBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInContactBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }
    @Test
    public void deletePerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.deletePerson(null));
    }

    @Test
    public void deletePerson_personNotInContactBook_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> modelManager.deletePerson(ALICE));
    }

    @Test
    public void deletePerson_personInContactBook_personRemovedFromContactBook() {
        modelManager.addPerson(ALICE);
        modelManager.deletePerson(ALICE);
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void deletePerson_personInContactBook_filteredListUpdated() {
        modelManager.addPerson(ALICE);
        modelManager.updateFilteredPersonList(person -> person.equals(ALICE));
        assertEquals(1, modelManager.getFilteredPersonList().size());

        modelManager.deletePerson(ALICE);

        assertEquals(0, modelManager.getFilteredPersonList().size());
    }

    @Test
    public void deletePerson_personDeletedTwice_throwsPersonNotFoundException() {
        modelManager.addPerson(ALICE);
        modelManager.deletePerson(ALICE);

        assertThrows(PersonNotFoundException.class, () -> modelManager.deletePerson(ALICE));
    }

    @Test
    public void deletePerson_contactBookNull_throwsAssertionError() {
        ModelManager manager = new ModelManager();
        setContactBook(manager, null);

        assertThrows(AssertionError.class, () -> manager.deletePerson(ALICE));
    }

    @Test
    public void deletePerson_personNotRemoved_throwsAssertionError() {
        ModelManager manager = new ModelManager();
        ContactBook stubbornContactBook = new ContactBookThatDoesNotRemove(ALICE);
        setContactBook(manager, stubbornContactBook);

        assertThrows(AssertionError.class, () -> manager.deletePerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        ContactBook contactBook = new ContactBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        ContactBook differentContactBook = new ContactBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(contactBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(contactBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different contactBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentContactBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(contactBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setContactBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(contactBook, differentUserPrefs)));
    }

    @Test
    public void saveAndRetrieveCommandsFromHistory() {
        // Save commands
        modelManager.saveNewCommand("test3");
        modelManager.saveNewCommand("test2");
        modelManager.saveNewCommand("test1");
        modelManager.saveNewCommand("test0");

        for (int i = 0; i < 5; i++) {
            try {
                // Get the first 4 and assert commands are accurate
                String command = modelManager.getPreviousCommand();
                assertEquals("test" + i, command);
            } catch (EndOfCommandHistoryException e) {
                // Check out-of-bounds return proper error message
                assertEquals("End of Command History reached", e.getMessage());
            }
        }

        // Check get Next Features
        for (int i = 3; i >= 0; i--) {
            // Reverse direction and get the 4 commands again using NextCommand
            String command = modelManager.getNextCommand();
            assertEquals("test" + i, command);
        }

        // Check twice getting beyond latest command (Expect return empty string)
        assertEquals("", modelManager.getNextCommand());
        assertEquals("", modelManager.getNextCommand());
    }

    private void setContactBook(ModelManager manager, ContactBook replacement) {
        try {
            Field contactBookField = ModelManager.class.getDeclaredField("contactBook");
            contactBookField.setAccessible(true);
            contactBookField.set(manager, replacement);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Unable to configure contact book for test", e);
        }
    }

    private static class ContactBookThatDoesNotRemove extends ContactBook {
        ContactBookThatDoesNotRemove(Person stubbornPerson) {
            addPerson(stubbornPerson);
        }

        @Override
        public void removePerson(Person key) {
            // Do nothing to simulate a failed removal
        }
    }
}
