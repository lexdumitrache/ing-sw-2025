package View.Client.Actions;

import Controller.Enums.MatchLevel;
import View.Client.ClientState;

import java.util.List;
import java.util.Map;

/**
 * Represents an action to create a new game with a specified match level.
 * This action is executed by the client to initiate a new game session.
 */
public class CreateGameAction implements Action {

    /**
     * The match level for the game to be created.
     */
    private final MatchLevel matchLevel;

    /**
     * Constructs a CreateGameAction with the specified match level.
     *
     * @param matchLevel The level of the match to be created.
     */
    public CreateGameAction(MatchLevel matchLevel) {
        this.matchLevel = matchLevel;
    }

    /**
     * Executes the action to create a new game with the specified match level.
     * This method creates a new ClientState with the match level.
     *
     * @param state The current client state.
     * @return A new ClientState with the specified match level.
     */
    @Override
    public ClientState execute(ClientState state) {
        return state.create(matchLevel);
    }

    /**
     * Returns a string representation of the action.
     * This method is used for debugging purposes.
     *
     * @return A string representation of the CreateGameAction.
     */
    static public ActionConstructor getConstructor() {
        return new ActionConstructor() {
            @Override
            public CreateGameAction create(Map<String, String> args) throws IllegalArgumentException {
                final MatchLevel matchLevel = MatchLevel.valueOf(args.get("MatchLevel").toUpperCase());

                return new CreateGameAction(matchLevel);
            }

            @Override
            public List<String> getArguments() {
                return List.of("MatchLevel");
            }
        };
    }
}
