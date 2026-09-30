package Model.Board.AdventureCards.Penalties;

import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Good;
import Model.Enums.Side;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for all penalty types.
 * Tests inheritance hierarchy, functionality, and edge cases.
 */
public class ComprehensivePenaltyTest {

    /**
     * Tests DaysPenalty functionality and inheritance.
     */
    @Test
    public void testDaysPenalty() {
        DaysPenalty penalty = new DaysPenalty(3);
        
        // Test inheritance
        assertTrue(penalty instanceof Penalty);
        assertTrue(penalty instanceof RegularPenalty);
        
        // Test functionality
        assertEquals(3, penalty.getAmount());
        
        // Test edge cases
        DaysPenalty zeroPenalty = new DaysPenalty(0);
        assertEquals(0, zeroPenalty.getAmount());
        
        DaysPenalty negativePenalty = new DaysPenalty(-1);
        assertEquals(-1, negativePenalty.getAmount());
        
        // Test large values
        DaysPenalty largePenalty = new DaysPenalty(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, largePenalty.getAmount());
    }

    /**
     * Tests CrewPenalty functionality and inheritance.
     */
    @Test
    public void testCrewPenalty() {
        CrewPenalty penalty = new CrewPenalty(2);
        
        // Test inheritance
        assertTrue(penalty instanceof Penalty);
        assertTrue(penalty instanceof RegularPenalty);
        
        // Test functionality
        assertEquals(2, penalty.getAmount());
        
        // Test edge cases
        CrewPenalty zeroPenalty = new CrewPenalty(0);
        assertEquals(0, zeroPenalty.getAmount());
        
        CrewPenalty largePenalty = new CrewPenalty(10);
        assertEquals(10, largePenalty.getAmount());
    }

    /**
     * Tests GoodsPenalty functionality and inheritance.
     */
    @Test
    public void testGoodsPenalty() {
        GoodsPenalty penalty = new GoodsPenalty(5);
        
        // Test inheritance
        assertTrue(penalty instanceof Penalty);
        assertTrue(penalty instanceof RegularPenalty);
        
        // Test functionality
        assertEquals(5, penalty.getAmount());
        
        // Test various amounts
        for (int i = 0; i <= 10; i++) {
            GoodsPenalty testPenalty = new GoodsPenalty(i);
            assertEquals(i, testPenalty.getAmount());
        }
    }

    /**
     * Tests CannonShotPenalty functionality with different shot configurations.
     */
    @Test
    public void testCannonShotPenalty() {
        // Test with empty shot list
        List<CannonShot> emptyShots = new ArrayList<>();
        CannonShotPenalty emptyPenalty = new CannonShotPenalty(emptyShots);
        assertTrue(emptyPenalty instanceof Penalty);
        
        int count = 0;
        for (CannonShot shot : emptyPenalty) {
            count++;
        }
        assertEquals(0, count);
        
        // Test with single shot
        List<CannonShot> singleShot = new ArrayList<>();
        singleShot.add(new CannonShot(true, Side.FRONT));
        CannonShotPenalty singlePenalty = new CannonShotPenalty(singleShot);
        
        count = 0;
        boolean hasLargeFront = false;
        for (CannonShot shot : singlePenalty) {
            count++;
            if (shot.isBig() && shot.getSide() == Side.FRONT) {
                hasLargeFront = true;
            }
        }
        assertEquals(1, count);
        assertTrue(hasLargeFront);
        
        // Test with multiple shots
        List<CannonShot> multipleShots = new ArrayList<>();
        multipleShots.add(new CannonShot(true, Side.FRONT));
        multipleShots.add(new CannonShot(false, Side.LEFT));
        multipleShots.add(new CannonShot(false, Side.RIGHT));
        multipleShots.add(new CannonShot(true, Side.REAR));
        
        CannonShotPenalty multiplePenalty = new CannonShotPenalty(multipleShots);
        
        count = 0;
        int largeShots = 0, smallShots = 0;
        boolean[] sidesHit = new boolean[4]; // FRONT, LEFT, RIGHT, REAR
        
        for (CannonShot shot : multiplePenalty) {
            count++;
            if (shot.isBig()) largeShots++;
            else smallShots++;
            
            switch (shot.getSide()) {
                case FRONT -> sidesHit[0] = true;
                case LEFT -> sidesHit[1] = true;
                case RIGHT -> sidesHit[2] = true;
                case REAR -> sidesHit[3] = true;
            }
        }
        
        assertEquals(4, count);
        assertEquals(2, largeShots);
        assertEquals(2, smallShots);
        assertTrue(sidesHit[0]); // FRONT
        assertTrue(sidesHit[1]); // LEFT
        assertTrue(sidesHit[2]); // RIGHT
        assertTrue(sidesHit[3]); // REAR
    }

    /**
     * Tests RegularPenalty abstract class behavior.
     */
    @Test
    public void testRegularPenaltyHierarchy() {
        // Test that all regular penalties extend RegularPenalty
        DaysPenalty daysPenalty = new DaysPenalty(1);
        CrewPenalty crewPenalty = new CrewPenalty(2);
        GoodsPenalty goodsPenalty = new GoodsPenalty(3);
        
        assertTrue(daysPenalty instanceof RegularPenalty);
        assertTrue(crewPenalty instanceof RegularPenalty);
        assertTrue(goodsPenalty instanceof RegularPenalty);
        
        // Test that they all have getAmount method
        assertEquals(1, daysPenalty.getAmount());
        assertEquals(2, crewPenalty.getAmount());
        assertEquals(3, goodsPenalty.getAmount());
        
        // Test polymorphism
        RegularPenalty[] penalties = {daysPenalty, crewPenalty, goodsPenalty};
        int[] expectedAmounts = {1, 2, 3};
        
        for (int i = 0; i < penalties.length; i++) {
            assertEquals(expectedAmounts[i], penalties[i].getAmount());
        }
    }

    /**
     * Tests penalty equality and consistency.
     */
    @Test
    public void testPenaltyConsistency() {
        // Test that penalties with same values behave consistently
        DaysPenalty days1 = new DaysPenalty(5);
        DaysPenalty days2 = new DaysPenalty(5);
        
        assertEquals(days1.getAmount(), days2.getAmount());
        
        // Test different penalty types with same amounts
        CrewPenalty crew = new CrewPenalty(5);
        GoodsPenalty goods = new GoodsPenalty(5);
        
        assertEquals(days1.getAmount(), crew.getAmount());
        assertEquals(crew.getAmount(), goods.getAmount());
        
        // But they should be different types
        assertNotEquals(days1.getClass(), crew.getClass());
        assertNotEquals(crew.getClass(), goods.getClass());
    }

    /**
     * Tests CannonShotPenalty with complex shot patterns.
     */
    @Test
    public void testComplexCannonShotPatterns() {
        // Test pattern from real Pirates card (ID 20)
        List<CannonShot> pirateShots = new ArrayList<>();
        pirateShots.add(new CannonShot(false, Side.FRONT)); // Small front
        pirateShots.add(new CannonShot(true, Side.FRONT));  // Large front
        pirateShots.add(new CannonShot(false, Side.FRONT)); // Small front
        
        CannonShotPenalty piratePenalty = new CannonShotPenalty(pirateShots);
        
        int frontShots = 0, largeShots = 0, smallShots = 0;
        for (CannonShot shot : piratePenalty) {
            if (shot.getSide() == Side.FRONT) frontShots++;
            if (shot.isBig()) largeShots++;
            else smallShots++;
        }
        
        assertEquals(3, frontShots);
        assertEquals(1, largeShots);
        assertEquals(2, smallShots);
        
        // Test pattern from real Pirates card (ID 39) - Level 2
        List<CannonShot> level2Shots = new ArrayList<>();
        level2Shots.add(new CannonShot(true, Side.FRONT));  // Large front
        level2Shots.add(new CannonShot(false, Side.FRONT)); // Small front
        level2Shots.add(new CannonShot(true, Side.FRONT));  // Large front
        
        CannonShotPenalty level2Penalty = new CannonShotPenalty(level2Shots);
        
        frontShots = 0;
        largeShots = 0;
        smallShots = 0;
        for (CannonShot shot : level2Penalty) {
            if (shot.getSide() == Side.FRONT) frontShots++;
            if (shot.isBig()) largeShots++;
            else smallShots++;
        }
        
        assertEquals(3, frontShots);
        assertEquals(2, largeShots); // More large shots in level 2
        assertEquals(1, smallShots);
    }

    /**
     * Tests penalty behavior with extreme values.
     */
    @Test
    public void testExtremeValues() {
        // Test maximum values
        DaysPenalty maxDays = new DaysPenalty(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, maxDays.getAmount());
        
        CrewPenalty maxCrew = new CrewPenalty(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, maxCrew.getAmount());
        
        // Test minimum values
        DaysPenalty minDays = new DaysPenalty(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, minDays.getAmount());
        
        // Test with very large cannon shot list
        List<CannonShot> manyShots = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            manyShots.add(new CannonShot(i % 2 == 0, Side.values()[i % 4]));
        }
        
        CannonShotPenalty manyPenalty = new CannonShotPenalty(manyShots);
        int count = 0;
        for (CannonShot shot : manyPenalty) {
            count++;
        }
        assertEquals(100, count);
    }
}