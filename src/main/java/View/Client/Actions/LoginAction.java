package View.Client.Actions;

import View.Client.ClientState;

import java.util.List;
import java.util.Map;

/**
 * Represents an action to log in a user with a specified username.
 * This action is executed on the client state to perform the login operation.
 */
public class LoginAction implements Action {

    /**
     * The username of the user attempting to log in.
     */
    private final String username;

/**
     * Constructs a LoginAction with the specified username.
     * @param username The username of the user attempting to log in.
     */
    public LoginAction(String username) {
        this.username = username;
    }

    /**
     * Executes the login action on the given client state.
     * This method attempts to log in the user with the specified username.
     *
     * @param state The current client state.
     * @return The updated client state after attempting to log in.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.login(username);
    }

    /**
     * Returns a string representation of the login action.
     * This method is used for debugging or logging purposes.
     *
     * @return A string indicating the login action with the username.
     */
    public static ActionConstructor getConstructor() {
        return new ActionConstructor() {

            @Override
            public Action create(Map<String, String> args) throws IllegalArgumentException {
                if(args.get("username") == null){
                    throw new IllegalArgumentException("Username is required");
                }
                return new LoginAction(args.get("username"));
            }

            @Override
            public List<String> getArguments() {
                return List.of("username");
            }
        };
    }
}
