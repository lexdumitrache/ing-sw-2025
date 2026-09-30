package Model.Board.AdventureCards.Penalties;

import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CannonShotPenaltyTest {

    @Test
    public void testConstructor() {
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(false, Side.FRONT));
        shots.add(new CannonShot(true, Side.LEFT));
        
        CannonShotPenalty penalty = new CannonShotPenalty(shots);
        
        // Count shots and check their properties
        int count = 0;
        CannonShot firstShot = null;
        CannonShot secondShot = null;
        
        for (CannonShot shot : penalty) {
            count++;
            if (count == 1) {
                firstShot = shot;
            } else if (count == 2) {
                secondShot = shot;
            }
        }
        
        assertEquals(2, count);
        assertNotNull(firstShot);
        assertNotNull(secondShot);
        assertFalse(firstShot.isBig());
        assertEquals(Side.FRONT, firstShot.getSide());
        assertTrue(secondShot.isBig());
        assertEquals(Side.LEFT, secondShot.getSide());
    }
    
    @Test
    public void testEmptyList() {
        CannonShotPenalty penalty = new CannonShotPenalty(new ArrayList<>());
        
        // Check if the iterator returns any elements
        int count = 0;
        for (CannonShot shot : penalty) {
            count++;
        }
        
        assertEquals(0, count);
    }
    
//    @Test
//    public void testNullList() {
//        assertThrows(NullPointerException.class, () -> new CannonShotPenalty(null));
//    }
    @Test
    public void testNullList() {
        CannonShotPenalty penalty = new CannonShotPenalty((List<CannonShot>) null);
        // Constructor accepts null, but iterator will throw NullPointerException
        assertThrows(NullPointerException.class, penalty::iterator);
    }

    @Test
    public void testJsonConstructor() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 1);
        JsonObject penalty = new JsonObject();
        JsonArray shots = new JsonArray();
        
        JsonObject shot1 = new JsonObject();
        shot1.addProperty("isLarge", true);
        shot1.addProperty("direction", "FRONT");
        shots.add(shot1);
        
        JsonObject shot2 = new JsonObject();
        shot2.addProperty("isLarge", false);
        shot2.addProperty("direction", "LEFT");
        shots.add(shot2);
        
        penalty.add("shots", shots);
        json.add("penalty", penalty);
        
        CannonShotPenalty cannonPenalty = new CannonShotPenalty(json);
        
        int count = 0;
        for (CannonShot shot : cannonPenalty) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testJsonConstructorMissingShots() {
        JsonObject json = new JsonObject();
        json.addProperty("id", "2");
        JsonObject penalty = new JsonObject();
        json.add("penalty", penalty);
        
        assertThrows(IllegalArgumentException.class, () -> new CannonShotPenalty(json));
    }

    @Test
    public void testJsonConstructorInvalidShotData() {
        JsonObject json = new JsonObject();
        json.addProperty("id", 3);
        JsonObject penalty = new JsonObject();
        JsonArray shots = new JsonArray();
        
        JsonObject invalidShot = new JsonObject();
        invalidShot.addProperty("isLarge", true);
        // Missing direction
        shots.add(invalidShot);
        
        penalty.add("shots", shots);
        json.add("penalty", penalty);
        
        assertThrows(IllegalArgumentException.class, () -> new CannonShotPenalty(json));
    }
}