package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Ship.Coordinates;

import java.util.List;
import java.util.Map;

/**
 * Command for getting a specific good and placing it in a cargo hold.
 * Used during cargo collection phases from planets or rewards.
 */
public class GetGoodCommand extends Command {
    /** The index of the good to get */
    private final int goodIndex;
    /** The coordinates of the target cargo hold */
    private final Coordinates coordinates;
    /** The index within the cargo hold to place the good */
    private final int cargoHoldIndex;
    
    /**
     * Constructs a new GetGoodCommand.
     *
     * @param playerName the name of the player getting the good
     * @param goodIndex the index of the good to get
     * @param coordinates the coordinates of the cargo hold
     * @param cargoHoldIndex the index within the cargo hold
     */
    public GetGoodCommand(String playerName, int goodIndex,
                          Coordinates coordinates, int cargoHoldIndex) {
        super(playerName);
        this.goodIndex = goodIndex;
        this.coordinates = coordinates;
        this.cargoHoldIndex = cargoHoldIndex;
    }
    
    /**
     * Executes the get good command by calling the controller's getGood method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        controller.getGood(getPlayerName(), goodIndex, coordinates, cargoHoldIndex);
    }
    
    /**
     * Gets the good index.
     *
     * @return the good index
     */
    public int getGoodIndex() {
        return goodIndex;
    }
    
    /**
     * Gets the coordinates.
     *
     * @return the coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }
    
    /**
     * Gets the cargo hold index.
     *
     * @return the cargo hold index
     */
    public int getCargoHoldIndex() {
        return cargoHoldIndex;
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {
                final int goodIndex;
                try{
                    goodIndex = Integer.parseInt(args.get("goodIndex"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the goodIndex. Did you provide an Integer?");
                }

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

                final int cargoHoldIndex;
                try{
                    cargoHoldIndex = Integer.parseInt(args.get("cargoHoldIndex"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the cargoHoldIndex. Did you provide an Integer?");
                }

                return new GetGoodCommand(username, goodIndex, new Coordinates(row, column), cargoHoldIndex);
            }

            @Override
            public List<String> getArguments() {
                return List.of("goodIndex", "row", "column", "cargoHoldIndex");
            }
        };
    }
}