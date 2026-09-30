package View.Client.Actions;

import Controller.Commands.Command;
import Controller.Commands.CommandConstructor;
import Networking.Messages.ControllerMessage;
import Networking.Messages.UpdateListMessage;
import View.Client.Client;
import View.Client.ClientState;
import View.Client.Visualizes.ViewComponent;
import View.Client.Visualizes.ViewShipBoard;
import View.Client.Visualizes.Visualize;
import View.States.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface for creating actions in the client.
 * Each action can be constructed with a set of arguments.
 */
public interface ActionConstructor {
    /**
     * Creates an action based on the provided arguments.
     *
     * @param args A map of arguments required to create the action.
     * @return An instance of Action that can be executed in the client state.
     * @throws IllegalArgumentException if the arguments are invalid for the action.
     */
    Action create(Map<String, String> args) throws IllegalArgumentException;

    /**
     * Returns a list of argument names required to create the action.
     * This is used for generating user interfaces or documentation.
     *
     * @return A list of argument names, or null if the action does not require arguments.
     */
    default List<String> getArguments(){
        return null;
    }

    /**
     * A static map that holds action constructors for various actions.
     * This map is initialized lazily to avoid unnecessary memory usage.
     */
    Map<String, ActionConstructor> actionConstructors = new HashMap<>();

    /**
     * Returns a map of action constructors, initializing it if it is empty.
     * This method is used to retrieve the available actions and their constructors.
     *
     * @return A map where keys are action names and values are ActionConstructor instances.
     */
    static Map<String, ActionConstructor> getActionConstructors() {
        if(actionConstructors.isEmpty()) {
            /*Actions*/
            actionConstructors.putAll(Map.of(
                    "Stop", _ -> ClientState::stop,
                    "RMI", _ -> state -> state.chooseProtocol("RMI"),
                    "TCP", _ -> state -> state.chooseProtocol("TCP"),
                    "Connect", ConnectAction.getConstructor(),
                    "Login", LoginAction.getConstructor(),
                    "Join", JoinAction.getConstructor(),
                    "Create", CreateGameAction.getConstructor(),
                    "UpdateList", _ -> (Action) state -> state.send(new UpdateListMessage())
            ));

            /*Visualizers*/
            actionConstructors.putAll(Map.of(
                    "BackToShip", _ -> (Visualize) () -> Client.view.getState().backToShip(),
                    "ViewCard", _ -> (Visualize) ViewCardState::new,
                    "ViewComponent", ViewComponent.getConstructor(),
                    "ViewFlightBoard", _ -> (Visualize) ViewFlightBoardState::new,
                    "ViewShip", ViewShipBoard.getConstructor(),
                    "ViewTiles", _ -> (Visualize) ViewTilesState::new,
                    "ViewRewards", _ -> (Visualize) ViewRewardsState::new
            ));

            /*Controller commands*/
            for(String command : CommandConstructor.getCommandConstructors().keySet()){
                CommandConstructor constructor = CommandConstructor.getCommandConstructor(command);
                actionConstructors.put(command, new ActionConstructor() {
                    @Override
                    public Action create(Map<String, String> args) throws IllegalArgumentException {
                        final Command command = constructor.create(Client.client.getState().getUsername(), args);
                        return state -> state.send(new ControllerMessage(command));
                    }

                    @Override
                    public List<String> getArguments() {
                        return constructor.getArguments();
                    }
                });
            }
        }

        return actionConstructors;
    }

    /**
     * Retrieves an ActionConstructor by its action name.
     * This method is used to find the constructor for a specific action.
     *
     * @param action The name of the action for which to retrieve the constructor.
     * @return An ActionConstructor instance corresponding to the action, or null if not found.
     */
    static ActionConstructor getActionConstructor(String action) {
        return getActionConstructors().get(action);
    }
}
