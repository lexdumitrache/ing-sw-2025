package Model.Factories;

import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Board.AdventureCards.OpenSpace;
import Model.Board.AdventureCards.Pirates;
import Model.Enums.CardLevel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdventureCardFactoryTest {

    @Test
    public void testCreateOpenSpace() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        json.addProperty("type", "OpenSpace");
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test.png");
        
        AdventureCardFilip card = AdventureCardFactory.fromJson(json);
        
        assertNotNull(card);
        assertTrue(card instanceof OpenSpace);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
    }
    
    @Test
    public void testCreatePirates() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("type", "Pirates");
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");
        json.addProperty("power", 4);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 3);
        json.add("reward", reward);
        
        JsonObject penalty = new JsonObject();
        penalty.addProperty("days", 2);
        
        JsonArray shots = new JsonArray();
        JsonObject shot = new JsonObject();
        shot.addProperty("isLarge", false);
        shot.addProperty("direction", "FRONT");
        shots.add(shot);
        penalty.add("shots", shots);
        
        json.add("penalty", penalty);
        
        AdventureCardFilip card = AdventureCardFactory.fromJson(json);
        
        assertNotNull(card);
        assertTrue(card instanceof Pirates);
        assertEquals(2, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
    }
    
    @Test
    public void testCreateInvalidCardType() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 3);
        json.addProperty("type", "InvalidType");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        
        assertThrows(IllegalArgumentException.class, () -> AdventureCardFactory.fromJson(json));
    }
}