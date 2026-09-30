package Model.Board.AdventureCards.Penalties;

import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a penalty that consists of a series of cannon shots.
 * This class implements Iterable to allow iteration over the cannon shots.
 */
public class CannonShotPenalty extends Penalty implements Iterable<CannonShot> {
    private final List<CannonShot> CannonShots;

    public CannonShotPenalty(List<CannonShot> CannonShots) {
        this.CannonShots = CannonShots;
    }

    public CannonShotPenalty(JsonObject json) {
        if(json.has("penalty")&&json.getAsJsonObject("penalty").has("shots")) {
            JsonArray arr= json.getAsJsonObject("penalty").getAsJsonArray("shots");
            List<CannonShot> shots = new ArrayList<>();
            for (JsonElement e : arr) {
                JsonObject o = e.getAsJsonObject();
                if(!o.has("isLarge") || !o.has("direction")) {
                    throw new IllegalArgumentException("Invalid cannon shot data in JSON at id: " + json.get("id").getAsInt());
                }
                boolean   large = o.get("isLarge").getAsBoolean();
                Side dir   = Side.valueOf(o.get("direction").getAsString().toUpperCase());
                shots.add(new CannonShot(large, dir));
            }
            this.CannonShots = shots;
        }else{
            throw new IllegalArgumentException("Missing penalty.shots in JSON at id: " + json.get("id").getAsString());
        }
    }


    @Override
    public final Iterator<CannonShot> iterator() {
        return this.CannonShots.iterator();
    }
}
