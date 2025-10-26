package seedu.address.model.person;

import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;

/**
 * Tests that a {@code Person}'s name or company contains the given keyword(s).
 * Name and company both support partial (substring) search, case-insensitive.
 */
public class NameOrCompanyPredicate implements Predicate<Person> {
    private final Optional<String> nameKeyword;
    private final Optional<String> companyKeyword;

    /**
     * Constructs a {@code NameOrCompanyPredicate} using the provided optional keywords
     * for the name and company fields.
     *
     * Each keyword, if present, is converted to lowercase to enable case-insensitive
     * comparison during matching.
     *
     * If both {@code nameKeyword} and {@code companyKeyword} are present,
     * a person must match both fields to be considered a match.
     * If only one is present, matching is performed on that field alone.
     *
     * @param nameKeyword an {@code Optional} containing the keyword to match against
     *                    a person's name, or an empty {@code Optional} if not specified
     * @param companyKeyword an {@code Optional} containing the keyword to match against
     *                       a person's company, or an empty {@code Optional} if not specified
     */
    public NameOrCompanyPredicate(Optional<String> nameKeyword, Optional<String> companyKeyword) {
        this.nameKeyword = nameKeyword.map(String::toLowerCase);
        this.companyKeyword = companyKeyword.map(String::toLowerCase);
    }

    @Override
    public boolean test(Person person) {
        String name = person.getName().fullName.toLowerCase();
        String company = person.getCompany().value.toLowerCase();

        boolean nameMatch = nameKeyword.map(k -> StringUtil.containsIgnoreCase(name, k)).orElse(false);
        boolean companyMatch = companyKeyword.map(k -> StringUtil.containsIgnoreCase(company, k)).orElse(false);

        // if both prefix exist, both must match
        if (nameKeyword.isPresent() && companyKeyword.isPresent()) {
            return nameMatch && companyMatch;
        }

        // otherwise, match whichever is provided
        return nameMatch || companyMatch;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NameOrCompanyPredicate)) {
            return false;
        }
        NameOrCompanyPredicate o = (NameOrCompanyPredicate) other;
        return nameKeyword.equals(o.nameKeyword)
                && companyKeyword.equals(o.companyKeyword);
    }
}
