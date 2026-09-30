package Model.Ship;

import Model.Enums.Good;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GoodCounterTest {

    @Test
    public void testConstructor() {
        GoodCounter counter = new GoodCounter();
        assertEquals(0, counter.getRed());
        assertEquals(0, counter.getYellow());
        assertEquals(0, counter.getGreen());
        assertEquals(0, counter.getBlue());
    }

    @Test
    public void testAddGoods() {
        GoodCounter counter = new GoodCounter();
        
        counter.addGood(Good.RED);
        assertEquals(1, counter.getRed());
        
        counter.addGood(Good.YELLOW);
        assertEquals(1, counter.getYellow());
        
        counter.addGood(Good.GREEN);
        assertEquals(1, counter.getGreen());
        
        counter.addGood(Good.BLUE);
        assertEquals(1, counter.getBlue());
        
        counter.addGood(Good.RED);
        assertEquals(2, counter.getRed());
    }

    @Test
    public void testRemoveGoods() {
        GoodCounter counter = new GoodCounter();
        
        counter.addGood(Good.RED);
        counter.addGood(Good.YELLOW);
        counter.addGood(Good.GREEN);
        counter.addGood(Good.BLUE);
        
        assertTrue(counter.removeGood(Good.RED));
        assertEquals(0, counter.getRed());
        
        assertTrue(counter.removeGood(Good.YELLOW));
        assertEquals(0, counter.getYellow());
        
        assertTrue(counter.removeGood(Good.GREEN));
        assertEquals(0, counter.getGreen());
        
        assertTrue(counter.removeGood(Good.BLUE));
        assertEquals(0, counter.getBlue());
        
        assertFalse(counter.removeGood(Good.RED));
        assertFalse(counter.removeGood(Good.YELLOW));
        assertFalse(counter.removeGood(Good.GREEN));
        assertFalse(counter.removeGood(Good.BLUE));
    }
}