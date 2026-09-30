// StartGameCommand.java
package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for creating a new game session.
 * Only the first player can create a new game with specified difficulty level.
 */
public class StartGameCommand extends Command {

    /**
     * Constructs a new StartGameCommand.
     *
     * @param playerName the name of the player creating the game
     */
    public StartGameCommand(String playerName) {
        super(playerName);
    }

    /**
     * Executes the new game command by calling the controller's startGame method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        controller.startGame(getPlayerName());
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public StartGameCommand create(String username, Map<String, String> args) throws IllegalArgumentException {
                return new StartGameCommand(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}