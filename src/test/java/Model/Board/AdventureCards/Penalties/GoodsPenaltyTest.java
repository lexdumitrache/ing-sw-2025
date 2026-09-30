package Model.Board.AdventureCards.Penalties;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GoodsPenaltyTest {

    @Test
    public void testConstructor() {
        GoodsPenalty penalty = new GoodsPenalty(2);
        assertEquals(2, penalty.getAmount());
    }
    
    @Test
    public void testZeroGoods() {
        GoodsPenalty penalty = new GoodsPenalty(0);
        assertEquals(0, penalty.getAmount());
    }
    
    @Test
    public void testNegativeGoods() {
        GoodsPenalty penalty = new GoodsPenalty(-1);
        // Implementation might handle negative values differently
        // This test assumes it stores the value as provided
        assertEquals(-1, penalty.getAmount());
    }
}