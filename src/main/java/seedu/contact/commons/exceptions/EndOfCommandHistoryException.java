package seedu.contact.commons.exceptions;

/**
 * Represents error of trying to access end of Command History
 */
public class EndOfCommandHistoryException extends Exception {
    public EndOfCommandHistoryException(String message) {
        super(message);
    }
}
