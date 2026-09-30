package Model.Board.AdventureCards;

import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Penalties.*;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.CardLevel;
import Model.Enums.Criteria;
import Model.Enums.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CombatZone class which represents combat zones in the game.
 * Tests constructor, JSON parsing, iteration, and visualization functionality.
 */
public class CombatZoneTest {

    /**
     * Tests the basic constructor with a list of combat zone lines.
     */
    @Test
    public void testConstructor() {
        List<CombatZoneLine> lines = new ArrayList<>();
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(2)));

        CombatZone card = new CombatZone(1, CardLevel.LEVEL_TWO, lines);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Zona di Guerra", card.getName());
        assertEquals("", card.getDescription());

        int count = 0;
        for (CombatZoneLine line : card) {
            count++;
            assertEquals(Criteria.FIRE_POWER, line.getOrderingCriteria());
            assertEquals(2, ((RegularPenalty)line.getPenalty()).getAmount());
        }
        assertEquals(1, count);
    }

    /**
     * Tests JSON constructor matching actual JSON structure from adventure_cards.json.
     */
    @Test
    public void testJsonConstructorMatchingActualJson() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 6);
        json.addProperty("type", "CombatZone");
        json.addProperty("level", "LEARNER");
        
        JsonArray lines = new JsonArray();
        
        // First line - MAN_POWER with DaysPenalty
        JsonObject line1 = new JsonObject();
        line1.addProperty("criteria", "MAN_POWER");
        JsonObject penalty1 = new JsonObject();
        penalty1.addProperty("type", "DaysPenalty");
        penalty1.addProperty("value", 3);
        line1.add("penalty", penalty1);
        lines.add(line1);
        
        // Second line - ENGINE_POWER with CrewPenalty
        JsonObject line2 = new JsonObject();
        line2.addProperty("criteria", "ENGINE_POWER");
        JsonObject penalty2 = new JsonObject();
        penalty2.addProperty("type", "CrewPenalty");
        penalty2.addProperty("value", 2);
        line2.add("penalty", penalty2);
        lines.add(line2);
        
        // Third line - FIRE_POWER with CannonShotPenalty
        JsonObject line3 = new JsonObject();
        line3.addProperty("criteria", "FIRE_POWER");
        JsonObject penalty3 = new JsonObject();
        JsonArray shots = new JsonArray();
        
        JsonObject shot1 = new JsonObject();
        shot1.addProperty("isLarge", false);
        shot1.addProperty("direction", "front");
        shots.add(shot1);
        
        JsonObject shot2 = new JsonObject();
        shot2.addProperty("isLarge", true);
        shot2.addProperty("direction", "front");
        shots.add(shot2);
        
        penalty3.add("shots", shots);
        line3.add("penalty", penalty3);
        lines.add(line3);
        
        json.add("lines", lines);
        json.addProperty("imagePath", "src/main/resources/pics/cards/6.jpg");

        CombatZone card = new CombatZone(json);
        assertEquals(6, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());

        int count = 0;
        for (CombatZoneLine line : card) {
            count++;
        }
        assertEquals(3, count);
    }

    /**
     * Tests JSON constructor with DaysPenalty.
     */
    @Test
    public void testJsonConstructorWithDaysPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 3);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");

        JsonArray lines = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "FIRE_POWER");

        JsonObject penaltyObj = new JsonObject();
        penaltyObj.addProperty("type", "DaysPenalty");
        penaltyObj.addProperty("value", 3);
        line.add("penalty", penaltyObj);

        lines.add(line);
        json.add("lines", lines);

        CombatZone card = new CombatZone(json);
        assertEquals(3, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());

        int count = 0;
        for (CombatZoneLine l : card) {
            count++;
            assertEquals(Criteria.FIRE_POWER, l.getOrderingCriteria());
            assertEquals(3, ((RegularPenalty)l.getPenalty()).getAmount());
        }
        assertEquals(1, count);
    }

    /**
     * Tests JSON constructor with CrewPenalty type.
     */
    @Test
    public void testJsonConstructorWithCrewPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 4);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");

        JsonArray lines = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "MAN_POWER");

        JsonObject penaltyObj = new JsonObject();
        penaltyObj.addProperty("type", "CrewPenalty");
        penaltyObj.addProperty("value", 2);
        line.add("penalty", penaltyObj);

        lines.add(line);
        json.add("lines", lines);

        CombatZone card = new CombatZone(json);
        assertEquals(4, card.getId());
        assertEquals(CardLevel.LEVEL_ONE, card.getLevel());

        Iterator<CombatZoneLine> iterator = card.iterator();
        assertTrue(iterator.hasNext());
        CombatZoneLine l = iterator.next();
        assertEquals(Criteria.MAN_POWER, l.getOrderingCriteria());
        assertTrue(l.getPenalty() instanceof CrewPenalty);
        assertEquals(2, ((CrewPenalty)l.getPenalty()).getAmount());
    }

    /**
     * Tests JSON constructor with GoodsPenalty type.
     */
    @Test
    public void testJsonConstructorWithGoodsPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 5);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");

        JsonArray lines = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "MAN_POWER");

        JsonObject penaltyObj = new JsonObject();
        penaltyObj.addProperty("type", "GoodsPenalty");
        penaltyObj.addProperty("value", 1);
        line.add("penalty", penaltyObj);

        lines.add(line);
        json.add("lines", lines);

        CombatZone card = new CombatZone(json);
        Iterator<CombatZoneLine> iterator = card.iterator();
        CombatZoneLine l = iterator.next();
        assertTrue(l.getPenalty() instanceof GoodsPenalty);
        assertEquals(1, ((GoodsPenalty)l.getPenalty()).getAmount());
    }

    /**
     * Tests JSON constructor with CannonShotPenalty (shots array).
     */
    @Test
    public void testJsonConstructorWithCannonShotPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 6);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");

        JsonArray lines = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "FIRE_POWER");

        JsonObject penaltyObj = new JsonObject();
        JsonArray shots = new JsonArray();

        JsonObject shot1 = new JsonObject();
        shot1.addProperty("isLarge", true);
        shot1.addProperty("direction", "FRONT");
        shots.add(shot1);

        JsonObject shot2 = new JsonObject();
        shot2.addProperty("isLarge", false);
        shot2.addProperty("direction", "REAR");
        shots.add(shot2);

        penaltyObj.add("shots", shots);
        line.add("penalty", penaltyObj);

        lines.add(line);
        json.add("lines", lines);

        CombatZone card = new CombatZone(json);
        Iterator<CombatZoneLine> iterator = card.iterator();
        CombatZoneLine l = iterator.next();
        assertTrue(l.getPenalty() instanceof CannonShotPenalty);

        CannonShotPenalty csp = (CannonShotPenalty) l.getPenalty();
        int shotCount = 0;
        for (CannonShot shot : csp) {
            shotCount++;
        }
        assertEquals(2, shotCount);
    }

    /**
     * Tests JSON constructor with unsupported penalty type.
     */
    @Test
    public void testJsonConstructorWithUnsupportedPenaltyType() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 7);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");

        JsonArray lines = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "FIRE_POWER");

        JsonObject penaltyObj = new JsonObject();
        penaltyObj.addProperty("type", "UnsupportedPenalty");
        penaltyObj.addProperty("value", 1);
        line.add("penalty", penaltyObj);

        lines.add(line);
        json.add("lines", lines);

        assertThrows(RuntimeException.class, () -> new CombatZone(json));
    }

    /**
     * Tests JSON constructor with fallback penalty (no type or shots).
     */
    @Test
    public void testJsonConstructorWithFallbackPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 8);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");

        JsonArray lines = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "FIRE_POWER");

        JsonObject penaltyObj = new JsonObject();
        // No type or shots - should throw exception
        line.add("penalty", penaltyObj);

        lines.add(line);
        json.add("lines", lines);

        assertThrows(IllegalArgumentException.class, () -> new CombatZone(json));
    }

    /**
     * Tests JSON constructor without lines array.
     */
    @Test
    public void testJsonConstructorWithoutLines() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 9);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");
        // No lines array

        assertThrows(IllegalArgumentException.class, () -> new CombatZone(json));
    }

    /**
     * Tests the iterator functionality.
     */
    @Test
    public void testIterator() {
        List<CombatZoneLine> lines = new ArrayList<>();
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(1)));
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new CrewPenalty(2)));

        CombatZone card = new CombatZone(1, CardLevel.LEVEL_ONE, lines);

        Iterator<CombatZoneLine> iterator = card.iterator();
        assertTrue(iterator.hasNext());

        CombatZoneLine first = iterator.next();
        assertEquals(Criteria.FIRE_POWER, first.getOrderingCriteria());

        assertTrue(iterator.hasNext());
        CombatZoneLine second = iterator.next();
        assertEquals(Criteria.MAN_POWER, second.getOrderingCriteria());

        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the visualize method with DaysPenalty.
     */
    @Test
    public void testVisualizeWithDaysPenalty() {
        List<CombatZoneLine> lines = new ArrayList<>();
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        shots.add(new CannonShot(false, Side.REAR));

        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(2)));
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new DaysPenalty(1))); // Add second line to avoid index error
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CannonShotPenalty(shots)));

        CombatZone card = new CombatZone(1, CardLevel.LEVEL_ONE, lines);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();

        assertTrue(output.contains("Combat Lines:"));
        assertTrue(output.contains("Criteria: FIRE_POWER"));
        assertTrue(output.contains("DaysPenalty"));
    }

    /**
     * Tests the visualize method with CrewPenalty.
     */
    @Test
    public void testVisualizeWithCrewPenalty() {
        List<CombatZoneLine> lines = new ArrayList<>();
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        shots.add(new CannonShot(false, Side.REAR));
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new CrewPenalty(1)));
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CrewPenalty(2))); // Add second line to avoid index error
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CannonShotPenalty(shots)));
        CombatZone card = new CombatZone(2, CardLevel.LEVEL_TWO, lines);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();

        assertTrue(output.contains("Criteria: MAN_POWER"));
        assertTrue(output.contains("CrewPenalty"));
    }

    /**
     * Tests the visualize method with GoodsPenalty.
     */
    @Test
    public void testVisualizeWithGoodsPenalty() {
        List<CombatZoneLine> lines = new ArrayList<>();
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        shots.add(new CannonShot(false, Side.REAR));
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new GoodsPenalty(3)));
        lines.add(new CombatZoneLine(Criteria.ENGINE_POWER, new GoodsPenalty(1))); // Add second line to avoid index error
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CannonShotPenalty(shots)));

        CombatZone card = new CombatZone(3, CardLevel.LEVEL_ONE, lines);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();

        assertTrue(output.contains("Criteria: MAN_POWER"));
        assertTrue(output.contains("GoodsPenalty"));
    }

    /**
     * Tests the visualize method with CannonShotPenalty.
     */
    @Test
    public void testVisualizeWithCannonShotPenalty() {
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        shots.add(new CannonShot(false, Side.REAR));

        List<CombatZoneLine> lines = new ArrayList<>();
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new DaysPenalty(1))); // Add second line to avoid casting error
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new DaysPenalty(1)));
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CannonShotPenalty(shots)));

        CombatZone card = new CombatZone(4, CardLevel.LEVEL_TWO, lines);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();

        assertTrue(output.contains("Shots:"));
        assertTrue(output.contains("large=true"));
        assertTrue(output.contains("large=false"));
        assertTrue(output.contains("dir=FRONT"));
        assertTrue(output.contains("dir=REAR"));
    }

    /**
     * Tests the visualize method with empty CannonShotPenalty.
     */
    @Test
    public void testVisualizeWithEmptyCannonShotPenalty() {
        List<CannonShot> shots = new ArrayList<>(); // Empty list


        List<CombatZoneLine> lines = new ArrayList<>();

        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new DaysPenalty(1))); // Add second line to avoid casting error
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new DaysPenalty(1))); // Add second line to avoid casting error
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CannonShotPenalty(shots)));

        CombatZone card = new CombatZone(5, CardLevel.LEVEL_ONE, lines);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        card.visualize();

        System.setOut(originalOut);
        String output = outputStream.toString();

        assertTrue(output.contains("Shots:"));
        //assertTrue(output.contains("(no shots)"));
    }

//    /**
//     * Tests the visualize method with unknown penalty type.
//     */
//    @Test
//    public void testVisualizeWithUnknownPenalty() {
//        // Create a custom penalty that doesn't match the known types
//        Penalty unknownPenalty = new Penalty() {
//            @Override
//            public String toString() {
//                return "UnknownPenalty";
//            }
//        };
//
//        List<CombatZoneLine> lines = new ArrayList<>();
//        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, unknownPenalty));
//
//        CombatZone card = new CombatZone(6, CardLevel.LEVEL_ONE, lines);
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        PrintStream originalOut = System.out;
//        System.setOut(new PrintStream(outputStream));
//
//        card.visualize();
//
//        System.setOut(originalOut);
//        String output = outputStream.toString();
//
//        assertTrue(output.contains("Unknown penalty type:"));
//    }

    /**
     * Tests the visualizeString method.
     */
    @Test
    public void testVisualizeString() {
        List<CombatZoneLine> lines = new ArrayList<>();
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        shots.add(new CannonShot(false, Side.REAR));

        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(2)));
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(2)));
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CannonShotPenalty(shots)));
        
        CombatZone card = new CombatZone(1, CardLevel.LEVEL_TWO, lines);
        String[] result = card.visualizeString();
        
        assertNotNull(result);
        assertTrue(result.length > 0);
        assertEquals("==========================", result[0]);
        assertEquals("ID: 1", result[1]);
        assertEquals("Nome: Zona di Guerra", result[2]);
        assertEquals("Livello: LEVEL_TWO", result[3]);
        assertEquals("Combat Lines:", result[4]);
        assertTrue(result[5].contains("Criteria: FIRE_POWER"));
    }

    /**
     * Tests the accept method.
     */
    @Test
    public void testAccept() {
        List<CombatZoneLine> lines = new ArrayList<>();
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(2)));
        
        CombatZone card = new CombatZone(1, CardLevel.LEVEL_TWO, lines);
        assertThrows(NullPointerException.class, () -> card.accept(null, null));
    }

    @Test
    public void testJsonConstructorWithoutPenalty() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 10);
        json.addProperty("level", "LEVEL_ONE");
        json.addProperty("imagePath", "test.png");

        JsonArray lines = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "FIRE_POWER");
        lines.add(line);
        json.add("lines", lines);

        assertThrows(IllegalArgumentException.class, () -> new CombatZone(json));
    }

    @Test
    public void testConstructorWithEmptyLines() {
        List<CombatZoneLine> lines = new ArrayList<>();
        CombatZone card = new CombatZone(1, CardLevel.LEVEL_ONE, lines);
        
        int count = 0;
        for (CombatZoneLine line : card) {
            count++;
        }
        assertEquals(0, count);
    }
}