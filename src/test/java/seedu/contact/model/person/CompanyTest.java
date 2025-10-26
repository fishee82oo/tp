package seedu.contact.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.contact.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CompanyTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Company(null));
    }

    @Test
    public void constructor_invalidContact_throwsIllegalArgumentException() {
        String invalidAddress = "";
        assertThrows(IllegalArgumentException.class, () -> new Company(invalidContact));
    }

    @Test
    public void isValidCompany() {
        // null company
        assertThrows(NullPointerException.class, () -> Company.isValidCompany(null));

        // invalid companies
        assertFalse(Company.isValidCompany("")); // empty string
        assertFalse(Company.isValidCompany(" ")); // spaces only
        assertFalse(Company.isValidCompany(generateString(Company.MAX_LENGTH + 1))); // too long

        // valid companies
        assertTrue(Company.isValidCompany("Google"));
        assertTrue(Company.isValidCompany("-")); // one character
        assertTrue(Company.isValidCompany("International Business Machines Corporation")); // long text
        assertTrue(Company.isValidCompany(generateString(Company.MAX_LENGTH))); // boundary length
    }

    @Test
    public void equals() {
        Company company = new Company("Google");

        // same values -> returns true
        assertTrue(company.equals(new Company("Google")));

        // same object -> returns true
        assertTrue(company.equals(company));

        // null -> returns false
        assertFalse(company.equals(null));

        // different types -> returns false
        assertFalse(company.equals(5.0f));

        // different values -> returns false
        assertFalse(company.equals(new Company("Microsoft")));
    }

    private String generateString(int length) {
        return "a".repeat(length);
    }
}
