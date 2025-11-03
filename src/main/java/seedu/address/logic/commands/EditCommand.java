package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPANY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DETAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_DELETE;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Company;
import seedu.address.model.person.Detail;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

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
            + "[" + PREFIX_DETAIL + "DETAIL] "
            + "[" + PREFIX_TAG + "TAG]... "
            + "[" + PREFIX_TAG_ADD + "TAG]... "
            + "[" + PREFIX_TAG_DELETE + "TAG]...\n"
            + "Note: " + PREFIX_TAG + " overwrites all tags, while " + PREFIX_TAG_ADD + " and "
            + PREFIX_TAG_DELETE + " add/remove specific tags.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com" + "\n"
            + "Example: " + COMMAND_WORD + " John Doe "
            + PREFIX_EMAIL + "john.doe@company.com" + "\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_TAG_ADD + "client " + PREFIX_TAG_DELETE + "friend";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Contact: \n%1$s";
    public static final String MESSAGE_NOT_EDITED =
            "At least one valid field to edit must be provided.\n"
            + "Valid prefixes for edit command are:\n"
            + PREFIX_NAME + " (name), "
            + PREFIX_PHONE + " (phone), "
            + PREFIX_EMAIL + " (email), "
            + PREFIX_COMPANY + " (company), "
            + PREFIX_DETAIL + " (detail), "
            + PREFIX_TAG + " (overwrite tags), "
            + PREFIX_TAG_ADD + " (add tags), "
            + PREFIX_TAG_DELETE + " (delete tags)";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the contact book.";
    public static final String MESSAGE_DUPLICATE_EMAIL = "This email already exists in the contact book.";
    public static final String MESSAGE_CONFLICTING_TAG_PREFIXES =
            "Cannot use t/ (overwrite tags) together with t+/ (add tags) or t-/ (delete tags) in the same command.";
    public static final String MESSAGE_TAG_NOT_FOUND =
            "The following tag(s) do not exist on this person and cannot be deleted: %1$s";
    public static final String MESSAGE_EMPTY_TAG_ADD = "Tags to add cannot be empty. Please provide at least one tag.";
    public static final String MESSAGE_EMPTY_TAG_DELETE =
            "Tags to delete cannot be empty. Please provide at least one tag.";
    public static final String MESSAGE_MULTIPLE_MATCHING_PERSONS =
            "There are multiple contacts' names matched with the reference.\n"
            + "Please use 'edit INDEX' to specify the contact you want to edit "
            + "in the following list of matched contacts.";
    public static final String MESSAGE_PERSON_NAME_NOT_FOUND =
            "The person name provided does not match any displayed contact.";
    private static final String PLACEHOLDER_EMAIL = "unknown@example.com";

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

        // Validate that tags to delete exist on the person
        if (editPersonDescriptor.getTagsToDelete().isPresent()) {
            Set<Tag> tagsToDelete = editPersonDescriptor.getTagsToDelete().get();
            Set<Tag> existingTags = personToEdit.getTags();
            Set<Tag> nonExistentTags = new HashSet<>();

            for (Tag tagToDelete : tagsToDelete) {
                if (!existingTags.contains(tagToDelete)) {
                    nonExistentTags.add(tagToDelete);
                }
            }

            if (!nonExistentTags.isEmpty()) {
                String tagNames = nonExistentTags.stream()
                        .map(tag -> tag.tagName)
                        .collect(java.util.stream.Collectors.joining(", "));
                throw new CommandException(String.format(MESSAGE_TAG_NOT_FOUND, tagNames));
            }
        }

        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        if (isEmailDuplicated(model, personToEdit, editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_EMAIL);
        }

        model.setPerson(personToEdit, editedPerson);
        //model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.updateFilteredPersonList(p -> p.equals(editedPerson));
        model.updateFocusedPerson(0);
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
        Detail updatedDetail = editPersonDescriptor.getDetail().orElse(personToEdit.getDetail());

        // Handle tags: if tags are set (overwrite), use them; otherwise, apply additions/deletions
        Set<Tag> updatedTags;
        if (editPersonDescriptor.getTags().isPresent()) {
            // Overwrite mode: replace all tags
            updatedTags = editPersonDescriptor.getTags().get();
        } else {
            // Addition/deletion mode: start with existing tags
            updatedTags = new HashSet<>(personToEdit.getTags());

            // Apply tag additions
            if (editPersonDescriptor.getTagsToAdd().isPresent()) {
                updatedTags.addAll(editPersonDescriptor.getTagsToAdd().get());
            }

            // Apply tag deletions
            if (editPersonDescriptor.getTagsToDelete().isPresent()) {
                updatedTags.removeAll(editPersonDescriptor.getTagsToDelete().get());
            }
        }

        return new Person(updatedName, updatedPhone, updatedEmail, updatedCompany, updatedDetail, updatedTags);
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
     * Checks if the edited contact's email is already used by another contact in the contact book.
     * Returns false if:
     * - The email is a placeholder email
     * - The email is the same as the original contact's email (no change)
     * - No other contact has this email
     */
    private boolean isEmailDuplicated(Model model, Person personToEdit, Person editedPerson) {
        String editedEmailValue = editedPerson.getEmail().value;
        String originalEmailValue = personToEdit.getEmail().value;

        // If email hasn't changed, it's not a duplicate
        if (editedEmailValue.equals(originalEmailValue)) {
            return false;
        }

        // If the email is a placeholder, allow it
        if (PLACEHOLDER_EMAIL.equals(editedEmailValue)) {
            return false;
        }

        // Check if any other person has this email
        return model.getAddressBook().getPersonList().stream()
                .filter(person -> !person.equals(personToEdit))
                .anyMatch(person -> person.getEmail().value.equals(editedEmailValue));
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
        private Detail detail;
        private Set<Tag> tags;
        private Set<Tag> tagsToAdd;
        private Set<Tag> tagsToDelete;

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
            setDetail(toCopy.detail);
            setTags(toCopy.tags);
            setTagsToAdd(toCopy.tagsToAdd);
            setTagsToDelete(toCopy.tagsToDelete);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, company, detail, tags, tagsToAdd, tagsToDelete);
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

        public void setDetail(Detail detail) {
            this.detail = detail;
        }

        public Optional<Detail> getDetail() {
            return Optional.ofNullable(detail);
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

        /**
         * Sets {@code tagsToAdd} to this object's {@code tagsToAdd}.
         * A defensive copy of {@code tagsToAdd} is used internally.
         */
        public void setTagsToAdd(Set<Tag> tagsToAdd) {
            this.tagsToAdd = (tagsToAdd != null) ? new HashSet<>(tagsToAdd) : null;
        }

        /**
         * Returns an unmodifiable tag set of tags to add, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tagsToAdd} is null.
         */
        public Optional<Set<Tag>> getTagsToAdd() {
            return (tagsToAdd != null) ? Optional.of(Collections.unmodifiableSet(tagsToAdd)) : Optional.empty();
        }

        /**
         * Sets {@code tagsToDelete} to this object's {@code tagsToDelete}.
         * A defensive copy of {@code tagsToDelete} is used internally.
         */
        public void setTagsToDelete(Set<Tag> tagsToDelete) {
            this.tagsToDelete = (tagsToDelete != null) ? new HashSet<>(tagsToDelete) : null;
        }

        /**
         * Returns an unmodifiable tag set of tags to delete, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tagsToDelete} is null.
         */
        public Optional<Set<Tag>> getTagsToDelete() {
            return (tagsToDelete != null) ? Optional.of(Collections.unmodifiableSet(tagsToDelete)) : Optional.empty();
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
                    && Objects.equals(detail, otherEditPersonDescriptor.detail)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(tagsToAdd, otherEditPersonDescriptor.tagsToAdd)
                    && Objects.equals(tagsToDelete, otherEditPersonDescriptor.tagsToDelete);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("company", company)
                    .add("detail", detail)
                    .add("tags", tags)
                    .add("tagsToAdd", tagsToAdd)
                    .add("tagsToDelete", tagsToDelete)
                    .toString();
        }
    }
}
