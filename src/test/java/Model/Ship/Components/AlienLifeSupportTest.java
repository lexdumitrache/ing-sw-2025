package Model.Ship.Components;

import Model.Enums.*;
import Model.Ship.ShipBoard;
import Model.Ship.Coordinates;
import Model.Exceptions.InvalidMethodParameters;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AlienLifeSupportTest {

    @Test
    public void testBrownAlienSupportConstructor() {
        AlienLifeSupport support = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.BROWN);
        
        assertEquals(Card.ALIEN_LIFE_SUPPORT, support.getType());
        assertEquals(AlienColor.BROWN, support.getColor());
    }

    @Test
    public void testPurpleAlienSupportConstructor() {
        AlienLifeSupport support = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.PURPLE);
        
        assertEquals(Card.ALIEN_LIFE_SUPPORT, support.getType());
        assertEquals(AlienColor.PURPLE, support.getColor());
    }

    @Test
    public void testJsonConstructor() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ALIEN_LIFE_SUPPORT");
        json.addProperty("imagePath", "test.png");
        json.addProperty("alienColor", "BROWN");
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        AlienLifeSupport support = new AlienLifeSupport(json);
        assertEquals(Card.ALIEN_LIFE_SUPPORT, support.getType());
        assertEquals(AlienColor.BROWN, support.getColor());
        assertEquals(ConnectorType.UNIVERSAL, support.getConnectorAt(Side.FRONT));
        assertEquals(ConnectorType.SINGLE, support.getConnectorAt(Side.REAR));
        assertEquals(ConnectorType.DOUBLE, support.getConnectorAt(Side.LEFT));
        assertEquals(ConnectorType.NONE, support.getConnectorAt(Side.RIGHT));
    }

    @Test
    public void testJsonConstructorMissingAlienColor() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ALIEN_LIFE_SUPPORT");
        json.addProperty("imagePath", "test.png");
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        assertThrows(RuntimeException.class, () -> new AlienLifeSupport(json));
    }

    @Test
    public void testAddedToShip() {
        AlienLifeSupport support = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.BROWN);
        ShipBoard ship = new ShipBoard();
        support.setShipBoard(ship);
        
        support.added();
        assertTrue(ship.getCondensedShip().getAlienSupports().contains(support));
        
        assertThrows(RuntimeException.class, support::added);
    }

    @Test
    public void testRemovedFromShip() {
        AlienLifeSupport support = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.PURPLE);
        ShipBoard ship = new ShipBoard();
        support.setShipBoard(ship);
        
        support.added();
        support.removed();
        assertFalse(ship.getCondensedShip().getAlienSupports().contains(support));
        
        assertThrows(RuntimeException.class, support::removed);
    }

    @Test
    public void testAddedWithConnectedCabins() throws InvalidMethodParameters {
        AlienLifeSupport brownSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, AlienColor.BROWN);
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        
        ShipBoard ship = new ShipBoard();
        brownSupport.setShipBoard(ship);
        cabin.setShipBoard(ship);
        
        ship.addComponent(brownSupport, new Coordinates(7, 7));
        ship.addComponent(cabin, new Coordinates(7, 8));
        
        boolean initialBrownCapacity = cabin.getCanContainBrown();
        brownSupport.added();
        cabin.added();
        
        assertTrue(cabin.getCanContainBrown());
        assertNotEquals(initialBrownCapacity, cabin.getCanContainBrown());
    }

    @Test
    public void testAddedWithConnectedCabinsPurple() throws InvalidMethodParameters {
        AlienLifeSupport purpleSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, AlienColor.PURPLE);
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        
        ShipBoard ship = new ShipBoard();
        purpleSupport.setShipBoard(ship);
        cabin.setShipBoard(ship);
        
        ship.addComponent(purpleSupport, new Coordinates(7, 7));
        ship.addComponent(cabin, new Coordinates(7, 8));
        
        boolean initialPurpleCapacity = cabin.getCanContainPurple();
        purpleSupport.added();
        cabin.added();
        
        assertTrue(cabin.getCanContainPurple());
        assertNotEquals(initialPurpleCapacity, cabin.getCanContainPurple());
    }

    @Test
    public void testRemovedWithConnectedCabins() throws InvalidMethodParameters {
        AlienLifeSupport brownSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, AlienColor.BROWN);
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY);
        
        ShipBoard ship = new ShipBoard();
        brownSupport.setShipBoard(ship);
        cabin.setShipBoard(ship);
        
        ship.addComponent(brownSupport, new Coordinates(7, 7));
        ship.addComponent(cabin, new Coordinates(7, 8));
        
        brownSupport.added();
        cabin.added();
        
        assertTrue(cabin.getCanContainBrown());
        brownSupport.removed();
        
        assertFalse(cabin.getCanContainBrown());
    }

    @Test
    public void testVisualize() {
        AlienLifeSupport support = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.BROWN);
        assertDoesNotThrow(() -> support.visualize());
    }

    @Test
    public void testRenderSmallBrown() {
        AlienLifeSupport brownSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.BROWN);
        
        String[] render = brownSupport.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("BAL"));
    }

    @Test
    public void testRenderSmallPurple() {
        AlienLifeSupport purpleSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.PURPLE);
        
        String[] render = purpleSupport.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("PAL"));
    }

    @Test
    public void testRenderBigBrown() {
        AlienLifeSupport brownSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.BROWN);
        
        String[] render = brownSupport.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
        assertTrue(render[2].contains("BROWN"));
    }

    @Test
    public void testRenderBigPurple() {
        AlienLifeSupport purpleSupport = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.PURPLE);
        
        String[] render = purpleSupport.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
        assertTrue(render[2].contains("PURPLE"));
    }

    @Test
    public void testRenderWithNoneConnectors() {
        AlienLifeSupport support = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, AlienColor.BROWN);
        
        String[] smallRender = support.renderSmall();
        assertNotNull(smallRender);
        assertEquals(3, smallRender.length);
        
        String[] bigRender = support.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
    }
}