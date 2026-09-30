package Controller;

import Controller.Enums.*;
import Controller.Exceptions.*;
import Model.Enums.Direction;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Coordinates;

import java.io.Serializable;
import java.util.List;

//import java.util.Map;

/**
 * Abstract class representing the state of the game.
 * Each state defines the behavior of the game at a specific point in time.
 * It provides methods for handling player actions and transitions between states.
 */
public abstract class State implements Serializable {

    private transient Controller controller;

    /** The player whose turn it is */
    private Player playerInTurn;

    /** Context for the state, used for contextual actions inside a card resolution*/
    protected transient Context context;

    public Controller getController() {
        return controller;
    }

    public State(Controller controller) {
        this.controller = controller;
        System.out.println(this.getClass().getSimpleName());
    }

    public State(Context context) {
        this.context = context;
        System.out.println(this.getClass().getSimpleName());
    }

    public void onEnter() {}

    public Player getPlayerInTurn() {
        return playerInTurn;
    }
    public void setPlayerInTurn(Player playerInTurn) {
        this.playerInTurn = playerInTurn;
    }

    public void login(String name) throws InvalidCommand, InvalidParameters{
        throw new InvalidCommand("Invalid command: login");
    }
    public void logout(String name) throws InvalidCommand, InvalidParameters, InvalidMethodParameters {
        throw new InvalidCommand("Invalid command: logout");
    }
    public void startGame(String Name) throws InvalidCommand, InvalidParameters{
        throw new InvalidCommand("Invalid command: startGame");
    }

    public void getComponent(String name, int index) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: getComponent");
    }
    public void reserveComponent(String name) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: reserveComponent");
    }
    public void placeComponent(String name, ComponentOrigin origin, Coordinates coordinates, Direction orientation) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: placeComponent");
    }
    public void lookDeck(String name, int index) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: lookDeck");
    }
    public void flipHourGlass(String name) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: flipHourGlass");
    }
    public void finishBuilding(String name, int position) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: finishBuilding");
    }
    public void placeCrew(String name, Coordinates coordinates, CrewType type) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: placeCrew");
    }



    // Adventure Card resolution
    public void pickNextCard(String name) throws InvalidCommand, InvalidParameters, InvalidContextualAction, InvalidMethodParameters {
        throw new InvalidCommand("Invalid command: pickNextCard");
    }

    public void deleteComponent(String name, Coordinates coordinates) throws InvalidCommand, InvalidParameters, InvalidMethodParameters {
        throw new InvalidCommand("Invalid command: deleteComponent");
    }
    public void leaveRace(String name) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: leaveRace");
    }
    public void getReward(String name, RewardType rewardType) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        throw new InvalidCommand("Invalid command: getReward");
    }
    //
    public void moveGood(String name, Coordinates oldCoordinates, Coordinates newCoordinates, int oldIndex, int newIndex) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        throw new InvalidCommand("Invalid command: moveGoods");
    }
    public void useItem(String name, ItemType itemType, Coordinates coordinates) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        throw new InvalidCommand("Invalid command: useItem");
    }
    public void declaresDouble(String name, DoubleType doubleType, double amount) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        throw new InvalidCommand("Invalid command: declaresDouble");
    }
    //ho finito di fare qualunque cosa stia facendo
    public void end(String name) throws InvalidCommand, InvalidParameters, InvalidMethodParameters {
        throw new InvalidCommand("Invalid command: end");
    }
    public void choosePlanet(String name, String planetName) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        throw new InvalidCommand("Invalid command: choosePlanet");
    }
    public void skipReward(String name) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: skipReward");
    }
    public void getGood(String name, int goodIndex, Coordinates coordinates, int CargoHoldIndex) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        throw new InvalidCommand("Invalid command: getGood");
    }
    public void throwDices(String playerName) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        throw new InvalidCommand("Invalid command: throwDices");
    }
    public void preBuiltShip(String name, int index) throws InvalidCommand, InvalidParameters {
        throw new InvalidCommand("Invalid command: preBuiltShip");
    }

    public boolean isDone() {
        return false;
    }

    public Context getContext() {
        return this.context;
    }

    public abstract List<String> getAvailableCommands();
}
