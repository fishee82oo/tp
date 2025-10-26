package seedu.contact.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.contact.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.contact.commons.exceptions.IllegalValueException;
import seedu.contact.commons.util.JsonUtil;
import seedu.contact.model.ContactBook;
import seedu.contact.testutil.TypicalPersons;

public class JsonSerializableCompanyBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsContactBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonContactBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonContactBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableContactBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableContactBook.class).get();
        ContactBook contactBookFromFile = dataFromFile.toModelType();
        ContactBook typicalPersonsContactBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(contactBookFromFile, typicalPersonsContactBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableContactBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableContactBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableContactBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableContactBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableContactBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

}
