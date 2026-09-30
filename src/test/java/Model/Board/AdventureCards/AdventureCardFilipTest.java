package Model.Board.AdventureCards;

import Model.Enums.CardLevel;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for AdventureCardFilip base class.
 * Tests JSON parsing, validation, and edge cases.
 */
public class AdventureCardFilipTest {

    // Test implementation for abstract class
    private static class TestCard extends AdventureCardFilip {
        public TestCard(int id, CardLevel level) {
            super(id, level);
        }
        
        public TestCard(JsonObject json) {
            super(json);
        }
        
        @Override
        public String getName() {
            return "Test Card";
        }
        
        @Override
        public String getDescription() {
            return "Test Description";
        }
    }

    /**
     * Tests basic constructor.
     */
    @Test
    public void testBasicConstructor() {
        TestCard card = new TestCard(1, CardLevel.LEARNER);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Test Card", card.getName());
        assertEquals("Test Description", card.getDescription());
        assertEquals("", card.getImagePath());
        assertEquals("", card.getBackCardImagePath());
    }

    /**
     * Tests JSON constructor with valid LEARNER level card.
     */
    @Test
    public void testJsonConstructorLearner() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test/path.png");
        
        TestCard card = new TestCard(json);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("test/path.png", card.getImagePath());
        assertEquals("src/main/resources/pics/cards/GT-cards_I_IT_0121.jpg", card.getBackCardImagePath());
    }

    /**
     * Tests JSON constructor with valid LEVEL_ONE card.
     */
    @Test
    public void testJsonConstructorLevelOne() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 9);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test/path.png");
        
        TestCard card = new TestCard(json);
        assertEquals(9, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
        assertEquals("src/main/resources/pics/cards/GT-cards_I_IT_0121.jpg", card.getBackCardImagePath());
    }

    /**
     * Tests JSON constructor with valid LEVEL_TWO card.
     */
    @Test
    public void testJsonConstructorLevelTwo() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 21);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test/path.png");
        
        TestCard card = new TestCard(json);
        assertEquals(21, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("src/main/resources/pics/cards/GT-cards_II_IT_0121.jpg", card.getBackCardImagePath());
    }

    /**
     * Tests JSON constructor with LEVEL_THREE throws exception.
     */
    @Test
    public void testJsonConstructorLevelThreeThrows() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 100);
        json.addProperty("level", "LEVEL_THREE");
        json.addProperty("imagePath", "test/path.png");
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> new TestCard(json));
        assertEquals("AdventureCardFilip level three is not supported", exception.getMessage());
    }

    /**
     * Tests JSON constructor with missing ID throws exception.
     */
    @Test
    public void testJsonConstructorMissingId() {
        JsonObject json = new JsonObject();
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test/path.png");
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> new TestCard(json));
        assertEquals("No ID provided", exception.getMessage());
    }

    /**
     * Tests JSON constructor with missing level throws exception.
     */
    @Test
    public void testJsonConstructorMissingLevel() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        json.addProperty("imagePath", "test/path.png");
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> new TestCard(json));
        assertEquals("No level provided for card with ID: 1", exception.getMessage());
    }

    /**
     * Tests JSON constructor with missing imagePath throws exception.
     */
    @Test
    public void testJsonConstructorMissingImagePath() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        json.addProperty("level", "LEARNER");
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> new TestCard(json));
        assertEquals("No image path provided", exception.getMessage());
    }

    /**
     * Tests JSON constructor with invalid level throws exception.
     */
    @Test
    public void testJsonConstructorInvalidLevel() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        json.addProperty("level", "INVALID_LEVEL");
        json.addProperty("imagePath", "test/path.png");
        
        assertThrows(IllegalArgumentException.class, () -> new TestCard(json));
    }

    /**
     * Tests JSON constructor with string ID (should work).
     */
    @Test
    public void testJsonConstructorStringId() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "5"); // String ID like in real JSON
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test/path.png");
        
        TestCard card = new TestCard(json);
        assertEquals(5, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
    }

    /**
     * Tests JSON constructor with lowercase level (should work due to toUpperCase).
     */
    @Test
    public void testJsonConstructorLowercaseLevel() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        json.addProperty("level", "learner");
        json.addProperty("imagePath", "test/path.png");
        
        TestCard card = new TestCard(json);
        assertEquals(CardLevel.LEARNER, card.getLevel());
    }

    /**
     * Tests visualize method output.
     */
    @Test
    public void testVisualize() {
        TestCard card = new TestCard(1, CardLevel.LEARNER);
        
        // Capture output
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        System.setOut(new java.io.PrintStream(outputStream));
        
        card.visualize();
        
        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.contains("ID: 1"));
        assertTrue(output.contains("Nome: Test Card"));
        assertTrue(output.contains("Livello: LEARNER"));
        assertTrue(output.contains("Immagine: "));
        assertTrue(output.contains("=========================="));
    }

    /**
     * Tests accept method (default implementation does nothing).
     */
    @Test
    public void testAcceptMethod() {
        TestCard card = new TestCard(1, CardLevel.LEARNER);
        // Should not throw exception
        assertDoesNotThrow(() -> card.accept(null, null));
    }

    /**
     * Tests visualizeString method (returns null by default).
     */
    @Test
    public void testVisualizeString() {
        TestCard card = new TestCard(1, CardLevel.LEARNER);
        assertNull(card.visualizeString());
    }

    /**
     * Tests with real JSON data structure from adventure_cards.json.
     */
    @Test
    public void testWithRealJsonStructure() {
        // Test with actual card structure like ID 5 (Smugglers)
        JsonObject json = new JsonObject();
        json.addProperty("id", "5");
        json.addProperty("type", "Smugglers");
        json.addProperty("level", "LEARNER");
        json.addProperty("power", 4);
        json.addProperty("imagePath", "src/main/resources/pics/cards/5.jpg");
        
        TestCard card = new TestCard(json);
        assertEquals(5, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("src/main/resources/pics/cards/5.jpg", card.getImagePath());
    }

    /**
     * Tests edge cases with different CardLevel values.
     */
    @Test
    public void testAllCardLevels() {
        // Test all valid levels
        for (CardLevel level : new CardLevel[]{CardLevel.LEARNER, CardLevel.LEVEL_ONE, CardLevel.LEVEL_TWO}) {
            JsonObject json = new JsonObject();
            json.addProperty("id", 1);
            json.addProperty("level", level.name());
            json.addProperty("imagePath", "test.png");
            
            if (level != CardLevel.LEVEL_THREE) {
                TestCard card = new TestCard(json);
                assertEquals(level, card.getLevel());
                
                // Verify back card image path
                if (level == CardLevel.LEVEL_TWO) {
                    assertEquals("src/main/resources/pics/cards/GT-cards_II_IT_0121.jpg", card.getBackCardImagePath());
                } else {
                    assertEquals("src/main/resources/pics/cards/GT-cards_I_IT_0121.jpg", card.getBackCardImagePath());
                }
            }
        }
    }
}