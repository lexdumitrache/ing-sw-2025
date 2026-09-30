package Controller.Server;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.PreMatchLobby.OffState;
import Networking.Agent;
import Networking.Messages.PrintMessage;
import Networking.Network;
import Networking.RMI.RMIConnectionHandler;
import Networking.TCP.TCPConnectionHandler;
import Networking.Utils;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements Agent {
    public static Server server;
    private static final Map<String, Pair<String, Runnable>> availableActions = new HashMap<>();
    private final Set<Network> uninitialized = new HashSet<>();
    private final Map<Network, String> players = new ConcurrentHashMap<>();
    private final Map<Integer, Controller> games = new ConcurrentHashMap<>();

    private int nextGameId = 0;
    private final RMIConnectionHandler RMIConnectionHandler;
    private final TCPConnectionHandler TCPconnectionHandler;

    public static void main(String[] args) throws IOException {
        final String hostname = Utils.NetworkSelector(args);

        final String TCPPort = Utils.getOption("--tcp-port", args);

        final String RMIPort = Utils.getOption("--rmi-port", args);

        System.out.println("Starting server @" + hostname);

        try {
            server = new Server(hostname, TCPPort, RMIPort);
        } catch (IOException e) {
            System.err.println("Could not start Controller.Server");
            throw e;
        }

        server.run();

        server.stop();
    }

    public Server(final String hostname, final String TCPPort, final String RMIPort) throws IOException {
        this.RMIConnectionHandler = new RMIConnectionHandler(this, hostname, RMIPort);
        try {
            this.TCPconnectionHandler = new TCPConnectionHandler(this, hostname, TCPPort);
        } catch (IOException e) {
            this.RMIConnectionHandler.setDone();
            throw e;
        }
    }

    String[] command;
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Dummy server");
        System.out.println("Get available commands by typing \"help\"");

        availableActions.put("help", new Pair<>("prints this message", this::help));
        availableActions.put("list", new Pair<>("prints all players", this::list));
        availableActions.put("games", new Pair<>("prints all games", this::games));
        availableActions.put("send", new Pair<>("sends a message to all networks", this::send));
        availableActions.put("stop", new Pair<>("stops all network connections", ()->{}));

        do {
            System.out.print("> ");
            String line = scanner.nextLine();
            command = line.split(" ", 2);

            try{
                availableActions.get(command[0]).getValue().run();
            }catch(NullPointerException e){
                System.out.println("Unknown command: " + command[0]);
            }
        } while (!command[0].equalsIgnoreCase("stop"));
    }

    private void games() {
        Arrays.asList(this.getGameIds()).forEach(System.out::println);;
    }

    public void help (){
        for (String command : availableActions.keySet()) {
            System.out.println(command + ": " + availableActions.get(command).getKey());
        }
    }

    public void list(){
        names().forEach(System.out::println);
    }

    public void send (){
        final Set<Network> networks = players.keySet();
        for (Network network : networks) {
            if(!network.send(new PrintMessage(command[1]))){
                disconnect(network);
            }
        }
    }

    public synchronized void stop () {
        this.RMIConnectionHandler.setDone();
        this.TCPconnectionHandler.setDone();

        final Set<Network> networks;
        synchronized(this.uninitialized) {
            networks = new HashSet<>(this.uninitialized);
        }
        synchronized (this.players) {
            networks.addAll(this.players.keySet());
        }

        synchronized (this.games) {
            final Set<Integer> gameIds =  this.games.keySet();

            for (Integer gameId : gameIds){
                Controller game = this.games.get(gameId);
                game.getModel().setState(new OffState(game));
                game.enqueueCommand(null);
            }
        }

        for(Network network : networks){
            disconnect(network);
        }
    }

    public synchronized void disconnect (Network network) {
        network.setDone();

        synchronized(this.players) {
            if(this.players.containsKey(network)) {
                this.players.remove(network);
                return;
            }
        }
        synchronized(this.uninitialized) {
            this.uninitialized.remove(network);
        }
    }

    public synchronized void connect (Network network) {
        synchronized(this.uninitialized) {
            this.uninitialized.add(network);
        }
    }

    public synchronized boolean login (Network network, String username) {
        //network was already logged in somehow
        if(!this.uninitialized.contains(network) || this.players.containsKey(network)) {
            return false;
        }

        //username already used
        if(this.players.containsValue(username))
            return false;

        this.uninitialized.remove(network);
        this.players.put(network, username);

        return true;
    }

    public Set<String> names (){
        Set<String> names = new HashSet<>();

        final Set<Network> networks = players.keySet();
        for(Network network : networks){
            if(network.isDone()){
                disconnect(network);
            } else if (players.get(network) != null){
                names.add(players.get(network));
            }
        }

        return names;
    }

    public Integer[] getGameIds(){
        synchronized(games) {
            final Integer[] gameIds = new Integer[this.games.size()];

            int i = 0;
            for (int gameId : this.games.keySet()) gameIds[i++] = gameId;

            return gameIds;
        }
    }

    public Controller createGame(MatchLevel matchLevel){
        synchronized(this.games){
            final int gameId = this.nextGameId++;
            System.err.println("Creating new controller");

            final Controller game = new Controller(matchLevel, gameId);

            this.games.put(gameId, game);

            return game;
        }
    }

    public void destroyGame(int gameId) {
        synchronized(this.games) {
            System.err.println("Destroying game " + gameId);

            this.games.remove(gameId);
        }
    }

    public Controller getGame(int gameId) {
        synchronized (this.games) {
            return this.games.get(gameId);
        }
    }

    public String getUsername(Network network) {
        synchronized(this.players) {
            return players.get(network);
        }
    }

    public Network getNetwork(String username) {
        synchronized(this.players) {
            for(Network network : this.players.keySet()) {
                if(this.players.get(network).equals(username))
                    return network;
            }

            return null;
        }
    }
}