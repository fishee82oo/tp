package seedu.contact.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.contact.testutil.PersonBuilder;

public class TagsContainsTagPredicateTest {

    @Test
    public void equals() {
        List<Tag> firstPredicateTagList = new ArrayList<>();
        firstPredicateTagList.add(new Tag("first"));

        List<Tag> secondPredicateTagList = new ArrayList<>();
        secondPredicateTagList.add(new Tag("first"));
        secondPredicateTagList.add(new Tag("second"));

        TagsContainTagPredicate firstPredicate = new TagsContainTagPredicate(firstPredicateTagList);
        TagsContainTagPredicate secondPredicate = new TagsContainTagPredicate(secondPredicateTagList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        TagsContainTagPredicate firstPredicateCopy = new TagsContainTagPredicate(firstPredicateTagList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different tags list -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // case-insensitive but same values -> returns true
        List<Tag> thirdPredicateTagList = new ArrayList<>();
        thirdPredicateTagList.add(new Tag("FiRsT"));
        thirdPredicateTagList.add(new Tag("SecOND"));

        TagsContainTagPredicate thirdPredicate = new TagsContainTagPredicate(thirdPredicateTagList);
        assertTrue(secondPredicate.equals(thirdPredicate));
    }

    @Test
    public void test_tagsContainsTag_returnsTrue() {
        List<Tag> predicateTagList = new ArrayList<>();
        predicateTagList.add(new Tag("friends"));
        predicateTagList.add(new Tag("colleAGUE"));

        TagsContainTagPredicate predicate = new TagsContainTagPredicate(predicateTagList);

        // One tag
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("FRIENDS").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("colleague").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("cOlLeAgUe").build()));

        // Multiple matching tags
        assertTrue(predicate.test(new PersonBuilder().withTags("fRIENds", "COLLeague").build()));

        // One matching, one not matching tag
        assertTrue(predicate.test(new PersonBuilder().withTags("friends", "notAColleague").build()));
    }

    @Test
    public void test_tagsDoesNotContainTag_returnsFalse() {
        // Zero tags
        TagsContainTagPredicate predicate = new TagsContainTagPredicate(new ArrayList<>());
        assertFalse(predicate.test(new PersonBuilder().withTags("friend").build()));

        List<Tag> predicateTagList = new ArrayList<>();
        predicateTagList.add(new Tag("friends"));
        assertFalse(predicate.test(new PersonBuilder().withTags("colleague").build()));
    }
}
