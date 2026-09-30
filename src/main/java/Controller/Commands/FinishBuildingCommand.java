package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for finishing ship building and choosing starting position.
 * Completes the building phase for a player.
 */
public class FinishBuildingCommand extends Command {
    /** The desired starting position on the flight track */
    private final int position;
    
    /**
     * Constructs a new FinishBuildingCommand.
     *
     * @param playerName the name of the player finishing building
     * @param position the desired starting position
     */
    public FinishBuildingCommand(String playerName, int position) {
        super(playerName);
        this.position = position;
    }
    
    /**
     * Executes the finish building command by calling the controller's finishBuilding method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        controller.finishBuilding(getPlayerName(), position);
    }
    
    /**
     * Gets the starting position.
     *
     * @return the starting position
     */
    public int getPosition() {
        return position;
    }

    /**
     *
     * @return constructor for the FinishBuildingCommand
     */
    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {
                final int position;
                try{
                    position = Integer.parseInt(args.get("position"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the position. Did you provide an Integer?");
                }

                return new FinishBuildingCommand(username, position);
            }

            @Override
            public List<String> getArguments() {
                return List.of("position");
            }
        };
    }
}