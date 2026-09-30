package Model.Board.AdventureCards;

import Model.Enums.CardLevel;
import Model.Enums.Good;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the AbandonedStation class which represents abandoned station encounters.
 * Tests constructor, JSON parsing, and visualization functionality.
 */
public class AbandonedStationTest {

    /**
     * Tests JSON constructor with actual card ID 8 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard8() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "8");
        json.addProperty("type", "AbandonedStation");
        json.addProperty("level", "LEARNER");
        json.addProperty("crewRequired", 5);
        json.addProperty("days", 1);
        
        JsonObject reward = new JsonObject();
        JsonArray goods = new JsonArray();
        goods.add(new JsonPrimitive("YELLOW"));
        goods.add(new JsonPrimitive("GREEN"));
        reward.add("goods", goods);
        json.add("reward", reward);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/8.jpg");

        AbandonedStation card = new AbandonedStation(json);
        assertEquals(8, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Stazione Abbandonata", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(5, card.getCrew());
        assertEquals(1, card.getLandingPenalty().getAmount());
    }

    /**
     * Tests JSON constructor with actual card ID 33 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard33() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "33");
        json.addProperty("type", "AbandonedStation");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("crewRequired", 7);
        json.addProperty("days", 1);
        
        JsonObject reward = new JsonObject();
        JsonArray goods = new JsonArray();
        goods.add(new JsonPrimitive("RED"));
        goods.add(new JsonPrimitive("YELLOW"));
        reward.add("goods", goods);
        json.add("reward", reward);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/33.jpg");

        AbandonedStation card = new AbandonedStation(json);
        assertEquals(33, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals(7, card.getCrew());
        assertEquals(1, card.getLandingPenalty().getAmount());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        List<Good> goods = Arrays.asList(Good.YELLOW, Good.GREEN);
        
        AbandonedStation card = new AbandonedStation(8, CardLevel.LEARNER, 5, goods, 1);
        assertEquals(8, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Stazione Abbandonata", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(5, card.getCrew());
        assertEquals(1, card.getLandingPenalty().getAmount());
        
        // Count the goods in the landing reward
        int goodsCount = 0;
        for (Good g : card.getLandingReward()) {
            goodsCount++;
        }
        assertEquals(2, goodsCount);
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
        json.addProperty("days", 0);
        json.addProperty("imagePath", "test.jpg");
        
        JsonObject reward = new JsonObject();
        JsonArray goods = new JsonArray();
        reward.add("goods", goods);
        json.add("reward", reward);
        
        AbandonedStation card = new AbandonedStation(json);
        assertEquals(50, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        // Should use default values
        assertEquals(1, card.getCrew());
        assertEquals(0, card.getLandingPenalty().getAmount());
        
        // Should have empty goods list
        int goodsCount = 0;
        for (Good g : card.getLandingReward()) {
            goodsCount++;
        }
        assertEquals(0, goodsCount);
    }

    /**
     * Tests JSON constructor with cargo reward.
     */
    @Test
    public void testJsonConstructorWithCargoReward() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 51);
        json.addProperty("level", "LEARNER");
        json.addProperty("crewRequired", 3);
        json.addProperty("days", 2);
        json.addProperty("imagePath", "test.jpg");
        
        JsonObject reward = new JsonObject();
        JsonArray goods = new JsonArray();
        goods.add("RED");
        goods.add("RED");
        goods.add("RED");
        reward.add("goods", goods); // Use goods instead of cargo
        json.add("reward", reward);
        
        AbandonedStation card = new AbandonedStation(json);
        assertEquals(51, card.getId());
        assertEquals(3, card.getCrew());
        assertEquals(2, card.getLandingPenalty().getAmount());
        
        // Should have 3 goods (RED)
        int goodsCount = 0;
        for (Good g : card.getLandingReward()) {
            goodsCount++;
            assertEquals(Good.RED, g);
        }
        assertEquals(3, goodsCount);
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        List<Good> goods = Arrays.asList(Good.YELLOW, Good.GREEN);
        AbandonedStation card = new AbandonedStation(8, CardLevel.LEARNER, 5, goods, 1);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.length() > 0);
    }

    /**
     * Tests visualize with no goods.
     */
    @Test
    public void testVisualizeWithNoGoods() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 52);
        json.addProperty("level", "LEARNER");
        json.addProperty("crewRequired", 1); // Add required field
        json.addProperty("days", 0);
        json.addProperty("imagePath", "test.jpg");
        
        JsonObject reward = new JsonObject();
        JsonArray goods = new JsonArray();
        reward.add("goods", goods);
        json.add("reward", reward);
        
        AbandonedStation card = new AbandonedStation(json);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.length() > 0);
    }
}