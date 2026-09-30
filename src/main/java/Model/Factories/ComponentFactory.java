package Model.Factories;

import Model.Ship.*;
import Model.Ship.Components.*;
import com.google.gson.JsonObject;
//import com.sun.jdi.connect.Connector;

/**
 * Factory for dynamically creating specific SpaceshipComponent instances from a JsonObject.
 */
public class ComponentFactory {

    /**
     * Builds a specific component from JSON using the "type" field.
     *
     * @param obj the JSON object to parse
     * @return a specific SpaceshipComponent instance
     */
    public static SpaceshipComponent fromJson(JsonObject obj) {
        String type = obj.get("type").getAsString();

        return switch (type) {
            case "CABIN" -> new Cabin(obj);
            case "ENGINE" -> new Engine(obj);
            case "CANNON" -> new Cannon(obj);
            case "CARGO_HOLD" -> new CargoHold(obj);
            case "BATTERY_COMPARTMENT" -> new BatteryCompartment(obj);
            case "SHIELD_GENERATOR" -> new ShieldGenerator(obj);
            case "ALIEN_LIFE_SUPPORT" -> new AlienLifeSupport(obj);
            case "STRUCTURAL_MODULE" -> new StructuralModule(obj);
            default -> throw new IllegalArgumentException("Unknown component type: " + type);
        };
    }
}
