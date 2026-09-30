package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for looking at predictable adventure card decks during building phase.
 * Players can preview upcoming adventure cards (Level 2 only).
 */
public class LookDeckCommand extends Command {
    /** The index of the deck to look at */
    private final int index;
    
    /**
     * Constructs a new LookDeckCommand.
     *
     * @param playerName the name of the player looking at the deck
     * @param index the index of the deck to examine
     */
    public LookDeckCommand(String playerName, int index) {
        super(playerName);
        this.index = index;
    }
    
    /**
     * Executes the look deck command by calling the controller's lookDeck method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters {
        controller.lookDeck(getPlayerName(), index);
    }
    
    /**
     * Gets the deck index.
     *
     * @return the deck index
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

                return new LookDeckCommand(username, index);
            }

            @Override
            public List<String> getArguments() {
                return List.of("index");
            }
        };
    }
}