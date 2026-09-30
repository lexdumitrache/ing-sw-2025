package View.Client.Actions;

import View.Client.ClientState;

/**
 * Represents an action to be executed when a user successfully logs in.
 * This action updates the client state to reflect a successful login.
 */
public class LoginSuccessAction implements Action {

    /**
     * The username of the user who has successfully logged in.
     */
    private final String username;

    /**
     * Constructs a LoginSuccessAction with the specified username.
     * @param username The username of the user who has successfully logged in.
     */
    public LoginSuccessAction(String username){
        this.username = username;
    }

    /**
     * Executes the login success action by updating the client state.
     * @param state The current client state.
     * @return The updated client state after a successful login.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.net_LoginSuccess(username);
    }
}
