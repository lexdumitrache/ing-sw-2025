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
            // the connection is handled by a game (e.g. a player who just rejoined): no game list to send
            return;
        }

        network.send(new ClientMessage(new UpdateListAction(server.getGameSummaries())));
    }
}
