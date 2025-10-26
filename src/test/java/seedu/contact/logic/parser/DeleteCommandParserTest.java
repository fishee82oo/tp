package seedu.contact.logic.parser;

import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.contact.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.contact.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.contact.commons.core.index.Index;
import seedu.contact.logic.commands.DeleteCommand;
import seedu.contact.model.person.Name;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validName_returnsDeleteCommand() {
        Name name = new Name("Alice Pauline");
        assertParseSuccess(parser, "Alice Pauline", new DeleteCommand(name));
    }

    @Test
    public void parse_validIndex_returnsDeleteCommand() {
        Index index = Index.fromOneBased(1);
        assertParseSuccess(parser, "1", new DeleteCommand(index));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "!@#", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
