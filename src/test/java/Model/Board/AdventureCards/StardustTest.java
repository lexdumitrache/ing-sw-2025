package Model.Board.AdventureCards;

import Model.Enums.CardLevel;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Stardust class which represents stardust encounters.
 * Tests constructor, JSON parsing, and visualization functionality.
 */
public class StardustTest {

    /**
     * Tests JSON constructor with actual card ID 4 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard4() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 4);
        json.addProperty("type", "Stardust");
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "src/main/resources/pics/cards/4.png");
        
        Stardust card = new Stardust(json);
        assertEquals(4, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Polvere Stellare", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests JSON constructor with actual card ID 35 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard35() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 35);
        json.addProperty("type", "Stardust");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "src/main/resources/pics/cards/35.png");
        
        Stardust card = new Stardust(json);
        assertEquals(35, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Polvere Stellare", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests JSON constructor with actual card ID 44 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard44() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 44);
        json.addProperty("type", "Stardust");
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "src/main/resources/pics/cards/4.png");
        
        Stardust card = new Stardust(json);
        assertEquals(44, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
        assertEquals("Polvere Stellare", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        Stardust card = new Stardust(4, CardLevel.LEARNER);
        assertEquals(4, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Polvere Stellare", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        Stardust card = new Stardust(4, CardLevel.LEARNER);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.length() > 0);
    }

    /**
     * Tests the visualizeString method.
     */
    @Test
    public void testVisualizeString() {
        Stardust card = new Stardust(4, CardLevel.LEARNER);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 4", result[1]);
        assertEquals("Nome: Polvere Stellare", result[2]);
        assertEquals("Livello: LEARNER", result[3]);
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        Stardust card = new Stardust(4, CardLevel.LEARNER);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }
}