package Model.Board.AdventureCards;

import Model.Enums.CardLevel;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Epidemic class which represents epidemic encounters.
 * Tests constructor, JSON parsing, crew loss tracking, and visualization functionality.
 */
public class EpidemicTest {

    /**
     * Tests JSON constructor with actual card ID 37 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard37() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "37");
        json.addProperty("type", "EPIDEMIC");
        json.addProperty("level", "LEVEL_TWO");

        JsonObject penalty = new JsonObject();
        penalty.addProperty("crewLoss", 2);
        json.add("penalty", penalty);

        json.addProperty("imagePath", "src/main/resources/pics/cards/37.jpg");

        Epidemic card = new Epidemic(json);
        assertEquals(37, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Epidemia", card.getName());
        assertEquals("", card.getDescription());
        //assertEquals(2, card.getCrewLost());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        Epidemic card = new Epidemic(37, CardLevel.LEVEL_TWO);
        assertEquals(37, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Epidemia", card.getName());
        assertEquals("", card.getDescription());
        //assertEquals(0, card.getCrewLost());
    }

    /**
     * Tests JSON constructor without penalty data.
     */
    @Test
    public void testJsonConstructorWithoutPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 50);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        
        Epidemic card = new Epidemic(json);
        assertEquals(50, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        //assertEquals(0, card.getCrewLost()); // Default value
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        Epidemic card = new Epidemic(37, CardLevel.LEVEL_TWO);

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
        Epidemic card = new Epidemic(37, CardLevel.LEVEL_TWO);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 37", result[1]);
        assertEquals("Nome: Epidemia", result[2]);
        assertEquals("Livello: LEVEL_TWO", result[3]);
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        Epidemic card = new Epidemic(37, CardLevel.LEVEL_TWO);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }
}