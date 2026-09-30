package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for selecting a pre-built ship during the building phase.
 * Players can choose from a list of pre-defined ships.
 */
public class PreBuiltShipCommand extends Command {
    private final int index;

    /**
     * Constructs a new PreBuiltShipCommand.
     *
     * @param playerName the name of the player selecting the ship
     * @param index the index of the pre-built ship to select
     */
    public PreBuiltShipCommand(String playerName, int index) {
        super(playerName);
        this.index = index;
    }

    /**
     * Executes the pre-built ship command by calling the controller's preBuiltShip method.
     *
     * @param controller the controller to execute the command on
     * @throws InvalidCommand if the command is invalid
     * @throws InvalidParameters if the parameters are invalid
     */
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        controller.preBuiltShip(getPlayerName(), index);
    }

    /**
     * Gets the index of the pre-built ship.
     *
     * @return the index of the pre-built ship
     */
    public int getIndex() {
        return index;
    }

    /**
     * Provides a constructor for creating PreBuiltShipCommand instances from command arguments.
     *
     * @return a CommandConstructor for PreBuiltShipCommand
     */
    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, java.util.Map<String, String> args) throws IllegalArgumentException {
                final int index;
                try {
                    index = Integer.parseInt(args.get("index"));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Could not parse the index. Did you provide an Integer?");
                }
                return new PreBuiltShipCommand(username, index);

            }

            @Override
            public List<String> getArguments() {
                return List.of("index");
            }
        };
    }


}
