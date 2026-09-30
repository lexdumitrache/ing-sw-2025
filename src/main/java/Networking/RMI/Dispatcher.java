package Networking.RMI;

import Networking.Messages.MessageQueue;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents a dispatcher that can handle message queues.
 * This interface extends Remote to allow for remote method invocation.
 */
public interface Dispatcher extends Remote {
    /**
     * Dispatches a message queue to the client.
     *
     * @param clientQueue The message queue to be dispatched.
     * @return The dispatched message queue.
     * @throws RemoteException if a remote communication error occurs.
     */
    MessageQueue dispatch(MessageQueue clientQueue) throws RemoteException;
}
