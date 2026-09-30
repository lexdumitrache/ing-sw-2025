package Model.Utils;

import Model.Enums.Direction;
import Model.Enums.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionSideUtilsTest {

    @Test
    public void testDirectionToSide() {
        assertEquals(Side.FRONT, DirectionSideUtils.convertDirectionToSide(Direction.UP));
        assertEquals(Side.RIGHT, DirectionSideUtils.convertDirectionToSide(Direction.RIGHT));
        assertEquals(Side.REAR, DirectionSideUtils.convertDirectionToSide(Direction.DOWN));
        assertEquals(Side.LEFT, DirectionSideUtils.convertDirectionToSide(Direction.LEFT));
    }
    
    @Test
    public void testSideToDirection() {
        assertEquals(Direction.UP, DirectionSideUtils.convertSideToDirection(Side.FRONT));
        assertEquals(Direction.RIGHT, DirectionSideUtils.convertSideToDirection(Side.RIGHT));
        assertEquals(Direction.DOWN, DirectionSideUtils.convertSideToDirection(Side.REAR));
        assertEquals(Direction.LEFT, DirectionSideUtils.convertSideToDirection(Side.LEFT));
    }
    
    @Test
    public void testInvalidValues() {
        // Test with null values if the implementation handles them
        assertThrows(NullPointerException.class, () -> DirectionSideUtils.convertDirectionToSide(null));
        assertThrows(NullPointerException.class, () -> DirectionSideUtils.convertSideToDirection(null));
    }
}