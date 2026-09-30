package Model;

import Controller.Context;
import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Board.CardDeck;
import Model.Board.FlightBoard;
import Model.Enums.Card;
import Model.Enums.CardLevel;
import Controller.Enums.MatchLevel;
import Controller.State;
import Model.Enums.ConnectorType;
import Model.Exceptions.InvalidMethodParameters;
import Model.Ship.Components.Cabin;
import Model.Ship.Components.Cannon;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.ShipBoard;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a game session of Galaxy Trucker.
 * Manages players, the tile pool, the flight board, and overall game state.
 */
public class Game implements Serializable, Cloneable {
    private final List<Player> players;
    private final MatchLevel level;
    private final SpaceshipComponent[] tiles;
    private final FlightBoard flightBoard;
    private State state;
    private boolean error = false;
    private final transient List<ShipBoard> preBuiltShips;
    private final transient Queue<SpaceshipComponent> centralCabins= new ArrayDeque<>();
    private String errorMessage=null;

    private List<String> renderCard;




    public static void main(String[] args) {

        int var=0;

        if (args.length >0) {
            try {
                var= Integer.parseInt(args[0]);
                System.out.println("Hai inserito il numero: " + var);
            } catch (NumberFormatException e) {
                System.out.println("L'argomento non è un intero valido.");
            }
        }




        // This variable can be set to 0, 1, or 2 to test different functionalities
        // 0: Pre-built ships
        // 1: Component loader and modification
        // 2: Players change position

        switch (var) {

            case 0:
                Game testGame = new Game(MatchLevel.TRIAL);
                Game testGame2 = new Game(MatchLevel.LEVEL2);

                List<ShipBoard> shipsL1=testGame.getPreBuiltShips();
                List<ShipBoard> shipsL2=testGame2.getPreBuiltShips();

                for (ShipBoard ship : shipsL1) {

                    System.out.println("Ship is valid:" + ship.validateShip());
                    ship.render(MatchLevel.TRIAL);

                }

                System.out.println("\n\n\n\n\n\n\n\n\n\n");
                System.out.println("Level 2 Ships:");

                System.out.println(shipsL2.isEmpty());

                for (ShipBoard ship : shipsL2) {

                    System.out.println("Ship is valid:" + ship.validateShip());
                    System.out.println("Ship can contain brown aliens: " + ship.getCondensedShip().canContainBrown());
                    System.out.println("Ship can contain purple aliens: " + ship.getCondensedShip().canContainPurple());
                    ship.render(MatchLevel.LEVEL2);


                }



                break;

            case 1:
                SpaceshipComponent[] tiless1 = ComponentLoader.loadComponents(false).toArray(new SpaceshipComponent[0]);

                //facciamo un po' di modifiche all'array e ai componenti per vedere se si riflettono in tiless2
                tiless1[0] = null;
                tiless1[1] = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
                Cabin cabinatest = (Cabin) tiless1[2];
                cabinatest.setVisible();


                SpaceshipComponent[] tiless2 = ComponentLoader.loadComponents(false).toArray(new SpaceshipComponent[0]);

                //facciamo display delle cose che abbiamo modificato

                for (int i = 0; i < 3; i++) {
                    System.out.printf("\n\n\n");
                    System.out.println("==========================");
                    System.out.println("Tile " + i + ": ");
                    if (tiless1[i] != null) {
                        tiless1[i].visualize();
                    } else {
                        System.out.println("null");
                    }
                    if (tiless2[i] != null) {
                        tiless2[i].visualize();
                    } else {
                        System.out.println("null");
                    }
                    System.out.println("==========================");
                }

                for (SpaceshipComponent s : tiless2) {
                    if (s != null) {
                        s.visualize();
                    } else {
                        System.out.println("null");
                    }
                }

                break;

            case 2:

                Game myGame = new Game(MatchLevel.TRIAL);
                myGame.addPlayer("Alice");
                Player alice = myGame.getPlayer("Alice");
                myGame.addPlayer("Bob");
                Player bob = myGame.getPlayer("Bob");
                myGame.addPlayer("Charlie");
                Player charlie = myGame.getPlayer("Charlie");
                myGame.addPlayer("Diana");
                Player diana = myGame.getPlayer("Diana");

                FlightBoard flightBoard = myGame.getFlightBoard();
                try {
                    flightBoard.setStartingPositions(alice, 1);
                    flightBoard.setStartingPositions(bob, 2);
                    flightBoard.setStartingPositions(charlie, 3);
                    flightBoard.setStartingPositions(diana, 4);
                } catch (InvalidMethodParameters e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Flight board after setting starting positions:");
                flightBoard.render();
                System.out.println("\n\n\n\n\n\n");

                try {
                    flightBoard.deltaFlightDays(alice, 12);
                    flightBoard.deltaFlightDays(bob, 12);
                    flightBoard.deltaFlightDays(charlie, 1);
                } catch (InvalidMethodParameters e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Flight board after delta flight days:");
                flightBoard.render();
                System.out.println("\n\n\n\n\n\n");

                try {
                    flightBoard.deltaFlightDays(alice, 2);
                    flightBoard.deltaFlightDays(alice, 1);

                } catch (InvalidMethodParameters e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Flight board after delta flight days forward for Alice:");
                flightBoard.render();
                System.out.println("\n\n\n\n\n\n");

                try {
                    flightBoard.deltaFlightDays(alice, -1);
                    flightBoard.deltaFlightDays(alice, -4);

                } catch (InvalidMethodParameters e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Flight board after delta flight days backward for Alice:");
                flightBoard.render();
                System.out.println("\n\n\n\n\n\n");

                try {
                    flightBoard.deltaFlightDays(diana, -1);

                } catch (InvalidMethodParameters e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Flight board after delta flight days backward for Diana:");
                flightBoard.render();
                System.out.println("\n\n\n\n\n\n");








                break;

            default:
                System.out.println("Invalid case");

                break;
        }
    }

    /**
     * Constructs a new Game instance based only on the match level.
     * Automatically loads components and adventure cards.
     *
     * @param level match difficulty level
     */
    public Game(MatchLevel level) {
        if (level == null)
            throw new IllegalArgumentException("Match level must not be null");

        this.players = new ArrayList<>();
        this.level = level;
        List<SpaceshipComponent> allTiles= ComponentLoader.loadComponents(false);

        this.centralCabins.addAll(allTiles.subList(0,4));
        allTiles.removeAll(centralCabins);

        Collections.shuffle(allTiles);
        this.tiles = allTiles.toArray(new SpaceshipComponent[0]);

        List<AdventureCardFilip> cards = AdventureCardLoader.loadAdventureCards(level, true);
        if (cards == null || cards.isEmpty()) {
            throw new IllegalStateException("Failed to load adventure cards");
        }

        preBuiltShips = PreBuildShipsLoader.loadPreBuiltShips(level);

        if (level == MatchLevel.TRIAL) {
            List<AdventureCardFilip> learnerCards = cards.stream()
                    .filter(c -> c.getLevel() == CardLevel.LEARNER)
                    .collect(Collectors.toList());
            
            if (learnerCards.size() < 8) {
                throw new IllegalStateException("Not enough LEARNER cards available. Found: " + learnerCards.size());
            }
            
            // Take only the first 8 cards
            learnerCards = learnerCards.subList(0, 8);
            CardDeck hiddenDeck = new CardDeck(learnerCards);
            this.flightBoard = new FlightBoard(hiddenDeck);
        } else {
            List<AdventureCardFilip> level1Cards = cards.stream()
                    .filter(c -> c.getLevel() == CardLevel.LEVEL_ONE)
                    .collect(Collectors.toList());
            List<AdventureCardFilip> level2Cards = cards.stream()
                    .filter(c -> c.getLevel() == CardLevel.LEVEL_TWO)
                    .collect(Collectors.toList());
            
            if (level1Cards.size() < 8 || level2Cards.size() < 8) {
                throw new IllegalStateException(String.format(
                    "Not enough cards for LEVEL2 game. Found: LEVEL_ONE=%d, LEVEL_TWO=%d", 
                    level1Cards.size(), level2Cards.size()));
            }

            List<CardDeck> pickableDecks = new ArrayList<>();

            // Creates 4 lists of AdventureCardFilip, each containg 1 card of level1 and 2 of level2
            List<AdventureCardFilip> peekable1 = level1Cards.subList(0, 1);
            peekable1.addAll(level2Cards.subList(0, 2));
            CardDeck peekableDeck1 = new CardDeck(peekable1);
            pickableDecks.add(peekableDeck1);

            List<AdventureCardFilip> peekable2 = level1Cards.subList(1, 2);
            peekable2.addAll(level2Cards.subList(2, 4));
            CardDeck peekableDeck2 = new CardDeck(peekable2);
            pickableDecks.add(peekableDeck2);

            List<AdventureCardFilip> peekable3 = level1Cards.subList(2, 3);
            peekable3.addAll(level2Cards.subList(4, 6));
            CardDeck peekableDeck3 = new CardDeck(peekable3);
            pickableDecks.add(peekableDeck3);


            List<AdventureCardFilip> hidden = level1Cards.subList(3, 4);
            hidden.addAll(level2Cards.subList(6, 8));
            CardDeck hiddenDeck = new CardDeck(hidden);



            this.flightBoard = new FlightBoard(hiddenDeck, pickableDecks);
        }
    }

    public List<ShipBoard> getPreBuiltShips() {
        return preBuiltShips;
    }

    public ShipBoard getPreBuiltShip(int index) {
        List<ShipBoard> allPreBuildShips = PreBuildShipsLoader.loadPreBuiltShips(level);
        if (index < 0 || index >= allPreBuildShips.size()) {
            throw new IndexOutOfBoundsException("Invalid pre-built ship index: " + index);
        }
        return allPreBuildShips.get(index);
    }

    public SpaceshipComponent[] getTiles() {
        return this.tiles;
    }

    public void addPlayer(String name) {
        Cabin centralCabin= (Cabin) centralCabins.poll();
        if (centralCabin == null) {
            throw new IllegalStateException("No more central cabins available");
        }
        this.players.add(new Player(name,centralCabin));

    }

    public void removePlayer(String name) {
        players.removeIf(p -> p.getName().equals(name));
    }

    public Player getPlayer(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public int rollDice() {
        return (int)(Math.random() * 6 + 1);
    }

    public SpaceshipComponent pickComponent(int index) {
        if (index < 0 || index >= tiles.length || tiles[index] == null)
            throw new IndexOutOfBoundsException("Invalid component index");
        SpaceshipComponent picked = tiles[index];
        tiles[index] = null;
        return picked;
    }

    public void addComponent(SpaceshipComponent component) {
        if (component == null)
            throw new IllegalArgumentException("Component cannot be null");
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == null) {
                tiles[i] = component;
                return;
            }
        }
        throw new IllegalStateException("No space left to add the component");
    }

    public List<SpaceshipComponent> viewVisibleComponents() {
        List<SpaceshipComponent> visible = new ArrayList<>();
        for (SpaceshipComponent c : tiles) {
            if (c != null && c.isVisible()) visible.add(c);
        }
        return visible;
    }

    public FlightBoard getFlightBoard() {
        return this.flightBoard;
    }

    public MatchLevel getLevel() {
        return this.level;
    }

    public void setState(State phase) {
        this.state = phase;
        if (phase!=null) {
            phase.onEnter();
        }
    }

    public State getState() {
        return state;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * TUI Tiles visualization method*/
    public void render(){
        // Prima stampiamo l'intestazione con i numeri delle colonne
        System.out.print("   "); // spazio per i numeri di riga
        for (int j = 0; j < 20; j++) {
            System.out.printf("   %2d  ", j); // numeri colonna centrati
        }
        System.out.println();

// Visualizzazione matrice 8x20 da array unidimensionale
        for (int i = 0; i < 8; i++) {
            // Prima otteniamo i disegni di tutti i componenti della riga i
            String[][] disegni = new String[20][];
            for (int j = 0; j < 20; j++) {
                // Conversione da coordinate 2D a indice 1D
                int indice = i * 20 + j;

                if (indice < tiles.length && tiles[indice] != null) {
                    if (tiles[indice].isVisible()) {
                        disegni[j] = tiles[indice].renderSmall();
                    } else {
                        disegni[j] = renderHidden(); // Rimuovi Arrays.toString()
                    }
                } else {
                    disegni[j] = renderEmpty(); // Rimuovi Arrays.toString()
                }
            }

            // Stampiamo riga per riga il disegno
            for (int riga = 0; riga < disegni[0].length; riga++) {
                // Stampiamo il numero di riga solo sulla riga centrale del componente
                if (riga == disegni[0].length / 2) {
                    System.out.printf("%2d ", i);
                } else {
                    System.out.print("   ");
                }

                // Stampiamo la griglia
                for (int j = 0; j < 20; j++) {
                    System.out.print(disegni[j][riga]);
                }
                System.out.println();
            }
        }

        String[] legenda = {
                "BAT - Battery compartment",
                "CAR - Cargo hold",
                "CAB - Cabin",
                "STR - Structural Module",
                "E1  - Single Engine",
                "E2  - Double Engine",
                "C1  - Single Cannon",
                "C2  - Double Cannon",
                "PAL - Purple Alien",
                "BAL - Brown Alien",
                "SH  - Shield",
                "↑   - Orientation"

                // Aggiungi altri elementi qui...
        };

// Stampa su 3 colonne
        for (int i = 0; i < legenda.length; i += 3) {
            // Prima colonna
            System.out.printf("%-30s", legenda[i]);

            // Seconda colonna (se esiste)
            if (i + 1 < legenda.length) {
                System.out.printf("%-30s", legenda[i + 1]);
            } else {
                System.out.printf("%-30s", "");
            }

            // Terza colonna (se esiste)
            if (i + 2 < legenda.length) {
                System.out.printf("%-25s", legenda[i + 2]);
            }

            System.out.println();
        }
    }

    public String[] renderHidden(){
        // Disegno vuoto con linee singole
        String[] righe = new String[3];
        righe[0] = "┌─────┐";
        righe[1] = "│  ?  │";
        righe[2] = "└─────┘";
        return righe;
    }

    public String[] renderEmpty() {
        // Disegno vuoto con linee singole
        String[] righe = new String[3];
        righe[0] = "┌─────┐";
        righe[1] = "│     │";
        righe[2] = "└─────┘";
        return righe;
    }

    @Override
    public Game clone(){
        return new Game(this);
    }

    private Game(Game old){
        this.players = new ArrayList<>();
        for(Player p : old.players){
            this.players.add(p.clone());
        }

        this.level = old.level;
        this.tiles = new SpaceshipComponent[old.tiles.length];

        for(int i = 0; i < old.tiles.length; i++){
            tiles[i] = (old.tiles[i] != null) ? old.tiles[i].clone() : null;
        }

        this.flightBoard = old.flightBoard.clone();
        this.state = old.state;
        final Context context = (old.state != null) ? old.state.getContext() : null;
        this.renderCard = (context != null) ? context.getRender() : new ArrayList<>();
        this.error = old.error;
        this.errorMessage = old.errorMessage;

        this.preBuiltShips = old.preBuiltShips;
    }

    public List<String> renderCard(){
        return this.renderCard;
    }

    /**
     * Displays a formatted table showing all players ordered by their credit count.
     * This method provides a clear leaderboard view for the TUI during the rewards phase,
     * displaying players from highest to lowest credit count. This helps players understand
     * the final standings and rewards distribution at the end of the game.
     */
    public void visualizeRewards() {
        Player[] allPlayers = flightBoard.getTurnOrder();

        // Sort players by credits in descending order (highest credits first)
        Player[] playersByCredits = Arrays.stream(allPlayers)
                .sorted(Comparator.comparingInt(Player::getCredits).reversed())
                .toArray(Player[]::new);

        // Print table header
        System.out.println("┌─────────────────────────────────────────────────────┐");
        System.out.println("│                   REWARDS LEADERBOARD               │");
        System.out.println("├─────────────────────────────────────────────────────┤");
        System.out.printf("│ %-4s  │ %-25s │ %-15s │%n", "Rank", "Player Name", "Credits");
        System.out.println("├───────┼───────────────────────────┼─────────────────┤");

        // Print each player's information ordered by credits
        for (int i = 0; i < playersByCredits.length; i++) {
            Player player = playersByCredits[i];
            String playerName = player.getName();
            int credits = player.getCredits();

            // Truncate player name if it's too long to fit in the table
            if (playerName.length() > 25) {
                playerName = playerName.substring(0, 22) + "...";
            }

            // Add medal emoji or indicator for top 3 positions
            String rankIndicator = getRankIndicator(i + 1);

            System.out.printf("│ %-4s │ %-25s │ %-15d │%n",
                    rankIndicator, playerName, credits);
        }

        // Print table footer with winner announcement
        System.out.println("└───────┴───────────────────────────┴─────────────────┘");

        if (playersByCredits.length > 0) {
            Player winner = playersByCredits[0];
            System.out.println("🏆 WINNER: " + winner.getName() +
                    " with " + winner.getCredits() + " credits! 🏆");
        }
    }

    /**
     * Returns a rank indicator string with special symbols for top positions.
     * Provides visual distinction for podium positions in the leaderboard.
     *
     * @param rank the player's rank position (1-based)
     * @return formatted rank string with appropriate symbol
     */
    private String getRankIndicator(int rank) {
        return switch (rank) {
            case 1 -> "🥇1st";
            case 2 -> "🥈2nd";
            case 3 -> "🥉3rd";
            default -> "  " + rank + "  ";
        };
    }

    public void setErrorMessage(String errorMessage) {this.errorMessage = errorMessage;}
    public String getErrorMessage() {return errorMessage;}
}




