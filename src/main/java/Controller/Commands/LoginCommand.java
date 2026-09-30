// LoginCommand.java
package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;
import Controller.Server.Server;
import Networking.Agent;
import Networking.Messages.ClientMessage;
import Networking.Messages.Handler;
import Networking.Network;
import View.Client.Actions.Action;
import View.Client.Actions.JoinSuccessAction;
import View.Client.ClientState;

import java.util.List;
import java.util.Map;

/**
 * Command for handling player login to a game session.
 * Allows a player to join an existing game by providing their name.
 */
public class LoginCommand extends Command {

    /**
     * Constructs a new LoginCommand.
     *
     * @param playerName the name of the player attempting to login
     */
    public LoginCommand(String playerName) {
        super(playerName);
    }

    /**
     * Executes the login command by calling the controller's login method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        final String username = this.getPlayerName();

        Agent agent = controller;
        Action action = new JoinSuccessAction(controller.getModel());

        try{
            controller.login(username);
        } catch (InvalidParameters | InvalidCommand e){
            agent = Server.server;
            action = ClientState::net_JoinFailed;
            System.out.println("Login Failed");
            throw e;
        } finally {
            if(Server.server!=null){
                final Network network = Server.server.getNetwork(username);
                network.send(new ClientMessage(action));

                new Handler<>(agent, network).start();
            }

        }
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {

                return new LoginCommand(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}
