package Networking.Messages;

import Controller.Server.Server;
import Networking.Agent;
import Networking.Network;
import View.Client.Actions.UpdateListAction;

/**
 * Represents a message sent by the server to update the list of game IDs.
 * This message is handled by the client to refresh the list of available games.
 */
public class UpdateListMessage implements Message {
    /**
     * Constructs an UpdateListMessage.
     * This message does not contain any data or perform any action.
     */
    @Override
    public void handle(Agent agent, Network network){
        final Server server;

        try{
            server = (Server) agent;
        } catch (ClassCastException e){
            System.err.println("Server object is not a Server");
            return;
        }

        network.send(new ClientMessage(new UpdateListAction(server.getGameIds())));
    }
}
