package Controller.Commands;

import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Exceptions.InvalidMethodParameters;
import Model.Ship.Coordinates;

import java.util.List;
import java.util.Map;

/**
 * Command for using items like batteries or crew during adventure resolution.
 * Handles activation of shields, double cannons, and crew sacrifices.
 */
public class UseItemCommand extends Command {
    /** The type of item being used */
    private final ItemType itemType;
    /** The coordinates where the item is used */
    private final Coordinates coordinates;

    
    /**
     * Constructs a new UseItemCommand.
     *
     * @param playerName the name of the player using the item
     * @param itemType the type of item to use
     * @param coordinates the coordinates where to use the item
     * @param amount the amount of items to use
     */
    public UseItemCommand(String playerName, ItemType itemType,
                          Coordinates coordinates) {
        super(playerName);
        this.itemType = itemType;
        this.coordinates = coordinates;
    }
    
    /**
     * Executes the use item command by calling the controller's useItem method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        controller.useItem(getPlayerName(), itemType, coordinates);
    }
    
    /**
     * Gets the item type.
     *
     * @return the item type
     */
    public ItemType getItemType() {
        return itemType;
    }
    
    /**
     * Gets the coordinates.
     *
     * @return the coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    public static CommandConstructor getBatteriesConstructor() {
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

                return new UseItemCommand(username, ItemType.BATTERIES,
                        new Coordinates(row, column));
            }

            @Override
            public List<String> getArguments() {
                return List.of("row", "column");
            }
        };
    }

    public static CommandConstructor getCrewConstructor() {
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

                return new UseItemCommand(username, ItemType.CREW,
                        new Coordinates(row, column));
            }

            @Override
            public List<String> getArguments() {
                return List.of( "row", "column");
            }
        };
    }
}