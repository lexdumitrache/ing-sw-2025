package Model.Board.AdventureCards.Components;

import Model.Enums.Good;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Planet class which represents planets in the game.
 * Tests constructor, occupation mechanics, and reward functionality.
 */
public class PlanetTest {

    /**
     * Tests the constructor with goods list.
     */
    @Test
    public void testConstructor() {
        List<Good> goods = new ArrayList<>();
        goods.add(Good.RED);
        goods.add(Good.BLUE);
        
        Planet planet = new Planet("Test Planet", goods);
        assertEquals("Test Planet", planet.getName());
        assertFalse(planet.isOccupied()); // Should start unoccupied
        
        // Count the goods in the landing reward and check if specific goods are present
        int goodsCount = 0;
        boolean hasRed = false;
        boolean hasBlue = false;
        
        for (Good g : planet.getLandingReward()) {
            goodsCount++;
            if (g == Good.RED) hasRed = true;
            if (g == Good.BLUE) hasBlue = true;
        }
        
        assertEquals(2, goodsCount);
        assertTrue(hasRed);
        assertTrue(hasBlue);
    }

    /**
     * Tests occupation mechanics - first occupation succeeds.
     */
    @Test
    public void testSetOccupiedFirstTime() {
        List<Good> goods = Arrays.asList(Good.GREEN);
        Planet planet = new Planet("Occupation Test", goods);
        
        assertFalse(planet.isOccupied());
        assertTrue(planet.setOccupied()); // Should succeed
        assertTrue(planet.isOccupied());
    }

    /**
     * Tests occupation mechanics - second occupation fails.
     */
    @Test
    public void testSetOccupiedSecondTime() {
        List<Good> goods = Arrays.asList(Good.YELLOW);
        Planet planet = new Planet("Double Occupation Test", goods);
        
        // First occupation
        assertTrue(planet.setOccupied());
        assertTrue(planet.isOccupied());
        
        // Second occupation should fail
        assertFalse(planet.setOccupied());
        assertTrue(planet.isOccupied()); // Should still be occupied
    }

    /**
     * Tests constructor with empty goods list.
     */
    @Test
    public void testConstructorWithEmptyGoods() {
        List<Good> emptyGoods = new ArrayList<>();
        Planet planet = new Planet("Empty Planet", emptyGoods);
        
        assertEquals("Empty Planet", planet.getName());
        assertFalse(planet.isOccupied());
        
        // Should have no goods
        int goodsCount = 0;
        for (Good g : planet.getLandingReward()) {
            goodsCount++;
        }
        assertEquals(0, goodsCount);
    }

    /**
     * Tests constructor with single good.
     */
    @Test
    public void testConstructorWithSingleGood() {
        List<Good> singleGood = Arrays.asList(Good.RED);
        Planet planet = new Planet("Single Good Planet", singleGood);
        
        assertEquals("Single Good Planet", planet.getName());
        
        int goodsCount = 0;
        Good foundGood = null;
        for (Good g : planet.getLandingReward()) {
            goodsCount++;
            foundGood = g;
        }
        
        assertEquals(1, goodsCount);
        assertEquals(Good.RED, foundGood);
    }

    /**
     * Tests constructor with multiple same goods.
     */
    @Test
    public void testConstructorWithMultipleSameGoods() {
        List<Good> multipleRed = Arrays.asList(Good.RED, Good.RED, Good.RED);
        Planet planet = new Planet("Multiple Red Planet", multipleRed);
        
        assertEquals("Multiple Red Planet", planet.getName());
        
        int goodsCount = 0;
        for (Good g : planet.getLandingReward()) {
            goodsCount++;
            assertEquals(Good.RED, g);
        }
        assertEquals(3, goodsCount);
    }

    /**
     * Tests constructor with all different goods.
     */
    @Test
    public void testConstructorWithAllGoods() {
        List<Good> allGoods = Arrays.asList(Good.RED, Good.BLUE, Good.GREEN, Good.YELLOW);
        Planet planet = new Planet("All Goods Planet", allGoods);
        
        assertEquals("All Goods Planet", planet.getName());
        
        int goodsCount = 0;
        boolean hasRed = false, hasBlue = false, hasGreen = false, hasYellow = false;
        
        for (Good g : planet.getLandingReward()) {
            goodsCount++;
            switch (g) {
                case RED -> hasRed = true;
                case BLUE -> hasBlue = true;
                case GREEN -> hasGreen = true;
                case YELLOW -> hasYellow = true;
            }
        }
        
        assertEquals(4, goodsCount);
        assertTrue(hasRed);
        assertTrue(hasBlue);
        assertTrue(hasGreen);
        assertTrue(hasYellow);
    }

    /**
     * Tests that getName is final (returns consistent value).
     */
    @Test
    public void testGetNameConsistency() {
        List<Good> goods = Arrays.asList(Good.BLUE);
        Planet planet = new Planet("Consistent Name", goods);
        
        String name1 = planet.getName();
        String name2 = planet.getName();
        
        assertEquals("Consistent Name", name1);
        assertEquals("Consistent Name", name2);
        assertEquals(name1, name2);
    }

    /**
     * Tests that isOccupied is final (returns consistent value).
     */
    @Test
    public void testIsOccupiedConsistency() {
        List<Good> goods = Arrays.asList(Good.GREEN);
        Planet planet = new Planet("Consistency Test", goods);
        
        assertFalse(planet.isOccupied());
        assertFalse(planet.isOccupied()); // Should be consistent
        
        planet.setOccupied();
        assertTrue(planet.isOccupied());
        assertTrue(planet.isOccupied()); // Should be consistent
    }

    /**
     * Tests that getLandingReward is final (returns same instance).
     */
    @Test
    public void testGetLandingRewardConsistency() {
        List<Good> goods = Arrays.asList(Good.YELLOW, Good.GREEN);
        Planet planet = new Planet("Reward Test", goods);
        
        var reward1 = planet.getLandingReward();
        var reward2 = planet.getLandingReward();
        
        assertSame(reward1, reward2); // Should be same instance
        assertNotNull(reward1);
        assertNotNull(reward2);
    }
}