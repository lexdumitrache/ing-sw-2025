package Model.Ship.Components;

import Model.Enums.*;
import Model.Ship.ShipBoard;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CargoHoldTest {

    @Test
    public void testNormalCargoHoldConstructor() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        
        assertEquals(Card.CARGO_HOLD, cargo.getType());
        assertEquals(3, cargo.getCapacity());
        assertNotNull(cargo.getGoods());
        assertEquals(3, cargo.getGoods().length);
        assertFalse(cargo.isSpecial());
    }

    @Test
    public void testSpecialCargoHoldConstructor() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 2, true);
        
        assertEquals(2, cargo.getCapacity());
        assertTrue(cargo.isSpecial());
        assertTrue(cargo.addGood(Good.RED)); // Special cargo can hold red goods
    }

    @Test
    public void testJsonConstructor() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CARGO_HOLD");
        json.addProperty("imagePath", "test.png");
        json.addProperty("cargoCapacity", 4);
        json.addProperty("isSpecial", true);
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        CargoHold cargo = new CargoHold(json);
        assertEquals(Card.CARGO_HOLD, cargo.getType());
        assertEquals(4, cargo.getCapacity());
        assertTrue(cargo.isSpecial());
        assertEquals(ConnectorType.UNIVERSAL, cargo.getConnectorAt(Side.FRONT));
        assertEquals(ConnectorType.SINGLE, cargo.getConnectorAt(Side.REAR));
        assertEquals(ConnectorType.DOUBLE, cargo.getConnectorAt(Side.LEFT));
        assertEquals(ConnectorType.NONE, cargo.getConnectorAt(Side.RIGHT));
    }

    @Test
    public void testJsonConstructorMissingCapacity() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CARGO_HOLD");
        json.addProperty("imagePath", "test.png");
        json.addProperty("isSpecial", false);
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        assertThrows(RuntimeException.class, () -> new CargoHold(json));
    }

    @Test
    public void testJsonConstructorMissingIsSpecial() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CARGO_HOLD");
        json.addProperty("imagePath", "test.png");
        json.addProperty("capacity", 3);
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        assertThrows(RuntimeException.class, () -> new CargoHold(json));
    }

    @Test
    public void testAddGoodToNormalCargo() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 2, false);
        
        assertTrue(cargo.addGood(Good.BLUE));
        assertTrue(cargo.addGood(Good.GREEN));
        assertFalse(cargo.addGood(Good.YELLOW)); // Full
        assertFalse(cargo.addGood(Good.RED)); // Normal cargo can't hold red
    }

    @Test
    public void testAddGoodToSpecialCargo() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 2, true);
        
        assertTrue(cargo.addGood(Good.RED)); // Special cargo can hold red
        assertTrue(cargo.addGood(Good.BLUE));
        assertFalse(cargo.addGood(Good.GREEN)); // Full
    }

    @Test
    public void testAddAllGoodTypes() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 4, true);
        
        assertTrue(cargo.addGood(Good.RED));
        assertTrue(cargo.addGood(Good.YELLOW));
        assertTrue(cargo.addGood(Good.GREEN));
        assertTrue(cargo.addGood(Good.BLUE));
        assertFalse(cargo.addGood(Good.RED)); // Full
    }

    @Test
    public void testAddGoodAt() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        
        assertTrue(cargo.addGoodAt(Good.BLUE, 0));
        assertTrue(cargo.addGoodAt(Good.GREEN, 2));
        assertFalse(cargo.addGoodAt(Good.YELLOW, 0)); // Already occupied
        assertFalse(cargo.addGoodAt(Good.RED, 1)); // Normal cargo can't hold red
        assertFalse(cargo.addGoodAt(Good.BLUE, -1)); // Invalid index
        assertFalse(cargo.addGoodAt(Good.BLUE, 5)); // Invalid index
    }

    @Test
    public void testAddGoodAtSpecialCargo() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, true);
        
        assertTrue(cargo.addGoodAt(Good.RED, 0)); // Special cargo can hold red
        assertTrue(cargo.addGoodAt(Good.BLUE, 1));
        assertTrue(cargo.addGoodAt(Good.GREEN, 2));
        assertFalse(cargo.addGoodAt(Good.YELLOW, 1)); // Already occupied
    }

    @Test
    public void testRemoveGood() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        
        cargo.addGood(Good.BLUE);
        cargo.addGood(Good.GREEN);
        
        cargo.removeGood(0);
        assertNull(cargo.getGoods()[0]);
        assertNotNull(cargo.getGoods()[1]);
        
        cargo.removeGood(-1); // Invalid index - should not crash
        cargo.removeGood(5); // Invalid index - should not crash
    }

    @Test
    public void testRemoveGoodFromEmptySlot() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        
        cargo.removeGood(0); // Remove from empty slot - should not crash
        assertNull(cargo.getGoods()[0]);
    }

    @Test
    public void testAddedToShip() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        ShipBoard ship = new ShipBoard();
        cargo.setShipBoard(ship);
        
        cargo.added();
        assertTrue(ship.getCondensedShip().getCargoHolds().contains(cargo));
        
        assertThrows(RuntimeException.class, cargo::added);
    }

    @Test
    public void testRemovedFromShip() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        ShipBoard ship = new ShipBoard();
        cargo.setShipBoard(ship);
        
        cargo.added();
        cargo.removed();
        assertFalse(ship.getCondensedShip().getCargoHolds().contains(cargo));
        
        assertThrows(RuntimeException.class, cargo::removed);
    }

    @Test
    public void testVisualize() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, true);
        assertDoesNotThrow(() -> cargo.visualize());
    }

    @Test
    public void testRenderSmall() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        
        String[] render = cargo.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("C-B"));
    }

    @Test
    public void testRenderBig() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, false);
        
        String[] render = cargo.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
        assertTrue(render[1].contains("NORMAL"));
    }

    @Test
    public void testRenderBigSpecial() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3, true);
        
        String[] render = cargo.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
        assertTrue(render[1].contains("SPECIAL"));
    }

    @Test
    public void testRenderWithGoods() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 4, true);
        cargo.addGood(Good.RED);
        cargo.addGood(Good.BLUE);
        cargo.addGood(Good.GREEN);
        
        String[] render = cargo.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
    }

    @Test
    public void testRenderWithNoneConnectors() {
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, 2, false);
        
        String[] smallRender = cargo.renderSmall();
        assertNotNull(smallRender);
        assertEquals(3, smallRender.length);
        
        String[] bigRender = cargo.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
    }

    @Test
    public void testCapacityLimits() {
        CargoHold smallCargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 1, false);
        
        assertTrue(smallCargo.addGood(Good.BLUE));
        assertFalse(smallCargo.addGood(Good.GREEN)); // Full
        
        CargoHold largeCargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 5, true);
        
        for (int i = 0; i < 5; i++) {
            assertTrue(largeCargo.addGood(Good.BLUE));
        }
        assertFalse(largeCargo.addGood(Good.GREEN)); // Full
    }
}