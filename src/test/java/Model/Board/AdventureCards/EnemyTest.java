package Model.Board.AdventureCards;

import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Rewards.Credits;
import Model.Enums.CardLevel;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    // Test implementation of Enemy for testing
    private static class TestEnemy extends Enemy<DaysPenalty, Credits> {
        public TestEnemy(int id, CardLevel level, int power, int lossDays, int winDays, int credits) {
            super(id, level, power, new DaysPenalty(lossDays), winDays, new Credits(credits));
        }
        
        public TestEnemy(JsonObject json, DaysPenalty lossPenalty, Credits winReward) {
            super(json, lossPenalty, winReward);
        }

        @Override
        public String getName() {
            return "Test Enemy";
        }

        @Override
        public String getDescription() {
            return "Test Enemy Description";
        }
    }

    @Test
    public void testConstructor() {
        TestEnemy enemy = new TestEnemy(1, CardLevel.LEVEL_TWO, 4, 2, 1, 3);
        assertEquals(1, enemy.getId());
        assertEquals(CardLevel.LEVEL_TWO, enemy.getLevel());
        assertEquals("Test Enemy", enemy.getName());
        assertEquals("Test Enemy Description", enemy.getDescription());
        assertEquals(4, enemy.getPower());
        assertEquals(2, enemy.getLossPenalty().getAmount());
        assertEquals(1, enemy.getWinPenalty().getAmount());
        assertEquals(3, enemy.getWinReward().getAmount());
    }

    @Test
    public void testJsonConstructor() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("power", 5);
        json.addProperty("imagePath", "test/path.jpg");
        
        JsonObject penalty = new JsonObject();
        penalty.addProperty("days", 2);
        json.add("penalty", penalty);
        
        TestEnemy enemy = new TestEnemy(json, new DaysPenalty(3), new Credits(4));
        assertEquals(2, enemy.getId());
        assertEquals(CardLevel.LEVEL_TWO, enemy.getLevel());
        assertEquals(5, enemy.getPower());
        assertEquals(3, enemy.getLossPenalty().getAmount());
        assertEquals(2, enemy.getWinPenalty().getAmount());
        assertEquals(4, enemy.getWinReward().getAmount());
    }
}