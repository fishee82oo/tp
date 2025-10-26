package seedu.contact.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.contact.commons.core.LogsCenter;
import seedu.contact.commons.exceptions.DataLoadingException;
import seedu.contact.commons.exceptions.IllegalValueException;
import seedu.contact.commons.util.FileUtil;
import seedu.contact.commons.util.JsonUtil;
import seedu.contact.model.ReadOnlyContactBook;

/**
 * A class to access ContactBook data stored as a json file on the hard disk.
 */
public class JsonContactBookStorage implements ContactBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonContactBookStorage.class);

    private Path filePath;

    public JsonContactBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getContactBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyContactBook> readContactBook() throws DataLoadingException {
        return readContactBook(filePath);
    }

    /**
     * Similar to {@link #readContactBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyContactBook> readContactBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableContactBook> jsonContactBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableContactBook.class);
        if (!jsonContactBook.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonContactBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveContactBook(ReadOnlyContactBook ContactBook) throws IOException {
        saveContactBook(ContactBook, filePath);
    }

    /**
     * Similar to {@link #saveContactBook(ReadOnlyContactBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveContactBook(ReadOnlyContactBook ContactBook, Path filePath) throws IOException {
        requireNonNull(ContactBook);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableContactBook(ContactBook), filePath);
    }

}
