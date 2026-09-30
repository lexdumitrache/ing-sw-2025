package Model.Ship.Components;

import Model.Enums.*;
import Model.Ship.ShipBoard;
import Model.Ship.Coordinates;
import Model.Exceptions.InvalidMethodParameters;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CabinTest {

    @Test
    public void testConstructor() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        
        assertEquals(Card.CABIN, cabin.getType());
        assertEquals(Crewmates.EMPTY, cabin.getOccupants());
        assertFalse(cabin.getCanContainBrown());
        assertFalse(cabin.getCanContainPurple());
    }

    @Test
    public void testConstructorWithImagePath() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.SINGLE_HUMAN, "test.png");
        
        assertEquals(Card.CABIN, cabin.getType());
        assertEquals(Crewmates.EMPTY, cabin.getOccupants()); // Constructor always sets to EMPTY
        assertEquals("test.png", cabin.getImagePath());
    }

    @Test
    public void testJsonConstructor() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "CABIN");
        json.addProperty("imagePath", "test.png");
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        Cabin cabin = new Cabin(json);
        assertEquals(Card.CABIN, cabin.getType());
        assertEquals(Crewmates.EMPTY, cabin.getOccupants());
        assertFalse(cabin.getCanContainBrown());
        assertFalse(cabin.getCanContainPurple());
        assertEquals(ConnectorType.UNIVERSAL, cabin.getConnectorAt(Side.FRONT));
        assertEquals(ConnectorType.SINGLE, cabin.getConnectorAt(Side.REAR));
        assertEquals(ConnectorType.DOUBLE, cabin.getConnectorAt(Side.LEFT));
        assertEquals(ConnectorType.NONE, cabin.getConnectorAt(Side.RIGHT));
    }

    @Test
    public void testOccupants() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        
        cabin.setOccupants(Crewmates.SINGLE_HUMAN);
        assertEquals(Crewmates.SINGLE_HUMAN, cabin.getOccupants());
        
        cabin.setOccupants(Crewmates.DOUBLE_HUMAN);
        assertEquals(Crewmates.DOUBLE_HUMAN, cabin.getOccupants());
        
        cabin.setOccupants(Crewmates.BROWN_ALIEN);
        assertEquals(Crewmates.BROWN_ALIEN, cabin.getOccupants());
        
        cabin.setOccupants(Crewmates.PURPLE_ALIEN);
        assertEquals(Crewmates.PURPLE_ALIEN, cabin.getOccupants());
    }

    @Test
    public void testAlienCapabilities() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        
        assertFalse(cabin.getCanContainBrown());
        assertFalse(cabin.getCanContainPurple());
        
        cabin.setCanContainBrown(2);
        assertTrue(cabin.getCanContainBrown());
        
        cabin.setCanContainPurple(1);
        assertTrue(cabin.getCanContainPurple());
        
        cabin.incrementCanContainBrown();
        cabin.incrementCanContainPurple();
        
        assertTrue(cabin.getCanContainBrown());
        assertTrue(cabin.getCanContainPurple());
    }

    @Test
    public void testDecrementAlienCapabilities() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        ShipBoard ship = new ShipBoard();
        cabin.setShipBoard(ship);
        
        cabin.setCanContainBrown(2);
        cabin.decrementCanContainBrown();
        assertTrue(cabin.getCanContainBrown());
        
        cabin.decrementCanContainBrown();
        assertFalse(cabin.getCanContainBrown());
        
        assertThrows(IllegalArgumentException.class, cabin::decrementCanContainBrown);
    }

    @Test
    public void testDecrementPurpleCapabilities() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        ShipBoard ship = new ShipBoard();
        cabin.setShipBoard(ship);
        
        cabin.setCanContainPurple(2);
        cabin.decrementCanContainPurple();
        assertTrue(cabin.getCanContainPurple());
        
        cabin.decrementCanContainPurple();
        assertFalse(cabin.getCanContainPurple());
        
        assertThrows(IllegalArgumentException.class, cabin::decrementCanContainPurple);
    }

    @Test
    public void testDecrementWithBrownAlienOccupant() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        ShipBoard ship = new ShipBoard();
        cabin.setShipBoard(ship);
        
        cabin.setCanContainBrown(1);
        cabin.setOccupants(Crewmates.BROWN_ALIEN);
        
        cabin.decrementCanContainBrown();
        assertEquals(Crewmates.EMPTY, cabin.getOccupants());
        assertFalse(ship.getCondensedShip().getAliens().hasBrownAlien());
    }

    @Test
    public void testDecrementWithPurpleAlienOccupant() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        ShipBoard ship = new ShipBoard();
        cabin.setShipBoard(ship);
        
        cabin.setCanContainPurple(1);
        cabin.setOccupants(Crewmates.PURPLE_ALIEN);
        
        cabin.decrementCanContainPurple();
        assertEquals(Crewmates.EMPTY, cabin.getOccupants());
        assertFalse(ship.getCondensedShip().getAliens().hasPurpleAlien());
    }

    @Test
    public void testAddedToShip() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        ShipBoard ship = new ShipBoard();
        cabin.setShipBoard(ship);
        
        cabin.added();
        assertTrue(ship.getCondensedShip().getCabins().contains(cabin));
        
        assertThrows(RuntimeException.class, cabin::added);
    }

    @Test
    public void testRemovedFromShip() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        ShipBoard ship = new ShipBoard();
        cabin.setShipBoard(ship);
        
        cabin.added();
        cabin.removed();
        assertFalse(ship.getCondensedShip().getCabins().contains(cabin));
        
        assertThrows(RuntimeException.class, cabin::removed);
    }

    @Test
    public void testAddedWithConnectedAlienSupport() throws InvalidMethodParameters {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        AlienLifeSupport brownSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, AlienColor.BROWN);
        
        ShipBoard ship = new ShipBoard();
        cabin.setShipBoard(ship);
        brownSupport.setShipBoard(ship);
        
        ship.addComponent(cabin, new Coordinates(7, 7));
        ship.addComponent(brownSupport, new Coordinates(7, 8));
        
        brownSupport.added();
        
        assertFalse(cabin.getCanContainBrown());
        cabin.added();
        assertTrue(cabin.getCanContainBrown());
    }

    @Test
    public void testVisualize() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        assertDoesNotThrow(() -> cabin.visualize());
    }

    @Test
    public void testRenderSmall() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        
        String[] smallRender = cabin.renderSmall();
        assertNotNull(smallRender);
        assertEquals(3, smallRender.length);
        assertTrue(smallRender[1].contains("CAB"));
    }

    @Test
    public void testRenderBigEmpty() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        
        String[] bigRender = cabin.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
        assertTrue(bigRender[1].contains("CABIN"));
    }

    @Test
    public void testRenderBigWithSingleHuman() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        cabin.setOccupants(Crewmates.SINGLE_HUMAN);
        
        String[] bigRender = cabin.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
        assertTrue(bigRender[4].contains("1 HUM"));
    }

    @Test
    public void testRenderBigWithDoubleHuman() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        cabin.setOccupants(Crewmates.DOUBLE_HUMAN);
        
        String[] bigRender = cabin.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
        assertTrue(bigRender[4].contains("2 HUM"));
    }

    @Test
    public void testRenderBigWithBrownAlien() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        cabin.setOccupants(Crewmates.BROWN_ALIEN);
        
        String[] bigRender = cabin.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
        assertTrue(bigRender[4].contains("BROWN"));
    }

    @Test
    public void testRenderBigWithPurpleAlien() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        cabin.setOccupants(Crewmates.PURPLE_ALIEN);
        
        String[] bigRender = cabin.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
        assertTrue(bigRender[4].contains("PURPLE"));
    }

    @Test
    public void testRenderWithNoneConnectors() {
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, Crewmates.EMPTY);
        
        String[] smallRender = cabin.renderSmall();
        assertNotNull(smallRender);
        assertEquals(3, smallRender.length);
        
        String[] bigRender = cabin.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
    }
}