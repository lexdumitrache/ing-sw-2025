package Model.Board.AdventureCards.Penalties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the abstract Penalty class.
 * Tests concrete implementations to verify abstract class behavior.
 */
public class PenaltyTest {

    // Test implementation of Penalty for testing
    private static class TestPenalty extends Penalty {
        private final String type;
        
        public TestPenalty(String type) {
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
    }

    /**
     * Tests that concrete implementations can extend Penalty.
     */
    @Test
    public void testConcreteImplementation() {
        TestPenalty penalty = new TestPenalty("TestType");
        
        assertNotNull(penalty);
        assertTrue(penalty instanceof Penalty);
        assertEquals("TestType", penalty.getType());
    }

    /**
     * Tests that Penalty can be used as base type.
     */
    @Test
    public void testAsBaseType() {
        Penalty penalty = new TestPenalty("BaseType");
        
        assertNotNull(penalty);
        assertTrue(penalty instanceof Penalty);
        assertTrue(penalty instanceof TestPenalty);
    }

    /**
     * Tests multiple concrete implementations.
     */
    @Test
    public void testMultipleImplementations() {
        TestPenalty penalty1 = new TestPenalty("Type1");
        TestPenalty penalty2 = new TestPenalty("Type2");
        
        assertNotEquals(penalty1.getType(), penalty2.getType());
        assertTrue(penalty1 instanceof Penalty);
        assertTrue(penalty2 instanceof Penalty);
    }
}