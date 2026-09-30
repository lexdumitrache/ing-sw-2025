package Networking.Messages;

import Controller.Server.Server;
import Networking.Agent;
import Networking.Network;
import View.Client.Actions.LoginFailedAction;
import View.Client.Actions.LoginSuccessAction;

/**
 * Represents a message sent by a client to log in with a specified username.
 * This message is handled by the server to process the login request.
 */
public class LoginMessage implements Message {

    /**
     * The username of the player attempting to log in.
     */
    final String username;

    /**
     * Constructs a LoginMessage with the specified username.
     *
     * @param username The username of the player attempting to log in.
     */
    public LoginMessage(String username) {
        this.username = username;
    }

    /**
     * Handles the login message in the context of a server agent.
     * This method attempts to log in the player with the specified username and sends a success or failure action.
     *
     * @param agent The server agent handling the message.
     * @param network The network over which the message is sent.
     */
    @Override
    public void handle(Agent agent, Network network) {
        final Server server;

        try{
            server = (Server) agent;
        } catch (ClassCastException e) {
            return;
        }

        network.send(new ClientMessage(server.login(network, username) ? new LoginSuccessAction(username) : new LoginFailedAction(username)));
    }
}
