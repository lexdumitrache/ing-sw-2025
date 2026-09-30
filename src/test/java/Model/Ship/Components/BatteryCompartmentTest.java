package Model.Ship.Components;

import Controller.Exceptions.InvalidContextualAction;
import Model.Enums.*;
import Model.Ship.ShipBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BatteryCompartmentTest {

    @Test
    public void testConstructor() {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3);
        
        assertEquals(Card.BATTERY_COMPARTMENT, battery.getType());
        assertEquals(3, battery.getCapacity());
        assertEquals(3, battery.getBatteries());
    }

    @Test
    public void testSetBatteries() {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 5);
        
        battery.setBatteries(2);
        assertEquals(2, battery.getBatteries());
        
        battery.setBatteries(0);
        assertEquals(0, battery.getBatteries());
    }

    @Test
    public void testRemoveBattery() throws InvalidContextualAction {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 2);
        
        assertEquals(2, battery.getBatteries());
        
        battery.removeBattery();
        assertEquals(1, battery.getBatteries());
        
        battery.removeBattery();
        assertEquals(0, battery.getBatteries());
        
        assertThrows(InvalidContextualAction.class, battery::removeBattery);
    }

    @Test
    public void testAddedToShip() {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3);
        ShipBoard ship = new ShipBoard();
        battery.setShipBoard(ship);
        
        battery.added();
        assertTrue(ship.getCondensedShip().getBatteryCompartments().contains(battery));
        
        assertThrows(RuntimeException.class, battery::added);
    }

    @Test
    public void testRemovedFromShip() {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3);
        ShipBoard ship = new ShipBoard();
        battery.setShipBoard(ship);
        
        battery.added();
        battery.removed();
        assertFalse(ship.getCondensedShip().getBatteryCompartments().contains(battery));
        
        assertThrows(RuntimeException.class, battery::removed);
    }

    @Test
    public void testVisualize() {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3);
        battery.visualize();
    }

    @Test
    public void testRenderMethods() {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3);
        
        String[] smallRender = battery.renderSmall();
        assertNotNull(smallRender);
        assertEquals(3, smallRender.length);
        assertTrue(smallRender[1].contains("BAT"));
        
        battery.renderBig();
    }

    @Test
    public void testZeroBatteries() {
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 0);
        assertEquals(0, battery.getCapacity());
        assertEquals(0, battery.getBatteries());
        assertThrows(InvalidContextualAction.class, battery::removeBattery);
    }
}