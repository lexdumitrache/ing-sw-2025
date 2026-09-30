package Controller.PreMatchLobby;

import Controller.Controller;
import Controller.State;

import java.util.List;

/**
 * Represents the OffState of the game, where no actions can be performed.
 * Enters when there are no more players in the game wheather because they have logged out or the game has ended.
 * This state is typically used when the game is not active or has ended.
 * Whenever game is in this state, controller will be deleted from available matches
 */
public class OffState extends State {

    /**
     * Constructor for the OffState.
     * Initializes the state with the given controller.
     *
     * @param controller The controller managing the game state.
     */
    public OffState(Controller controller) {
        super(controller);
    }

    /**
     * This method is called by the controller to check if the match is done.
     */
    @Override
    public boolean isDone() {
        return true;
    }

    /**
     * This method is called when the OffState is entered.
     * It performs any necessary actions when the game enters this state.
     */
    @Override
    public List<String> getAvailableCommands(){
        return List.of();
    }

}
