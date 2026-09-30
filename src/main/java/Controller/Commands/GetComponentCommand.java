package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for getting a component from the component pool during ship building phase.
 * Players can draw components from the shared pool to use in ship construction.
 */
public class GetComponentCommand extends Command {
    /** The index of the component in the pool */
    private final int index;
    
    /**
     * Constructs a new GetComponentCommand.
     *
     * @param playerName the name of the player getting the component
     * @param index the index of the component to retrieve
     */
    public GetComponentCommand(String playerName, int index) {
        super(playerName);
        this.index = index;
    }
    
    /**
     * Executes the get component command by calling the controller's getComponent method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        controller.getComponent(getPlayerName(), index);
    }
    
    /**
     * Gets the component index.
     *
     * @return the component index
     */
    public int getIndex() {
        return index;
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {
                final int index;
                try{
                    index = Integer.parseInt(args.get("index"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the index. Did you provide an Integer?");
                }

                return new GetComponentCommand(username, index);
            }

            @Override
            public List<String> getArguments() {
                return List.of("index");
            }
        };
    }
}