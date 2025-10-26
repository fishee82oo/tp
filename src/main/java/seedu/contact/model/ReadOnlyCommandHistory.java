package seedu.contact.model;

/**
 * Unmodifiable view of Command History
 */
public interface ReadOnlyCommandHistory {

    /**
     * Takes an index offset value n, and retrieves the nth latest command (zero-indexed)
     * @return String of target Command (Empty String "" if not found)
     */
    String getLastCommand();
    String getLastCommand(int n);
}
