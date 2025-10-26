package seedu.contact.model.person;

import static seedu.contact.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.contact.commons.util.ToStringBuilder;
import seedu.contact.model.tag.Tag;

/**
 * Represents a Person in the contact book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Company company;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Company company, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, company, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Company getCompany() {
        return company;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons are considered the same based on name (case-insensitive, normalized spacing)
     * and phone number. Used for duplicate detection.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }
        if (otherPerson == null) {
            return false;
        }

        String thisName = normalizeName(this.name.fullName);
        String otherName = normalizeName(otherPerson.name.fullName);

        return thisName.equalsIgnoreCase(otherName)
                && this.phone.equals(otherPerson.phone);
    }

    /**
     * Normalize a name string by trimming and collapsing multiple spaces into one.
     */
    private static String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && company.equals(otherPerson.company)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, company, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("company", company)
                .add("tags", tags)
                .toString();
    }

}
