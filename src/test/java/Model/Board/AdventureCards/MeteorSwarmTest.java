package Model.Board.AdventureCards;

import Model.Board.AdventureCards.Projectiles.Meteor;
import Model.Enums.CardLevel;
import Model.Enums.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the MeteorSwarm class which represents meteor swarm encounters.
 * Tests constructor, JSON parsing, iteration, and visualization functionality.
 */
public class MeteorSwarmTest {

    /**
     * Tests JSON constructor with actual card ID 2 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard2() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("type", "MeteorSwarm");
        json.addProperty("level", "LEARNER");
        
        JsonArray meteors = new JsonArray();
        
        JsonObject meteor1 = new JsonObject();
        meteor1.addProperty("large", true);
        meteor1.addProperty("direction", "front");
        meteors.add(meteor1);
        
        JsonObject meteor2 = new JsonObject();
        meteor2.addProperty("large", false);
        meteor2.addProperty("direction", "left");
        meteors.add(meteor2);
        
        JsonObject meteor3 = new JsonObject();
        meteor3.addProperty("large", false);
        meteor3.addProperty("direction", "right");
        meteors.add(meteor3);
        
        json.add("meteors", meteors);
        json.addProperty("imagePath", "src/main/resources/pics/cards/2.png");

        MeteorSwarm card = new MeteorSwarm(json);
        assertEquals(2, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Pioggia di Meteoriti", card.getName());
        assertEquals("", card.getDescription());
        
        int count = 0;
        for (Meteor m : card) {
            count++;
        }
        assertEquals(3, count);
    }

    /**
     * Tests JSON constructor with actual card ID 15 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard15() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 15);
        json.addProperty("type", "MeteorSwarm");
        json.addProperty("level", "LEVEL_ONE");
        
        JsonArray meteors = new JsonArray();
        
        // 5 small meteors from different directions
        JsonObject meteor1 = new JsonObject();
        meteor1.addProperty("large", false);
        meteor1.addProperty("direction", "front");
        meteors.add(meteor1);
        
        JsonObject meteor2 = new JsonObject();
        meteor2.addProperty("large", false);
        meteor2.addProperty("direction", "front");
        meteors.add(meteor2);
        
        JsonObject meteor3 = new JsonObject();
        meteor3.addProperty("large", false);
        meteor3.addProperty("direction", "left");
        meteors.add(meteor3);
        
        JsonObject meteor4 = new JsonObject();
        meteor4.addProperty("large", false);
        meteor4.addProperty("direction", "right");
        meteors.add(meteor4);
        
        JsonObject meteor5 = new JsonObject();
        meteor5.addProperty("large", false);
        meteor5.addProperty("direction", "rear");
        meteors.add(meteor5);
        
        json.add("meteors", meteors);
        json.addProperty("imagePath", "src/main/resources/pics/cards/15.png");

        MeteorSwarm card = new MeteorSwarm(json);
        assertEquals(15, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
        
        int count = 0;
        for (Meteor m : card) {
            count++;
        }
        assertEquals(5, count);
    }

    /**
     * Tests JSON constructor with actual card ID 24 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard24() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 24);
        json.addProperty("type", "MeteorSwarm");
        json.addProperty("level", "LEVEL_TWO");
        
        JsonArray meteors = new JsonArray();
        
        JsonObject meteor1 = new JsonObject();
        meteor1.addProperty("large", false);
        meteor1.addProperty("direction", "front");
        meteors.add(meteor1);
        
        JsonObject meteor2 = new JsonObject();
        meteor2.addProperty("large", false);
        meteor2.addProperty("direction", "front");
        meteors.add(meteor2);
        
        JsonObject meteor3 = new JsonObject();
        meteor3.addProperty("large", true);
        meteor3.addProperty("direction", "left");
        meteors.add(meteor3);
        
        JsonObject meteor4 = new JsonObject();
        meteor4.addProperty("large", false);
        meteor4.addProperty("direction", "left");
        meteors.add(meteor4);
        
        JsonObject meteor5 = new JsonObject();
        meteor5.addProperty("large", false);
        meteor5.addProperty("direction", "left");
        meteors.add(meteor5);
        
        json.add("meteors", meteors);
        json.addProperty("imagePath", "src/main/resources/pics/cards/24.png");

        MeteorSwarm card = new MeteorSwarm(json);
        assertEquals(24, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        
        int count = 0;
        boolean hasLargeMeteor = false;
        for (Meteor m : card) {
            count++;
            if (m.isBig()) {
                hasLargeMeteor = true;
            }
        }
        assertEquals(5, count);
        assertTrue(hasLargeMeteor);
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        List<Meteor> meteors = new ArrayList<>();
        meteors.add(new Meteor(true, Side.FRONT));
        meteors.add(new Meteor(false, Side.LEFT));
        meteors.add(new Meteor(false, Side.RIGHT));
        
        MeteorSwarm card = new MeteorSwarm(2, CardLevel.LEARNER, meteors);
        assertEquals(2, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Pioggia di Meteoriti", card.getName());
        assertEquals("", card.getDescription());
        
        int count = 0;
        for (Meteor m : card) {
            count++;
        }
        assertEquals(3, count);
    }

    /**
     * Tests JSON constructor without meteors array (should throw exception).
     */
    @Test
    public void testJsonConstructorWithoutMeteors() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 100);
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test.png");
        // No meteors array
        
        assertThrows(IllegalArgumentException.class, () -> new MeteorSwarm(json));
    }

    /**
     * Tests JSON constructor with meteor missing 'large' field.
     */
    @Test
    public void testJsonConstructorMissingLargeField() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 101);
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test.png");
        
        JsonArray meteors = new JsonArray();
        JsonObject meteor = new JsonObject();
        // Missing 'large' field
        meteor.addProperty("direction", "front");
        meteors.add(meteor);
        json.add("meteors", meteors);
        
        assertThrows(IllegalArgumentException.class, () -> new MeteorSwarm(json));
    }

    /**
     * Tests JSON constructor with meteor missing 'direction' field.
     */
    @Test
    public void testJsonConstructorMissingDirectionField() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 102);
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test.png");
        
        JsonArray meteors = new JsonArray();
        JsonObject meteor = new JsonObject();
        meteor.addProperty("large", true);
        // Missing 'direction' field
        meteors.add(meteor);
        json.add("meteors", meteors);
        
        assertThrows(IllegalArgumentException.class, () -> new MeteorSwarm(json));
    }

    /**
     * Tests the iterator functionality.
     */
    @Test
    public void testIterator() {
        List<Meteor> meteors = new ArrayList<>();
        meteors.add(new Meteor(true, Side.FRONT));
        meteors.add(new Meteor(false, Side.REAR));
        
        MeteorSwarm card = new MeteorSwarm(2, CardLevel.LEARNER, meteors);
        
        int count = 0;
        boolean foundLarge = false;
        boolean foundSmall = false;
        
        for (Meteor m : card) {
            count++;
            if (m.isBig()) {
                foundLarge = true;
                assertEquals(Side.FRONT, m.getSide());
            } else {
                foundSmall = true;
                assertEquals(Side.REAR, m.getSide());
            }
        }
        
        assertEquals(2, count);
        assertTrue(foundLarge);
        assertTrue(foundSmall);
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        List<Meteor> meteors = new ArrayList<>();
        meteors.add(new Meteor(true, Side.FRONT));
        meteors.add(new Meteor(false, Side.LEFT));
        
        MeteorSwarm card = new MeteorSwarm(2, CardLevel.LEARNER, meteors);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.contains("Total Meteors:"));
        assertTrue(output.contains("2"));
        assertTrue(output.contains("Details:"));
        assertTrue(output.contains("large=true"));
        assertTrue(output.contains("large=false"));
        assertTrue(output.contains("dir=FRONT"));
        assertTrue(output.contains("dir=LEFT"));
    }

    /**
     * Tests constructor with empty meteors list.
     */
    @Test
    public void testConstructorWithEmptyMeteors() {
        List<Meteor> emptyMeteors = new ArrayList<>();
        
        MeteorSwarm card = new MeteorSwarm(50, CardLevel.LEARNER, emptyMeteors);
        assertEquals(50, card.getId());
        
        int count = 0;
        for (Meteor m : card) {
            count++;
        }
        assertEquals(0, count);
    }

    /**
     * Tests visualize with empty meteors list.
     */
    @Test
    public void testVisualizeWithEmptyMeteors() {
        List<Meteor> emptyMeteors = new ArrayList<>();
        MeteorSwarm card = new MeteorSwarm(50, CardLevel.LEARNER, emptyMeteors);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();
        
        assertTrue(output.contains("Total Meteors:"));
        assertTrue(output.contains("0"));
        assertTrue(output.contains("Details:"));
    }

    /**
     * Tests the visualizeString method.
     */
    @Test
    public void testVisualizeString() {
        List<Meteor> meteors = new ArrayList<>();
        meteors.add(new Meteor(true, Side.FRONT));
        
        MeteorSwarm card = new MeteorSwarm(2, CardLevel.LEARNER, meteors);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 2", result[1]);
        assertEquals("Nome: Pioggia di Meteoriti", result[2]);
        assertEquals("Livello: LEARNER", result[3]);
        assertEquals("Total Meteors:  1", result[4]);
        assertEquals("Details:", result[5]);
        assertTrue(result[6].contains("large=true"));
        assertTrue(result[6].contains("dir=FRONT"));
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        List<Meteor> meteors = new ArrayList<>();
        meteors.add(new Meteor(true, Side.FRONT));
        
        MeteorSwarm card = new MeteorSwarm(2, CardLevel.LEARNER, meteors);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }
}