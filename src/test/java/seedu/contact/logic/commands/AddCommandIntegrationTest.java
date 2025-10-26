package seedu.contact.logic.commands;

import static seedu.contact.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.contact.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.contact.testutil.TypicalPersons.getTypicalContactBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.contact.logic.Messages;
import seedu.contact.model.Model;
import seedu.contact.model.ModelManager;
import seedu.contact.model.UserPrefs;
import seedu.contact.model.person.Person;
import seedu.contact.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalContactBook(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getContactBook(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getContactBook().getPersonList().get(0);
        assertCommandFailure(new AddCommand(personInList), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

}
