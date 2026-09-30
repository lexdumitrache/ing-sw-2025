package Model.Board;

import Model.Board.AdventureCards.AdventureCardFilip;
import Model.AdventureCardLoader;
import Model.Player;
import Controller.Enums.MatchLevel;


import java.util.List;

/**
 * Builder for constructing a FlightBoard for the game.
 * Loads the proper adventure deck based on the game level.
 */
public class FlightBoardBuilder {
    private List<Player> players;
    private MatchLevel level;

    public FlightBoardBuilder setPlayers(List<Player> players) {
        this.players = players;
        return this;
    }

    public FlightBoardBuilder setLevel(MatchLevel level) {
        this.level = level;
        return this;
    }

    public FlightBoard build() {
        if (players == null || players.isEmpty() || level == null) {
            throw new IllegalStateException("Players and level must be specified.");
        }

        // Load and shuffle the adventure deck from JSON based on level
        List<AdventureCardFilip> cards = AdventureCardLoader.loadAdventureCards(level, true);
        CardDeck deck = new CardDeck(cards);

        return new FlightBoard(players, deck, level);
    }
}
