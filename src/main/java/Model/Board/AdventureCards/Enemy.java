package Model.Board.AdventureCards;

import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Penalties.GoodsPenalty;
import Model.Board.AdventureCards.Penalties.Penalty;
import Model.Board.AdventureCards.Rewards.Credits;
import Model.Board.AdventureCards.Rewards.Reward;
import Model.Enums.CardLevel;
import com.google.gson.JsonObject;

/**
 * @param <P> Penalty inflicted upon the player if the card is not beaten
 * @param <R> Reward given to the player if the card is beaten
 */
public abstract class Enemy<P extends Penalty, R extends Reward> extends AdventureCardFilip {
    private final int power;
    private final P lossPenalty;
    private final DaysPenalty winPenalty;
    private final R winReward;

    public Enemy(int id, CardLevel level, int power, P lossPenalty, int days, R winReward) {
        super(id, level);
        this.power = power;
        this.lossPenalty = lossPenalty;
        this.winPenalty = new DaysPenalty(days);
        this.winReward = winReward;
    }
    
    /**
     * Constructor for creating an Enemy from a JSON object.
     * This is meant to be used by subclasses.
     * 
     * @param json the JSON object containing the card data
     * @param lossPenalty the penalty if the player loses
     * @param winReward the reward if the player wins
     */
    protected Enemy(JsonObject json, P lossPenalty, R winReward) {
        super(json);

        if(!json.has("power")) {
            throw new IllegalArgumentException("JSON must contain 'power' field for ID: " + getId());
        }

        this.power = json.get("power").getAsInt();

        if(!(json.has("penalty")&&json.getAsJsonObject("penalty").has("days"))) {
            throw new IllegalArgumentException("Missing 'penalty.shots' in JSON at id: " + getId());
        }
        // day cost
        int days=json.getAsJsonObject("penalty").get("days").getAsInt();
        this.winPenalty = new DaysPenalty(days);

        this.lossPenalty = lossPenalty;
        this.winReward = winReward;
    }

    public final int getPower() {
        return power;
    }

    public final P getLossPenalty() {
        return lossPenalty;
    }

    public final DaysPenalty getWinPenalty() {
        return winPenalty;
    }

    public final R getWinReward() {
        return winReward;
    }


}
