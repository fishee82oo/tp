package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagsContainTagPredicate;

/**
 * Filters and lists all persons in the contact book whose contact information contains any of the tags.
 * Keyword matching is case insensitive.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Filters all persons whose contact information contains any of "
            + "the specified tags (case-insensitive) and displays them as a list.\n"
            + "Tags should be alphanumeric and at most " + Tag.MAX_LENGTH + " characters long.\n"
            + "Parameters: t/TAG [t/TAG]...\n"
            + "Example: " + COMMAND_WORD + " t/ client t/ colleague";

    private static final Logger logger = LogsCenter.getLogger(FilterCommand.class);

    private final TagsContainTagPredicate predicate;

    /**
     * Creates a FilterCommand object with a given {@TagsContainTagPredicate} object.
     * @param predicate {@TagsContainTagPredicate} object to filter by
     */
    public FilterCommand(TagsContainTagPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
        logger.fine("FilterCommand object created with predicate: " + predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        logger.info("Executing filter command");

        model.updateFilteredPersonList(predicate);
        int filteredListSize = model.getFilteredPersonList().size();

        logger.info("Filter command executed successfully. Number of persons found: " + filteredListSize);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, filteredListSize));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FilterCommand)) {
            return false;
        }

        FilterCommand otherFilterCommand = (FilterCommand) other;
        return predicate.equals(otherFilterCommand.predicate);
    }
}
