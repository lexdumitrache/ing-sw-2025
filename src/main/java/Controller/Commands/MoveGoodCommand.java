package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Ship.Coordinates;

import java.util.List;
import java.util.Map;

/**
 * Command for moving goods between cargo holds.
 * Used during cargo management phases.
 */
public class MoveGoodCommand extends Command {
    /** The coordinates of the source cargo hold */
    private final Coordinates oldCoordinates;
    /** The coordinates of the destination cargo hold */
    private final Coordinates newCoordinates;
    /** The index of the good in the source cargo hold */
    private final int oldIndex;
    /** The target index in the destination cargo hold */
    private final int newIndex;
    
    /**
     * Constructs a new MoveGoodsCommand.
     *
     * @param playerName the name of the player moving goods
     * @param oldCoordinates the source coordinates
     * @param newCoordinates the destination coordinates
     * @param oldIndex the source index
     * @param newIndex the destination index
     */
    public MoveGoodCommand(String playerName, Coordinates oldCoordinates,
                           Coordinates newCoordinates, int oldIndex, int newIndex) {
        super(playerName);
        this.oldCoordinates = oldCoordinates;
        this.newCoordinates = newCoordinates;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }
    
    /**
     * Executes the move goods command by calling the controller's moveGood method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        controller.moveGood(getPlayerName(), oldCoordinates, newCoordinates, oldIndex, newIndex);
    }
    
    /**
     * Gets the old coordinates.
     *
     * @return the old coordinates
     */
    public Coordinates getOldCoordinates() {
        return oldCoordinates;
    }
    
    /**
     * Gets the new coordinates.
     *
     * @return the new coordinates
     */
    public Coordinates getNewCoordinates() {
        return newCoordinates;
    }
    
    /**
     * Gets the old index.
     *
     * @return the old index
     */
    public int getOldIndex() {
        return oldIndex;
    }
    
    /**
     * Gets the new index.
     *
     * @return the new index
     */
    public int getNewIndex() {
        return newIndex;
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {
                final int oldRow;
                try{
                    oldRow = Integer.parseInt(args.get("oldRow"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the oldRow. Did you provide an Integer?");
                }

                final int oldColumn;
                try{
                    oldColumn = Integer.parseInt(args.get("oldColumn"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the oldColumn. Did you provide an Integer?");
                }

                final int newRow;
                try{
                    newRow = Integer.parseInt(args.get("newRow"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the newRow. Did you provide an Integer?");
                }

                final int newColumn;
                try{
                    newColumn = Integer.parseInt(args.get("newColumn"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the newColumn. Did you provide an Integer?");
                }

                final int oldIndex;
                try{
                    oldIndex = Integer.parseInt(args.get("oldIndex"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the oldIndex. Did you provide an Integer?");
                }

                final int newIndex;
                try{
                    newIndex = Integer.parseInt(args.get("newIndex"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the newIndex. Did you provide an Integer?");
                }

                return new MoveGoodCommand(username,
                        new Coordinates(oldRow, oldColumn),
                        new Coordinates(newRow, newColumn),
                        oldIndex, newIndex);
            }

            @Override
            public List<String> getArguments() {
                return List.of("oldRow", "oldColumn", "newRow", "newColumn", "oldIndex", "newIndex");
            }
        };
    }

    public static CommandConstructor getRemoveConstructor() {
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
                    column = Integer.parseInt(args.get("oldColumn"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the column. Did you provide an Integer?");
                }

                final int index;
                try{
                    index = Integer.parseInt(args.get("index"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the index. Did you provide an Integer?");
                }


                return new MoveGoodCommand(username,
                        new Coordinates(row, column),
                        null,
                        index, index);
            }

            @Override
            public List<String> getArguments() {
                return List.of("row", "column", "index");
            }
        };
    }
}