package Networking.RMI;

import Networking.Messages.Message;
import Networking.Messages.MessageQueue;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a message queue that can be used to enqueue and dequeue messages.
 * This class implements the MessageQueue interface and extends UnicastRemoteObject to allow for remote method invocation.
 */
public class RMIMessageQueue extends UnicastRemoteObject implements MessageQueue {

    /**
     * The queue that holds the messages.
     * This is a thread-safe queue that allows for synchronized access to enqueue and dequeue operations.
     */
    private final Queue<Message> queue = new LinkedList<>();

    /**
     * Constructs a new RMIMessageQueue.
     * This constructor initializes the message queue and allows for remote method invocation.
     *
     * @throws RemoteException if a remote communication error occurs.
     */
    public RMIMessageQueue() throws RemoteException {
        super();
    }

    /**
     * Enqueues a message to the queue.
     * This method is synchronized to ensure thread safety during the enqueue operation.
     *
     * @param message The message to be enqueued.
     * @return true if the message was successfully enqueued, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public synchronized boolean enqueue(Message message) throws RemoteException {
        this.notifyAll();
        return this.queue.add(message);
    }

    /**
     * Dequeues a message from the queue.
     * This method is synchronized to ensure thread safety during the dequeue operation.
     * If the queue is empty, it will wait until a message is available.
     *
     * @return The dequeued message, or null if the queue was empty and no message was available.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public synchronized Message dequeue() throws RemoteException {
        if (this.queue.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                System.err.println("Could not wait on dequeue? Why?");
                throw new RuntimeException(e);
            }
            return null;
        }
        return queue.poll();
    }
}
