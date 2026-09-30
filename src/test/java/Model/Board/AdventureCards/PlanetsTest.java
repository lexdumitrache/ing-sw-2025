package Model.Board.AdventureCards;

import Model.Board.AdventureCards.Components.Planet;
import Model.Enums.CardLevel;
import Model.Enums.Good;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Planets class which represents planet encounters.
 * Tests constructor, JSON parsing, iteration, and visualization functionality.
 */
public class PlanetsTest {

    /**
     * Tests JSON constructor with actual card ID 3 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard3() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 3);
        json.addProperty("type", "Planets");
        json.addProperty("level", "LEARNER");
        
        JsonArray planetsArray = new JsonArray();
        
        JsonObject planet1 = new JsonObject();
        planet1.addProperty("name", "Planet 1");
        JsonArray goods1 = new JsonArray();
        goods1.add(new JsonPrimitive("RED"));
        goods1.add(new JsonPrimitive("RED"));
        planet1.add("goods", goods1);
        planetsArray.add(planet1);
        
        JsonObject planet2 = new JsonObject();
        planet2.addProperty("name", "Planet 2");
        JsonArray goods2 = new JsonArray();
        goods2.add(new JsonPrimitive("RED"));
        goods2.add(new JsonPrimitive("BLUE"));
        goods2.add(new JsonPrimitive("BLUE"));
        planet2.add("goods", goods2);
        planetsArray.add(planet2);
        
        JsonObject planet3 = new JsonObject();
        planet3.addProperty("name", "Planet 3");
        JsonArray goods3 = new JsonArray();
        goods3.add(new JsonPrimitive("YELLOW"));
        planet3.add("goods", goods3);
        planetsArray.add(planet3);
        
        json.add("planets", planetsArray);
        
        JsonObject landingPenalty = new JsonObject();
        landingPenalty.addProperty("value", 2);
        json.add("landingPenalty", landingPenalty);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/3.png");

        Planets card = new Planets(json);
        assertEquals(3, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        
        int count = 0;
        for (Planet p : card) {
            count++;
        }
        assertEquals(3, count);
    }

    /**
     * Tests JSON constructor with actual card ID 12 from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorCard12() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 12);
        json.addProperty("type", "Planets");
        json.addProperty("level", "LEVEL_ONE");
        
        JsonArray planetsArray = new JsonArray();
        
        JsonObject planet1 = new JsonObject();
        planet1.addProperty("name", "Planet 1");
        JsonArray goods1 = new JsonArray();
        goods1.add(new JsonPrimitive("RED"));
        goods1.add(new JsonPrimitive("GREEN"));
        goods1.add(new JsonPrimitive("BLUE"));
        goods1.add(new JsonPrimitive("BLUE"));
        goods1.add(new JsonPrimitive("BLUE"));
        planet1.add("goods", goods1);
        planetsArray.add(planet1);
        
        json.add("planets", planetsArray);
        
        JsonObject landingPenalty = new JsonObject();
        landingPenalty.addProperty("value", 3);
        json.add("landingPenalty", landingPenalty);
        
        json.addProperty("imagePath", "src/main/resources/pics/cards/12.png");

        Planets card = new Planets(json);
        assertEquals(12, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
    }

    /**
     * Tests the basic constructor.
     */
    @Test
    public void testConstructor() {
        List<Planet> planets = new ArrayList<>();
        List<Good> goods = new ArrayList<>();
        goods.add(Good.RED);
        goods.add(Good.YELLOW);
        planets.add(new Planet("Test Planet", goods));
        
        Planets card = new Planets(3, CardLevel.LEARNER, 2, planets);
        assertEquals(3, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Pianeti", card.getName());
        assertEquals("", card.getDescription());
        
        int count = 0;
        for (Planet p : card) {
            count++;
            assertEquals("Test Planet", p.getName());
        }
        assertEquals(1, count);
    }

    /**
     * Tests JSON constructor with minimal data.
     */
    @Test
    public void testJsonConstructorWithMinimalData() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 50);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");
        
        JsonArray planetsArray = new JsonArray();
        json.add("planets", planetsArray);
        
        JsonObject landingPenalty = new JsonObject();
        landingPenalty.addProperty("value", 0);
        json.add("landingPenalty", landingPenalty);
        
        Planets card = new Planets(json);
        assertEquals(50, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());
        
        int count = 0;
        for (Planet p : card) {
            count++;
        }
        assertEquals(0, count);
    }

    /**
     * Tests the iterator functionality.
     */
    @Test
    public void testIterator() {
        List<Planet> planets = new ArrayList<>();
        planets.add(new Planet("First", Arrays.asList(Good.RED)));
        planets.add(new Planet("Second", Arrays.asList(Good.BLUE)));
        
        Planets card = new Planets(3, CardLevel.LEARNER, 1, planets);
        
        int count = 0;
        for (Planet p : card) {
            count++;
            if (count == 1) {
                assertEquals("First", p.getName());
            } else if (count == 2) {
                assertEquals("Second", p.getName());
            }
        }
        assertEquals(2, count);
    }

    /**
     * Tests the visualize method.
     */
    @Test
    public void testVisualize() {
        List<Planet> planets = new ArrayList<>();
        planets.add(new Planet("Test Planet", Arrays.asList(Good.RED, Good.BLUE)));
        
        Planets card = new Planets(3, CardLevel.LEARNER, 2, planets);

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
        List<Planet> planets = new ArrayList<>();
        planets.add(new Planet("Test Planet", Arrays.asList(Good.RED)));
        
        Planets card = new Planets(3, CardLevel.LEARNER, 2, planets);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 3", result[1]);
        assertEquals("Nome: Pianeti", result[2]);
        assertEquals("Livello: LEARNER", result[3]);
        assertEquals("Planets:", result[4]);
        assertTrue(result[5].contains("Test Planet"));
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        List<Planet> planets = new ArrayList<>();
        Planets card = new Planets(3, CardLevel.LEARNER, 2, planets);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }

    /**
     * Tests JSON constructor missing planets array.
     */
    @Test
    public void testJsonConstructorMissingPlanets() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 100);
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test.png");
        
        assertThrows(IllegalArgumentException.class, () -> new Planets(json));
    }

    /**
     * Tests JSON constructor missing landingPenalty.
     */
    @Test
    public void testJsonConstructorMissingLandingPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 101);
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test.png");
        
        JsonArray planetsArray = new JsonArray();
        json.add("planets", planetsArray);
        
        assertThrows(IllegalArgumentException.class, () -> new Planets(json));
    }

    /**
     * Tests getLandingPenalty method.
     */
    @Test
    public void testGetLandingPenalty() {
        List<Planet> planets = new ArrayList<>();
        Planets card = new Planets(3, CardLevel.LEARNER, 5, planets);
        assertEquals(5, card.getLandingPenalty().getAmount());
    }
}