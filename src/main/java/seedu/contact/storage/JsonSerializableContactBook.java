package seedu.contact.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.contact.commons.exceptions.IllegalValueException;
import seedu.contact.model.ContactBook;
import seedu.contact.model.ReadOnlyContactBook;
import seedu.contact.model.person.Person;

/**
 * An Immutable ContactBook that is serializable to JSON format.
 */
@JsonRootName(value = "contactbook")
class JsonSerializableContactBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableContactBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableContactBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyContactBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableContactBook}.
     */
    public JsonSerializableContactBook(ReadOnlyContactBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
    }

    /**
     * Converts this contact book into the model's {@code ContactBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ContactBook toModelType() throws IllegalValueException {
        ContactBook contactBook = new ContactBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (contactBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            contactBook.addPerson(person);
        }
        return contactBook;
    }

}
