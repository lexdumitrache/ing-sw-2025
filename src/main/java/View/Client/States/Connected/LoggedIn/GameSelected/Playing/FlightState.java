package View.Client.States.Connected.LoggedIn.GameSelected.Playing;

import Controller.Context;
import Model.Game;
import Networking.Network;
import View.Client.Client;
import View.Client.States.Connected.LoggedIn.GameSelected.PlayingState;
import View.States.ViewFlightBoardState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the game when a player is in flight.
 * This state allows the player to view their card and perform actions related to flight.
 */
public final class FlightState extends PlayingState {

    /**
     * Constructs a FlightState with the specified network, username, and game.
     * This constructor initializes the state and sets the view to the flight board state.
     *
     * @param network The network connection for the game.
     * @param username The username of the player.
     * @param game The current game instance.
     */
    public FlightState(Network network, String username, Game game) {
        super(network, username, game);
        Client.view.setState(new ViewFlightBoardState());
    }


    /**
     * Returns a list of available commands for the flight state.
     * This method includes the "ViewCard" command and all commands from the parent class.
     *
     * @return A list of available commands for the flight state.
     */
    @Override
    public List<String> getAvailableCommands() {
        List<String> commands = new ArrayList<>();

        commands.add("ViewCard");

        commands.addAll(super.getAvailableCommands());

        return commands;
    }
    /**
     * Executes the command to view the player's card.
     * This method retrieves the card information from the game and prints it to the console.
     * It is used to visualize the current state of the player's card during flight.
     */
    /*Visualizer*/
    @Override
    public void viewCard(){
        for(String line : this.getGame().renderCard()){
            System.out.println(line);
        }
    }

    /**
     * Returns the context of the current game state.
     * This method provides the context for the flight state, which includes the game and player information.
     *
     * @return The context of the flight state.
     */
    @Override
    public RewardState net_Reward() {
        return new RewardState(this.getNetwork(), this.getUsername(), this.getGame());
    }
}
