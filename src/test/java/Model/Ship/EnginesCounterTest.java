package Model.Ship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnginesCounterTest {

    @Test
    public void testConstructor() {
        EnginesCounter counter = new EnginesCounter();
        assertEquals(0, counter.getSingleEngines());
        assertEquals(0, counter.getDoubleEngines());
    }

    @Test
    public void testSingleEngines() {
        EnginesCounter counter = new EnginesCounter();
        
        counter.setSingleEngines(3);
        assertEquals(3, counter.getSingleEngines());
        
        counter.incrementSingleEngines();
        assertEquals(4, counter.getSingleEngines());
        
        counter.decrementSingleEngines();
        assertEquals(3, counter.getSingleEngines());
        
        counter.setSingleEngines(0);
        assertThrows(IllegalArgumentException.class, counter::decrementSingleEngines);
    }

    @Test
    public void testDoubleEngines() {
        EnginesCounter counter = new EnginesCounter();
        
        counter.setDoubleEngines(2);
        assertEquals(2, counter.getDoubleEngines());
        
        counter.incrementDoubleEngines();
        assertEquals(3, counter.getDoubleEngines());
        
        counter.decrementDoubleEngines();
        assertEquals(2, counter.getDoubleEngines());
        
        counter.setDoubleEngines(0);
        assertThrows(IllegalArgumentException.class, counter::decrementDoubleEngines);
    }
}