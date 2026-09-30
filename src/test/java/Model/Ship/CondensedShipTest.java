package Model.Ship;

import Model.Enums.*;
import Model.Ship.Components.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CondensedShipTest {

    @Test
    public void testConstructor() {
        CondensedShip ship = new CondensedShip();
        assertNotNull(ship.getCabins());
        assertNotNull(ship.getBatteryCompartments());
        assertNotNull(ship.getCargoHolds());
        assertNotNull(ship.getCannons());
        assertNotNull(ship.getEnginesList());
        assertNotNull(ship.getAlienSupports());
        assertNotNull(ship.getEngines());
        assertNotNull(ship.getAliens());
        assertNotNull(ship.getShields());
    }

    @Test
    public void testCabinOperations() {
        CondensedShip ship = new CondensedShip();
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        
        ship.addCabin(cabin);
        assertEquals(1, ship.getCabins().size());
        assertTrue(ship.getCabins().contains(cabin));
        
        ship.removeCabin(cabin);
        assertEquals(0, ship.getCabins().size());
    }

    @Test
    public void testBatteryOperations() {
        CondensedShip ship = new CondensedShip();
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 2);
        
        ship.addBatteryCompartment(battery);
        assertEquals(1, ship.getBatteryCompartments().size());
        
        ship.removeBatteryCompartment(battery);
        assertEquals(0, ship.getBatteryCompartments().size());
    }

    @Test
    public void testCargoOperations() {
        CondensedShip ship = new CondensedShip();
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 2, false);
        
        ship.addCargoHold(cargo);
        assertEquals(1, ship.getCargoHolds().size());
        
        ship.removeCargoHold(cargo);
        assertEquals(0, ship.getCargoHolds().size());
    }

    @Test
    public void testCannonOperations() {
        CondensedShip ship = new CondensedShip();
        Cannon cannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, false);
        
        ship.addCannon(cannon);
        assertEquals(1, ship.getCannons().size());
        
        ship.removeCannon(cannon);
        assertEquals(0, ship.getCannons().size());
    }

    @Test
    public void testEngineOperations() {
        CondensedShip ship = new CondensedShip();
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, false);
        
        ship.addEngine(engine);
        assertEquals(1, ship.getEnginesList().size());
        
        ship.removeEngine(engine);
        assertEquals(0, ship.getEnginesList().size());
    }

    @Test
    public void testAlienSupportOperations() {
        CondensedShip ship = new CondensedShip();
        AlienLifeSupport alien = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, AlienColor.BROWN);
        
        ship.addAlienSupport(alien);
        assertEquals(1, ship.getAlienSupports().size());
        
        ship.removeAlienSupport(alien);
        assertEquals(0, ship.getAlienSupports().size());
    }

    @Test
    public void testTotalBatteries() {
        CondensedShip ship = new CondensedShip();
        assertEquals(0, ship.getTotalBatteries());
        
        BatteryCompartment battery1 = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3);
        ship.addBatteryCompartment(battery1);
        
        BatteryCompartment battery2 = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 2);
        ship.addBatteryCompartment(battery2);
        
        assertEquals(5, ship.getTotalBatteries());
    }

    @Test
    public void testTotalCrew() {
        CondensedShip ship = new CondensedShip();
        assertEquals(0, ship.getTotalCrew());
        assertEquals(0, ship.getTotalHumans());
        
        Cabin cabin1 = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        cabin1.setOccupants(Crewmates.SINGLE_HUMAN);
        ship.addCabin(cabin1);
        
        Cabin cabin2 = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        cabin2.setOccupants(Crewmates.DOUBLE_HUMAN);
        ship.addCabin(cabin2);
        
        Cabin cabin3 = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        ship.addCabin(cabin3);
        
        assertEquals(3, ship.getTotalCrew());
        assertEquals(3, ship.getTotalHumans());
    }

    @Test
    public void testCanContainAliens() {
        ShipBoard ship=new ShipBoard();
        CondensedShip condensedShip = ship.getCondensedShip();
        assertFalse(condensedShip.canContainBrown());
        assertFalse(condensedShip.canContainPurple());
        
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        cabin.setCanContainBrown(1);
        cabin.setCanContainPurple(1);
        cabin.setShipBoard(ship);
        condensedShip.addCabin(cabin);
        
        assertTrue(condensedShip.canContainBrown());
        assertTrue(condensedShip.canContainPurple());
    }

    @Test
    public void testPowerCalculations() {
        CondensedShip ship = new CondensedShip();
        assertEquals(0.0, ship.getBasePower());
        assertEquals(0.0, ship.getMaxPower());
        
        Cannon singleCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, false);
        singleCannon.setOrientation(Direction.UP);
        ship.addCannon(singleCannon);
        
        assertEquals(1.0, ship.getBasePower());
        assertEquals(1.0, ship.getMaxPower());
    }

    @Test
    public void testThrustCalculations() {
        CondensedShip ship = new CondensedShip();
        assertEquals(0.0, ship.getBaseThrust());
        assertEquals(0.0, ship.getMaxThrust());
        
        ship.getEngines().incrementSingleEngines();
        assertEquals(1.0, ship.getBaseThrust());
        assertEquals(1.0, ship.getMaxThrust());
        
        ship.getEngines().incrementDoubleEngines();
        assertEquals(1.0, ship.getBaseThrust());
        assertEquals(3.0, ship.getMaxThrust());
    }

    @Test
    public void testGoodToDiscard() {
        CondensedShip ship = new CondensedShip();
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 4, true);
        cargo.addGood(Good.RED);
        cargo.addGood(Good.YELLOW);
        cargo.addGood(Good.GREEN);
        cargo.addGood(Good.BLUE);
        ship.addCargoHold(cargo);
        
        GoodCounter result = ship.goodToDiscard(2);
        assertEquals(1, result.getRed());
        assertEquals(1, result.getYellow());
        assertEquals(0, result.getGreen());
        assertEquals(0, result.getBlue());
    }

    @Test
    public void testTotalCrewWithAliens() {
        CondensedShip ship = new CondensedShip();
        
        Cabin cabin1 = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        cabin1.setOccupants(Crewmates.BROWN_ALIEN);
        ship.addCabin(cabin1);
        
        Cabin cabin2 = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        cabin2.setOccupants(Crewmates.PURPLE_ALIEN);
        ship.addCabin(cabin2);
        
        assertEquals(2, ship.getTotalCrew());
        assertEquals(0, ship.getTotalHumans());
    }

    @Test
    public void testTotalDoubleCannonsFront() {
        CondensedShip ship = new CondensedShip();
        
        Cannon doubleCannon1 = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        doubleCannon1.setOrientation(Direction.UP);
        ship.addCannon(doubleCannon1);
        
        Cannon doubleCannon2 = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        doubleCannon2.setOrientation(Direction.DOWN);
        ship.addCannon(doubleCannon2);
        
        DoubleCannonsCounter result = ship.getTotalDoubleCannons();
        assertEquals(1, result.getFrontCannons());
        assertEquals(1, result.getOtherCannons());
    }

    @Test
    public void testGoodToDiscardPriority() {
        CondensedShip ship = new CondensedShip();
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 8, true);
        cargo.addGood(Good.RED);
        cargo.addGood(Good.RED);
        cargo.addGood(Good.YELLOW);
        cargo.addGood(Good.YELLOW);
        cargo.addGood(Good.GREEN);
        cargo.addGood(Good.GREEN);
        cargo.addGood(Good.BLUE);
        cargo.addGood(Good.BLUE);
        ship.addCargoHold(cargo);
        
        GoodCounter result = ship.goodToDiscard(5);
        assertEquals(2, result.getRed());
        assertEquals(2, result.getYellow());
        assertEquals(1, result.getGreen());
        assertEquals(0, result.getBlue());
    }

    @Test
    public void testGoodToDiscardZero() {
        CondensedShip ship = new CondensedShip();
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 2, true);
        cargo.addGood(Good.RED);
        cargo.addGood(Good.BLUE);
        ship.addCargoHold(cargo);
        
        GoodCounter result = ship.goodToDiscard(0);
        assertEquals(0, result.getRed());
        assertEquals(0, result.getYellow());
        assertEquals(0, result.getGreen());
        assertEquals(0, result.getBlue());
    }

    @Test
    public void testPowerWithAliens() {
        CondensedShip ship = new CondensedShip();
        
        Cannon cannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, false);
        cannon.setOrientation(Direction.UP);
        ship.addCannon(cannon);
        
        ship.getAliens().setPurpleAlien(true);
        
        assertEquals(3.0, ship.getBasePower());
        assertEquals(3.0, ship.getMaxPower());
    }

    @Test
    public void testThrustWithAliens() {
        CondensedShip ship = new CondensedShip();
        
        ship.getEngines().incrementSingleEngines();
        ship.getAliens().setBrownAlien(true);
        
        assertEquals(3.0, ship.getBaseThrust());
        assertEquals(3.0, ship.getMaxThrust());
    }

    @Test
    public void testPowerWithSideCannons() {
        CondensedShip ship = new CondensedShip();
        
        Cannon sideCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, false);
        sideCannon.setOrientation(Direction.LEFT);
        ship.addCannon(sideCannon);
        
        assertEquals(0.5, ship.getBasePower());
        assertEquals(0.5, ship.getMaxPower());
    }

    @Test
    public void testMaxPowerWithDoubleCannons() {
        CondensedShip ship = new CondensedShip();
        
        Cannon singleCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, false);
        singleCannon.setOrientation(Direction.UP);
        ship.addCannon(singleCannon);
        
        Cannon doubleFrontCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        doubleFrontCannon.setOrientation(Direction.UP);
        ship.addCannon(doubleFrontCannon);
        
        Cannon doubleSideCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        doubleSideCannon.setOrientation(Direction.LEFT);
        ship.addCannon(doubleSideCannon);
        
        assertEquals(1.0, ship.getBasePower());
        assertEquals(4.0, ship.getMaxPower()); // 1 + 2 + 1
    }

    @Test
    public void testNoPowerWithoutCannons() {
        CondensedShip ship = new CondensedShip();
        ship.getAliens().setPurpleAlien(true);
        
        assertEquals(0.0, ship.getBasePower());
        assertEquals(0.0, ship.getMaxPower());
    }

    @Test
    public void testNoThrustWithoutEngines() {
        CondensedShip ship = new CondensedShip();
        ship.getAliens().setBrownAlien(true);
        
        assertEquals(0.0, ship.getBaseThrust());
        assertEquals(0.0, ship.getMaxThrust());
    }
}