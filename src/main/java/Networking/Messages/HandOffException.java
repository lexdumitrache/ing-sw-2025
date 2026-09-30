package Networking.Messages;

/**
 * Thrown by a message to stop the Handler that is reading it, because another agent
 * (e.g. a game Controller instead of the Server) is taking over the connection.
 */
public class HandOffException extends RuntimeException {
    public HandOffException(String message) {
        super(message);
    }
}
