package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameOrCompanyPredicate;

/**
 * Integration tests (interaction with the Model) for {@code FindCommand}.
 * Covers name-based, company-based, combined, and case-insensitive searches.
 */
public class FindCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameOrCompanyPredicate firstPredicate =
                new NameOrCompanyPredicate(Optional.of("Alice"), Optional.empty());
        NameOrCompanyPredicate secondPredicate =
                new NameOrCompanyPredicate(Optional.empty(), Optional.of("Google"));

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> false
        assertFalse(findFirstCommand.equals(1));

        // null -> false
        assertFalse(findFirstCommand.equals(null));

        // different predicates -> false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_noMatch_returnsEmptyList() {
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.of("Nonexistent"), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_partialNameMatch_success() {
        // "ali" should match "Alice"
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.of("ali"), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().contains(ALICE));
    }

    @Test
    public void execute_caseInsensitiveName_success() {
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.of("aLiCe"), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().contains(ALICE));
    }

    @Test
    public void execute_partialCompanyMatch_success() {
        // Adjust this keyword according to BENSON’s company in TypicalPersons
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.empty(), Optional.of("Micro"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW,
                expectedModel.getFilteredPersonList().size());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().contains(BENSON));
    }

    @Test
    public void execute_nameAndCompanyBothMatch_success() {
        // Only match if both name and company fit
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.of("Carl"), Optional.of("Amazon"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW,
                expectedModel.getFilteredPersonList().size());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().contains(CARL));
    }

    @Test
    public void execute_nameAndCompanyMismatch_returnsEmptyList() {
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.of("Alice"), Optional.of("Amazon"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void toStringMethod() {
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.of("Alice"), Optional.empty());
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }
}
