package seedu.contact.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.contact.testutil.TypicalPersons.getTypicalContactBook;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.contact.commons.core.GuiSettings;
import seedu.contact.model.ContactBook;
import seedu.contact.model.ReadOnlyContactBook;
import seedu.contact.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonContactBookStorage contactBookStorage = new JsonContactBookStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(contactBookStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void contactBookReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonContactBookStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonContactBookStorageTest} class.
         */
        ContactBook original = getTypicalContactBook();
        storageManager.saveContactBook(original);
        ReadOnlyContactBook retrieved = storageManager.readContactBook().get();
        assertEquals(original, new ContactBook(retrieved));
    }

    @Test
    public void getContactBookFilePath() {
        assertNotNull(storageManager.getContactBookFilePath());
    }

}
