package seedu.contact.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.contact.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.contact.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.contact.commons.core.index.Index;
import seedu.contact.commons.util.CollectionUtil;
import seedu.contact.commons.util.ToStringBuilder;
import seedu.contact.logic.Messages;
import seedu.contact.logic.commands.exceptions.CommandException;
import seedu.contact.model.Model;
import seedu.contact.model.person.Company;
import seedu.contact.model.person.Email;
import seedu.contact.model.person.Name;
import seedu.contact.model.person.Person;
import seedu.contact.model.person.Phone;
import seedu.contact.model.tag.Tag;

/**
 * Edits the details of an existing person in the contact book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number or contact name used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) or NAME "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_COMPANY + "COMPANY] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com" + "\n"
            + "Example: " + COMMAND_WORD + " John Doe "
            + PREFIX_EMAIL + "john.doe@company.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: \n%1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the contact book.";
    public static final String MESSAGE_MULTIPLE_MATCHING_PERSONS =
            "There are multiple contacts’ names matched with the reference.\n"
            + "Please use 'edit INDEX' to specify the contact you want to edit "
            + "in the following list of matched contacts.";
    public static final String MESSAGE_PERSON_NAME_NOT_FOUND =
            "The person name provided does not match any displayed contact.";

    private final Index index;
    private final String nameReference;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.nameReference = null;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    /**
     * Constructs an EditCommand that targets a person by name reference rather than index.
     */
    public EditCommand(String nameReference, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(nameReference);
        requireNonNull(editPersonDescriptor);

        this.index = null;
        this.nameReference = nameReference;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToEdit;
        if (index != null) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            personToEdit = lastShownList.get(index.getZeroBased());
        } else {
            String normalizedQuery = normalizeName(nameReference);

            // Find exact name matches (case-insensitive, multiple spaces collapsed)
            java.util.List<Person> matchedPersons = lastShownList.stream()
                    .filter(p -> normalizeName(p.getName().toString()).equalsIgnoreCase(normalizedQuery))
                    .collect(java.util.stream.Collectors.toList());

            if (matchedPersons.isEmpty()) {
                throw new CommandException(MESSAGE_PERSON_NAME_NOT_FOUND);
            }

            if (matchedPersons.size() > 1) {
                model.updateFilteredPersonList(p -> normalizeName(p.getName().toString())
                        .equalsIgnoreCase(normalizedQuery));
                throw new CommandException(MESSAGE_MULTIPLE_MATCHING_PERSONS);
            }

            personToEdit = matchedPersons.get(0);
        }
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Creates and returns a {@code Person} with details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Company updatedCompany = editPersonDescriptor.getCompany().orElse(personToEdit.getCompany());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());

        return new Person(updatedName, updatedPhone, updatedEmail, updatedCompany, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return java.util.Objects.equals(index, otherEditCommand.index)
                && java.util.Objects.equals(nameReference, otherEditCommand.nameReference)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("nameReference", nameReference)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    private static String normalizeName(String raw) {
        String trimmed = raw.trim();
        return trimmed.replaceAll("\\s+", " ");
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Company company;
        private Set<Tag> tags;

        public EditPersonDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setCompany(toCopy.company);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, company, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setCompany(Company company) {
            this.company = company;
        }

        public Optional<Company> getCompany() {
            return Optional.ofNullable(company);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(company, otherEditPersonDescriptor.company)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("company", company)
                    .add("tags", tags)
                    .toString();
        }
    }
}
