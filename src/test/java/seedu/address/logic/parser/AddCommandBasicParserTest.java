package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommandBasic;
import seedu.address.model.person.Company;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Unit tests for {@code AddCommandBasicParser}.
 * Verifies that the parser correctly interprets valid inputs
 * and rejects missing mandatory fields.
 */
public class AddCommandBasicParserTest {

    private final AddCommandBasicParser parser = new AddCommandBasicParser();

    @Test
    public void parse_allFieldsPresent_success() throws Exception {
        String userInput = " n/John Doe p/88880000";
        Person expectedPerson = new Person(
                new Name("John Doe"),
                new Phone("88880000"),
                new Email("unknown@example.com"),
                new Company("N/A"),
                new HashSet<Tag>());

        AddCommandBasic expectedCommand = new AddCommandBasic(expectedPerson);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingPhone_failure() {
        String userInput = " n/John";
        try {
            parser.parse(userInput);
            assert false : "Expected ParseException to be thrown.";
        } catch (Exception e) {
            assert e.getMessage().contains("Invalid command format");
        }
    }

    @Test
    public void parse_missingName_failure() {
        String userInput = " p/88880000"; // Missing n/
        try {
            parser.parse(userInput);
            assert false : "Expected ParseException to be thrown.";
        } catch (Exception e) {
            assert e.getMessage().contains("Invalid command format");
        }
    }

    @Test
    public void parse_whitespaceAfterPrefix_failure() {
        String userInput = " n/ John p/88880000";
        try {
            parser.parse(userInput);
            assert false : "Expected ParseException to be thrown.";
        } catch (Exception e) {
            assert e.getMessage().contains("Invalid command format");
        }
    }

}
