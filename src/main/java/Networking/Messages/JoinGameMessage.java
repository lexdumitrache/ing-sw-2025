package Networking.Messages;

import Controller.Commands.LoginCommand;
import Controller.Controller;
import Controller.Server.Server;
import Networking.Agent;
import Networking.Network;
import View.Client.ClientState;

/**
 * Represents a message sent to join an existing game identified by its game ID.
 * This message is handled by the server to process the player's request to join a game.
 */
public class JoinGameMessage implements Message {

    /**
     * The ID of the game that the player wants to join.
     */
    private final int gameID;

    /**
     * Constructs a JoinGameMessage with the specified game ID.
     *
     * @param gameID The ID of the game to join.
     */
    public JoinGameMessage(int gameID) {
        this.gameID = gameID;
    }

    /**
     * Handles the message in the context of a server agent.
     * This method retrieves the username of the player and enqueues a login command for the specified game.
     *
     * @param agent The server agent handling the message.
     * @param network The network over which the message is sent.
     * @throws UnsupportedOperationException if the agent is not a Server instance.
     */
    @Override
    public void handle(Agent agent, Network network) throws UnsupportedOperationException {
        final Server server;

        try{
            server = (Server) agent;
        } catch (ClassCastException e) {
            return;
        }

        final String username = server.getUsername(network);

        final Controller game = server.getGame(this.gameID);

        if(username == null || game == null){
            network.send(new ClientMessage(
                    ClientState::net_JoinFailed
            ));
            return;
        }

        game.enqueueCommand(new LoginCommand(username));

        throw new RuntimeException("Killing ServerHandler. Hopefully controller will handle login soon...");
    }
}
