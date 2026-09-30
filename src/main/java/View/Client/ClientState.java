package View.Client;

import Controller.Enums.MatchLevel;
import Model.Game;
import Model.Ship.Coordinates;
import Networking.Messages.Message;

import java.util.List;

/**
 * Represents the state of the client in a networked game.
 * This interface defines the methods that can be invoked on the client state,
 * including actions for connecting, creating games, joining games, and handling network messages.
 */
public interface ClientState {

    /**
     * Returns a list of available commands that can be executed in this state.
     * This method provides the commands that the user can perform based on the current state.
     *
     * @return A list of strings representing the available commands.
     */
    default List<String> getAvailableCommands() {
        return List.of("Stop");
    }

    /**
     * Returns the username associated with this client state.
     * This method provides access to the username of the player currently in this state.
     *
     * @return The username of the player.
     */
    default String getUsername() {
        throw new UnsupportedOperationException("Cannot invoke getUsername on " + this.getClass().getName());
    }

    /**
     * Returns the network associated with this client state.
     * This method provides access to the network connection used by the client.
     *
     * @return The network instance associated with this client state.
     */
    default ClientState chooseProtocol(String choice){
        throw new UnsupportedOperationException("Cannot invoke chooseProtocol on " + this.getClass().getName());
    }

    /**
     * Connects to the server with the specified hostname and port.
     * This method initiates a connection to the game server.
     *
     * @param hostname The hostname of the server to connect to.
     * @param port     The port number of the server to connect to.
     * @return A new ClientState representing the connection state.
     */
    default ClientState connect(String hostname, Integer port){
        throw new UnsupportedOperationException("Cannot invoke connect on " + this.getClass().getName());
    }

    /**
     * Creates a new game with the specified match level.
     * This method sends a request to create a game and returns the resulting state.
     *
     * @param matchLevel The level of the match to be created.
     * @return A new ClientState representing the game creation state.
     */
    default ClientState create(MatchLevel matchLevel) {
        throw new UnsupportedOperationException("Cannot invoke create on " + this.getClass().getName());
    }

    /**
     * Joins an existing game with the specified game ID.
     * This method sends a request to join a game and returns the resulting state.
     *
     * @param gameId The ID of the game to join.
     * @return A new ClientState representing the join game state.
     */
    default ClientState join(int gameId){
        throw new UnsupportedOperationException("Cannot invoke join on " + this.getClass().getName());
    }

    /**
     * Logs in the user with the specified username.
     * This method sends a login request and returns the resulting state.
     *
     * @param username The username of the user to log in.
     * @return A new ClientState representing the login state.
     */
    default ClientState login(String username) {
        throw new UnsupportedOperationException("Cannot invoke login on " + this.getClass().getName());
    }

    /**
     * Sends a message to the server.
     * This method is used to send various types of messages based on the current state.
     *
     * @param message The message to be sent.
     * @return A new ClientState representing the state after sending the message.
     */
    default ClientState send(Message message){
        throw new UnsupportedOperationException("Cannot invoke send on " + this.getClass().getName());
    }

    /**
     * Stops the current client state.
     * This method is used to terminate the current state and return a new state indicating completion.
     *
     * @return A new ClientState representing the stopped state.
     */
    default ClientState stop() {
        return new ClientState() {
            @Override
            public boolean isDone() {
                return true;
            }
        };
    }

    /**
     * Checks if the current client state is done.
     * This method indicates whether the current state has completed its operations.
     *
     * @return true if the state is done, false otherwise.
     */
    default boolean isDone(){
        return false;
    }

    /**
     * Returns a string representation of the current client state.
     * This method provides a description of the current state for debugging or logging purposes.
     *
     * @return A string representing the current client state.
     */
    /*Received from network*/
    default ClientState net_Fly() {
        throw new UnsupportedOperationException("Cannot invoke fly on " + this.getClass().getName());
    }

    /**
     * Handles the failure of joining a game.
     * This method is invoked when the client fails to join a game.
     *
     * @return A new ClientState representing the state after a failed join attempt.
     */
    default ClientState net_JoinFailed() {
        throw new UnsupportedOperationException("Cannot invoke joinFailed on " + this.getClass().getName());
    }

    /**
     * Handles the successful joining of a game.
     * This method is invoked when the client successfully joins a game.
     *
     * @param game The game that has been joined.
     * @return A new ClientState representing the state after joining the game.
     */
    default ClientState net_JoinSuccess(Game game){
        throw new UnsupportedOperationException("Cannot invoke joinSuccess on " + this.getClass().getName());
    }

    /**
     * Handles the action of leaving a game.
     * This method is invoked when the client leaves a game with the specified username.
     *
     * @param username The username of the user leaving the game.
     * @return A new ClientState representing the state after leaving the game.
     */

    default ClientState net_Leave(String username) {
        throw new UnsupportedOperationException("Cannot invoke leave on " + this.getClass().getName());
    }

    /**
     * Handles the failure of logging in.
     * This method is invoked when the client fails to log in with the specified username.
     *
     * @param username The username that failed to log in.
     * @return A new ClientState representing the state after a failed login attempt.
     */
    default ClientState net_LoginFailed(String username){
        throw new UnsupportedOperationException("Cannot invoke loginFailed on " + this.getClass().getName());
    }

    /**
     * Handles the successful login of a user.
     * This method is invoked when the client successfully logs in with the specified username.
     *
     * @param username The username that has successfully logged in.
     * @return A new ClientState representing the state after a successful login.
     */
    default ClientState net_LoginSuccess(String username){
        throw new UnsupportedOperationException("Cannot invoke loginSuccess on " + this.getClass().getName());
    }

    /**
     * Handles the action of rewarding the client.
     * This method is invoked to process rewards in the client state.
     *
     * @return A new ClientState representing the state after processing rewards.
     */

    default ClientState net_Reward() {
        throw new UnsupportedOperationException("Cannot invoke reward on " + this.getClass().getName());
    }

    /**
     * Handles the action of starting the client.
     * This method is invoked to initiate the client state.
     *
     * @return A new ClientState representing the started state.
     */
    default ClientState net_Start() {
        throw new UnsupportedOperationException("Cannot invoke start on " + this.getClass().getName());
    }

    default ClientState net_ViewCardDeck(String username) {
        throw new UnsupportedOperationException("Cannot invoke viewCardDeck on " + this.getClass().getName());
    }

    /**
     * Updates the game state with the provided game instance.
     * This method is used to refresh the game state in the client.
     *
     * @param game The new game instance to update the state with.
     * @return The updated ClientState with the new game.
     */
    default ClientState updateGame(Game game){
        throw new UnsupportedOperationException("Cannot invoke updateGame on " + this.getClass().getName());
    }

    /**
     * Updates the list of games available in the client state.
     * This method is used to refresh the list of games based on the provided game IDs.
     *
     * @param newGamesList An array of integers representing the new list of game IDs.
     * @return The updated ClientState with the new games list.
     */
    default ClientState updateList(Integer[] newGamesList) {
        throw new UnsupportedOperationException("Cannot invoke updateList on " + this.getClass().getName());
    }

    /*Visualizations*/

    /**
     * Lists the available games in a user-friendly format.
     * This method collects the game IDs from the gamesList and displays them using the Client's view.
     */
    default void list(){
        throw new UnsupportedOperationException("Cannot invoke list on " + this.getClass().getName());
    }

    /**
     * Views the card associated with the current game.
     * This method is used to display the card that represents the current game state.
     */
    default void viewCard() {
        throw new UnsupportedOperationException("Cannot invoke viewCard on " + this.getClass().getName());
    }

    default void viewCardDeck() {
        throw new UnsupportedOperationException("Cannot invoke viewCardDeck on " +  this.getClass().getName());
    }

    /**
     * Views the component at the specified coordinates for the given username.
     * This method is used to display a specific component in the game based on its coordinates.
     *
     * @param username    The username of the player whose component is being viewed.
     * @param coordinates The coordinates of the component to be viewed.
     */
    default void viewComponent(String username, Coordinates coordinates){
        throw new UnsupportedOperationException("Cannot invoke viewComponent on " + this.getClass().getName());
    }

    /**
     * Views the flight board of the current game.
     * This method is used to display the flight board, which contains information about the current game state.
     */
    default void viewFlightBoard(){
        throw new UnsupportedOperationException("Cannot invoke viewFlightBoard on " + this.getClass().getName());
    }

    /**
     * Views the players in the current game.
     * This method is used to display the list of players currently in the game.
     */
    default void viewPlayers(){
        throw new UnsupportedOperationException("Cannot invoke viewPlayers on " + this.getClass().getName());
    }

    /**
     * Views the rewards available in the current game.
     * This method is used to display the rewards that can be claimed or viewed.
     */
    default void viewRewards(){
        throw new UnsupportedOperationException("Cannot invoke viewRewards on " + this.getClass().getName());
    }

    /**
     * Views the ship board of the player with the specified username.
     * If the player does not exist, it sets the view state to ViewFlightBoardState.
     *
     * @param username The username of the player whose ship board is being viewed.
     */
    default void viewShipBoard(String username){
        throw new UnsupportedOperationException("Cannot invoke viewShipBoard on " + this.getClass().getName());
    }

    /**
     * Views the tiles in the current game.
     * This method is used to display the tiles available in the game.
     */
    default void viewTiles(){
        throw new UnsupportedOperationException("Cannot invoke viewShipBoard on " + this.getClass().getName());
    }
}
