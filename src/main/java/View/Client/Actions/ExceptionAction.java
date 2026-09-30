package View.Client.Actions;

import View.Client.Client;
import View.Client.ClientState;

/**
 * Represents an action that handles exceptions by logging the error message.
 * This action is executed in the context of the client state.
 */
public class ExceptionAction implements Action {

    /**
     * The exception to be logged.
     */
    private final Exception e;

    /**
     * Constructs an ExceptionAction with the specified exception.
     *
     * @param e The exception to be logged.
     */
    public ExceptionAction(Exception e) {
        this.e = e;
    }

    /**
     * Executes the action by logging the exception message.
     *
     * @param state The current client state.
     * @return The updated client state, which remains unchanged in this case.
     */
    @Override
    public ClientState execute(ClientState state) {
        Client.view.log(e.getMessage());
        return state;
    }
}
