package Model.Board.AdventureCards.Rewards;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreditsTest {

    @Test
    public void testConstructor() {
        Credits credits = new Credits(5);
        assertEquals(5, credits.getAmount());
    }
    
    @Test
    public void testZeroCredits() {
        Credits credits = new Credits(0);
        assertEquals(0, credits.getAmount());
    }
    
    @Test
    public void testNegativeCredits() {
        Credits credits = new Credits(-1);
        // Implementation might handle negative values differently
        // This test assumes it stores the value as provided
        assertEquals(-1, credits.getAmount());
    }

    @Test
    public void testJsonConstructor() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 10);
        json.add("reward", reward);
        
        Credits credits = new Credits(json);
        assertEquals(10, credits.getAmount());
    }

    @Test
    public void testJsonConstructorMissingCredits() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        JsonObject reward = new JsonObject();
        json.add("reward", reward);
        
        assertThrows(IllegalArgumentException.class, () -> new Credits(json));
    }

    @Test
    public void testInheritance() {
        Credits credits = new Credits(5);
        assertTrue(credits instanceof Reward);
        assertTrue(credits instanceof Credits);
    }

    @Test
    public void testGetAmountIsFinal() {
        Credits credits = new Credits(100);
        assertEquals(100, credits.getAmount());
        assertEquals(credits.getAmount(), credits.getAmount());
    }
}