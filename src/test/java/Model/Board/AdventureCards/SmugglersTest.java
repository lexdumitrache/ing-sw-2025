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
 * Tests for the Smugglers class which represents smuggler encounters.
 * Tests constructor, JSON parsing, and visualization functionality.
 */
public class SmugglersTest {

    /**
     * Tests JSON constructor with actual card ID 5 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard5() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 5);
        json.addProperty("type", "Smugglers");
        json.addProperty("level", "LEARNER");
        json.addProperty("power", 4);
        
        // Add required penalty field
        JsonObject penalty = new JsonObject();
        penalty.addProperty("stealGoodsOnLoss", 2);
        penalty.addProperty("days", 1);
        JsonArray shots = new JsonArray();
        JsonObject shot = new JsonObject();
        shot.addProperty("isLarge", false);
        shot.addProperty("direction", "FRONT");
        shots.add(shot);
        penalty.add("shots", shots);
        json.add("penalty", penalty);
        
        JsonArray rewardGoods = new JsonArray();
        rewardGoods.add(new JsonPrimitive("YELLOW"));
        rewardGoods.add(new JsonPrimitive("GREEN"));
        rewardGoods.add(new JsonPrimitive("BLUE"));
        json.add("rewardGoods", rewardGoods);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/5.jpg");
        
        Smugglers card = new Smugglers(json);
        assertEquals(5, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
    }

    /**
     * Tests JSON constructor with actual card ID 40 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard40() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 40);
        json.addProperty("type", "Smugglers");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("power", 8);
        
        // Add required penalty field
        JsonObject penalty = new JsonObject();
        penalty.addProperty("stealGoodsOnLoss", 3);
        penalty.addProperty("days", 2);
        JsonArray shots = new JsonArray();
        JsonObject shot = new JsonObject();
        shot.addProperty("isLarge", false);
        shot.addProperty("direction", "FRONT");
        shots.add(shot);
        penalty.add("shots", shots);
        json.add("penalty", penalty);
        
        JsonArray rewardGoods = new JsonArray();
        rewardGoods.add(new JsonPrimitive("RED"));
        rewardGoods.add(new JsonPrimitive("YELLOW"));
        rewardGoods.add(new JsonPrimitive("YELLOW"));
        json.add("rewardGoods", rewardGoods);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/40.jpg");
        
        Smugglers card = new Smugglers(json);
        assertEquals(40, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
    }

    /**
     * Tests JSON constructor with actual card ID 45 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard45() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 45);
        json.addProperty("type", "Smugglers");
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("power", 4);
        
        // Add required penalty field
        JsonObject penalty = new JsonObject();
        penalty.addProperty("stealGoodsOnLoss", 2);
        penalty.addProperty("days", 1);
        JsonArray shots = new JsonArray();
        JsonObject shot = new JsonObject();
        shot.addProperty("isLarge", false);
        shot.addProperty("direction", "FRONT");
        shots.add(shot);
        penalty.add("shots", shots);
        json.add("penalty", penalty);
        
        JsonArray rewardGoods = new JsonArray();
        rewardGoods.add(new JsonPrimitive("YELLOW"));
        rewardGoods.add(new JsonPrimitive("GREEN"));
        rewardGoods.add(new JsonPrimitive("BLUE"));
        json.add("rewardGoods", rewardGoods);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/5.jpg");
        
        Smugglers card = new Smugglers(json);
        assertEquals(45, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        List<Good> goods = Arrays.asList(Good.RED, Good.BLUE, Good.GREEN);
        Smugglers card = new Smugglers(5, CardLevel.LEARNER, 4, 2, 1, goods);
        assertEquals(5, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Contrabbandieri", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests the visualize method using JSON constructor.
     */
    @Test
    public void testVisualize() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 5);
        json.addProperty("type", "Smugglers");
        json.addProperty("level", "LEARNER");
        json.addProperty("power", 4);
        
        // Add required penalty field
        JsonObject penalty = new JsonObject();
        penalty.addProperty("stealGoodsOnLoss", 2);
        penalty.addProperty("days", 1);
        JsonArray shots = new JsonArray();
        JsonObject shot = new JsonObject();
        shot.addProperty("isLarge", false);
        shot.addProperty("direction", "FRONT");
        shots.add(shot);
        penalty.add("shots", shots);
        json.add("penalty", penalty);
        
        JsonArray rewardGoods = new JsonArray();
        rewardGoods.add(new JsonPrimitive("YELLOW"));
        rewardGoods.add(new JsonPrimitive("GREEN"));
        rewardGoods.add(new JsonPrimitive("BLUE"));
        json.add("rewardGoods", rewardGoods);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/5.jpg");
        
        Smugglers card = new Smugglers(json);

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
        JsonObject json = new JsonObject();
        json.addProperty("id", 5);
        json.addProperty("level", "LEARNER");
        json.addProperty("power", 4);
        json.addProperty("imagePath", "test.jpg");
        
        JsonObject penalty = new JsonObject();
        penalty.addProperty("stealGoodsOnLoss", 2);
        penalty.addProperty("days", 1);
        json.add("penalty", penalty);
        
        JsonArray rewardGoods = new JsonArray();
        rewardGoods.add(new JsonPrimitive("RED"));
        json.add("rewardGoods", rewardGoods);
        
        Smugglers card = new Smugglers(json);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 5", result[1]);
        assertEquals("Nome: Contrabbandieri", result[2]);
        assertEquals("Livello: LEARNER", result[3]);
        assertEquals("Power:                 4", result[4]);
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        List<Good> goods = Arrays.asList(Good.RED);
        Smugglers card = new Smugglers(5, CardLevel.LEARNER, 4, 2, 1, goods);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }
}