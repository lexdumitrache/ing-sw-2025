package Model.Board.AdventureCards.Penalties;

import com.google.gson.JsonObject;

/**
 * Represents a penalty that requires the player to pay a certain amount of goods.
 * This class extends RegularPenalty, which handles the amount of goods to be paid.
 */
public class GoodsPenalty extends RegularPenalty {
    public GoodsPenalty(int amount) {
        super(amount);
    }
    public GoodsPenalty(JsonObject json) {
        super(json);

    }
}
