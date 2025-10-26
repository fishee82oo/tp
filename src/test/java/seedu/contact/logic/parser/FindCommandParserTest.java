package seedu.contact.logic.parser;

import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.contact.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.contact.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.contact.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.contact.logic.commands.FindCommand;
import seedu.contact.model.person.NameOrCompanyPredicate;

/**
 * Unit tests for {@code FindCommandParser}.
 */
public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_namePrefix_success() {
        // find n/Alice
        FindCommand expectedCommand =
                new FindCommand(new NameOrCompanyPredicate(Optional.of("Alice"), Optional.empty()));

        assertParseSuccess(parser, " n/Alice", expectedCommand);
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + " n/Alice", expectedCommand);

        // case-insensitive
        FindCommand expectedCommandCaseInsensitive =
                new FindCommand(new NameOrCompanyPredicate(Optional.of("aLiCe"), Optional.empty()));
        assertParseSuccess(parser, " n/aLiCe", expectedCommandCaseInsensitive);
    }

    @Test
    public void parse_companyPrefix_success() {
        // find c/Google
        FindCommand expectedCommand =
                new FindCommand(new NameOrCompanyPredicate(Optional.empty(), Optional.of("Google")));

        assertParseSuccess(parser, " c/Google", expectedCommand);
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + " c/Google", expectedCommand);
    }

    @Test
    public void parse_nameAndCompany_success() {
        // find n/Alice c/Google
        FindCommand expectedCommand =
                new FindCommand(new NameOrCompanyPredicate(Optional.of("Alice"), Optional.of("Google")));

        assertParseSuccess(parser, " n/Alice c/Google", expectedCommand);
        assertParseSuccess(parser, " c/Google n/Alice", expectedCommand); // order shouldn't matter
    }

    @Test
    public void parse_missingPrefix_failure() {
        // no prefix at all -> invalid
        assertParseFailure(parser, " Alice", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, " Google", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyValue_failure() {
        // n/ but no keyword
        assertParseFailure(parser, " n/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

        // c/ but no keyword
        assertParseFailure(parser, " c/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

        // both empty
        assertParseFailure(parser, " n/ c/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_bothPrefixEmpty_failure() {
        // no prefix and no keyword
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void toStringMethod_matchesExpectedFormat() {
        NameOrCompanyPredicate predicate =
                new NameOrCompanyPredicate(Optional.of("Alice"), Optional.empty());
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertParseSuccess(parser, " n/Alice", findCommand);
    }
}
