package Controller.Commands;

import Controller.Controller;
import Controller.Enums.ComponentOrigin;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Direction;
import Model.Ship.Coordinates;

import java.util.List;
import java.util.Map;

/**
 * Command for placing a component on the ship board during building phase.
 * Handles component placement with proper connection validation.
 */
public class PlaceComponentCommand extends Command {
    /** The origin of the component (hand or reserved) */
    private final ComponentOrigin origin;
    /** The coordinates where to place the component */
    private final Coordinates coordinates;
    /** The orientation of the component */
    private final Direction orientation;
    
    /**
     * Constructs a new PlaceComponentCommand.
     *
     * @param playerName the name of the player placing the component
     * @param origin the origin of the component
     * @param coordinates the coordinates for placement
     * @param orientation the orientation of the component
     */
    public PlaceComponentCommand(String playerName, ComponentOrigin origin,
                                 Coordinates coordinates, Direction orientation) {
        super(playerName);
        this.origin = origin;
        this.coordinates = coordinates;
        this.orientation = orientation;
    }
    
    /**
     * Executes the place component command by calling the controller's placeComponent method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        controller.placeComponent(getPlayerName(), origin, coordinates, orientation);
    }
    
    /**
     * Gets the component origin.
     *
     * @return the component origin
     */
    public ComponentOrigin getOrigin() {
        return origin;
    }
    
    /**
     * Gets the placement coordinates.
     *
     * @return the coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }
    
    /**
     * Gets the component orientation.
     *
     * @return the orientation
     */
    public Direction getOrientation() {
        return orientation;
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public PlaceComponentCommand create(String username, Map<String, String> args) throws IllegalArgumentException {
                final int row, column;
                final ComponentOrigin origin;
                final Direction orientation;

                origin = ComponentOrigin.valueOf(args.get("origin").toUpperCase());

                try{
                    row = Integer.parseInt(args.get("row"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the row. Did you provide an Integer?");
                }

                try{
                    column = Integer.parseInt(args.get("column"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the column. Did you provide an Integer?");
                }

                orientation = Direction.valueOf(args.get("orientation").toUpperCase());

                return new PlaceComponentCommand(username, origin,
                        new Coordinates(row, column), orientation);
            }

            @Override
            public List<String> getArguments() {
                return List.of("origin", "row", "column", "orientation");
            }
        };
    }
}