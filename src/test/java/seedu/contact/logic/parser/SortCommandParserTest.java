package seedu.contact.logic.parser;

import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_FIELD;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.contact.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.contact.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.contact.logic.Messages;
import seedu.contact.logic.commands.SortCommand;

public class SortCommandParserTest {

    private SortCommandParser parser = new SortCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsSortCommand() {
        // no leading and trailing whitespaces
        SortCommand expectedSortCommand = new SortCommand("names", "asc");
        assertParseSuccess(parser, " " + PREFIX_FIELD + " names " + PREFIX_ORDER + " asc ", expectedSortCommand);

        // leading and traling whitespaces
        assertParseSuccess(parser, "\n  " + PREFIX_FIELD + " \n names  \n \t  " + PREFIX_ORDER + "\n asc \n",
                expectedSortCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedResult = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE);
        // input between sort and first prefix
        assertParseFailure(parser, "error " + PREFIX_FIELD + " names " + PREFIX_ORDER + " asc ", expectedResult);

        // PREFIX_FIELD not present
        assertParseFailure(parser, " " + PREFIX_ORDER + " asc ", expectedResult);

        // PREFIX_ORDER not present
        assertParseFailure(parser, " " + PREFIX_FIELD + " names ", expectedResult);

        // BOTH PREFIX_ORDER and PREFIX_FIELD not present
        assertParseFailure(parser, "", expectedResult);
        assertParseFailure(parser, " names asc", expectedResult);

        // Duplicate PREFIX
        assertParseFailure(parser, " " + PREFIX_FIELD + " names " + PREFIX_FIELD + " tags " + PREFIX_ORDER + " asc ",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FIELD));

        assertParseFailure(parser, " " + PREFIX_FIELD + " names " + PREFIX_ORDER + " asc " + PREFIX_ORDER + " desc ",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ORDER));

        assertParseFailure(parser, " " + PREFIX_FIELD + " names " + PREFIX_FIELD + " tags " + PREFIX_ORDER + " asc "
                + PREFIX_ORDER + " desc ", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_FIELD, PREFIX_ORDER));

        assertParseFailure(parser, " " + PREFIX_FIELD + " names " + PREFIX_FIELD + " tags ", expectedResult);
        assertParseFailure(parser, " " + PREFIX_ORDER + " asc " + PREFIX_ORDER + " desc ", expectedResult);
    }
}
