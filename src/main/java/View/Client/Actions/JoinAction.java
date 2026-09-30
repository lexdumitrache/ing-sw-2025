package View.Client.Actions;

import View.Client.ClientState;

import java.util.List;
import java.util.Map;

/**
 * Represents an action to join an existing game identified by its game ID.
 * This action is executed on the client state to allow the player to join the specified game.
 */
public class JoinAction implements Action {

    /**
     * The ID of the game that the player wants to join.
     */
    private final int gameId;

    /**
     * Constructs a JoinAction with the specified game ID.
     *
     * @param gameId The ID of the game to join.
     */
    public JoinAction(int gameId) {
        this.gameId = gameId;
    }

    /**
     * Executes the action on the client state to join the specified game.
     *
     * @param state The current client state.
     * @return The updated client state after joining the game.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.join(gameId);
    }

    /**
     * Returns a string representation of the action.
     *
     * @return A string indicating the action to join a game with the specified ID.
     */
    public static ActionConstructor getConstructor() {
        return new ActionConstructor() {
            @Override
            public JoinAction create(Map<String, String> args) throws IllegalArgumentException {
                final int gameId;

                try {
                    gameId = Integer.parseInt(args.get("gameId"));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(e);
                }

                return new JoinAction(gameId);
            }

            @Override
            public List<String> getArguments() {
                return List.of("gameId");
            }
        };
    }
}
