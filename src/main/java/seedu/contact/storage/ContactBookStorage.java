package seedu.contact.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import seedu.contact.commons.exceptions.DataLoadingException;
import seedu.contact.model.ContactBook;
import seedu.contact.model.ReadOnlyContactBook;

/**
 * Represents a storage for {@link ContactBook}.
 */
public interface ContactBookStorage {

    /**
     * Returns the file path of the data file.
     */
    Path getContactBookFilePath();

    /**
     * Returns ContactBook data as a {@link ReadOnlyContactBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    Optional<ReadOnlyContactBook> readContactBook() throws DataLoadingException;

    /**
     * @see #getContactBookFilePath()
     */
    Optional<ReadOnlyContactBook> readContactBook(Path filePath) throws DataLoadingException;

    /**
     * Saves the given {@link ReadOnlyContactBook} to the storage.
     * @param contactBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveContactBook(ReadOnlyContactBook contactBook) throws IOException;

    /**
     * @see #saveContactBook(ReadOnlyContactBook)
     */
    void saveContactBook(ReadOnlyContactBook contactBook, Path filePath) throws IOException;

}
