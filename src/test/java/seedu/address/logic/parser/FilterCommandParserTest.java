package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FIELD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagsContainTagPredicate;

public class FilterCommandParserTest {

    private FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "    ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFilterCommand() {
        // no leading and trailing whitespaces
        FilterCommand expectedFilterCommand = new FilterCommand(
                new TagsContainTagPredicate(Arrays.asList(new Tag("friend"), new Tag("delta one sales"))));
        assertParseSuccess(parser, " " + PREFIX_TAG + "friend " + PREFIX_TAG + "delta one sales ",
                expectedFilterCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedResult = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);
        // input between filter and first prefix
        assertParseFailure(parser, "error " + PREFIX_TAG + " friend ", expectedResult);

        // PREFIX_TAG not present
        assertParseFailure(parser, "error", expectedResult);

        // PREFIX_TAG present but empty tag
        assertParseFailure(parser, " " + PREFIX_TAG, expectedResult);

        // Whitespace between prefix and tag value
        assertParseFailure(parser, " " + PREFIX_TAG + " friend", expectedResult);
        assertParseFailure(parser, " " + PREFIX_TAG + "  friend", expectedResult);

        // Non-alphanumeric tag content after normalization
        assertParseFailure(parser, " " + PREFIX_TAG + "/////", expectedResult);
        assertParseFailure(parser, " " + PREFIX_TAG + "sales/ops", expectedResult);

        // Empty tag in a stream of tags
        assertParseFailure(
                parser, " " + PREFIX_TAG + " friend " + PREFIX_TAG + " " + PREFIX_TAG + " colleague", expectedResult);

        // Extra invalid tags (argMultimap parses wrongly)
        String fieldPrefix = PREFIX_FIELD.getPrefix();
        String namePrefix = PREFIX_NAME.getPrefix();

        assertParseFailure(parser, " " + PREFIX_TAG + " friends " + fieldPrefix + " colleague ", expectedResult);
        assertParseFailure(
                parser, " " + PREFIX_TAG + " friends " + fieldPrefix + " colleague " + PREFIX_TAG + " colleague",
                expectedResult);
        assertParseFailure(parser, " " + PREFIX_TAG + " friends " + namePrefix + "buddy ", expectedResult);
        assertParseFailure(parser, " " + PREFIX_TAG + fieldPrefix + " colleague ", expectedResult);
    }

    @Test
    public void parse_tagsWithRepeatedSpaces_returnsFilterCommand() {
        TagsContainTagPredicate predicate = new TagsContainTagPredicate(Arrays.asList(
                new Tag("friend network"), new Tag("delta one sales")));
        FilterCommand expectedFilterCommand = new FilterCommand(predicate);

        assertParseSuccess(parser,
                " " + PREFIX_TAG + "friend  network " + PREFIX_TAG + "delta   one   sales",
                expectedFilterCommand);
    }

    @Test
    public void parse_duplicateTags_returnsFilterCommand() {
        // duplicate tags are discarded
        FilterCommand expectedFilterCommand = new FilterCommand(
                new TagsContainTagPredicate(Arrays.asList(new Tag("friends"))));

        assertParseSuccess(parser,
                " " + PREFIX_TAG + "FRIENDS " + PREFIX_TAG + "friends " + PREFIX_TAG + "fRiEnDs",
                expectedFilterCommand);
    }
}
