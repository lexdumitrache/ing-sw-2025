package View.Client.Actions;

import Model.Game;
import View.Client.ClientState;

/**
 * Sent to a player who logged in again after losing their connection: puts them back into their game.
 */
public class RejoinAction implements Action {

    private final Game game;

    public RejoinAction(Game game) {
        this.game = game;
    }

    @Override
    public ClientState execute(ClientState state) {
        return state.net_Rejoin(game);
    }
}
