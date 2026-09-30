package Model.Board.AdventureCards;

import Model.Enums.CardLevel;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Slavers class which represents slaver encounters.
 * Tests constructor, JSON parsing, and visualization functionality.
 */
public class SlaversTest {

    /**
     * Tests JSON constructor with actual card ID 19 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard19() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "19");
        json.addProperty("type", "Slavers");
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("power", 6);
        
        JsonObject penalty = new JsonObject();
        penalty.addProperty("crewLoss", 3);
        penalty.addProperty("days", 1);
        json.add("penalty", penalty);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 5);
        json.add("reward", reward);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/19.jpg");

        Slavers card = new Slavers(json);
        assertEquals(19, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
    }

    /**
     * Tests JSON constructor with actual card ID 38 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard38() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "38");
        json.addProperty("type", "Slavers");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("power", 7);
        
        JsonObject penalty = new JsonObject();
        penalty.addProperty("crewLoss", 4);
        penalty.addProperty("days", 2);
        json.add("penalty", penalty);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 8);
        json.add("reward", reward);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/38.jpg");

        Slavers card = new Slavers(json);
        assertEquals(38, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        Slavers card = new Slavers(19, CardLevel.LEVEL_ONE, 6, 3, 1, 5);
        assertEquals(19, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
        assertEquals("Schiavisti", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        Slavers card = new Slavers(19, CardLevel.LEVEL_ONE, 6, 3, 1, 5);

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
        Slavers card = new Slavers(19, CardLevel.LEVEL_ONE, 6, 3, 1, 5);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 19", result[1]);
        assertEquals("Nome: Schiavisti", result[2]);
        assertEquals("Livello: LEVEL_ONE", result[3]);
        assertEquals("Power:                6", result[4]);
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        Slavers card = new Slavers(19, CardLevel.LEVEL_ONE, 6, 3, 1, 5);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }
}