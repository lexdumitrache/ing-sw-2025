package Model;

import Controller.Enums.MatchLevel;
import Model.Ship.ShipBoard;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PreBuildShipsLoaderTest {

    @Test
    public void testLoadPreBuiltShipsTrial() {
        List<ShipBoard> ships = PreBuildShipsLoader.loadPreBuiltShips(MatchLevel.TRIAL);
        assertNotNull(ships);
    }

    @Test
    public void testLoadPreBuiltShipsLevel2() {
        List<ShipBoard> ships = PreBuildShipsLoader.loadPreBuiltShips(MatchLevel.LEVEL2);
        assertNotNull(ships);
    }
}