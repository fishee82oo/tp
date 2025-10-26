package seedu.contact.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.logging.Logger;

import seedu.contact.commons.core.LogsCenter;
import seedu.contact.logic.commands.FilterCommand;
import seedu.contact.logic.parser.exceptions.ParseException;
import seedu.contact.model.tag.Tag;
import seedu.contact.model.tag.TagsContainTagPredicate;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    private static final Logger logger = LogsCenter.getLogger(FilterCommandParser.class);

    /**
     * Parses the given {@code String} of arguments in the context of the FilterCommand
     * and returns a FilterCommand object for execution.
     *
     * @throws ParseException if the user does not confirm to the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        logger.fine("Parsing filter command with arguments: " + args);

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TAG);
        checkValidTokens(argMultimap);

        TagsContainTagPredicate predicate =
                new TagsContainTagPredicate(argMultimap.getAllValues(PREFIX_TAG).stream()
                                                       .map(token -> token.toLowerCase())
                                                       .distinct()
                                                       .map(token -> new Tag(token))
                                                       .toList());

        logger.info("Successfully parsed filter command with tags: "
                + argMultimap.getAllValues(PREFIX_TAG));

        return new FilterCommand(predicate);
    }

    /**
     * Checks if the {@code argMultmap} object is valid after tokenizing.
     *
     * Checks include:
     * <ul>
     *   <li>Check that PREFIX_TAG tokens exist</li>
     *   <li>Ensure that there is no input between filter and first prefix</li>
     *   <li>Ensure that there are no empty tags</li>
     *   <li>Ensure that all tags are valid names</li>
     * </ul>
     *
     * Postcondition: argMultimap contains mapping for PREFIX_TAG
     * where the mappings are valid tag names.
     * @param argMultimap Tokenized multimap to be checked
     * @throws ParseException If {@code argMultimap} is invalid
     */
    private void checkValidTokens(ArgumentMultimap argMultimap) throws ParseException {
        String errorMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

        if (!argMultimap.getValue(PREFIX_TAG).isPresent()) {
            logger.warning("No tags provided in filter command");
            throw new ParseException(errorMessage);
        }

        if (!argMultimap.getPreamble().isEmpty()) {
            logger.warning("Preamble found in filter command");
            throw new ParseException(errorMessage);
        }

        boolean containsEmptyTag = argMultimap.getAllValues(PREFIX_TAG).stream()
                                              .anyMatch(tagName -> tagName.isEmpty());

        if (containsEmptyTag) {
            logger.warning("Empty tag found in filter command");
            throw new ParseException(errorMessage);
        }

        if (argMultimap.getAllValues(PREFIX_TAG).stream()
                       .anyMatch(tagName -> !Tag.isValidTagName(tagName))) {
            logger.warning("Invalid tag name found in filter command");
            throw new ParseException(errorMessage);
        }
    }
}
