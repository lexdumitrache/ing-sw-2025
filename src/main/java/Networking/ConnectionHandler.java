package Networking;

/**
 * Represents a handler for managing the completion of connections.
 * This interface provides a method to signal that a connection is done.
 * Implementations of this interface can define specific actions to take
 * when a connection is completed.
 */
public interface ConnectionHandler {
    void setDone();
}
