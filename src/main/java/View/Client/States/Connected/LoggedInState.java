package View.Client.States.Connected;

import Networking.Messages.EmptyMessage;
import Networking.Network;
import View.Client.ClientState;
import View.Client.States.ConnectedState;

/**
 * Represents a state in which the client is logged in to the network.
 * This state extends the ConnectedState and provides functionality specific to logged-in users.
 */
public abstract class LoggedInState extends ConnectedState {
    private final String username;


    /**
     * Constructs a LoggedInState with the specified network and username.
     *
     * @param network The network to which the client is connected.
     * @param username The username of the logged-in user.
     */
    public LoggedInState(Network network, String username){
        super(network);
        this.username = username;
    }

    /**
     * Returns the username of the logged-in user.
     *
     * @return The username of the user.
     */
    @Override
    public final String getUsername() {
        return this.username;
    }

    /**
     * Stops the current client state by sending an empty message to the network.
     * This method is typically called when the user logs out or disconnects.
     *
     * @return The updated client state after stopping.
     */
    @Override
    public ClientState stop(){
        return super.stop();
    }
}
