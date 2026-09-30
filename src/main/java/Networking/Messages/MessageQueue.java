package Networking.Messages;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents a message queue that can be used to enqueue and dequeue messages.
 * This interface extends Remote to allow for remote method invocation.
 */
public interface MessageQueue extends Remote {
    /**
     * Enqueues a message to the queue.
     *
     * @param message The message to be enqueued.
     * @return true if the message was successfully enqueued, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    boolean enqueue(Message message) throws RemoteException;

    /**
     * Dequeues a message from the queue.
     *
     * @return The dequeued message.
     * @throws RemoteException if a remote communication error occurs.
     */
    Message dequeue() throws RemoteException;
}
