package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Exceptions.InvalidMethodParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for throwing dice during various game events.
 * Used for determining meteor impacts, combat results, and other random events.
 */
public class ThrowDicesCommand extends Command {
    
    /**
     * Constructs a new ThrowDicesCommand.
     *
     * @param playerName the name of the player throwing the dice
     */
    public ThrowDicesCommand(String playerName) {
        super(playerName);
    }
    
    /**
     * Executes the throw dices command by calling the controller's throwDices method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        controller.throwDices(getPlayerName());
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {

                return new ThrowDicesCommand(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}