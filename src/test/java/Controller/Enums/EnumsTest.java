package Controller.Enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for all Controller enums.
 * Tests enum values, methods, and consistency.
 */
public class EnumsTest {

    @Test
    public void testComponentOriginEnum() {
        ComponentOrigin[] values = ComponentOrigin.values();
        assertNotNull(values);
        assertTrue(values.length > 0);
        
        // Test specific values
        assertTrue(containsValue(values, ComponentOrigin.HAND));
        assertTrue(containsValue(values, ComponentOrigin.FIRST_RESERVED));
        assertTrue(containsValue(values, ComponentOrigin.SECOND_RESERVED));
        
        // Test valueOf
        assertEquals(ComponentOrigin.HAND, ComponentOrigin.valueOf("HAND"));
        assertEquals(ComponentOrigin.FIRST_RESERVED, ComponentOrigin.valueOf("FIRST_RESERVED"));
        assertEquals(ComponentOrigin.SECOND_RESERVED, ComponentOrigin.valueOf("SECOND_RESERVED"));
        
        // Test toString
        assertNotNull(ComponentOrigin.HAND.toString());
        assertNotNull(ComponentOrigin.FIRST_RESERVED.toString());
        assertNotNull(ComponentOrigin.SECOND_RESERVED.toString());
    }

    @Test
    public void testCrewTypeEnum() {
        CrewType[] values = CrewType.values();
        assertNotNull(values);
        assertTrue(values.length > 0);
        
        // Test specific values
        assertTrue(containsValue(values, CrewType.HUMAN));
        assertTrue(containsValue(values, CrewType.BROWN_ALIEN));
        assertTrue(containsValue(values, CrewType.PURPLE_ALIEN));
        
        // Test valueOf
        assertEquals(CrewType.HUMAN, CrewType.valueOf("HUMAN"));
        assertEquals(CrewType.BROWN_ALIEN, CrewType.valueOf("BROWN_ALIEN"));
        assertEquals(CrewType.PURPLE_ALIEN, CrewType.valueOf("PURPLE_ALIEN"));
        
        // Test toString
        assertNotNull(CrewType.HUMAN.toString());
        assertNotNull(CrewType.BROWN_ALIEN.toString());
        assertNotNull(CrewType.PURPLE_ALIEN.toString());
    }

    @Test
    public void testDoubleTypeEnum() {
        DoubleType[] values = DoubleType.values();
        assertNotNull(values);
        assertTrue(values.length > 0);
        
        // Test specific values
        assertTrue(containsValue(values, DoubleType.ENGINES));
        assertTrue(containsValue(values, DoubleType.CANNONS));
        
        // Test valueOf
        assertEquals(DoubleType.ENGINES, DoubleType.valueOf("ENGINES"));
        assertEquals(DoubleType.CANNONS, DoubleType.valueOf("CANNONS"));
        
        // Test toString
        assertNotNull(DoubleType.ENGINES.toString());
        assertNotNull(DoubleType.CANNONS.toString());
    }

    @Test
    public void testItemTypeEnum() {
        ItemType[] values = ItemType.values();
        assertNotNull(values);
        assertTrue(values.length > 0);
        
        // Test specific values
        assertTrue(containsValue(values, ItemType.BATTERIES));
        assertTrue(containsValue(values, ItemType.CREW));
        
        // Test valueOf
        assertEquals(ItemType.BATTERIES, ItemType.valueOf("BATTERIES"));
        assertEquals(ItemType.CREW, ItemType.valueOf("CREW"));
        
        // Test toString
        assertNotNull(ItemType.BATTERIES.toString());
        assertNotNull(ItemType.CREW.toString());
    }

    @Test
    public void testMatchLevelEnum() {
        MatchLevel[] values = MatchLevel.values();
        assertNotNull(values);
        assertTrue(values.length > 0);
        
        // Test specific values
        assertTrue(containsValue(values, MatchLevel.TRIAL));
        assertTrue(containsValue(values, MatchLevel.LEVEL2));
        
        // Test valueOf
        assertEquals(MatchLevel.TRIAL, MatchLevel.valueOf("TRIAL"));
        assertEquals(MatchLevel.LEVEL2, MatchLevel.valueOf("LEVEL2"));
        
        // Test toString
        assertNotNull(MatchLevel.TRIAL.toString());
        assertNotNull(MatchLevel.LEVEL2.toString());
    }

    @Test
    public void testRewardTypeEnum() {
        RewardType[] values = RewardType.values();
        assertNotNull(values);
        assertTrue(values.length > 0);
        
        // Test specific values
        assertTrue(containsValue(values, RewardType.GOODS));
        assertTrue(containsValue(values, RewardType.CREDITS));
        
        // Test valueOf
        assertEquals(RewardType.GOODS, RewardType.valueOf("GOODS"));
        assertEquals(RewardType.CREDITS, RewardType.valueOf("CREDITS"));
        
        // Test toString
        assertNotNull(RewardType.GOODS.toString());
        assertNotNull(RewardType.CREDITS.toString());
    }

    @Test
    public void testEnumValueOfInvalidName() {
        // Test that valueOf throws IllegalArgumentException for invalid names
        assertThrows(IllegalArgumentException.class, () -> ComponentOrigin.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> CrewType.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> DoubleType.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> ItemType.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> MatchLevel.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> RewardType.valueOf("INVALID"));
    }

    @Test
    public void testEnumValueOfNullName() {
        // Test that valueOf throws NullPointerException for null names
        assertThrows(NullPointerException.class, () -> ComponentOrigin.valueOf(null));
        assertThrows(NullPointerException.class, () -> CrewType.valueOf(null));
        assertThrows(NullPointerException.class, () -> DoubleType.valueOf(null));
        assertThrows(NullPointerException.class, () -> ItemType.valueOf(null));
        assertThrows(NullPointerException.class, () -> MatchLevel.valueOf(null));
        assertThrows(NullPointerException.class, () -> RewardType.valueOf(null));
    }

    @Test
    public void testEnumEquality() {
        // Test enum equality
        assertEquals(ComponentOrigin.HAND, ComponentOrigin.HAND);
        assertNotEquals(ComponentOrigin.HAND, ComponentOrigin.FIRST_RESERVED);
        
        assertEquals(CrewType.HUMAN, CrewType.HUMAN);
        assertNotEquals(CrewType.HUMAN, CrewType.BROWN_ALIEN);
        
        assertEquals(DoubleType.ENGINES, DoubleType.ENGINES);
        assertNotEquals(DoubleType.ENGINES, DoubleType.CANNONS);
        
        assertEquals(ItemType.BATTERIES, ItemType.BATTERIES);
        assertNotEquals(ItemType.BATTERIES, ItemType.CREW);
        
        assertEquals(MatchLevel.TRIAL, MatchLevel.TRIAL);
        assertNotEquals(MatchLevel.TRIAL, MatchLevel.LEVEL2);
        
        assertEquals(RewardType.GOODS, RewardType.GOODS);
        assertNotEquals(RewardType.GOODS, RewardType.CREDITS);
    }

    @Test
    public void testEnumHashCode() {
        // Test that hashCode is consistent
        assertEquals(ComponentOrigin.HAND.hashCode(), ComponentOrigin.HAND.hashCode());
        assertEquals(CrewType.HUMAN.hashCode(), CrewType.HUMAN.hashCode());
        assertEquals(DoubleType.ENGINES.hashCode(), DoubleType.ENGINES.hashCode());
        assertEquals(ItemType.BATTERIES.hashCode(), ItemType.BATTERIES.hashCode());
        assertEquals(MatchLevel.TRIAL.hashCode(), MatchLevel.TRIAL.hashCode());
        assertEquals(RewardType.GOODS.hashCode(), RewardType.GOODS.hashCode());
    }

    @Test
    public void testEnumOrdinal() {
        // Test ordinal values are consistent
        ComponentOrigin[] componentOrigins = ComponentOrigin.values();
        for (int i = 0; i < componentOrigins.length; i++) {
            assertEquals(i, componentOrigins[i].ordinal());
        }
        
        CrewType[] crewTypes = CrewType.values();
        for (int i = 0; i < crewTypes.length; i++) {
            assertEquals(i, crewTypes[i].ordinal());
        }
        
        DoubleType[] doubleTypes = DoubleType.values();
        for (int i = 0; i < doubleTypes.length; i++) {
            assertEquals(i, doubleTypes[i].ordinal());
        }
        
        ItemType[] itemTypes = ItemType.values();
        for (int i = 0; i < itemTypes.length; i++) {
            assertEquals(i, itemTypes[i].ordinal());
        }
        
        MatchLevel[] matchLevels = MatchLevel.values();
        for (int i = 0; i < matchLevels.length; i++) {
            assertEquals(i, matchLevels[i].ordinal());
        }
        
        RewardType[] rewardTypes = RewardType.values();
        for (int i = 0; i < rewardTypes.length; i++) {
            assertEquals(i, rewardTypes[i].ordinal());
        }
    }

    @Test
    public void testEnumName() {
        // Test name() method
        assertEquals("HAND", ComponentOrigin.HAND.name());
        assertEquals("FIRST_RESERVED", ComponentOrigin.FIRST_RESERVED.name());
        assertEquals("SECOND_RESERVED", ComponentOrigin.SECOND_RESERVED.name());
        
        assertEquals("HUMAN", CrewType.HUMAN.name());
        assertEquals("BROWN_ALIEN", CrewType.BROWN_ALIEN.name());
        assertEquals("PURPLE_ALIEN", CrewType.PURPLE_ALIEN.name());
        
        assertEquals("ENGINES", DoubleType.ENGINES.name());
        assertEquals("CANNONS", DoubleType.CANNONS.name());
        
        assertEquals("BATTERIES", ItemType.BATTERIES.name());
        assertEquals("CREW", ItemType.CREW.name());
        
        assertEquals("TRIAL", MatchLevel.TRIAL.name());
        assertEquals("LEVEL2", MatchLevel.LEVEL2.name());
        
        assertEquals("GOODS", RewardType.GOODS.name());
        assertEquals("CREDITS", RewardType.CREDITS.name());
    }

    @Test
    public void testEnumCompareTo() {
        // Test compareTo method (based on ordinal)
        ComponentOrigin[] componentOrigins = ComponentOrigin.values();
        if (componentOrigins.length > 1) {
            assertTrue(componentOrigins[0].compareTo(componentOrigins[1]) < 0);
            assertTrue(componentOrigins[1].compareTo(componentOrigins[0]) > 0);
            assertEquals(0, componentOrigins[0].compareTo(componentOrigins[0]));
        }
        
        CrewType[] crewTypes = CrewType.values();
        if (crewTypes.length > 1) {
            assertTrue(crewTypes[0].compareTo(crewTypes[1]) < 0);
            assertTrue(crewTypes[1].compareTo(crewTypes[0]) > 0);
            assertEquals(0, crewTypes[0].compareTo(crewTypes[0]));
        }
    }

    @Test
    public void testEnumGetDeclaringClass() {
        // Test getDeclaringClass method
        assertEquals(ComponentOrigin.class, ComponentOrigin.HAND.getDeclaringClass());
        assertEquals(CrewType.class, CrewType.HUMAN.getDeclaringClass());
        assertEquals(DoubleType.class, DoubleType.ENGINES.getDeclaringClass());
        assertEquals(ItemType.class, ItemType.BATTERIES.getDeclaringClass());
        assertEquals(MatchLevel.class, MatchLevel.TRIAL.getDeclaringClass());
        assertEquals(RewardType.class, RewardType.GOODS.getDeclaringClass());
    }

    @Test
    public void testEnumValuesArrayIndependence() {
        // Test that values() returns a new array each time
        ComponentOrigin[] values1 = ComponentOrigin.values();
        ComponentOrigin[] values2 = ComponentOrigin.values();
        
        assertNotSame(values1, values2);
        assertArrayEquals(values1, values2);
        
        // Modifying one array shouldn't affect the other
        if (values1.length > 0) {
            values1[0] = null;
            assertNotNull(values2[0]);
        }
    }

    @Test
    public void testAllEnumsCoverage() {
        // Ensure all enums have at least one value
        assertTrue(ComponentOrigin.values().length > 0);
        assertTrue(CrewType.values().length > 0);
        assertTrue(DoubleType.values().length > 0);
        assertTrue(ItemType.values().length > 0);
        assertTrue(MatchLevel.values().length > 0);
        assertTrue(RewardType.values().length > 0);
    }

    @Test
    public void testEnumSwitchStatements() {
        // Test that enums can be used in switch statements
        for (ComponentOrigin origin : ComponentOrigin.values()) {
            String result = switch (origin) {
                case HAND -> "hand";
                case FIRST_RESERVED -> "first_reserved";
                case SECOND_RESERVED -> "second_reserved";
            };
            assertNotNull(result);
        }
        
        for (CrewType crew : CrewType.values()) {
            String result = switch (crew) {
                case HUMAN -> "human";
                case BROWN_ALIEN -> "brown_alien";
                case PURPLE_ALIEN -> "purple_alien";
            };
            assertNotNull(result);
        }
        
        for (DoubleType doubleType : DoubleType.values()) {
            String result = switch (doubleType) {
                case ENGINES -> "engines";
                case CANNONS -> "cannons";
            };
            assertNotNull(result);
        }
        
        for (ItemType item : ItemType.values()) {
            String result = switch (item) {
                case BATTERIES -> "batteries";
                case CREW -> "crew";
            };
            assertNotNull(result);
        }
        
        for (MatchLevel level : MatchLevel.values()) {
            String result = switch (level) {
                case TRIAL -> "trial";
                case LEVEL2 -> "level2";
            };
            assertNotNull(result);
        }
        
        for (RewardType reward : RewardType.values()) {
            String result = switch (reward) {
                case GOODS -> "goods";
                case CREDITS -> "credits";
            };
            assertNotNull(result);
        }
    }

    @Test
    public void testEnumSerialization() {
        // Test that enums can be converted to string and back
        for (ComponentOrigin origin : ComponentOrigin.values()) {
            String name = origin.name();
            assertEquals(origin, ComponentOrigin.valueOf(name));
        }
        
        for (CrewType crew : CrewType.values()) {
            String name = crew.name();
            assertEquals(crew, CrewType.valueOf(name));
        }
        
        for (DoubleType doubleType : DoubleType.values()) {
            String name = doubleType.name();
            assertEquals(doubleType, DoubleType.valueOf(name));
        }
        
        for (ItemType item : ItemType.values()) {
            String name = item.name();
            assertEquals(item, ItemType.valueOf(name));
        }
        
        for (MatchLevel level : MatchLevel.values()) {
            String name = level.name();
            assertEquals(level, MatchLevel.valueOf(name));
        }
        
        for (RewardType reward : RewardType.values()) {
            String name = reward.name();
            assertEquals(reward, RewardType.valueOf(name));
        }
    }

    @Test
    public void testEnumInstanceOf() {
        // Test instanceof checks
        assertTrue(ComponentOrigin.HAND instanceof ComponentOrigin);
        assertTrue(ComponentOrigin.HAND instanceof Enum);
        
        assertTrue(CrewType.HUMAN instanceof CrewType);
        assertTrue(CrewType.HUMAN instanceof Enum);
        
        assertTrue(DoubleType.ENGINES instanceof DoubleType);
        assertTrue(DoubleType.ENGINES instanceof Enum);
        
        assertTrue(ItemType.BATTERIES instanceof ItemType);
        assertTrue(ItemType.BATTERIES instanceof Enum);
        
        assertTrue(MatchLevel.TRIAL instanceof MatchLevel);
        assertTrue(MatchLevel.TRIAL instanceof Enum);
        
        assertTrue(RewardType.GOODS instanceof RewardType);
        assertTrue(RewardType.GOODS instanceof Enum);
    }

    // Helper method to check if array contains value
    private <T> boolean containsValue(T[] array, T value) {
        for (T item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}