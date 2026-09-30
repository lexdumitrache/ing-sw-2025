package Model;

import Model.Ship.Components.SpaceshipComponent;
import Model.Factories.ComponentFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads spaceship components from a JSON file and returns them as a shuffled array.
 */
public class ComponentLoader {

    private static final String COMPONENTS_PATH = "src/main/resources/spaceship_components.json";

    public static List<SpaceshipComponent> loadComponents(boolean shuffle) {
        try (FileReader reader = new FileReader(COMPONENTS_PATH)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            List<SpaceshipComponent> components = new ArrayList<>();

            for (JsonElement el : array) {
                components.add(ComponentFactory.fromJson(el.getAsJsonObject()));
            }

            if (shuffle) {
                Collections.shuffle(components);
            }
            return components;
        } catch (FileNotFoundException e){
            throw new RuntimeException("Spaceship components JSON file not found at: " + COMPONENTS_PATH, e);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load spaceship components from JSON", e);
        }
    }
}
