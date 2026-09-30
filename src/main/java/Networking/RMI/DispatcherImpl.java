package Networking.RMI;

import Controller.Server.Server;
import Networking.Messages.Handler;
import Networking.Messages.MessageQueue;
import Networking.TimeoutThread;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Represents a dispatcher that handles incoming client requests and connects them to the server.
 * This class implements the Dispatcher interface and extends UnicastRemoteObject to allow for remote method invocation.
 */
public class DispatcherImpl extends UnicastRemoteObject implements Dispatcher {

    /**
     * The server instance that this dispatcher will connect to.
     */
    private final Server server;

    public DispatcherImpl(Server server) throws RemoteException {
        super();
        this.server = server;
    }

    /**
     * Dispatches a message queue to the client.
     * This method creates a new RMIMessageQueue for incoming messages and connects it to the server's network.
     * It starts a TimeoutThread and a Handler to manage the communication.
     *
     * @param clientQueue The message queue provided by the client.
     * @return The server's inQueue, which will be used as the outQueue for the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public MessageQueue dispatch(MessageQueue clientQueue) throws RemoteException {
        System.out.println("Got new client request");

        RMIMessageQueue inQueue = new RMIMessageQueue();

        RMINetwork network = new RMINetwork(inQueue, clientQueue);

        new TimeoutThread(network).start();
        new Handler<Server>(this.server, network).start();

        this.server.connect(network);

        // Return server's inQueue to client (which will use it as outQueue)
        return inQueue;
    }
}
