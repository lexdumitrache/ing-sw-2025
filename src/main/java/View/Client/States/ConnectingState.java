package View.Client.States;

import Networking.Messages.Handler;
import Networking.Network;
import Networking.RMI.RMINetwork;
import Networking.TCP.TCPNetwork;
import View.Client.Client;
import View.Client.ClientState;
import View.Client.States.Connected.LoginState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the client when it is attempting to connect to a server.
 * This state allows the user to initiate a connection using either RMI or TCP.
 */
public final class ConnectingState implements ClientState {
    private final boolean useRMI;

    /**
     * Constructs a ConnectingState with the specified connection type.
     *
     * @param useRMI true if RMI should be used for the connection, false for TCP.
     */
    public ConnectingState(boolean useRMI) {
        this.useRMI = useRMI;
    }

    /**
     * Attempts to connect to the server using the specified hostname and port.
     * If the connection is successful, it starts a new Handler thread and transitions to the LoginState.
     *
     * @param hostname The hostname (IP address) of the server to connect to.
     * @param port     The port number of the server to connect to.
     * @return A new ClientState representing the login state after a successful connection.
     */
    @Override
    public ClientState connect(String hostname, Integer port) {
        final Network network;
        try {
            network = useRMI ? new RMINetwork(hostname, port) : new TCPNetwork(hostname, port);
        } catch (Exception e) {
            System.out.println("Could not connect with " + (useRMI ? "RMI" : "TCP") + " @" + hostname + ":" + port);
            return this;
        }

        new Handler<>(Client.client, network).start();
        return new LoginState(network);
    }

    /**
     * Returns the name of the state.
     * This method is used for debugging and logging purposes.
     *
     * @return A string representing the name of the state.
     */
    @Override
    public List<String> getAvailableCommands() {
        List<String> actions = new ArrayList<>();

        actions.add("Connect");

        actions.addAll(ClientState.super.getAvailableCommands());

        return actions;
    }
}
