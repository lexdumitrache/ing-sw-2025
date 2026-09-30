package View.Client.States.Connected.LoggedIn.GameSelected;

import Model.Game;
import Model.Player;
import Model.Ship.Coordinates;
import Networking.Network;
import View.Client.Client;
import View.Client.States.Connected.LoggedIn.GameSelected.Playing.FlightState;
import View.Client.States.Connected.LoggedIn.GameSelectedState;
import View.States.ViewFlightBoardState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the game when it is currently being played.
 * This class extends GameSelectedState and provides methods to handle
 * actions and visualizations related to the game in progress.
 */
public abstract class PlayingState extends GameSelectedState {
    /**
     * Constructs a PlayingState with the specified network, username, and game.
     *
     * @param network  The network connection for the game.
     * @param username The username of the player.
     * @param game     The current game being played.
     */
    public PlayingState(Network network, String username, Game game) {
        super(network, username, game);
    }

    /**
     * Executes the action to fly in the game.
     * This method creates a new FlightState object representing the current flight state of the game.
     *
     * @return A FlightState object representing the current flight state.
     */
    @Override
    public FlightState net_Fly() {
        return new FlightState(this.getNetwork(), this.getUsername(), this.getGame());
    }

    /**
     * Returns a list of available commands for the current game state.
     * This includes commands for visualizing the flight board and ship,
     * as well as any additional visualizers available in the game state.
     *
     * @return A list of strings representing the available commands.
     */
    @Override
    public List<String> getAvailableCommands() {
        List<String> commands = new ArrayList<>();

        /*Visualizes*/
        commands.addAll(List.of("ViewFlightBoard", "ViewShip"));

        commands.addAll(Client.view.getState().getAvailableVisualizers());

        commands.addAll(super.getAvailableCommands());

        return commands;
    }

    /**
     * Returns a list of available visualizers for the current game state.
     * This includes commands for viewing the flight board and ship board,
     * as well as viewing specific components on the ship board.
     *
     * @return A list of strings representing the available visualizers.
     */
    /*Visualizers*/
    @Override
    public void viewFlightBoard() {
        this.getGame().getFlightBoard().visualize(this.getGame().getState().getPlayerInTurn());
    }


    /**
     * Views the ship board of the player with the specified username.
     * If the player does not exist, it sets the view state to ViewFlightBoardState.
     *
     * @param username The username of the player whose ship board is being viewed.
     */
    @Override
    public void viewShipBoard(String username) {
        final Player player = this.getGame().getPlayer(username);

        if(player == null){
            Client.view.setState(new ViewFlightBoardState());
            return;
        }

        player.getShipBoard().render(this.getGame().getLevel());
    }

    /**
     * Views a specific component of the ship board based on the provided coordinates.
     * This method retrieves the component from the player's ship board and prints its details.
     *
     * @param username    The username of the player whose ship board is being viewed.
     * @param coordinates The coordinates of the component to be viewed.
     */
    @Override
    public void viewComponent(String username, Coordinates coordinates){
        final Player player = this.getGame().getPlayer(username);

        if(player == null){
            Client.view.setState(new ViewFlightBoardState());
            return;
        }

        final String[] component = player.getShipBoard().getComponent(coordinates).renderBig();

        for(String s : component){
            System.out.println(s);
        }
    }
}
