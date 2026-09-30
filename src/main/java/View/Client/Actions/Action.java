package View.Client.Actions;

import View.Client.ClientState;

import java.io.Serializable;

/**
 * Represents an action that can be executed by the client.
 * Actions are used to modify the client state and can be visualized.
 */
public interface Action extends Serializable {
    /**
     * Executes the action on the given client state.
     *
     * @param state The current state of the client.
     * @return The updated client state after executing the action.
     */
    ClientState execute(ClientState state);

    /**
     * Checks if the action should be visualized.
     * By default, actions are not visualized.
     *
     * @return true if the action should be visualized, false otherwise.
     */
    default boolean isVisualize() {
        return false;
    }

    /**
     * Gets the constructor for this action.
     * This method is used for serialization and deserialization of actions.
     *
     * @return The constructor for this action, or null if not applicable.
     */
    static ActionConstructor getConstructor() {
        return null;
    }
}
