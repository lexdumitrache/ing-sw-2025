package View.Client.Actions;

import Model.Game;
import View.Client.ClientState;

/**
 * Represents an action to be executed when a player successfully joins a game.
 * This action updates the client state to reflect the successful join operation.
 */
public class JoinSuccessAction implements Action {
    /**
     * The game that the player has successfully joined.
     */
    private final Game game;

    /**
     * Constructs a JoinSuccessAction with the specified game.
     *
     * @param game The game that the player has successfully joined.
     */
    public JoinSuccessAction(Game game) {
        this.game = game;
    }

    /**
     * Executes the action by updating the client state to reflect the successful join operation.
     *
     * @param state The current client state.
     * @return The updated client state after the join operation.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.net_JoinSuccess(game);
    }
}
