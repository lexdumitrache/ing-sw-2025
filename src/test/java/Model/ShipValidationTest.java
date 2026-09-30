package Model;

import Controller.Enums.MatchLevel;
import Model.Ship.ShipBoard;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Checks that every pre-built ship in ships.json is legal, so a player choosing one never starts with an invalid ship.
 */
public class ShipValidationTest {

    @ParameterizedTest
    @EnumSource(MatchLevel.class)
    public void testPreBuiltShipsAreValid(MatchLevel level) {
        List<ShipBoard> ships = new Game(level).getPreBuiltShips();

        assertFalse(ships.isEmpty(), "No pre-built ships for " + level);

        for (int i = 0; i < ships.size(); i++) {
            ShipBoard ship = ships.get(i);
            String name = level + " pre-built ship " + (i + 1);

            assertTrue(ship.validateShip(), name + " is not valid");
            assertFalse(ship.getCondensedShip().getEnginesList().isEmpty(), name + " has no engines");
            assertFalse(ship.getCondensedShip().getCabins().isEmpty(), name + " has no cabins");
        }
    }
}
