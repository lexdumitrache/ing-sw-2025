package View.Client.States.Connected;

import Networking.Network;
import View.Client.ClientState;
import View.Client.States.Connected.LoggedIn.GameSelectionState;
import View.Client.States.ConnectedState;

/**
 * Represents the state of the client when a user has logged in but has not yet confirmed their login.
 * This state is used to handle the transition from an unconfirmed login to a confirmed game selection state.
 */
public final class UnconfirmedLoginState extends ConnectedState {

    /**
     * The username of the user who has logged in.
     */
    private final String username;

    /**
     * Constructs an UnconfirmedLoginState with the specified network and username.
     *
     * @param network The network associated with this state.
     * @param username The username of the user who has logged in.
     */
    public UnconfirmedLoginState(Network network, String username) {
        super(network);
        this.username = username;
    }

    /**
     * Returns the username of the user who has logged in.
     *
     * @return The username of the logged-in user.
     */
    @Override
    public ClientState net_LoginSuccess(String username){
        if(!username.equals(this.username)){
            return this.stop();
        }

        return new GameSelectionState(this.getNetwork(), this.username);
    }

    /**
     * Handles the case when a login attempt fails.
     * This method is called when the user tries to log in but the login is unsuccessful.
     *
     * @param username The username that was attempted to be logged in.
     * @return A new UnconfirmedLoginState indicating that the login failed.
     */
    @Override
    public LoginState net_LoginFailed(String username) {
        return new LoginState(this.getNetwork());
    }
}
