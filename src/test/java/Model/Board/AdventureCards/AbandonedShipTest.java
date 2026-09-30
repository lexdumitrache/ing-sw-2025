package Model.Board.AdventureCards;

import Model.Enums.CardLevel;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the AbandonedShip class which represents abandoned ship encounters.
 * Tests constructor, JSON parsing, and visualization functionality.
 */
public class AbandonedShipTest {

    /**
     * Tests JSON constructor with actual card ID 7 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard7() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "7");
        json.addProperty("type", "AbandonedShip");
        json.addProperty("level", "LEARNER");
        json.addProperty("crewRequired", 3);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 4);
        json.add("reward", reward);
        
        json.addProperty("days", 1);
        json.addProperty("imagePath", "src/main/resources/pics/cards/7.jpg");

        AbandonedShip card = new AbandonedShip(json);
        assertEquals(7, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Nave Abbandonata", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(3, card.getCrew());
        assertEquals(4, card.getLandingReward().getAmount());
        assertEquals(1, card.getLandingPenalty().getAmount());
    }

    /**
     * Tests JSON constructor with actual card ID 31 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard31() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "31");
        json.addProperty("type", "AbandonedShip");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("crewRequired", 4);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 6);
        json.add("reward", reward);
        
        json.addProperty("days", 1);
        json.addProperty("imagePath", "src/main/resources/pics/cards/31.jpg");

        AbandonedShip card = new AbandonedShip(json);
        assertEquals(31, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals(4, card.getCrew());
        assertEquals(6, card.getLandingReward().getAmount());
        assertEquals(1, card.getLandingPenalty().getAmount());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        AbandonedShip card = new AbandonedShip(7, CardLevel.LEARNER, 3, 4, 1);
        assertEquals(7, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Nave Abbandonata", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(3, card.getCrew());
        assertEquals(4, card.getLandingReward().getAmount());
        assertEquals(1, card.getLandingPenalty().getAmount());
    }

    /**
     * Tests JSON constructor with minimal data (using defaults).
     */
    @Test
    public void testJsonConstructorWithMinimalData() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 50);
        json.addProperty("level", "LEARNER");
        json.addProperty("crewRequired", 1); // Add required field
        json.addProperty("imagePath", "test.jpg");
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 0);
        json.add("reward", reward);
        
        json.addProperty("days", 0);
        
        AbandonedShip card = new AbandonedShip(json);
        assertEquals(50, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        // Should use default values
        assertEquals(1, card.getCrew());
        assertEquals(0, card.getLandingReward().getAmount());
        assertEquals(0, card.getLandingPenalty().getAmount());
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        AbandonedShip card = new AbandonedShip(7, CardLevel.LEARNER, 3, 4, 1);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.contains("Nave Abbandonata"));
        assertTrue(output.contains("Crew: 3"));
        assertTrue(output.contains("Credits: 4"));
        assertTrue(output.contains("Days: 1"));
    }

    /**
     * Tests getters.
     */
    @Test
    public void testGetters() {
        AbandonedShip card = new AbandonedShip(7, CardLevel.LEARNER, 3, 4, 1);
        
        assertEquals(3, card.getWinPenalty().getAmount());
        assertEquals(4, card.getLandingReward().getAmount());
        assertEquals(1, card.getLandingPenalty().getAmount());
    }
}