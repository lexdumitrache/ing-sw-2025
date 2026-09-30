package Model.Board.AdventureCards.Penalties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrewPenaltyTest {

    @Test
    public void testConstructor() {
        CrewPenalty penalty = new CrewPenalty(2);
        assertEquals(2, penalty.getAmount());
    }
    
    @Test
    public void testZeroCrew() {
        CrewPenalty penalty = new CrewPenalty(0);
        assertEquals(0, penalty.getAmount());
    }
    
    @Test
    public void testNegativeCrew() {
        CrewPenalty penalty = new CrewPenalty(-1);
        // Implementation might handle negative values differently
        // This test assumes it stores the value as provided
        assertEquals(-1, penalty.getAmount());
    }
}