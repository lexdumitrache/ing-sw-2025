package Model.Board.AdventureCards.Penalties;

import com.google.gson.JsonObject;

/**
 * Represents a penalty that requires the player to pay a certain amount of crew.
 * This class extends RegularPenalty, which handles the amount of crew to be paid.
 */
public class CrewPenalty extends RegularPenalty {
    public CrewPenalty(int amount) {
        super(amount);
    }

    public CrewPenalty(JsonObject json) {
        super(json);

    }
}
