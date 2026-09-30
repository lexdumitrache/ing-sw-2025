package Model.Ship;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Coordinates class which represents a position on the ship board.
 * Tests basic functionality like construction, getters, and utility methods.
 */
public class CoordinatesTest {

    /**
     * Tests the constructor and getters.
     */
    @Test
    public void testConstructorAndGetters() {
        Coordinates coordinates = new Coordinates(3, 4);
        assertEquals(3, coordinates.getI());
        assertEquals(4, coordinates.getJ());
    }
    
    /**
     * Tests constructor with zero coordinates.
     */
    @Test
    public void testConstructorZeroCoordinates() {
        Coordinates coordinates = new Coordinates(0, 0);
        assertEquals(0, coordinates.getI());
        assertEquals(0, coordinates.getJ());
    }
    
    /**
     * Tests constructor with negative coordinates.
     */
    @Test
    public void testConstructorNegativeCoordinates() {
        Coordinates coordinates = new Coordinates(-1, -2);
        assertEquals(-1, coordinates.getI());
        assertEquals(-2, coordinates.getJ());
    }
    
    /**
     * Tests the toString method.
     */
    @Test
    public void testToString() {
        Coordinates coordinates = new Coordinates(3, 4);
        String expected = "Coordinates{i=3, j=4}";
        assertEquals(expected, coordinates.toString());
        
        Coordinates coordinates2 = new Coordinates(0, 0);
        assertEquals("Coordinates{i=0, j=0}", coordinates2.toString());
        
        Coordinates coordinates3 = new Coordinates(-1, -2);
        assertEquals("Coordinates{i=-1, j=-2}", coordinates3.toString());
    }
    
    /**
     * Tests the manhattanDistance method.
     */
    @Test
    public void testManhattanDistance() {
        Coordinates coord1 = new Coordinates(0, 0);
        Coordinates coord2 = new Coordinates(3, 4);
        
        assertEquals(7, coord1.manhattanDistance(coord2));
        assertEquals(7, coord2.manhattanDistance(coord1)); // Symmetric
        
        // Same coordinates
        assertEquals(0, coord1.manhattanDistance(new Coordinates(0, 0)));
        
        // Adjacent coordinates
        assertEquals(1, coord1.manhattanDistance(new Coordinates(0, 1)));
        assertEquals(1, coord1.manhattanDistance(new Coordinates(1, 0)));
        
        // Negative coordinates
        Coordinates coord3 = new Coordinates(-2, -3);
        Coordinates coord4 = new Coordinates(1, 2);
        assertEquals(8, coord3.manhattanDistance(coord4));
    }
    
    /**
     * Tests manhattanDistance with same coordinates.
     */
    @Test
    public void testManhattanDistanceSameCoordinates() {
        Coordinates coord = new Coordinates(5, 7);
        assertEquals(0, coord.manhattanDistance(coord));
        assertEquals(0, coord.manhattanDistance(new Coordinates(5, 7)));
    }
    
    /**
     * Tests object identity vs equality.
     * Since equals() is not implemented, objects are only equal if they're the same instance.
     */
    @Test
    public void testObjectIdentity() {
        Coordinates coord1 = new Coordinates(3, 4);
        Coordinates coord2 = new Coordinates(3, 4);
        
        // Same instance
        assertSame(coord1, coord1);
        
        // Different instances (no equals() method implemented)
        assertNotSame(coord1, coord2);
        
        // Default equals behavior (reference equality)
        assertEquals(coord1, coord1);
        assertNotEquals(coord1, coord2); // Different objects
    }
    
    /**
     * Tests hashCode consistency.
     * Since hashCode() is not overridden, each instance has different hash codes.
     */
    @Test
    public void testHashCodeConsistency() {
        Coordinates coord1 = new Coordinates(3, 4);
        Coordinates coord2 = new Coordinates(3, 4);
        
        // Same object should have consistent hash code
        assertEquals(coord1.hashCode(), coord1.hashCode());
        
        // Different objects will have different hash codes (default Object.hashCode())
        assertNotEquals(coord1.hashCode(), coord2.hashCode());
    }
    
    /**
     * Tests with large coordinate values.
     */
    @Test
    public void testLargeCoordinates() {
        Coordinates coord = new Coordinates(1000, 2000);
        assertEquals(1000, coord.getI());
        assertEquals(2000, coord.getJ());
        
        Coordinates origin = new Coordinates(0, 0);
        assertEquals(3000, origin.manhattanDistance(coord));
    }
}