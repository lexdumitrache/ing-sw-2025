package Networking.RMI;

import Networking.Messages.Message;
import Networking.Messages.MessageQueue;
import Networking.Messages.QuitMessage;
import Networking.Network;
import Networking.TimeoutThread;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMINetwork is a class that implements the Network interface for RMI communication.
 * It handles reading messages from an incoming queue and sending messages to an outgoing queue.
 * It also manages the connection state and timeout functionality.
 */
public class RMINetwork implements Network {


    private final RMIMessageQueue inQueue;
    private final MessageQueue outQueue;
    private boolean done = false;

    /**
     * The timeout duration in milliseconds. If the connection is idle for this duration,
     * it will be considered done and the connection will be closed.
     */
    private long timeout = System.currentTimeMillis() + TIMEOUT;;

    /**
     * Constructs a new RMINetwork instance that connects to the specified hostname and port.
     * It retrieves the Dispatcher from the RMI registry and initializes the inQueue and outQueue.
     *
     * @param hostname The hostname of the RMI registry.
     * @param port The port number of the RMI registry. If null, defaults to 1099.
     * @throws RemoteException if a remote communication error occurs.
     * @throws NotBoundException if the Dispatcher is not bound in the registry.
     */
    public RMINetwork(String hostname, Integer port) throws RemoteException, NotBoundException {
        final Registry registry = LocateRegistry.getRegistry(hostname, port != null ? port : 1099);

        final Dispatcher dispatcher = (Dispatcher) registry.lookup("Dispatcher");

        this.inQueue = new RMIMessageQueue();

        this.outQueue = dispatcher.dispatch(this.inQueue);

        new TimeoutThread(this).start();
    }

    /**
     * Constructs a new RMINetwork instance with the specified inQueue and outQueue.
     * This constructor is used when the queues are already created and passed in.
     *
     * @param inQueue The incoming message queue.
     * @param outQueue The outgoing message queue.
     * @throws RemoteException if a remote communication error occurs.
     */
    public RMINetwork(RMIMessageQueue inQueue, MessageQueue outQueue) throws RemoteException {
        this.inQueue = inQueue;
        this.outQueue = outQueue;

        new TimeoutThread(this).start();
    }

    /**
     * Reads a message from the inQueue.
     * This method attempts to dequeue a message and returns it. If an exception occurs,
     * it sets the connection as done and returns null.
     *
     * @return The dequeued message, or null if an error occurred.
     */
    @Override
    public Message read(){
        try {
            return this.inQueue.dequeue();
        } catch (RemoteException e) {
            this.setDone();
            return null;
        }
    }


    /**
     * Sends a message to the outQueue.
     * This method attempts to enqueue the message and returns true if successful, false otherwise.
     *
     * @param message The message to be sent.
     * @return true if the message was successfully enqueued, false otherwise.
     */
    @Override
    public boolean send(Message message) {
        try {
            return this.outQueue.enqueue(message);
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Closes the connection by sending a QuitMessage and unexporting the inQueue.
     * This method is synchronized to ensure thread safety when closing the connection.
     */
    @Override
    public synchronized void setDone() {
        if(this.done) return;
        send(new QuitMessage());
        this.done = true;
        try {
            synchronized (this.inQueue) {
                UnicastRemoteObject.unexportObject(this.inQueue, true);
                this.inQueue.notifyAll();
            }

        } catch (NoSuchObjectException e) {
            System.err.println("Tried to unexport non-exported queue");
        }
    }

    /**
     * Checks if the connection is done.
     * This method is synchronized to ensure thread safety when checking the connection state.
     *
     * @return true if the connection is done, false otherwise.
     */
    @Override
    public synchronized boolean isDone() {
        return this.done;
    }

    /**
     * Gets TIMEOUT value.
     *
     * @return The inQueue.
     */
    @Override
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * Sets the timeout duration for the connection.
     * This method allows the timeout to be adjusted dynamically.
     *
     * @param timeout The new timeout duration in milliseconds.
     */
    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;

    }
}
