package Model.Board.AdventureCards;

import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.CardLevel;
import Model.Enums.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Pirates class which represents pirate encounters.
 * Tests constructor, JSON parsing, and visualization functionality.
 */
public class PiratesTest {

    /**
     * Tests JSON constructor with actual card ID 20 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard20() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "20");
        json.addProperty("type", "Pirates");
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("power", 5);
        
        JsonObject penalty = new JsonObject();
        JsonArray shots = new JsonArray();
        
        JsonObject shot1 = new JsonObject();
        shot1.addProperty("isLarge", false);
        shot1.addProperty("direction", "FRONT");
        shots.add(shot1);
        
        JsonObject shot2 = new JsonObject();
        shot2.addProperty("isLarge", true);
        shot2.addProperty("direction", "FRONT");
        shots.add(shot2);
        
        JsonObject shot3 = new JsonObject();
        shot3.addProperty("isLarge", false);
        shot3.addProperty("direction", "FRONT");
        shots.add(shot3);
        
        penalty.add("shots", shots);
        penalty.addProperty("days", 1);
        json.add("penalty", penalty);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 4);
        json.add("reward", reward);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/20.jpg");

        Pirates card = new Pirates(json);
        assertEquals(20, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
    }

    /**
     * Tests JSON constructor with actual card ID 39 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard39() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "39");
        json.addProperty("type", "Pirates");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("power", 6);
        
        JsonObject penalty = new JsonObject();
        JsonArray shots = new JsonArray();
        
        JsonObject shot1 = new JsonObject();
        shot1.addProperty("isLarge", true);
        shot1.addProperty("direction", "front");
        shots.add(shot1);
        
        JsonObject shot2 = new JsonObject();
        shot2.addProperty("isLarge", false);
        shot2.addProperty("direction", "front");
        shots.add(shot2);
        
        JsonObject shot3 = new JsonObject();
        shot3.addProperty("isLarge", true);
        shot3.addProperty("direction", "front");
        shots.add(shot3);
        
        penalty.add("shots", shots);
        penalty.addProperty("days", 2);
        json.add("penalty", penalty);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 7);
        json.add("reward", reward);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/39.jpg");

        Pirates card = new Pirates(json);
        assertEquals(39, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(false, Side.FRONT));
        shots.add(new CannonShot(true, Side.FRONT));
        
        Pirates card = new Pirates(20, CardLevel.LEVEL_ONE, 5, shots, 1, 4);
        assertEquals(20, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
        assertEquals("Pirati", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        shots.add(new CannonShot(false, Side.FRONT));
        
        Pirates card = new Pirates(20, CardLevel.LEVEL_ONE, 5, shots, 1, 4);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.length() > 0);
    }

    /**
     * Tests the visualizeString method.
     */
    @Test
    public void testVisualizeString() {
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        
        Pirates card = new Pirates(20, CardLevel.LEVEL_ONE, 5, shots, 1, 4);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 20", result[1]);
        assertEquals("Nome: Pirati", result[2]);
        assertEquals("Livello: LEVEL_ONE", result[3]);
        assertEquals("Power:                5", result[4]);
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        List<CannonShot> shots = new ArrayList<>();
        Pirates card = new Pirates(20, CardLevel.LEVEL_ONE, 5, shots, 1, 4);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }
}