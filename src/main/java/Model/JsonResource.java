package Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Reads the JSON data files bundled in src/main/resources.
 * Loading from the classpath (instead of a file path) lets the jars run from any directory.
 */
public final class JsonResource {

    private JsonResource() {}

    /**
     * @param name the resource name, e.g. "ships.json"
     * @return the top level JSON array contained in the resource
     * @throws IOException if the resource is missing or cannot be read
     */
    public static JsonArray readArray(String name) throws IOException {
        InputStream stream = JsonResource.class.getClassLoader().getResourceAsStream(name);
        if (stream == null) {
            throw new IOException("Resource not found on classpath: " + name);
        }

        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonArray();
        }
    }
}
