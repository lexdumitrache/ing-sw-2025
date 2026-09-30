package View.Client.Actions;

import Model.Game;
import View.Client.ClientState;

/**
 * Represents an action to update the game state in the client.
 * This action is used to modify the current game state with a new game instance.
 */
public class UpdateGameAction implements Action {

    /**
     * The game instance to update the client state with.
     */
    private final Game game;

    /**
     * Constructs an UpdateGameAction with the specified game.
     *
     * @param game The game instance to update the client state with.
     */
    public UpdateGameAction(Game game) {
        this.game = game;
    }

    /**
     * Gets the game instance associated with this action.
     *
     * @return The game instance to update the client state with.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.updateGame(this.game);
    }
}
