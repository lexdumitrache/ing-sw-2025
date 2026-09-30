package View.Client.States.Connected;

import Networking.Messages.LoginMessage;
import Networking.Network;
import View.Client.ClientState;
import View.Client.States.ConnectedState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of a client that is connected to the server and is in the process of logging in.
 * This state allows the client to perform login actions and provides a list of available commands.
 */
public final class LoginState extends ConnectedState {
    /**
     * Constructs a LoginState with the specified network.
     *
     * @param network The network associated with this client state.
     */
    public LoginState(Network network) {
        super(network);
    }

    /**
     * Returns a list of available commands in the login state.
     * This includes the "Login" command and any commands available in the parent class.
     *
     * @return A list of strings representing available commands.
     */
    @Override
    public List<String> getAvailableCommands() {
        final List<String> actions = new ArrayList<>();

        actions.add("Login");
        actions.addAll(super.getAvailableCommands());

        return actions;
    }

    /**
     * Attempts to log in the user with the specified username.
     * If the login is successful, it returns a ClientState representing the logged-in state.
     * If the login is not confirmed, it returns an UnconfirmedLoginState.
     *
     * @param username The username of the user attempting to log in.
     * @return A ClientState representing the result of the login attempt.
     */
    @Override
    public ClientState login(String username){
        final ClientState sendResult = this.send(new LoginMessage(username));

        if(sendResult.isDone()){
            return sendResult;
        }

        return new UnconfirmedLoginState(this.getNetwork(), username);
    }
}
