package View.Client.Actions;

import View.Client.ClientState;

/**
 * Represents an action that is executed when a login attempt fails.
 * This action updates the client state to reflect the failure of the login attempt.
 */
public class LoginFailedAction implements Action {

    /**
     * The username that was attempted to be logged in.
     */
    final String username;

    /**
     * Constructs a LoginFailedAction with the specified username.
     * @param username The username that was attempted to be logged in.
     */
    public LoginFailedAction(String username) {
        this.username = username;
    }

    /**
     * Executes the action by updating the client state to reflect a failed login attempt.
     * @param state The current client state.
     * @return The updated client state after the login failure.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.net_LoginFailed(username);
    }
}
