package Networking.Messages;

import Networking.Agent;
import Networking.Network;
import View.Client.Client;
import View.Client.ClientState;

/**
 * Represents a message sent to quit the game.
 * This message is handled by the client to stop the game and by the network to set the done state.
 */
public class QuitMessage implements Message {

    /**
     * Constructs a QuitMessage.
     * This message does not contain any data or perform any action.
     */
    @Override
    public void handle(Network network) {
        System.out.println("Received QuitCommand");
        network.setDone();
    }

    /**
     * Handles the QuitMessage by stopping the client and setting the network to done.
     * @param agent The agent that sent the message, expected to be a Client.
     * @param network The network on which the message was sent.
     */
    @Override
    public void handle(Agent agent, Network network) {
        final Client client;

        try{
            client = (Client) agent;
        } catch (ClassCastException e) {
            handle(network);
            return;
        }

        client.execute(ClientState::stop);
    }
}
