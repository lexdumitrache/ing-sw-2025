package Model.Ship;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DoubleCannonsCounter class which tracks double cannons on the ship.
 * Tests both front-facing and other-direction double cannon counting.
 */
public class DoubleCannonsCounterTest {

    /**
     * Tests the default constructor to ensure a new DoubleCannonsCounter is properly initialized:
     * - Both front and other cannon counts should start at zero
     */
    @Test
    public void testDefaultConstructor() {
        DoubleCannonsCounter counter = new DoubleCannonsCounter();
        assertEquals(0, counter.getFrontCannons());
        assertEquals(0, counter.getOtherCannons());
    }
    
    /**
     * Tests the parameterized constructor:
     * - The counter should initialize with the specified values
     * - Front and other cannon counts can be set independently
     */
    @Test
    public void testParameterizedConstructor() {
        DoubleCannonsCounter counter = new DoubleCannonsCounter(2, 3);
        assertEquals(2, counter.getFrontCannons());
        assertEquals(3, counter.getOtherCannons());
    }
    
    /**
     * Tests the getter methods:
     * - getFrontCannons() should return the correct front cannon count
     * - getOtherCannons() should return the correct other cannon count
     */
    @Test
    public void testGetters() {
        DoubleCannonsCounter counter = new DoubleCannonsCounter(1, 2);
        assertEquals(1, counter.getFrontCannons());
        assertEquals(2, counter.getOtherCannons());
    }
}