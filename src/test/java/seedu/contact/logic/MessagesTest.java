package seedu.contact.logic;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.contact.model.person.Company;
import seedu.contact.model.person.Email;
import seedu.contact.model.person.Name;
import seedu.contact.model.person.Person;
import seedu.contact.model.person.Phone;

public class MessagesTest {

    @Test
    public void format_includesAllFields_whenValidPerson() {
        Person person = new Person(
                new Name("Linghui"),
                new Phone("80396190"),
                new Email("linghui@nus.edu.sg"), // valid
                new Company("NUS Computing"), // valid
                new HashSet<>()
        );

        String formatted = Messages.format(person);

        assertTrue(formatted.contains("Name: Linghui"));
        assertTrue(formatted.contains("Email: linghui@nus.edu.sg"));
        assertTrue(formatted.contains("Company: NUS Computing"));
    }
}
