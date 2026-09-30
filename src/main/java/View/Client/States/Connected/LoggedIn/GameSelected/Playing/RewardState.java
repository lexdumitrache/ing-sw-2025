package View.Client.States.Connected.LoggedIn.GameSelected.Playing;

import Model.Game;
import Networking.Network;
import View.Client.Client;
import View.Client.States.Connected.LoggedIn.GameSelected.PlayingState;
import View.States.ViewRewardsState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the game where players can view their rewards.
 * This state extends the PlayingState and allows players to visualize their rewards.
 */
public class RewardState extends PlayingState {

    /**
     * Constructs a RewardState with the specified network, username, and game.
     * This constructor initializes the state and sets the view to ViewRewardsState.
     *
     * @param network The network connection for the game.
     * @param username The username of the player.
     * @param game The current game instance.
     */
    public RewardState(Network network, String username, Game game) {
        super(network, username, game);
        Client.view.setState(new ViewRewardsState());
    }

    /**
     * Returns the name of the state.
     * This method is used for debugging and logging purposes.
     *
     * @return A string representing the name of the state.
     */
    @Override
    public List<String> getAvailableCommands() {
        List<String> commands = new ArrayList<>();

        commands.add("ViewRewards");

        commands.addAll(super.getAvailableCommands());

        return commands;
    }

    /**
     * Executes the action to view rewards.
     * This method is called when the player chooses to view their rewards.
     *
     * @return The updated client state after executing the action.
     */
    /*Visualizer*/
    @Override
    public void viewRewards() {
        this.getGame().visualizeRewards();
    }
}
