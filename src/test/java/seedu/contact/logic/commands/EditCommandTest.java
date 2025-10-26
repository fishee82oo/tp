package seedu.contact.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.contact.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.contact.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.contact.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.contact.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.contact.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.contact.testutil.TypicalPersons.getTypicalContactBook;

import org.junit.jupiter.api.Test;

import seedu.contact.commons.core.index.Index;
import seedu.contact.logic.Messages;
import seedu.contact.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.model.ContactBook;
import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.UserPrefs;
import seedu.contact.model.person.Person;
import seedu.contact.testutil.EditPersonDescriptorBuilder;
import seedu.contact.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalContactBook(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ContactBook(model.getContactBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ContactBook(model.getContactBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ContactBook(model.getContactBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ContactBook(model.getContactBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void executeByName_someFieldsSpecifiedUnfilteredList_success() {
        Person original = model.getFilteredPersonList().get(0);
        String targetName = original.getName().toString();

        Person editedPerson = new PersonBuilder(original).withPhone(VALID_PHONE_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(targetName, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new ContactBook(model.getContactBook()), new UserPrefs());
        expectedModel.setPerson(original, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void executeByName_multipleMatches_failure() {
        // Build a model with two persons of the same name
        ContactBook contactBook = new ContactBook();
        Person john1 = new PersonBuilder().withName("John Smith").withPhone("80000001")
                .withEmail("john1@example.com").withCompany("Google").build();
        Person john2 = new PersonBuilder().withName("John Smith").withPhone("80000002")
                .withEmail("john2@example.com").withCompany("Microsoft").build();
        Person other = new PersonBuilder().withName("Jane Doe").withPhone("80000003")
                .withEmail("jane@example.com").withCompany("Amazon").build();
        contactBook.addPerson(john1);
        contactBook.addPerson(john2);
        contactBook.addPerson(other);
        Model multiModel = new ModelManager(contactBook, new UserPrefs());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand("John   Smith", descriptor); // extra spaces to test normalization

        try {
            editCommand.execute(multiModel);
        } catch (CommandException ce) {
            assertEquals(EditCommand.MESSAGE_MULTIPLE_MATCHING_PERSONS, ce.getMessage());
            // filtered list should now contain only the two Johns
            assertEquals(2, multiModel.getFilteredPersonList().size());
            assertTrue(multiModel.getFilteredPersonList().contains(john1));
            assertTrue(multiModel.getFilteredPersonList().contains(john2));
            return;
        }
        throw new AssertionError("Expected CommandException was not thrown.");
    }

    @Test
    public void executeByName_nameNotFound_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand("Nonexistent Name", descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_PERSON_NAME_NOT_FOUND);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in contact book
        Person personInList = model.getContactBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of contact book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of contact book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getContactBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));

        // name-based vs index-based -> returns false
        assertFalse(standardCommand.equals(new EditCommand("Alice Pauline", DESC_AMY)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", nameReference=" + null
                + ", editPersonDescriptor=" + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    @Test
    public void toStringMethod_nameBased() {
        String nameRef = "Alice Pauline";
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(nameRef, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + null + ", nameReference=" + nameRef
                + ", editPersonDescriptor=" + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    @Test
    public void equals_nameBased() {
        String nameRef = "Alice Pauline";
        EditPersonDescriptor descriptor = new EditPersonDescriptor(DESC_AMY);

        // same values -> returns true
        assertTrue(
                new EditCommand(nameRef, descriptor)
                        .equals(new EditCommand(nameRef, new EditPersonDescriptor(DESC_AMY)))
        );

        // different name -> returns false
        assertFalse(
                new EditCommand(nameRef, descriptor)
                        .equals(new EditCommand(
                                "Alice   Pauline  ",
                                new EditPersonDescriptor(DESC_AMY)
                        ))
        );
    }

    @Test
    public void executeByName_normalizationSingleMatch_success() throws Exception {
        // Build a model with a single matching name (case-insensitive, extra spaces)
        ContactBook contactBook = new ContactBook();
        Person john = new PersonBuilder().withName("John Smith").withPhone("80000001")
                .withEmail("john@example.com").withCompany("Google").build();
        Person jane = new PersonBuilder().withName("Jane Doe").withPhone("80000002")
                .withEmail("jane@example.com").withCompany("Microsoft").build();
        contactBook.addPerson(john);
        contactBook.addPerson(jane);
        Model singleModel = new ModelManager(contactBook, new UserPrefs());

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand("  JOHN    SMITH  ", descriptor);

        Person editedJohn = new PersonBuilder(john).withPhone(VALID_PHONE_BOB).build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedJohn));

        Model expectedModel = new ModelManager(new ContactBook(singleModel.getContactBook()), new UserPrefs());
        expectedModel.setPerson(john, editedJohn);

        assertCommandSuccess(editCommand, singleModel, expectedMessage, expectedModel);
    }

}
