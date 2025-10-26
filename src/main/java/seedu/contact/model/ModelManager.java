package seedu.contact.model;

import static java.util.Objects.requireNonNull;
import static seedu.contact.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.contact.commons.core.GuiSettings;
import seedu.contact.commons.core.LogsCenter;
import seedu.contact.commons.exceptions.EndOfCommandHistoryException;
import seedu.contact.model.person.Person;
import seedu.contact.model.person.exceptions.PersonNotFoundException;

/**
 * Represents the in-memory model of the contact book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ContactBook contactBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final CommandHistory commandHistory;

    /**
     * Initializes a ModelManager with the given contactBook and userPrefs.
     */
    public ModelManager(ReadOnlyContactBook contactBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(contactBook, userPrefs);

        logger.fine("Initializing with contact book: " + contactBook + " and user prefs " + userPrefs);

        this.contactBook = new ContactBook(contactBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.commandHistory = new CommandHistory();
        filteredPersons = new FilteredList<>(this.contactBook.getPersonList());
    }

    public ModelManager() {
        this(new ContactBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getContactBookFilePath() {
        return userPrefs.getContactBookFilePath();
    }

    @Override
    public void setContactBookFilePath(Path contactBookFilePath) {
        requireNonNull(contactBookFilePath);
        userPrefs.setContactBookFilePath(contactBookFilePath);
    }

    //=========== ContactBook ================================================================================

    @Override
    public void setContactBook(ReadOnlyContactBook contactBook) {
        this.contactBook.resetData(contactBook);
    }

    @Override
    public ReadOnlyContactBook getContactBook() {
        return contactBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return contactBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        requireNonNull(target);

        if (contactBook == null) {
            throw new AssertionError("Contact book should not be null when deleting a person");
        }

        if (!contactBook.hasPerson(target)) {
            logger.warning(() -> "Attempted to delete a non-existent person: " + target);
            throw new PersonNotFoundException();
        }

        logger.fine(() -> "Deleting person: " + target);

        contactBook.removePerson(target);

        if (contactBook.hasPerson(target)) {
            throw new AssertionError("Person should be removed from the contact book after deletion");
        }

        logger.fine(() -> "Successfully deleted person: " + target);
    }

    @Override
    public void addPerson(Person person) {
        contactBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        contactBook.setPerson(target, editedPerson);
    }

    @Override
    public void sortPersons(Comparator<Person> comparator) {
        requireNonNull(comparator);
        contactBook.sort(comparator);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedContactBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return contactBook.equals(otherModelManager.contactBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

    //=========== Command History Accessors =============================================================
    @Override
    public void saveNewCommand(String newCommand) {
        commandHistory.saveNewCommand(newCommand);
    }

    @Override
    public String getPreviousCommand() throws EndOfCommandHistoryException {
        return commandHistory.getPreviousCommand();
    }

    @Override
    public String getNextCommand() {
        return commandHistory.getNextCommand();
    }

}
