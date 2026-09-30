package Model;

import Model.Ship.Components.SpaceshipComponent;
import Model.Factories.ComponentFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads spaceship components from a JSON file and returns them as a shuffled array.
 */
public class ComponentLoader {

    private static final String COMPONENTS_PATH = "spaceship_components.json";

    public static List<SpaceshipComponent> loadComponents(boolean shuffle) {
        try {
            JsonArray array = JsonResource.readArray(COMPONENTS_PATH);
            List<SpaceshipComponent> components = new ArrayList<>();

            for (JsonElement el : array) {
                components.add(ComponentFactory.fromJson(el.getAsJsonObject()));
            }

            if (shuffle) {
                Collections.shuffle(components);
            }
            return components;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load spaceship components from JSON", e);
        }
    }
}
