package Networking.Messages;

import Controller.Commands.LoginCommand;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Server.Server;
import Networking.Agent;
import Networking.Network;

/**
 * Represents a message sent to create a new game with a specified match level.
 * This message is handled by the server to initiate the game creation process.
 */
public class CreateGameMessage implements Message {
    private final MatchLevel matchLevel;

    /**
     * Constructs a CreateGameMessage with the specified match level.
     *
     * @param matchLevel The level of the match to be created.
     */
    public CreateGameMessage(MatchLevel matchLevel) {
        this.matchLevel = matchLevel;
    }

    /**
     * Handles the message in the context of a server agent.
     * This method creates a new game with the specified match level and enqueues a login command.
     *
     * @param agent The server agent handling the message.
     * @param network The network over which the message is sent.
     */
    @Override
    public void handle(Agent agent, Network network){


        final Server server;

        try{
            server = (Server) agent;
        } catch (ClassCastException e){
            System.err.println("\"server\" is not a Server");
            return;
        }

        final String username;

        //don't accept non-logged in players (should be impossible)
        if((username = server.getUsername(network)) == null){
            return;
        }

        System.err.println("Creating new game");

        final Controller game = server.createGame(this.matchLevel);

        game.enqueueCommand(new LoginCommand(username));

        throw new RuntimeException("Killing ServerHandler. Hopefully controller will handle login soon...");
    }
}
