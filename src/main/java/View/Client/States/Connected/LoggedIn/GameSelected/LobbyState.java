package View.Client.States.Connected.LoggedIn.GameSelected;

import Model.Game;
import Model.Player;
import Networking.Network;
import View.Client.Client;
import View.Client.States.Connected.LoggedIn.GameSelected.Playing.BuildingState;
import View.Client.States.Connected.LoggedIn.GameSelectedState;
import View.States.ViewPlayersState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the lobby state in a game where players can see who is connected.
 * This state allows players to view the list of connected players and start the game.
 */
public final class LobbyState extends GameSelectedState {
    public LobbyState(Network network, String username, Game game) {
        super(network, username, game);
        Client.view.setState(new ViewPlayersState());
    }

    /**
     * Starts the game by transitioning to the BuildingState.
     * This method is called when the player decides to start the game.
     *
     * @return A new BuildingState instance representing the game state.
     */
    @Override
    public BuildingState net_Start() {
        return new BuildingState(this.getNetwork(), this.getUsername(), this.getGame());
    }

    /**
     * Returns a string representation of the LobbyState.
     * This method is used for debugging and logging purposes.
     *
     * @return A string indicating the current state as "Lobby".
     */
    /*Visualizer*/
    @Override
    public void viewPlayers() {
        final List<String> names = new ArrayList<>();
        for (Player player : this.getGame().getPlayers()) {
            names.add(player.getName());
        }

        Client.view.showOptions("Players connected", names);
    }
}
