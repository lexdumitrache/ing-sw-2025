package View.Client.States.Connected.LoggedIn;

import Model.Game;
import Networking.Network;
import View.Client.Client;
import View.Client.States.Connected.LoggedIn.GameSelected.LobbyState;
import View.Client.States.Connected.LoggedInState;
import View.States.ViewNothingState;

/**
 * Represents the state of a logged-in user who has selected a game but has not yet confirmed their selection.
 * This state is part of the client's connected states and handles the transition to the lobby state upon successful game join.
 */
public final class UnconfirmedSelectionState extends LoggedInState {
    public UnconfirmedSelectionState(Network network, String username) {
        super(network, username);
        Client.view.setState(new ViewNothingState());
    }

    /**
     * Handles the event when a user successfully logs in.
     * This method transitions the client state to the unconfirmed selection state.
     *
     * @return A new UnconfirmedSelectionState instance, indicating that the user has logged in and selected a game.
     */
    @Override
    public LobbyState net_JoinSuccess(Game game){
        return new LobbyState(this.getNetwork(), this.getUsername(), game);
    }

    /**
     * Handles the event when a user fails to join a game.
     * This method transitions the client state back to the game selection state.
     *
     * @return A new GameSelectionState instance, indicating that the user can select another game.
     */
    @Override
    public GameSelectionState net_JoinFailed(){
        return new GameSelectionState(this.getNetwork(), this.getUsername());
    }
}
