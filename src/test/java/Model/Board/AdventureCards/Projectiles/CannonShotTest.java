package Model.Board.AdventureCards.Projectiles;

import Model.Enums.Side;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CannonShotTest {

    @Test
    public void testConstructor() {
        CannonShot shot = new CannonShot(true, Side.FRONT);
        assertTrue(shot.isBig());
        assertEquals(Side.FRONT, shot.getSide());
    }
    
    @Test
    public void testSingleShot() {
        CannonShot shot = new CannonShot(false, Side.LEFT);
        assertFalse(shot.isBig());
        assertEquals(Side.LEFT, shot.getSide());
    }
    
    @Test
    public void testAllSides() {
        CannonShot front = new CannonShot(false, Side.FRONT);
        assertEquals(Side.FRONT, front.getSide());
        
        CannonShot left = new CannonShot(false, Side.LEFT);
        assertEquals(Side.LEFT, left.getSide());
        
        CannonShot right = new CannonShot(false, Side.RIGHT);
        assertEquals(Side.RIGHT, right.getSide());
        
        CannonShot rear = new CannonShot(false, Side.REAR);
        assertEquals(Side.REAR, rear.getSide());
    }

    @Test
    public void testInheritance() {
        CannonShot shot = new CannonShot(true, Side.FRONT);
        assertTrue(shot instanceof Projectile);
        assertTrue(shot instanceof CannonShot);
    }

    @Test
    public void testBigAndSmallShots() {
        CannonShot bigShot = new CannonShot(true, Side.FRONT);
        CannonShot smallShot = new CannonShot(false, Side.FRONT);
        
        assertTrue(bigShot.isBig());
        assertFalse(smallShot.isBig());
        assertEquals(bigShot.getSide(), smallShot.getSide());
    }

    @Test
    public void testGettersConsistency() {
        CannonShot shot = new CannonShot(true, Side.LEFT);
        
        assertEquals(shot.isBig(), shot.isBig());
        assertEquals(shot.getSide(), shot.getSide());
        assertSame(shot.getSide(), shot.getSide());
    }
}