package Model.Ship;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShieldCounterTest {

    @Test
    public void testConstructor() {
        ShieldCounter counter = new ShieldCounter();
        assertEquals(0, counter.getNorthShields());
        assertEquals(0, counter.getEastShields());
        assertEquals(0, counter.getSouthShields());
        assertEquals(0, counter.getWestShields());
    }

    @Test
    public void testNorthShields() {
        ShieldCounter counter = new ShieldCounter();
        counter.setNorthShields(5);
        assertEquals(5, counter.getNorthShields());
        
        counter.incrementNorthShields();
        assertEquals(6, counter.getNorthShields());
        
        counter.decrementNorthShields();
        assertEquals(5, counter.getNorthShields());
        
        counter.setNorthShields(0);
        assertThrows(IllegalArgumentException.class, counter::decrementNorthShields);
    }

    @Test
    public void testEastShields() {
        ShieldCounter counter = new ShieldCounter();
        counter.setEastShields(3);
        assertEquals(3, counter.getEastShields());
        
        counter.incrementEastShields();
        assertEquals(4, counter.getEastShields());
        
        counter.decrementEastShields();
        assertEquals(3, counter.getEastShields());
        
        counter.setEastShields(0);
        assertThrows(IllegalArgumentException.class, counter::decrementEastShields);
    }

    @Test
    public void testSouthShields() {
        ShieldCounter counter = new ShieldCounter();
        counter.setSouthShields(2);
        assertEquals(2, counter.getSouthShields());
        
        counter.incrementSouthShields();
        assertEquals(3, counter.getSouthShields());
        
        counter.decrementSouthShields();
        assertEquals(2, counter.getSouthShields());
        
        counter.setSouthShields(0);
        assertThrows(IllegalArgumentException.class, counter::decrementSouthShields);
    }

    @Test
    public void testWestShields() {
        ShieldCounter counter = new ShieldCounter();
        counter.setWestShields(4);
        assertEquals(4, counter.getWestShields());
        
        counter.incrementWestShields();
        assertEquals(5, counter.getWestShields());
        
        counter.decrementWestShields();
        assertEquals(4, counter.getWestShields());
        
        counter.setWestShields(0);
        assertThrows(IllegalArgumentException.class, counter::decrementWestShields);
    }
}