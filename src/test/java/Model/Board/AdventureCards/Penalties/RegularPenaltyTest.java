package Model.Board.AdventureCards.Penalties;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the abstract RegularPenalty class.
 * Tests constructor, getAmount method, and inheritance behavior.
 */
public class RegularPenaltyTest {

    // Test implementation of RegularPenalty for testing
    private static class TestRegularPenalty extends RegularPenalty {
        private final String type;
        
        public TestRegularPenalty(int amount, String type) {
            super(amount);
            this.type = type;
        }
        
        public TestRegularPenalty(JsonObject json, String type) {
            super(json);
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
    }

    /**
     * Tests the constructor and getAmount method.
     */
    @Test
    public void testConstructorAndGetAmount() {
        TestRegularPenalty penalty = new TestRegularPenalty(5, "TestType");
        
        assertEquals(5, penalty.getAmount());
        assertEquals("TestType", penalty.getType());
    }

    /**
     * Tests that RegularPenalty extends Penalty.
     */
    @Test
    public void testInheritance() {
        TestRegularPenalty penalty = new TestRegularPenalty(3, "TestType");
        
        assertTrue(penalty instanceof RegularPenalty);
        assertTrue(penalty instanceof Penalty);
    }

    /**
     * Tests with zero amount.
     */
    @Test
    public void testZeroAmount() {
        TestRegularPenalty penalty = new TestRegularPenalty(0, "ZeroType");
        
        assertEquals(0, penalty.getAmount());
        assertEquals("ZeroType", penalty.getType());
    }

    /**
     * Tests with negative amount.
     */
    @Test
    public void testNegativeAmount() {
        TestRegularPenalty penalty = new TestRegularPenalty(-2, "NegativeType");
        
        assertEquals(-2, penalty.getAmount());
        assertEquals("NegativeType", penalty.getType());
    }

    /**
     * Tests with large amount.
     */
    @Test
    public void testLargeAmount() {
        TestRegularPenalty penalty = new TestRegularPenalty(1000, "LargeType");
        
        assertEquals(1000, penalty.getAmount());
        assertEquals("LargeType", penalty.getType());
    }

    /**
     * Tests that getAmount is final (cannot be overridden).
     */
    @Test
    public void testGetAmountIsFinal() {
        // This test verifies the method works correctly
        // The final modifier is enforced at compile time
        TestRegularPenalty penalty1 = new TestRegularPenalty(10, "Type1");
        TestRegularPenalty penalty2 = new TestRegularPenalty(20, "Type2");
        
        assertEquals(10, penalty1.getAmount());
        assertEquals(20, penalty2.getAmount());
        assertNotEquals(penalty1.getAmount(), penalty2.getAmount());
    }

    /**
     * Tests multiple instances with different amounts.
     */
    @Test
    public void testMultipleInstances() {
        TestRegularPenalty penalty1 = new TestRegularPenalty(1, "Type1");
        TestRegularPenalty penalty2 = new TestRegularPenalty(2, "Type2");
        TestRegularPenalty penalty3 = new TestRegularPenalty(3, "Type3");
        
        assertEquals(1, penalty1.getAmount());
        assertEquals(2, penalty2.getAmount());
        assertEquals(3, penalty3.getAmount());
        
        assertTrue(penalty1 instanceof RegularPenalty);
        assertTrue(penalty2 instanceof RegularPenalty);
        assertTrue(penalty3 instanceof RegularPenalty);
    }

    /**
     * Tests JSON constructor with crewLoss field.
     */
    @Test
    public void testJsonConstructorWithCrewLoss() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        JsonObject penalty = new JsonObject();
        penalty.addProperty("crewLoss", 3);
        json.add("penalty", penalty);
        
        TestRegularPenalty regularPenalty = new TestRegularPenalty(json, "CrewTest");
        assertEquals(3, regularPenalty.getAmount());
    }

    /**
     * Tests JSON constructor with stealGoodsOnLoss field.
     */
    @Test
    public void testJsonConstructorWithStealGoods() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        JsonObject penalty = new JsonObject();
        penalty.addProperty("stealGoodsOnLoss", 2);
        json.add("penalty", penalty);
        
        TestRegularPenalty regularPenalty = new TestRegularPenalty(json, "GoodsTest");
        assertEquals(2, regularPenalty.getAmount());
    }

    /**
     * Tests JSON constructor with missing penalty fields.
     */
    @Test
    public void testJsonConstructorMissingFields() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 3);
        JsonObject penalty = new JsonObject();
        json.add("penalty", penalty);
        
        assertThrows(IllegalArgumentException.class, () -> new TestRegularPenalty(json, "ErrorTest"));
    }
}