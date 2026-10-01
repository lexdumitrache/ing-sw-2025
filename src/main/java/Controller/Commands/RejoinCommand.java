package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidParameters;
import Controller.Server.Server;
import Networking.Messages.Handler;
import Networking.Network;

import java.util.List;
import java.util.Map;

/**
 * Sent by the server when a player who lost their connection logs in again: gives them their seat back.
 */
public class RejoinCommand extends Command {

    public RejoinCommand(String playerName) {
        super(playerName);
    }

    @Override
    public void execute(Controller controller) throws InvalidParameters {
        final String name = this.getPlayerName();
        // the old connection may not have been noticed as lost yet
        controller.playerDisconnected(name);
        try {
            controller.reconnect(name);
        } catch (InvalidParameters e) {
            // give the connection back to the server, so the player can still use the game list
            final Network network = Server.server == null ? null : Server.server.getNetwork(name);
            if (network != null && !network.isDone()) {
                new Handler<>(Server.server, network).start();
            }
            throw e;
        }
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) {
                return new RejoinCommand(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}
