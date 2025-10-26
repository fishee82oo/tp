package seedu.contact.logic.parser;

import static seedu.contact.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import seedu.contact.logic.commands.AddCommandBasic;
import seedu.contact.logic.parser.exceptions.ParseException;
import seedu.contact.model.person.Company;
import seedu.contact.model.person.Email;
import seedu.contact.model.person.Name;
import seedu.contact.model.person.Person;
import seedu.contact.model.person.Phone;
import seedu.contact.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommandBasic object.
 */
public class AddCommandBasicParser implements Parser<AddCommandBasic> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommandBasic
     * and returns an AddCommandBasic object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    @Override
    public AddCommandBasic parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_PHONE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommandBasic.MESSAGE_USAGE));
        }
        // handle the duplicate of prefix
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE);

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());

        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).orElse("unknown@example.com"));
        Company company = ParserUtil.parseCompany(argMultimap.getValue(PREFIX_COMPANY).orElse("N/A"));
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        Person person = new Person(name, phone, email, company, tagList);

        return new AddCommandBasic(person);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
