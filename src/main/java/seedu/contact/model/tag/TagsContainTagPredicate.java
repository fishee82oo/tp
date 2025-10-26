package seedu.contact.model.tag;

import java.util.List;
import java.util.function.Predicate;

import seedu.contact.model.person.Person;

/**
 * Tests that a {@code Person}'s {@code Tags} matches any of the tags given.
 */
public class TagsContainTagPredicate implements Predicate<Person> {
    private final List<Tag> tags;

    public TagsContainTagPredicate(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean test(Person person) {
        return tags.stream()
                .anyMatch(tag -> person.getTags().contains(tag));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagsContainTagPredicate)) {
            return false;
        }

        TagsContainTagPredicate otherTagsContainTagPredicate = (TagsContainTagPredicate) other;
        return tags.equals(otherTagsContainTagPredicate.tags);
    }
}
