package Model.Ship;

import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Crewmates;
import Model.Ship.Components.Cabin;
import Model.Ship.Components.SpaceshipComponent;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlienCounterTest {

    @Test
    public void testConstructor() {
        AlienCounter counter = new AlienCounter();
        assertFalse(counter.hasBrownAlien());
        assertFalse(counter.hasPurpleAlien());
    }
    
    @Test
    public void testSetAliens() {
        AlienCounter counter = new AlienCounter();
        
        counter.setBrownAlien(true);
        assertTrue(counter.hasBrownAlien());
        assertFalse(counter.hasPurpleAlien());
        
        counter.setPurpleAlien(true);
        assertTrue(counter.hasBrownAlien());
        assertTrue(counter.hasPurpleAlien());
        
        counter.setBrownAlien(false);
        assertFalse(counter.hasBrownAlien());
        assertTrue(counter.hasPurpleAlien());
    }

}