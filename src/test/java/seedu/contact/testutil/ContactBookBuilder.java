package seedu.contact.testutil;

import seedu.contact.model.ContactBook;
import seedu.contact.model.person.Person;

/**
 * A utility class to help with building Addressbook objects.
 * Example usage: <br>
 *     {@code ContactBook ab = new ContactBookBuilder().withPerson("John", "Doe").build();}
 */
public class ContactBookBuilder {

    private ContactBook contactBook;

    public ContactBookBuilder() {
        contactBook = new ContactBook();
    }

    public ContactBookBuilder(ContactBook contactBook) {
        this.contactBook = contactBook;
    }

    /**
     * Adds a new {@code Person} to the {@code ContactBook} that we are building.
     */
    public ContactBookBuilder withPerson(Person person) {
        contactBook.addPerson(person);
        return this;
    }

    public ContactBook build() {
        return contactBook;
    }
}
