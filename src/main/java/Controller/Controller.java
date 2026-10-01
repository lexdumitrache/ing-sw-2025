package Controller;

import Controller.Commands.Command;
import Controller.Enums.*;
import Controller.Exceptions.*;
import Controller.PreMatchLobby.LogInState;
import Controller.PreMatchLobby.OffState;
// Import del modello
import Controller.Server.Server;
import Model.Enums.Direction;
import Model.Exceptions.InvalidMethodParameters;
import Model.Game;

import Model.Player;
import Model.Ship.Coordinates;
import Networking.Agent;
import Networking.Messages.ClientMessage;
import Networking.Messages.Handler;
import Networking.Network;
import View.Client.Actions.Action;
import View.Client.Actions.RejoinAction;
import View.Client.Actions.UpdateGameAction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Controller implements Agent {
    /** How often connections are checked while no command arrives. */
    private static final long CONNECTION_CHECK_MS = 1000;
    /** How long a disconnected player has to come back before the auto-pilot plays for them. */
    private static long autoPilotDelayMs = 15000;
    /** How long a game with nobody connected is kept, waiting for someone to rejoin. */
    private static final long ABANDONED_GAME_MS = 120000;

    private final Game model;
    private final Queue<Command> commandQueue= new LinkedList<>();
    private final MatchLevel matchLevel;
    private final int gameID;
    private Action queuedAction;

    /** Players whose connection was lost, with the time it was noticed. */
    private final Map<String, Long> disconnectedSince = new ConcurrentHashMap<>();
    private long nobodyConnectedSince = -1;

    public Controller(MatchLevel matchLevel, int GameID) {
        this.model = new Game(matchLevel);
        this.model.setState(new LogInState(this));
        this.matchLevel = matchLevel;
        this.gameID = GameID;

        //create the thread that reads and executes commands
        new Thread(this, "Controller#" + this.gameID).start();
    }

    // Getter for game model
    public Game getModel() {
        return model;
    }

    public int getGameID() {
        return gameID;
    }

    public void enqueueCommand(Command command) {
        synchronized (this.commandQueue) {
            this.commandQueue.add(command);
            this.commandQueue.notifyAll();
        }
    }

    public Command dequeueCommand() {
        synchronized (this.commandQueue) {
            // loop guards against spurious wakeups; null is still returned when a null (shutdown) command is enqueued
            while(this.commandQueue.isEmpty()){
                try{
                    this.commandQueue.wait();
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return this.commandQueue.poll();
        }
    }

    /**
     * Waits up to timeoutMs for a command.
     *
     * @return the command, or null if none arrived in time (or a null shutdown command was enqueued)
     */
    private Command pollCommand(long timeoutMs) {
        synchronized (this.commandQueue) {
            if (this.commandQueue.isEmpty()) {
                try {
                    this.commandQueue.wait(timeoutMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return this.commandQueue.poll();
        }
    }

    public MatchLevel getMatchLevel() {return matchLevel;}

    /* ------------------------------------------------------------ disconnections */

    /**
     * @return true if the player is in this game but their connection was lost
     */
    public boolean isDisconnected(String name) {
        return disconnectedSince.containsKey(name);
    }

    /**
     * Records that a player lost their connection. Before the game starts they are simply removed.
     */
    public void playerDisconnected(String name) {
        if (disconnectedSince.containsKey(name) || model.getPlayer(name) == null) {
            return;
        }
        System.out.println("Player " + name + " disconnected from game " + gameID);

        if (model.getState() instanceof LogInState) {
            try {
                model.getState().logout(name);
            } catch (InvalidCommand | InvalidParameters | InvalidMethodParameters e) {
                System.err.println("Could not remove " + name + " from the lobby: " + e.getMessage());
            }
            return;
        }
        disconnectedSince.put(name, System.currentTimeMillis());
    }

    /**
     * Gives a disconnected player their seat back on their new connection.
     *
     * @throws InvalidParameters if the player is not disconnected from this game or has no connection
     */
    public void reconnect(String name) throws InvalidParameters {
        if (!disconnectedSince.containsKey(name)) {
            throw new InvalidParameters(name + " is not disconnected from this game");
        }
        final Network network = Server.server == null ? null : Server.server.getNetwork(name);
        if (network == null || network.isDone()) {
            throw new InvalidParameters("No connection for " + name);
        }
        disconnectedSince.remove(name);
        network.send(new ClientMessage(new RejoinAction(this.model.clone())));
        new Handler<>(this, network).start();
        System.out.println("Player " + name + " rejoined game " + gameID);
    }

    /**
     * Notices players whose connection was lost, and closes the game if nobody has been connected for a while.
     *
     * @return true if something changed
     */
    private boolean checkConnections() {
        if (Server.server == null) {
            return false;
        }
        boolean changed = false;
        for (Player player : new ArrayList<>(model.getPlayers())) {
            final Network network = Server.server.getNetwork(player.getName());
            if ((network == null || network.isDone()) && !disconnectedSince.containsKey(player.getName())) {
                playerDisconnected(player.getName());
                changed = true;
            }
        }

        final boolean anyoneConnected = model.getPlayers().stream().anyMatch(p -> !disconnectedSince.containsKey(p.getName()));
        if (anyoneConnected || model.getPlayers().isEmpty()) {
            nobodyConnectedSince = -1;
        } else if (nobodyConnectedSince < 0) {
            nobodyConnectedSince = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - nobodyConnectedSince > ABANDONED_GAME_MS) {
            System.out.println("Closing game " + gameID + ": nobody has been connected for " + ABANDONED_GAME_MS / 1000 + "s");
            model.setState(new OffState(this));
        }
        return changed;
    }

    /**
     * Lets the auto-pilot play for the players who have been disconnected for longer than the grace period.
     *
     * @return true if it made a move
     */
    public boolean runAutoPilot() {
        final long now = System.currentTimeMillis();
        final Set<String> absent = new HashSet<>();
        disconnectedSince.forEach((name, since) -> {
            if (now - since >= autoPilotDelayMs) {
                absent.add(name);
            }
        });
        return !absent.isEmpty() && AutoPilot.play(this, absent);
    }

    /**
     * Changes how long a disconnected player has to come back before the auto-pilot plays for them (used by tests).
     */
    public static void setAutoPilotDelay(long delayMs) {
        autoPilotDelayMs = delayMs;
    }


    /*
    public void send(Map<String, Object> command) {model.getState().execute(this);}
    */


    public void login(String name) throws InvalidCommand, InvalidParameters {
        model.getState().login(name);
    }
    public void logout(String name) throws InvalidCommand, InvalidParameters, InvalidMethodParameters {
        model.getState().logout(name);
    }
    public void startGame(String name) throws InvalidCommand, InvalidParameters {
        model.getState().startGame(name);
    }
    public void getComponent(String name, int index) throws InvalidCommand, InvalidParameters {
        model.getState().getComponent(name, index);
    }
    public void reserveComponent(String name) throws InvalidCommand, InvalidParameters {
        model.getState().reserveComponent(name);
    }
    public void placeComponent(String name, ComponentOrigin origin, Coordinates coordinates, Direction orientation) throws InvalidCommand, InvalidParameters {
        model.getState().placeComponent(name, origin, coordinates, orientation);
    }
    public void lookDeck(String name, int index) throws InvalidCommand, InvalidParameters {
        model.getState().lookDeck(name, index);
    }
    public void flipHourGlass(String name) throws InvalidCommand, InvalidParameters {
        model.getState().flipHourGlass(name);
    }
    public void finishBuilding(String name, int position) throws InvalidCommand, InvalidParameters {
        model.getState().finishBuilding(name, position);
    }
    public void placeCrew(String name, Coordinates coordinates, CrewType type) throws InvalidCommand, InvalidParameters {
        model.getState().placeCrew(name, coordinates, type);
    }
    public void preBuiltShip(String name, int index) throws InvalidCommand, InvalidParameters {
        model.getState().preBuiltShip(name, index);
    }
    // Adventure Card resolution
    public void pickNextCard(String name) throws InvalidCommand, InvalidParameters, InvalidContextualAction, InvalidMethodParameters {
        model.getState().pickNextCard(name);
    }
    public void deleteComponent(String name, Coordinates coordinates) throws InvalidCommand, InvalidParameters, InvalidMethodParameters {
        model.getState().deleteComponent(name, coordinates);
    }
    public void leaveRace(String name) throws InvalidCommand, InvalidParameters {
        model.getState().leaveRace(name);
    }
    public void getReward(String name, RewardType rewardType) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        model.getState().getReward(name, rewardType);
    }
    public void moveGood(String name, Coordinates oldCoordinates, Coordinates newCoordinates, int oldIndex, int newIndex) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        model.getState().moveGood(name, oldCoordinates, newCoordinates, oldIndex, newIndex);
    }
    public void useItem(String name, ItemType itemType, Coordinates coordinates) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        model.getState().useItem(name, itemType, coordinates);
    }
    public void declaresDouble(String name, DoubleType doubleType, double amount) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        model.getState().declaresDouble(name, doubleType, amount);
    }
    public void end(String name) throws InvalidCommand, InvalidParameters, InvalidMethodParameters {
        model.getState().end(name);
    }
    public void choosePlanet(String name, String planetName) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        model.getState().choosePlanet(name, planetName);
    }
    public void skipReward(String name) throws InvalidCommand, InvalidParameters {
        model.getState().skipReward(name);
    }
    public void getGood(String name, int goodIndex, Coordinates coordinates, int CargoHoldIndex) throws InvalidCommand, InvalidParameters, InvalidContextualAction {
        model.getState().getGood(name, goodIndex, coordinates, CargoHoldIndex);
    }
    public void throwDices(String playerName) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        model.getState().throwDices(playerName);
    }

    /**
     * Sets an action to be queued for sending to clients.
     * */
    public synchronized void setQueuedAction(Action action){
        this.queuedAction = action;
    }

    /**
     * Sends the current game state and any queued action to all players.
     */
    public synchronized void sendAll() {
        final Game modelClone = this.model.clone();

        if(Server.server==null){
            return;
        }

        for(Player player : this.getModel().getPlayers()){
            if(disconnectedSince.containsKey(player.getName())){
                // a player who rejoins receives the whole game when they are back
                continue;
            }
            Network network = Server.server.getNetwork(player.getName());
            if(network != null && !network.isDone()) {

                network.send(new ClientMessage(
                        new UpdateGameAction(modelClone)
                ));
                if(this.queuedAction != null) {
                    network.send(new ClientMessage(this.queuedAction));
                }
            }
        }

        this.queuedAction = null;
    }

    /**
     * The main loop of the controller that processes commands.
     * It continues until the game state is marked as done.
     */
    public void run(){
        while(!this.model.getState().isDone() && !Thread.currentThread().isInterrupted()){
            // wake up regularly, even without commands, to notice lost connections
            final Command command = this.pollCommand(CONNECTION_CHECK_MS);
            boolean changed = false;

            if(command != null){
                changed = true;
                try {
                    command.execute(this);


                    this.model.setError(false);
                    this.model.setErrorMessage(null);

                    String playerName=command.getPlayerName();
                    Player currentPlayer=model.getPlayer(playerName);
                    if (currentPlayer != null) {
                        currentPlayer.setError(false);
                        currentPlayer.setErrorMessage(null);
                    }else{
                        System.out.println("Strange bug command didn't throw anything but could not find player "+playerName);
                    }
                } catch (InvalidCommand | InvalidParameters | InvalidMethodParameters | InvalidContextualAction e) {
                    this.reportError(command, e.getMessage());
                } catch (RuntimeException e) {
                    // an unexpected bug must not kill the game thread, otherwise every player gets stuck
                    System.err.println("Unexpected error in game " + this.gameID + " while executing " + command.getClass().getSimpleName());
                    e.printStackTrace(System.err);
                    this.reportError(command, "unexpected server error (" + e + ")");
                }

            }

            try {
                changed |= this.checkConnections();
                if (Server.server != null) {
                    changed |= this.runAutoPilot();
                }
            } catch (RuntimeException e) {
                System.err.println("Unexpected error in game " + this.gameID + " while handling disconnections");
                e.printStackTrace(System.err);
            }

            if (changed) {
                try {
                    this.sendAll();
                } catch (RuntimeException e) {
                    System.err.println("Unexpected error in game " + this.gameID + " while sending updates");
                    e.printStackTrace(System.err);
                }
            }
        }

        if(Server.server==null){return;}

        Server.server.destroyGame(this.gameID);
    }

    /**
     * Marks the model and the player who sent the command as being in error.
     */
    private void reportError(Command command, String message) {
        this.model.setError(true);
        String playerName=command.getPlayerName();
        Player currentPlayer=model.getPlayer(playerName);

        if(currentPlayer != null){
            this.model.setErrorMessage(playerName+" committed an error: "+message);
            currentPlayer.setError(true);
            currentPlayer.setErrorMessage("You committed an error: "+message);
        }else{
            this.model.setErrorMessage("Error: "+message);
        }
    }
}
