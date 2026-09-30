package Networking.RMI;

import Controller.Server.Server;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMIConnectionHandler is responsible for setting up the RMI connection
 * between the server and clients. It binds the Dispatcher to the RMI registry
 * and handles the unbinding when the connection is no longer needed.
 */
public class RMIConnectionHandler implements Networking.ConnectionHandler {

    /**
     * The Dispatcher instance that handles incoming client requests.
     */
    private final Dispatcher dispatcher;
    /**
     * The RMI registry where the Dispatcher is bound.
     */
    private final Registry registry;

    /**
     * Constructs an RMIConnectionHandler that sets up the RMI connection
     * with the specified server, hostname, and port.
     *
     * @param server The server instance to connect to.
     * @param hostname The hostname for the RMI connection.
     * @param port The port number for the RMI registry.
     * @throws RemoteException if a remote communication error occurs.
     */
    public RMIConnectionHandler(Server server, String hostname, String port) throws RemoteException {
        int PORT;
        try {
            PORT = Integer.parseInt(port);
        }  catch (NumberFormatException e) {
            PORT = 1099;
        }

        this.dispatcher = new DispatcherImpl(server);

        System.setProperty("java.rmi.server.hostname", hostname);

        this.registry = LocateRegistry.createRegistry(PORT);


        this.registry.rebind("Dispatcher", this.dispatcher);

        System.out.println("RMI connection handler started at port " + PORT);
    }


    /**
     * Unbind the dispatcher from the RMI registry and unexport the dispatcher object.
     * This is done to clean up resources and ensure that the dispatcher is no longer accessible.
     */
    @Override
    public void setDone() {

        try {
            this.registry.unbind("Dispatcher");
        } catch (NotBoundException e) {
            System.err.println("RMIConnectionHandler.setDone(): Could not unbind Dispatcher as it's apparently not bound/doesn't exist (Lucky, but how?)");
        } catch (RemoteException e) {
            System.err.println("RMIConnectionHandler.setDone(): Something went wrong in the networking layer. Doesn't matter, I'm out of here!");
        }finally {
            try {
                UnicastRemoteObject.unexportObject(this.dispatcher, true);
            } catch (NoSuchObjectException e) {
                System.err.println("RMIConnectionHandler.setDone(): Could not unexport the dispatcher object");
            }
        }
    }
}
