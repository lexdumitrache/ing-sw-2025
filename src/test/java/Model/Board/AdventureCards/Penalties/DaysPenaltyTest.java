package Model.Board.AdventureCards.Penalties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DaysPenaltyTest {

    @Test
    public void testConstructor() {
        DaysPenalty penalty = new DaysPenalty(3);
        assertEquals(3, penalty.getAmount());
    }
    
    @Test
    public void testZeroDays() {
        DaysPenalty penalty = new DaysPenalty(0);
        assertEquals(0, penalty.getAmount());
    }
    
    @Test
    public void testNegativeDays() {
        DaysPenalty penalty = new DaysPenalty(-1);
        // Implementation might handle negative values differently
        // This test assumes it stores the value as provided
        assertEquals(-1, penalty.getAmount());
    }
}