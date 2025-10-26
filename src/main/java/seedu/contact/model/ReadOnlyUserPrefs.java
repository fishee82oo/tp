package seedu.contact.model;

import java.nio.file.Path;

import seedu.contact.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    Path getContactBookFilePath();

}
