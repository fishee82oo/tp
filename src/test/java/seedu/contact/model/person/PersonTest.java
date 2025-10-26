package seedu.contact.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_COMPANY_BOB;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.contact.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.contact.testutil.Assert.assertThrows;
import static seedu.contact.testutil.TypicalPersons.ALICE;
import static seedu.contact.testutil.TypicalPersons.BOB;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.contact.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withCompany(VALID_COMPANY_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // different phone, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns True
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different company -> returns false
        editedAlice = new PersonBuilder(ALICE).withCompany(VALID_COMPANY_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", company=" + ALICE.getCompany() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void isSamePerson_caseInsensitiveAndWhitespaceIgnored_returnsTrue() {
        Person p1 = new Person(new Name("John   Doe"), new Phone("91234567"), new Email("john@example.com"),
                new Company("Kent Ridge"), new HashSet<>());

        Person p2 = new Person(new Name("  john doe "), new Phone("91234567"), new Email("john.d@example.com"),
                new Company("Kent Ridge"), new HashSet<>());

        assertTrue(p1.isSamePerson(p2));
    }

    @Test
    public void isSamePerson_differentPhone_returnsFalse() {
        Person p1 = new Person(new Name("John Doe"), new Phone("91234567"), new Email("john@example.com"),
                new Company("Kent Ridge"), new HashSet<>());

        Person p2 = new Person(new Name("John Doe"), new Phone("98765432"), new Email("john@example.com"),
                new Company("Kent Ridge"), new HashSet<>());

        assertFalse(p1.isSamePerson(p2));
    }

}
