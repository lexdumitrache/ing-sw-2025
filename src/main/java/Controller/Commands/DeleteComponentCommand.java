package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;
import Model.Exceptions.InvalidMethodParameters;
import Model.Ship.Coordinates;

import java.util.List;
import java.util.Map;

/**
 * Command for deleting components from ship during damage resolution.
 * Used when components are destroyed by combat, meteors, or penalties.
 */
public class DeleteComponentCommand extends Command {
    /** The coordinates of the component to delete */
    private final Coordinates coordinates;
    
    /**
     * Constructs a new DeleteComponentCommand.
     *
     * @param playerName the name of the player whose component is being deleted
     * @param coordinates the coordinates of the component to delete
     */
    public DeleteComponentCommand(String playerName, Coordinates coordinates) {
        super(playerName);
        this.coordinates = coordinates;
    }
    
    /**
     * Executes the delete component command by calling the controller's deleteComponent method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidMethodParameters {
        controller.deleteComponent(getPlayerName(), coordinates);
    }
    
    /**
     * Gets the coordinates of the component to delete.
     *
     * @return the coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {
                final int row;
                try{
                    row = Integer.parseInt(args.get("row"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the row. Did you provide an Integer?");
                }

                final int column;
                try{
                    column = Integer.parseInt(args.get("column"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the column. Did you provide an Integer?");
                }

                return new DeleteComponentCommand(username, new Coordinates(row, column));
            }

            @Override
            public List<String> getArguments() {
                return List.of("row", "column");
            }
        };
    }
}