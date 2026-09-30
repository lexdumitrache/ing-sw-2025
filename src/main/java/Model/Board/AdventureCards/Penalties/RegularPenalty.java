package Model.Board.AdventureCards.Penalties;

import com.google.gson.JsonObject;

/**
 * Represents a regular penalty in the game, which can be a penalty for crew loss or losing goods on loss.
 * This class extends the Penalty class and provides a way to handle penalties that require a specific amount.
 */
public abstract class RegularPenalty extends Penalty {
    private final int amount;

    public RegularPenalty(int amount) {
        this.amount = amount;
    }

    public RegularPenalty(JsonObject json) {
        boolean crewPenaltyCondition =json.has("penalty") && json.getAsJsonObject("penalty").has("crewLoss");
        boolean goodsPenaltyCondition =json.has("penalty") && json.getAsJsonObject("penalty").has("stealGoodsOnLoss");
        if(crewPenaltyCondition) {
            this.amount=json.getAsJsonObject("penalty").get("crewLoss").getAsInt();
        } else if (goodsPenaltyCondition) {
            this.amount=json.getAsJsonObject("penalty").get("stealGoodsOnLoss").getAsInt();
        } else{
            throw new IllegalArgumentException("Missing 'penalty.crewLoss' or penalty.stealGoodsOnLoss in JSON at id: " + json.get("id").getAsInt());
        }
    }



    public final int getAmount() {
        return amount;
    }
}
