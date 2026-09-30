package Model.Ship.Components;

import Model.Enums.*;
import Model.Ship.ShipBoard;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShieldGeneratorTest {

    @Test
    public void testConstructor() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        
        assertEquals(Card.SHIELD_GENERATOR, shield.getType());
        assertEquals(Direction.UP, shield.getOrientation());
    }

    @Test
    public void testJsonConstructor() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "SHIELD_GENERATOR");
        json.addProperty("imagePath", "test.png");
        json.addProperty("orientation", "DOWN");
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        ShieldGenerator shield = new ShieldGenerator(json);
        assertEquals(Card.SHIELD_GENERATOR, shield.getType());
        assertEquals(Direction.UP, shield.getOrientation());
        assertEquals(ConnectorType.UNIVERSAL, shield.getConnectorAt(Side.FRONT));
        assertEquals(ConnectorType.SINGLE, shield.getConnectorAt(Side.REAR));
        assertEquals(ConnectorType.DOUBLE, shield.getConnectorAt(Side.LEFT));
        assertEquals(ConnectorType.NONE, shield.getConnectorAt(Side.RIGHT));
    }

    @Test
    public void testJsonConstructorMissingOrientation() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "SHIELD_GENERATOR");
        json.addProperty("imagePath", "test.png");
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        ShieldGenerator shield = new ShieldGenerator(json);
        assertEquals(Direction.UP, shield.getOrientation()); // Should default to UP
    }

    @Test
    public void testAddedToShipUpOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        int initialNorth = ship.getCondensedShip().getShields().getNorthShields();
        int initialEast = ship.getCondensedShip().getShields().getEastShields();
        
        shield.added();
        
        assertEquals(initialNorth + 1, ship.getCondensedShip().getShields().getNorthShields());
        assertEquals(initialEast + 1, ship.getCondensedShip().getShields().getEastShields());
    }

    @Test
    public void testAddedToShipRightOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.RIGHT);
        shield.setOrientation(Direction.RIGHT);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        int initialSouth = ship.getCondensedShip().getShields().getSouthShields();
        int initialEast = ship.getCondensedShip().getShields().getEastShields();
        
        shield.added();
        
        assertEquals(initialSouth + 1, ship.getCondensedShip().getShields().getSouthShields());
        assertEquals(initialEast + 1, ship.getCondensedShip().getShields().getEastShields());
    }

    @Test
    public void testAddedToShipDownOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.DOWN);
        shield.setOrientation(Direction.DOWN);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        int initialSouth = ship.getCondensedShip().getShields().getSouthShields();
        int initialWest = ship.getCondensedShip().getShields().getWestShields();
        
        shield.added();
        
        assertEquals(initialSouth + 1, ship.getCondensedShip().getShields().getSouthShields());
        assertEquals(initialWest + 1, ship.getCondensedShip().getShields().getWestShields());
    }

    @Test
    public void testAddedToShipLeftOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.LEFT);
        shield.setOrientation(Direction.LEFT);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        int initialNorth = ship.getCondensedShip().getShields().getNorthShields();
        int initialWest = ship.getCondensedShip().getShields().getWestShields();
        
        shield.added();
        
        assertEquals(initialNorth + 1, ship.getCondensedShip().getShields().getNorthShields());
        assertEquals(initialWest + 1, ship.getCondensedShip().getShields().getWestShields());
    }

    @Test
    public void testRemovedFromShipUpOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        shield.added();
        int northAfterAdd = ship.getCondensedShip().getShields().getNorthShields();
        int eastAfterAdd = ship.getCondensedShip().getShields().getEastShields();
        
        shield.removed();
        
        assertEquals(northAfterAdd - 1, ship.getCondensedShip().getShields().getNorthShields());
        assertEquals(eastAfterAdd - 1, ship.getCondensedShip().getShields().getEastShields());
    }

    @Test
    public void testRemovedFromShipRightOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.RIGHT);
        shield.setOrientation(Direction.RIGHT);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        shield.added();
        int southAfterAdd = ship.getCondensedShip().getShields().getSouthShields();
        int eastAfterAdd = ship.getCondensedShip().getShields().getEastShields();
        
        shield.removed();
        
        assertEquals(southAfterAdd - 1, ship.getCondensedShip().getShields().getSouthShields());
        assertEquals(eastAfterAdd - 1, ship.getCondensedShip().getShields().getEastShields());
    }

    @Test
    public void testRemovedFromShipDownOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.DOWN);
        shield.setOrientation(Direction.DOWN);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        shield.added();
        int southAfterAdd = ship.getCondensedShip().getShields().getSouthShields();
        int westAfterAdd = ship.getCondensedShip().getShields().getWestShields();
        
        shield.removed();
        
        assertEquals(southAfterAdd - 1, ship.getCondensedShip().getShields().getSouthShields());
        assertEquals(westAfterAdd - 1, ship.getCondensedShip().getShields().getWestShields());
    }

    @Test
    public void testRemovedFromShipLeftOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.LEFT);
        shield.setOrientation(Direction.LEFT);
        ShipBoard ship = new ShipBoard();
        shield.setShipBoard(ship);
        
        shield.added();
        int northAfterAdd = ship.getCondensedShip().getShields().getNorthShields();
        int westAfterAdd = ship.getCondensedShip().getShields().getWestShields();
        
        shield.removed();
        
        assertEquals(northAfterAdd - 1, ship.getCondensedShip().getShields().getNorthShields());
        assertEquals(westAfterAdd - 1, ship.getCondensedShip().getShields().getWestShields());
    }

    @Test
    public void testVisualize() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        assertDoesNotThrow(() -> shield.visualize());
    }

    @Test
    public void testRenderSmallUpOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        
        String[] render = shield.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("S↑→"));
    }

    @Test
    public void testRenderSmallRightOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.RIGHT);
        shield.setOrientation(Direction.RIGHT);
        
        String[] render = shield.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("S→↓"));
    }

    @Test
    public void testRenderSmallDownOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.DOWN);
        shield.setOrientation(Direction.DOWN);
        
        String[] render = shield.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("S↓←"));
    }

    @Test
    public void testRenderSmallLeftOrientation() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.LEFT);
        shield.setOrientation(Direction.LEFT);
        
        String[] render = shield.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("S←↑"));
    }

    @Test
    public void testRenderBig() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        
        String[] render = shield.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
        assertTrue(render[1].contains("SHIELD"));
    }

    @Test
    public void testRenderWithNoneConnectors() {
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, Direction.UP);
        
        String[] smallRender = shield.renderSmall();
        assertNotNull(smallRender);
        assertEquals(3, smallRender.length);
        
        String[] bigRender = shield.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
    }

    @Test
    public void testMultipleShieldsOnShip() {
        ShieldGenerator shield1 = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        ShieldGenerator shield2 = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.DOWN);
        ShieldGenerator shield3 = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.LEFT);
        ShieldGenerator shield4 = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.RIGHT);
        
        shield2.setOrientation(Direction.DOWN);
        shield3.setOrientation(Direction.LEFT);
        shield4.setOrientation(Direction.RIGHT);
        
        ShipBoard ship = new ShipBoard();
        shield1.setShipBoard(ship);
        shield2.setShipBoard(ship);
        shield3.setShipBoard(ship);
        shield4.setShipBoard(ship);
        
        shield1.added(); // UP: +1 North, +1 East
        shield2.added(); // DOWN: +1 South, +1 West
        shield3.added(); // LEFT: +1 North, +1 West
        shield4.added(); // RIGHT: +1 South, +1 East
        
        assertEquals(2, ship.getCondensedShip().getShields().getNorthShields()); // UP + LEFT
        assertEquals(2, ship.getCondensedShip().getShields().getSouthShields()); // DOWN + RIGHT
        assertEquals(2, ship.getCondensedShip().getShields().getEastShields());  // UP + RIGHT
        assertEquals(2, ship.getCondensedShip().getShields().getWestShields());  // DOWN + LEFT
    }
}