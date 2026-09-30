package View.Client.States.Connected.LoggedIn;

import Model.Game;
import Networking.Messages.LeaveGameMessage;
import Networking.Network;
import View.Client.Client;
import View.Client.ClientState;
import View.Client.States.Connected.LoggedInState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a state in which a game has been selected by the user.
 * This state extends the LoggedInState and provides functionality
 * for handling game-specific actions and updates.
 */
public abstract class GameSelectedState extends LoggedInState {

    /**
     * The game that has been selected by the user.
     * This field is used to store the current game state.
     */
    private Game game;

    /**
     * Constructs a GameSelectedState with the specified network, username, and game.
     *
     * @param network  The network instance used for communication.
     * @param username The username of the user in this state.
     * @param game     The game that has been selected by the user.
     */
    public GameSelectedState(Network network, String username, Game game) {
        super(network, username);
        this.game = game;
    }


    /**
     * Returns the current game instance associated with this state.
     *
     * @return The game that has been selected by the user.
     */
    public final Game getGame() {
        return game;
    }


    /**
     * Handles the action of leaving the current game.
     * If the username matches the current user's username, it sends a leave game message
     * and returns a new GameSelectionState. Otherwise, it returns the current state.
     *
     * @param username The username of the user attempting to leave the game.
     * @return A new ClientState representing the game selection state or the current state.
     */
    @Override
    public ClientState net_Leave(String username) {
        if(!Objects.equals(username, this.getUsername())) {
            return this;
        }

        final ClientState sendResult;

        if((sendResult = this.send(new LeaveGameMessage())).isDone()){
            return sendResult;
        }

        return new GameSelectionState(this.getNetwork(), this.getUsername());
    }

    /**
     * Updates the game state with the provided game instance.
     * This method is used to refresh the game state in the client.
     *
     * @param game The new game instance to update the state with.
     * @return The updated ClientState with the new game.
     */
    @Override
    public ClientState updateGame(Game game){
        this.game = game;

        String errors = null;
        final String error, playerError;
        if((error = game.getErrorMessage()) != null){
            errors = "\u001B[33m" + error;
        }if((playerError = game.getPlayer(this.getUsername()).getErrorMessage()) != null){
            errors = (errors != null) ? errors + "\n" + "\u001B[31m" + playerError : "\u001B[31m" + playerError;
        }
        Client.view.log(errors);

        return this;
    }

    /**
     * Returns a list of available commands for the current game state.
     * This method combines the available commands from the game state
     * with those from the parent class.
     *
     * @return A list of strings representing the available commands.
     */
    @Override
    public List<String> getAvailableCommands() {
        final List<String> commands = new ArrayList<>(game.getState().getAvailableCommands());

        commands.addAll(super.getAvailableCommands());

        return commands;
    }
}
