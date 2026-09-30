package View.Client.Actions;

import View.Client.ClientState;

/**
 * Represents an action to update the list of game IDs in the client state.
 * This action is executed to refresh the list of available games.
 */
public class UpdateListAction implements Action {

    /**
     * The new list of game IDs to update in the client state.
     * This array contains the IDs of the games that are currently available.
     */
    private final Integer[] newList;

    /**
     * Constructs an UpdateListAction with the specified new list of game IDs.
     * This constructor initializes the action with the provided list, which will be used to update the client state.
     *
     * @param newList An array of Integer representing the new list of game IDs.
     */
    public UpdateListAction(Integer[] newList) {
        this.newList = newList;
    }

    /**
     * Executes the action to update the client state with the new list of game IDs.
     * This method modifies the current client state by replacing the existing list with the new one.
     *
     * @param state The current state of the client.
     * @return The updated client state after executing the action.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.updateList(newList);
    }
}
