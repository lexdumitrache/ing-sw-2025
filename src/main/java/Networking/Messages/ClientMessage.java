package Networking.Messages;

import Networking.Agent;
import Networking.Network;
import View.Client.Actions.Action;
import View.Client.Client;

/**
 * Represents a message sent by a client containing an action to be executed.
 * This message is handled by the client agent to perform the specified action.
 */
public class ClientMessage implements Message{
    private final Action action;

    /**
     * Constructs a ClientMessage with the specified action.
     * @param action
     */
    public ClientMessage(Action action) {
        this.action = action;
    }

    /**
     * Gets the action contained in this message.
     * @return The action to be executed by the client.
     */
    @Override
    public void handle(Agent agent, Network network) {
        final Client client;

        try{
            client = (Client) agent;
        }catch (ClassCastException e){
            return;
        }

        client.execute(action);
    }
}
