package Model.Factories;

import Model.Board.AdventureCards.*;
import com.google.gson.JsonObject;

/**
 * AdventureCardFactory
 *
 * Responsible for instantiating specific AdventureCardFilip subclasses
 * based on the "type" field in a JsonObject.
 */
public class AdventureCardFactory {

    /**
     * Creates a specific AdventureCardFilip from its JSON representation.
     *
     * @param json the JsonObject representing the adventure card
     * @return the corresponding AdventureCardFilip instance
     * @throws IllegalArgumentException if the card type is unknown
     */
    public static AdventureCardFilip fromJson(JsonObject json) {
        String type = json.get("type").getAsString();

        return switch (type) {
            case "OpenSpace" -> new OpenSpace(json);
            case "MeteorSwarm" -> new MeteorSwarm(json);
            case "Stardust" -> new Stardust(json);
            case "Planets" -> new Planets(json);
            case "Pirates" -> new Pirates(json);
            case "Slavers" -> new Slavers(json);
            case "Smugglers" -> new Smugglers(json);
            case "AbandonedShip" -> new AbandonedShip(json);
            case "AbandonedStation" -> new AbandonedStation(json);
            case "CombatZone" -> new CombatZone(json);
            case "Epidemic" -> new Epidemic(json);
            default -> throw new IllegalArgumentException("Unknown card type: " + type);
        };
    }
}
