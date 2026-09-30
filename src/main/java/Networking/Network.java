package Networking;

import Networking.Messages.Message;

/**
 * Represents a network interface for sending and receiving messages.
 * This interface defines methods for sending messages, reading messages,
 * and managing the network state.
 */
public interface Network {
// Constant for the default timeout duration in milliseconds
    long TIMEOUT = 10000;

    /**
     * Sends a message over the network.
     *
     * @param message The message to be sent.
     * @return true if the message was successfully sent, false otherwise.
     */
    boolean send(Message message);

    /**
     * Reads a message from the network.
     *
     * @return The message read from the network.
     */
    Message read();

    /**
     * Closes the network connection.
     * This method should be called to clean up resources when the network is no longer needed.
     */
    void setDone();

    /**
     * Checks if the network connection is done.
     *
     * @return true if the connection is done, false otherwise.
     */
    boolean isDone();

    /**
     * Gets the timeout duration for the network.
     *
     * @return The timeout duration in milliseconds.
     */
    long getTimeout();

    void setTimeout(long timeout);
}
