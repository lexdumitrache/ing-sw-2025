package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Exceptions.InvalidMethodParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for picking the next adventure card during flight phase.
 * The leader draws and resolves the next card from the adventure deck.
 */
public class PickNextCardCommand extends Command {
    
    /**
     * Constructs a new PickNextCardCommand.
     *
     * @param playerName the name of the player (leader) picking the card
     */
    public PickNextCardCommand(String playerName) {
        super(playerName);
    }
    
    /**
     * Executes the pick next card command by calling the controller's pickNextCard method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) {
        try {
            controller.pickNextCard(getPlayerName());
        } catch (InvalidCommand | InvalidParameters | InvalidContextualAction | InvalidMethodParameters e) {
            System.err.println("Failed to pick next card: " + e.getMessage());
        }
    }

    public static CommandConstructor getConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {

                return new PickNextCardCommand(username);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}