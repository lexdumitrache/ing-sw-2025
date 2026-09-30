package Model.Utils;

import Model.Enums.CardLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for CardLevelMapper utility class.
 * Tests mapping between JSON level strings and CardLevel enum values.
 */
public class CardLevelMapperTest {

    /**
     * Tests correct mapping of all valid JSON level strings.
     */
    @Test
    public void testMapJsonLevelToCardLevel() {
        assertEquals(CardLevel.LEARNER, CardLevelMapper.mapJsonLevelToCardLevel("TRIAL"));
        assertEquals(CardLevel.LEVEL_ONE, CardLevelMapper.mapJsonLevelToCardLevel("LEVEL1"));
        assertEquals(CardLevel.LEVEL_TWO, CardLevelMapper.mapJsonLevelToCardLevel("LEVEL2"));
        assertEquals(CardLevel.LEVEL_THREE, CardLevelMapper.mapJsonLevelToCardLevel("LEVEL3"));
    }

    /**
     * Tests null input throws NullPointerException.
     */
    @Test
    public void testNullInput() {
        assertThrows(NullPointerException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel(null));
    }

    /**
     * Tests invalid level string throws IllegalArgumentException.
     */
    @Test
    public void testInvalidLevelString() {
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("LEVEL4"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("level1"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel(""));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("LEARNER"));
    }

    /**
     * Tests case sensitivity - mapper expects exact case.
     */
    @Test
    public void testCaseSensitivity() {
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("trial"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("Trial"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("level1"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("Level1"));
    }

    /**
     * Tests comparison with direct CardLevel.valueOf approach.
     * This tests both approaches mentioned in AdventureCardFilip.
     */
    @Test
    public void testComparisonWithDirectValueOf() {
        // Test cases where CardLevelMapper and CardLevel.valueOf would give different results
        
        // CardLevelMapper approach
        assertEquals(CardLevel.LEARNER, CardLevelMapper.mapJsonLevelToCardLevel("TRIAL"));
        assertEquals(CardLevel.LEVEL_ONE, CardLevelMapper.mapJsonLevelToCardLevel("LEVEL1"));
        
        // Direct CardLevel.valueOf approach (what's currently used in AdventureCardFilip)
        assertEquals(CardLevel.LEARNER, CardLevel.valueOf("LEARNER"));
        assertEquals(CardLevel.LEVEL_ONE, CardLevel.valueOf("LEVEL_ONE"));
        assertEquals(CardLevel.LEVEL_TWO, CardLevel.valueOf("LEVEL_TWO"));
        
        // Show the difference - these would fail with CardLevel.valueOf
        assertThrows(IllegalArgumentException.class, () -> CardLevel.valueOf("TRIAL"));
        assertThrows(IllegalArgumentException.class, () -> CardLevel.valueOf("LEVEL1"));
        assertThrows(IllegalArgumentException.class, () -> CardLevel.valueOf("LEVEL2"));
    }

    /**
     * Tests edge cases with whitespace and special characters.
     */
    @Test
    public void testEdgeCases() {
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel(" TRIAL "));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("TRIAL\n"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("\tLEVEL1"));
    }

    /**
     * Tests all CardLevel enum values for completeness.
     */
    @Test
    public void testAllCardLevelValues() {
        // Verify all CardLevel enum values exist
        CardLevel[] allLevels = CardLevel.values();
        assertTrue(allLevels.length >= 4);
        
        boolean hasLearner = false, hasLevelOne = false, hasLevelTwo = false, hasLevelThree = false;
        for (CardLevel level : allLevels) {
            switch (level) {
                case LEARNER -> hasLearner = true;
                case LEVEL_ONE -> hasLevelOne = true;
                case LEVEL_TWO -> hasLevelTwo = true;
                case LEVEL_THREE -> hasLevelThree = true;
            }
        }
        
        assertTrue(hasLearner);
        assertTrue(hasLevelOne);
        assertTrue(hasLevelTwo);
        assertTrue(hasLevelThree);
    }

    /**
     * Tests mapper consistency - same input should always give same output.
     */
    @Test
    public void testMapperConsistency() {
        String[] inputs = {"TRIAL", "LEVEL1", "LEVEL2", "LEVEL3"};
        
        for (String input : inputs) {
            CardLevel result1 = CardLevelMapper.mapJsonLevelToCardLevel(input);
            CardLevel result2 = CardLevelMapper.mapJsonLevelToCardLevel(input);
            assertEquals(result1, result2, "Mapper should be consistent for input: " + input);
        }
    }
}