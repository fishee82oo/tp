package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameOrCompanyPredicate;

/**
 * Parses input arguments and creates a new FindCommand object.
 * Supports prefixes n/ for name, c/ for company.
 */
public class FindCommandParser implements Parser<FindCommand> {

    @Override
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_COMPANY);

        Optional<String> name = argMultimap.getValue(CliSyntax.PREFIX_NAME);
        Optional<String> company = argMultimap.getValue(CliSyntax.PREFIX_COMPANY);

        if (name.isEmpty() && company.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (name.isPresent() && name.get().trim().isEmpty()
                || company.isPresent() && company.get().trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(new NameOrCompanyPredicate(name, company));
    }
}
