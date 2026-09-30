package Model.Board;

import Model.AdventureCardLoader;
import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Enums.CardLevel;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Controller.Enums.MatchLevel;

import java.io.Serializable;
import java.util.*;

/**
 * FlightBoard: it holds all the good stuff you
 * need during the building and the flying phase.
 *
 * <p>This class would really benefit from getting
 * split into LearnerFlightBoard and LevelTwoFlightBoard.
 * Why? In the learner flight, specifically in the building
 * phase, timer doesn't exist (see page 4 of
 * {@code galaxy-trucker-rules-it.pdf}), the hiddenCardDeck
 * is technically not formed yet (see page 9) and there
 * is no such thing as a peekableCardDeck. Regardless of
 * the phase, the number of cells differs, as does the layout,
 * and it would overall be a good idea to abstract this better.
 *
 * <p>Also would benefit if split into FlightBoard
 * and BuildingFlightBoard (or BuildingBoard).
 * Why? timer, hiddenCardDeck and peekableCardDecks
 * do not exist while flying (but this is a less pressing
 * issue than the one stated earlier).
 */
public class FlightBoard implements Serializable, Cloneable {
    private final int cellNumber;

    private Map<String, CardDeck> bookedDecks = new HashMap<>();

    private Timer timer;

    private List<CardDeck> peekableCardDecks;

    private CardDeck hiddenCardDeck;

    private CardDeck upcomingCardDeck;

    private final Map<Player, Integer> playerPositions;

    private final Map<Player, Integer> playerTotalDistance;

    private final List<Player> ffPlayers;

    /**
     * FlightBoard default constructor stub.
     *
     * <p>Now always throws because: How can I know if
     * it's supposed to be a learner or a level 2
     * flightboard? How can I make such assumption?
     * Should I make such an assumption?
     *
     * @throws UnsupportedOperationException always
     */

    public static void main(String[] args) {

        List<AdventureCardFilip> cards = AdventureCardLoader.loadAdventureCards(MatchLevel.LEVEL2, false);

        System.out.println(cards.size());
        for (AdventureCardFilip card : cards) {
            card.visualize();
            System.out.println("\n\n\n\n\n\n");
        }

    }

    public FlightBoard() {
        throw new UnsupportedOperationException("This FlightBoard constructor is no longer accepted");
    }

    /**
     * FlightBoard constructor of learner level.
     * Sets the number of cells to 18.
     * Sets the hidden card deck to the passed value.
     * Sets the player positions map to an empty HashMap.
     * Sets every other member to {@code null}.
     *
     * @param hiddenCards the deck of cards to be used later in the
     *                    flight phase
     * @throws AssertionError if something is wrong with passed deck:
     *                        the number of cards is not 8, the level of the cards is not LEARNER...
     * @see CardDeck
     * @see AssertionError
     */
    public FlightBoard(CardDeck hiddenCards) throws AssertionError {
        this.cellNumber = 18;
        this.timer = null;
        this.peekableCardDecks = null;


        this.hiddenCardDeck = hiddenCards;

        assert (this.hiddenCardDeck.peekCards().size() == 8);

        for (AdventureCardFilip card : this.hiddenCardDeck) {
            assert (card.getLevel() == CardLevel.LEARNER);
        }

        this.upcomingCardDeck = null;
        this.playerPositions = new HashMap<>();
        this.playerTotalDistance = new HashMap<>();
        this.ffPlayers = new ArrayList<>();
    }

    /**
     * FlightBoard constructor of level 2.
     * Sets the number of cells to 24.
     * Sets the timer to a new Timer.
     * Sets the peekable card decks to the passed value.
     * Sets the hidden card deck to the passed value.
     * Sets the player positions map to an empty HashMap.
     * Sets every other member to {@code null}.
     *
     * @param hiddenCardDeck the deck of cards to be used later in the
     *                       flight phase
     * @param peekableCardDecks the list of peekable card decks
     * @throws AssertionError if something is wrong with passed decks:
     *                        peekableCardDecks size is not 3, peekableCardDecks elements size is not 4...
     */
    public FlightBoard(CardDeck hiddenCardDeck, List<CardDeck> peekableCardDecks) {
        this.cellNumber = 24;
        this.timer = new Timer();

        assert (3 == peekableCardDecks.size());
        for (CardDeck cardDeck : peekableCardDecks) {
            assert (3 == cardDeck.peekCards().size());
        }
        this.peekableCardDecks = peekableCardDecks;

        this.hiddenCardDeck = hiddenCardDeck;
        assert (3 == this.hiddenCardDeck.peekCards().size());

        this.upcomingCardDeck = null;
        this.playerPositions = new HashMap<>();
        this.playerTotalDistance = new HashMap<>();
        this.ffPlayers = new ArrayList<>();
    }

    public Map<String, CardDeck> getBookedDecks() {return bookedDecks;}

    public void render(){
        System.out.println("FlightBoard: " + cellNumber + " cells");
        Player[]turnOrder=getTurnOrder();

        for (Player player : turnOrder) {
            int position = getPosition(player);
            int totalDistance = getTotalDistance(player);
            int credit = player.getCredits();
            System.out.println("Player: " + player.getName() + ", Position: " + position + ", Total Distance: " + totalDistance+
                    ", Credit: " + credit);

        }


        List<Player> dubbedPlayers = getDubbedPlayers();
        if (!dubbedPlayers.isEmpty()) {
            System.out.println("Dubbed Players: ");
            for (Player dubbedPlayer : dubbedPlayers) {
                System.out.println(" - " + dubbedPlayer.getName());
            }
        }
    }

    public List<Player> getDubbedPlayers() {
        Player[] players = this.getTurnOrder();
        List<Player> dubbedPlayers = new ArrayList<>();
        int length = players.length;

        if (length>0) {
            int firstPlayerDistance = playerTotalDistance.get(players[length-1]);

            if(playerTotalDistance.get(players[0])>firstPlayerDistance) {
                firstPlayerDistance = playerTotalDistance.get(players[0]);
            }

            for(int i=0;i<length;i++){

                int otherPlayerDistance = playerTotalDistance.get(players[i]);
                if((firstPlayerDistance-otherPlayerDistance)>cellNumber){
                    dubbedPlayers.add(players[i]);
                }
            }
        }


        return dubbedPlayers;
    }

    public final int getCellNumber() {
        return cellNumber;
    }

    public final Timer getTimer() {
        return timer;
    }

    public CardDeck getPeekableCardDeck(int index) throws IndexOutOfBoundsException {
        return peekableCardDecks.get(index);
    }

    public CardDeck getHiddenCardDeck() {
        return hiddenCardDeck;
    }

    public CardDeck getUpcomingCardDeck() {
        return upcomingCardDeck;
    }

    /**
     * Sets the upcoming card deck by combining all peekable card decks
     * and the hidden card deck, shuffling them, and clearing the
     * peekable card decks and the hidden card deck.
     *
     * @return true if the upcoming card deck was successfully set,
     *         false if the timer is not in the last phase with no time left
     */
    public boolean setUpcomingCardDeck() {
//        if (timer == null || !(timer.getPhase() == Timer.Phase.LAST_PHASE && timer.getTimeLeft() == 0.0f)) {
//            return false;
//        }
        List<AdventureCardFilip> cards = new ArrayList<>();
        upcomingCardDeck = new CardDeck();
        // Aggiungi le carte dei peekableCardDecks
        if (cellNumber!=18) {
            for (CardDeck deck : peekableCardDecks) {
                while (!deck.peekCards().isEmpty()) {
                    upcomingCardDeck.pushCard(deck.popCard());
                }
            }
        }

        // Aggiungi le carte del hiddenCardDeck
        while (!hiddenCardDeck.peekCards().isEmpty()) {
            upcomingCardDeck.pushCard(hiddenCardDeck.popCard());
        }

        upcomingCardDeck.shuffleDeck();

        peekableCardDecks = null;
        hiddenCardDeck = null;

        this.timer = null;

        return true;
    }

    /**
     * Returns the position of the specified player, or {@code null}
     * if no position was found for such player.
     *
     * @param player the player whose associated position is to be returned
     * @return the position to which the specified player is mapped, or
     * {@code null} if this map contains no position for such player
     * @throws NullPointerException if the specified key is null
     */
    public int getPosition(Player player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        return playerPositions.get(player);
    }

    public int getTotalDistance(Player player) throws NullPointerException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        return playerTotalDistance.get(player);
    }

    public FlightBoard(List<Player> players, CardDeck deck, MatchLevel level) {
        this.cellNumber = (level == MatchLevel.TRIAL) ? 18 : 24;
        this.timer = (level == MatchLevel.LEVEL2) ? new Timer() : null;
        this.hiddenCardDeck = deck;
        this.peekableCardDecks = (level == MatchLevel.LEVEL2) ? new ArrayList<>() : null;
        this.upcomingCardDeck = null;
        this.playerPositions = new HashMap<>();
        this.playerTotalDistance = new HashMap<>();
        for (Player p : players) {
            playerPositions.put(p, 0);
            playerTotalDistance.put(p, 0);
        }
        this.ffPlayers = new ArrayList<>();
    }


    public void updatePosition(Player player, int position) {
        playerPositions.put(player, position);
    }


    /**
     * Sets the starting positions of the players.
     * @param player
     * @param position
     * @throws InvalidMethodParameters
     */
    public void setStartingPositions(Player player, int position) throws InvalidMethodParameters {
        if(this.getCellNumber()==18){
            if(position==4){playerPositions.put(player, 0); playerTotalDistance.put(player, 0);}
            else if (position==3) {playerPositions.put(player, 1); playerTotalDistance.put(player, 1);}
            else if (position==2) {playerPositions.put(player, 2); playerTotalDistance.put(player, 2);}
            else if (position==1) {playerPositions.put(player, 4); playerTotalDistance.put(player, 4);}
            else{throw new InvalidMethodParameters("Invalid position: " + position+"Should be between 1 and");}


        } else if (this.getCellNumber() == 24) {

            if(position==4){playerPositions.put(player, 0); playerTotalDistance.put(player, 0);}
            else if (position==3) {playerPositions.put(player, 1); playerTotalDistance.put(player, 1);}
            else if (position==2) {playerPositions.put(player, 3); playerTotalDistance.put(player, 3);}
            else if (position==1) {playerPositions.put(player, 5); playerTotalDistance.put(player, 5);}
            else{throw new InvalidMethodParameters("Invalid position: " + position+"Should be between 1 and 4");}


        } else {
            throw new IllegalStateException("Invalid cell number: " + this.getCellNumber());

        }

    }


    /**
     * Moves the player by the specified number of days.
     * If the number of days is negative, the player moves backwards.
     * If the number of days is positive, the player moves forwards.
     *
     * @param player the player to move
     * @param deltaDays the number of days to move
     * @throws InvalidMethodParameters if the player is not found in the flight board
     */
    public void deltaFlightDays(Player player, int deltaDays) throws InvalidMethodParameters {

        /*
        The main idea is that recoursively, the position of the player is updated,
        the number of players between the current position and the new position is counted, and the funzion is called again
         */
        if (!playerPositions.containsKey(player)) {
            throw new InvalidMethodParameters("Player not found in flight board");
        }


        int currentPosition = playerPositions.get(player);
        int newPosition = (currentPosition + deltaDays) % cellNumber;
        if (newPosition < 0) {
            newPosition += cellNumber; // Ensure newPosition is non-negative
        }
        int currentTotalDistance = playerTotalDistance.get(player);
        playerTotalDistance.put(player, currentTotalDistance + deltaDays);
        int new_delta=0;



        Collection<Integer> allPositions=playerPositions.values();
        if (deltaDays > 0) {

            if(newPosition<currentPosition){
                //this means that the player has passed the end of the board,
                //so we don't need to count the players that are between the current position and the new position
                //instead we need to count the players between the start of the table and the new position
                //and the players between the current position and the end of the table
                for (Integer position : allPositions) {
                    if (position <= newPosition || position > currentPosition) {
                        new_delta++;
                    }
                }
            }else{
                for (Integer position : allPositions) {
                    if (position <= newPosition && position > currentPosition) {
                        new_delta++;
                    }
                }
            }
            playerPositions.put(player, newPosition);
            deltaFlightDays(player, new_delta);
        }else if(deltaDays<0){

            if(newPosition>currentPosition){
                for (Integer position : allPositions) {
                    if (position >= newPosition || position < currentPosition) {
                        new_delta--;
                    }
                }
            }else{
                for (Integer position : allPositions) {
                    if (position >= newPosition && position < currentPosition) {
                        new_delta--;
                    }
                }
            }
            playerPositions.put(player, newPosition);
            deltaFlightDays(player, new_delta);
        }

    }


    public Player[] getTurnOrder() {
        return playerTotalDistance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .map(Map.Entry::getKey)
                .toArray(Player[]::new);
    }

    public void removePlayingPlayer(Player player) {
        if (playerPositions.containsKey(player)) {
            playerPositions.remove(player);
            playerTotalDistance.remove(player);
            ffPlayers.add(player);
        } else {
            throw new IllegalArgumentException("Player not found in flight board");
        }
    }

    public List<Player> getFinishedFlightPlayers() {
        return ffPlayers;
    }

    public Set<Player> getFlyingPlayers() {return playerPositions.keySet();}

    public void visualize(Player playerInTurn) {
        Player[] turnOrder = getTurnOrder();

        // Print table header
        System.out.println("┌─────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                         FLIGHT BOARD STATUS                         │");
        System.out.println("├─────────────────────────────────────────────────────────────────────┤");
        System.out.printf("│ %-3s │ %-25s │ %-15s │ %-15s │%n", "Pos", "Player Name", "Credits", "Position");
        System.out.println("├─────┼───────────────────────────┼─────────────────┼─────────────────┤");

        if (turnOrder.length > 0) {
            // Print each player's information in turn order
            for (int i = 0; i < turnOrder.length; i++) {
                Player player = turnOrder[i];
                String playerName = player.getName();
                int credits = player.getCredits();
                int position = playerPositions.get(player); // Assumo esista questo metodo

                // Add current turn indicator for the first player (current turn)
                String turnIndicator = (player.equals(playerInTurn)) ? "▶ " : "  ";

                // Truncate player name if it's too long to fit in the table
                if (playerName.length() > 25) {
                    playerName = playerName.substring(0, 22) + "...";
                }

                String displayName = turnIndicator + playerName;

                System.out.printf("│ %-3d │ %-25s │ %-15d │ %-15d │%n",
                        (i + 1), displayName, credits, position);
            }
        }

        // Print table footer
        System.out.println("└─────┴───────────────────────────┴─────────────────┴─────────────────┘");
        if (turnOrder.length > 0) {
            System.out.println("Turn Order: 1st = " + turnOrder[0].getName() +
                    " | Last = " + turnOrder[turnOrder.length - 1].getName());
        }

        if(getTimer() != null) {
            System.out.println("Timer: " + getTimer().getTimeLeft() + " seconds left" +
                    " | Phase: " + getTimer().getPhase());
        }
    }

    @Override
    public FlightBoard clone() {
        return new FlightBoard(this);
    }

    private FlightBoard(FlightBoard old) {
        this.cellNumber = old.cellNumber;

        this.timer = (old.timer != null) ? old.timer.clone() : null;

        if(old.peekableCardDecks != null) {
            this.peekableCardDecks = new ArrayList<>();

            for (CardDeck deck : old.peekableCardDecks) {
                this.peekableCardDecks.add(deck.clone());
            }
        }

        this.hiddenCardDeck = (old.hiddenCardDeck != null) ? old.hiddenCardDeck.clone() : null;

        this.upcomingCardDeck = (old.upcomingCardDeck != null) ? old.upcomingCardDeck.clone() : null;

        this.playerPositions = new HashMap<>(old.playerPositions);
        this.playerTotalDistance = new HashMap<>(old.playerTotalDistance);
        this.ffPlayers = new ArrayList<>(old.ffPlayers);

        for(String name : old.bookedDecks.keySet()) {
            this.bookedDecks.put(name, old.bookedDecks.get(name).clone());
        }
    }
}
