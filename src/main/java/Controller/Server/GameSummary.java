package Controller.Server;

import Controller.Enums.MatchLevel;

import java.io.Serializable;
import java.util.List;

/**
 * What the game list shows about a game.
 *
 * @param id      the game id, used to join it
 * @param level   the match level
 * @param players the names of the players in the game
 * @param started true once the game has left the lobby (it can no longer be joined)
 */
public record GameSummary(int id, MatchLevel level, List<String> players, boolean started) implements Serializable {

    public static final int MAX_PLAYERS = 4;

    public boolean canJoin() {
        return !started && players.size() < MAX_PLAYERS;
    }
}
