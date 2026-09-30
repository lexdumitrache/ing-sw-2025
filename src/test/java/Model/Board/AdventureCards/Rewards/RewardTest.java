package Model.Board.AdventureCards.Rewards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the abstract Reward class.
 * Tests concrete implementations to verify abstract class behavior.
 */
public class RewardTest {

    // Test implementation of Reward for testing
    private static class TestReward extends Reward {
        private final String type;
        
        public TestReward(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
    }

    /**
     * Tests that concrete implementations can extend Reward.
     */
    @Test
    public void testConcreteImplementation() {
        TestReward reward = new TestReward("TestType");
        
        assertNotNull(reward);
        assertTrue(reward instanceof Reward);
        assertEquals("TestType", reward.getType());
    }

    /**
     * Tests that Reward can be used as base type.
     */
    @Test
    public void testAsBaseType() {
        Reward reward = new TestReward("BaseType");
        
        assertNotNull(reward);
        assertTrue(reward instanceof Reward);
        assertTrue(reward instanceof TestReward);
    }

    /**
     * Tests multiple concrete implementations.
     */
    @Test
    public void testMultipleImplementations() {
        TestReward reward1 = new TestReward("Type1");
        TestReward reward2 = new TestReward("Type2");
        
        assertNotEquals(reward1.getType(), reward2.getType());
        assertTrue(reward1 instanceof Reward);
        assertTrue(reward2 instanceof Reward);
    }
}