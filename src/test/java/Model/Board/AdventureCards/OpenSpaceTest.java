package Model.Board.AdventureCards;

import Model.Enums.CardLevel;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the OpenSpace class which represents open space encounters.
 * Tests constructor, JSON parsing, and visualization functionality.
 */
public class OpenSpaceTest {

    /**
     * Tests JSON constructor with actual card ID 1 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard1() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        json.addProperty("type", "OpenSpace");
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "src/main/resources/pics/cards/1.png");
        
        OpenSpace card = new OpenSpace(json);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Spazio Aperto", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests JSON constructor with actual card ID 9 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard9() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 9);
        json.addProperty("type", "OpenSpace");
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "src/main/resources/pics/cards/9.png");
        
        OpenSpace card = new OpenSpace(json);
        assertEquals(9, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
        assertEquals("Spazio Aperto", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests JSON constructor with actual card ID 21 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard21() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 21);
        json.addProperty("type", "OpenSpace");
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "src/main/resources/pics/cards/21.png");
        
        OpenSpace card = new OpenSpace(json);
        assertEquals(21, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Spazio Aperto", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        OpenSpace card = new OpenSpace(1, CardLevel.LEARNER);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Spazio Aperto", card.getName());
        assertEquals("", card.getDescription());
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        OpenSpace card = new OpenSpace(1, CardLevel.LEARNER);

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
        OpenSpace card = new OpenSpace(1, CardLevel.LEARNER);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 1", result[1]);
        assertEquals("Nome: Spazio Aperto", result[2]);
        assertEquals("Livello: LEARNER", result[3]);
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        OpenSpace card = new OpenSpace(1, CardLevel.LEARNER);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }
}