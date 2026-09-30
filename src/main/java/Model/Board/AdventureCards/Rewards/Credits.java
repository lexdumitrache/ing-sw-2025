package Model.Board.AdventureCards.Rewards;

import Model.Board.AdventureCards.Components.Planet;
import com.google.gson.JsonObject;

/**
 * Represents a reward in the form of credits in the game.
 * This class extends the Reward class, which handles the properties of rewards.
 */
public class Credits extends Reward{
    private final int amount;

    public Credits(int amount) {
        this.amount = amount;
    }

    public Credits(JsonObject json) {
        if(json.has("reward") && json.getAsJsonObject("reward").has("credits")) {
            this.amount = json.getAsJsonObject("reward").get("credits").getAsInt();
        } else {
            throw new IllegalArgumentException("Missing 'reward.credits' in JSON at id: " + json.get("id").getAsInt());
        }
    }

    public final int getAmount() {
        return amount;
    }
}
