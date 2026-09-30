package View.Client.States;

import Networking.Messages.Message;
import Networking.Network;
import View.Client.ClientState;

/**
 * Represents a state in which the client is connected to the network.
 * This class provides methods to send messages and handle disconnection.
 */
public abstract class ConnectedState implements ClientState {
    private final Network network;

    /**
     * Constructs a ConnectedState with the specified network.
     *
     * @param network The network instance associated with this connected state.
     */
    protected ConnectedState(Network network) {
        this.network = network;
    }

    /**
     * Gets the network associated with this connected state.
     *
     * @return The network instance.
     */
    public Network getNetwork() {
        return this.network;
    }

    /**
     * Sends a message through the network.
     * If the message cannot be sent, it stops the client connection.
     *
     * @param message The message to be sent.
     * @return The updated client state after sending the message.
     */
    @Override
    public final ClientState send(Message message){
        if(!this.network.send(message)){
            return this.stop();
        }

        return this;
    }

    /**
     * Handles the disconnection from the network.
     * This method is called when the client stops its connection.
     *
     * @return The updated client state after stopping the connection.
     */
    @Override
    public ClientState stop(){
        this.network.setDone();
        return ClientState.super.stop();
    }
}
