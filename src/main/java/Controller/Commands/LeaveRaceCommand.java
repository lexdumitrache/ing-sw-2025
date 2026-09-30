package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for voluntarily leaving the race during flight phase.
 * Players can abandon the race to limit losses.
 */
public class LeaveRaceCommand extends Command {
    
    /**
     * Constructs a new LeaveRaceCommand.
     *
     * @param playerName the name of the player leaving the race
     */
    public LeaveRaceCommand(String playerName) {
        super(playerName);
    }
    
    /**
     * Executes the leave race command by calling the controller's leaveRace method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters{
        controller.leaveRace(getPlayerName());
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {

                return new LeaveRaceCommand(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}