package seedu.contact.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.contact.model.ContactBook;
import seedu.contact.model.ReadOnlyContactBook;
import seedu.contact.model.person.Company;
import seedu.contact.model.person.Email;
import seedu.contact.model.person.Name;
import seedu.contact.model.person.Person;
import seedu.contact.model.person.Phone;
import seedu.contact.model.tag.Tag;

/**
 * Contains utility methods for populating {@code ContactBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Company("Google"),
                getTagSet("sales")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Company("Amazon"),
                getTagSet("colleagues", "friends", "procurement")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("unknown@example.com"),
                new Company("N/A"), getTagSet()),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Company("NUS Computing"),
                getTagSet("professor")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Company("AMD"),
                getTagSet("classmates", "sales")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Company("TikTok"),
                getTagSet("procurement"))
        };
    }

    public static ReadOnlyContactBook getSampleContactBook() {
        ContactBook sampleAb = new ContactBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
