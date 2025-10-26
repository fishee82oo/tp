package seedu.contact.model;

import javafx.collections.ObservableList;
import seedu.contact.model.person.Person;

/**
 * Unmodifiable view of an contact book
 */
public interface ReadOnlyContactBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

}
