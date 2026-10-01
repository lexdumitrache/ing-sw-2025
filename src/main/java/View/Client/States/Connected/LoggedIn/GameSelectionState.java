package View.Client.States.Connected.LoggedIn;

import View.Client.States.Connected.LoggedIn.GameSelected.Playing.RewardState;
import View.Client.States.Connected.LoggedIn.GameSelected.Playing.FlightState;
import View.Client.States.Connected.LoggedIn.GameSelected.Playing.BuildingState;
import View.Client.States.Connected.LoggedIn.GameSelected.LobbyState;
import Controller.Server.GameSummary;
import Model.Game;
import Controller.Enums.MatchLevel;
import Networking.Messages.CreateGameMessage;
import Networking.Messages.JoinGameMessage;
import Networking.Messages.UpdateListMessage;
import Networking.Network;
import View.Client.Client;
import View.Client.ClientState;
import View.Client.States.Connected.LoggedInState;
import View.States.ViewGamesState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the client when the user is logged in and can select games.
 * This state allows the user to create or join games and view the list of available games.
 */
public final class GameSelectionState extends LoggedInState {
    /**
     * The list of available games represented by their IDs.
     * This array is updated when the server sends a new list of games.
     */
    private GameSummary[] gamesList = new GameSummary[0];

    /**
     * Constructs a GameSelectionState with the specified network and username.
     * This constructor initializes the state by sending an UpdateListMessage to fetch the list of games.
     *
     * @param network  The network connection used for communication.
     * @param username The username of the logged-in user.
     */
    public GameSelectionState(Network network, String username){
        super(network, username);

        this.send(new UpdateListMessage());

        Client.view.setState(new ViewGamesState());
    }

    /**
     * Returns the list of available games.
     * This method provides access to the current list of game IDs that the user can join or view.
     *
     * @return An array of integers representing the IDs of available games.
     */
    @Override
    public ClientState create(MatchLevel matchLevel){
        final ClientState sendResult = this.send(new CreateGameMessage(matchLevel));

        if(sendResult.isDone()){
            return sendResult;
        }

        return new UnconfirmedSelectionState(this.getNetwork(), this.getUsername());
    }

    /**
     * Joins a game with the specified game ID.
     * This method sends a JoinGameMessage to the server and returns a new state based on the response.
     *
     * @param gameId The ID of the game to join.
     * @return A new ClientState representing the result of the join operation.
     */
    @Override
    public ClientState join(int gameId){
        final ClientState sendResult = this.send(new JoinGameMessage(gameId));

        if(sendResult.isDone()){
            return sendResult;
        }

        return new UnconfirmedSelectionState(this.getNetwork(), this.getUsername());
    }

    /**
     * Updates the list of available games with a new list provided by the server.
     * This method replaces the current gamesList with the new list.
     *
     * @param newGamesList An array of integers representing the new list of game IDs.
     * @return The updated ClientState with the new games list.
     */
    public GameSummary[] getGamesList() {
        return this.gamesList;
    }

    /**
     * Puts the player back into the game they were playing before losing their connection,
     * in the client state matching the phase the game is in.
     */
    @Override
    public ClientState net_Rejoin(Game game) {
        final Controller.State phase = game.getState();
        final ClientState state;

        if (phase instanceof Controller.PreMatchLobby.LogInState) {
            state = new LobbyState(this.getNetwork(), this.getUsername(), game);
        } else if (phase != null && phase.getClass().getPackageName().equals("Controller.RealTimeBuilding")) {
            state = new BuildingState(this.getNetwork(), this.getUsername(), game);
        } else if (phase instanceof Controller.GamePhases.RewardsPhase) {
            state = new RewardState(this.getNetwork(), this.getUsername(), game);
        } else {
            state = new FlightState(this.getNetwork(), this.getUsername(), game);
        }
        final ClientState result = state.updateGame(game);
        Client.view.notify("Welcome back! You rejoined your game.");
        return result;
    }

    @Override
    public ClientState updateList(GameSummary[] newGamesList){
        this.gamesList = newGamesList;

        return this;
    }

    /**
     * Returns a list of available commands that can be executed in this state.
     * This method provides the commands related to game selection, such as updating the list, joining a game, and creating a game.
     *
     * @return A list of strings representing the available commands.
     */
    @Override
    public List<String> getAvailableCommands(){
        final List<String> list = new ArrayList<>();

        list.add("UpdateList");
        list.add("Join");
        list.add("Create");
        list.addAll(super.getAvailableCommands());

        return list;
    }

    /**
     * Lists the available games in a user-friendly format.
     * This method collects the game IDs from the gamesList and displays them using the Client's view.
     */
    /*Visualization*/
    @Override
    public void list(){
        final List<String> games = new ArrayList<>();
        for(GameSummary game : this.gamesList){
            games.add(game.id() + ": " + game.level() + ", " + String.join(", ", game.players())
                    + (game.started() ? " (in progress)" : game.canJoin() ? "" : " (full)"));
        }
        Client.view.showOptions("Games are ", games);
    }
}
