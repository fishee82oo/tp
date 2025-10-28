package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FIELD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SortCommand object
 */
public class SortCommandParser implements Parser<SortCommand> {

    private static final Logger logger = LogsCenter.getLogger(SortCommandParser.class);

    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns a SortCommand object for execution.
     *
     * @throws ParseException if the user does not conform to the expected format
     */
    public SortCommand parse(String args) throws ParseException {
        requireNonNull(args);
        logger.fine("Parsing sort command with arguments: " + args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_FIELD, PREFIX_ORDER);
        if (ParserUtil.hasWhitespaceAfterAnyPrefix(args, PREFIX_FIELD, PREFIX_ORDER)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
        checkValidTokens(argMultimap);

        String field = argMultimap.getValue(PREFIX_FIELD).orElse("");
        String order = argMultimap.getValue(PREFIX_ORDER).orElse("");

        logger.info("Successfully parsed sort command - field: " + field + ", order: " + order);
        return new SortCommand(field, order);
    }

    /**
     * Checks if the prefixes are valid from tokenizing.
     *
     * Checks include:
     * <ul>
     *  <li>Check if Both PREFIX_FIELD and PREFIX_ORDER tokens exist</li>
     *  <li>Ensure that there is no input between sort and first prefix</li>
     *  <li>Ensure that there is no duplicate prefix used</li>
     * </ul>
     *
     * Postcondition: argMultimap contains 2 tokens where one token<br>
     * corresponds to PREFIX_FIELD and the other token corresponds
     * to PREFIX_ORDER
     * @param argMultimap Tokenized multimap to be checked
     * @throws ParseException If {@code argMultimap} is invalid
     */
    private void checkValidTokens(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.getValue(PREFIX_FIELD).isPresent() || !argMultimap.getValue(PREFIX_ORDER).isPresent()) {
            logger.warning("Missing required prefix - field or order not provided");
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }

        if (!argMultimap.getPreamble().isEmpty()) {
            logger.warning("Preamble found in sort command: " + argMultimap.getPreamble());
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_FIELD, PREFIX_ORDER);
    }
}
