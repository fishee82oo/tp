package seedu.contact.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import seedu.contact.commons.core.index.Index;
import seedu.contact.logic.commands.EditCommand;
import seedu.contact.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.contact.logic.parser.exceptions.ParseException;
import seedu.contact.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_COMPANY, PREFIX_TAG);

        String preamble = argMultimap.getPreamble();
        Index index = null;
        boolean isIndex;
        try {
            index = ParserUtil.parseIndex(preamble);
            isIndex = true;
        } catch (ParseException pe) {
            isIndex = false;
        }

        // If the preamble is explicitly "0", surface an invalid index error
        // instead of treating it as a name-based edit.
        String trimmedPreamble = preamble.trim();
        if (!isIndex && "0".equals(trimmedPreamble)) {
            throw new ParseException(ParserUtil.MESSAGE_INVALID_INDEX);
        }

        boolean anyFieldPresent = argMultimap.getValue(PREFIX_NAME).isPresent()
                || argMultimap.getValue(PREFIX_PHONE).isPresent()
                || argMultimap.getValue(PREFIX_EMAIL).isPresent()
                || argMultimap.getValue(PREFIX_COMPANY).isPresent()
                || !argMultimap.getAllValues(PREFIX_TAG).isEmpty();
        if (trimmedPreamble.isEmpty() && !anyFieldPresent) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_COMPANY);

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPersonDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPersonDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPersonDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_COMPANY).isPresent()) {
            editPersonDescriptor.setCompany(ParserUtil.parseCompany(argMultimap.getValue(PREFIX_COMPANY).get()));
        }
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        if (isIndex) {
            return new EditCommand(index, editPersonDescriptor);
        }

        // Name-based: require non-empty preamble after trimming; validation will occur during execution
        String nameReference = trimmedPreamble;
        if (nameReference.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        return new EditCommand(nameReference, editPersonDescriptor);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(ParserUtil.parseTags(tagSet));
    }

}
