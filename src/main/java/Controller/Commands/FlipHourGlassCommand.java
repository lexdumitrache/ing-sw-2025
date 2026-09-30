package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for flipping the hourglass timer during building phase.
 * Advances the timer to the next phase (Level 2 only).
 */
public class FlipHourGlassCommand extends Command {
    
    /**
     * Constructs a new FlipHourGlassCommand.
     *
     * @param playerName the name of the player flipping the hourglass
     */
    public FlipHourGlassCommand(String playerName) {
        super(playerName);
    }
    
    /**
     * Executes the flip hourglass command by calling the controller's flipHourGlass method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        controller.flipHourGlass(getPlayerName());
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {

                return new FlipHourGlassCommand(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}