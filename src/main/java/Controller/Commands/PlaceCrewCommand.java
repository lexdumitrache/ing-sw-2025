package Controller.Commands;

import Controller.Controller;
import Controller.Enums.CrewType;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;
import Model.Ship.Coordinates;

import java.util.List;
import java.util.Map;

/**
 * Command for placing crew members (aliens) in ship cabins.
 * Used during the crew placement phase after ship building.
 */
public class PlaceCrewCommand extends Command {
    /** The coordinates of the cabin to place crew in */
    private final Coordinates coordinates;
    /** The type of crew member to place */
    private final CrewType type;
    
    /**
     * Constructs a new PlaceCrewCommand.
     *
     * @param playerName the name of the player placing crew
     * @param coordinates the coordinates of the target cabin
     * @param type the type of crew member to place
     */
    public PlaceCrewCommand(String playerName, Coordinates coordinates, CrewType type) {
        super(playerName);
        this.coordinates = coordinates;
        this.type = type;
    }
    
    /**
     * Executes the place crew command by calling the controller's placeCrew method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller)throws InvalidCommand, InvalidParameters {
        controller.placeCrew(getPlayerName(), coordinates, type);
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
     * Gets the crew type.
     *
     * @return the crew type
     */
    public CrewType getType() {
        return type;
    }

    public static CommandConstructor getHumanConstructor() {
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

                return new PlaceCrewCommand(username, new Coordinates(row, column), CrewType.HUMAN);
            }


            @Override
            public List<String> getArguments() {
                return List.of("row", "column");
            }
        };
    }

    public static CommandConstructor getBrownConstructor() {
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

                return new PlaceCrewCommand(username, new Coordinates(row, column), CrewType.BROWN_ALIEN);
            }


            @Override
            public List<String> getArguments() {
                return List.of("row", "column");
            }
        };
    }

    public static CommandConstructor getPurpleConstructor() {
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

                return new PlaceCrewCommand(username, new Coordinates(row, column), CrewType.PURPLE_ALIEN);
            }


            @Override
            public List<String> getArguments() {
                return List.of("row", "column");
            }
        };
    }
}