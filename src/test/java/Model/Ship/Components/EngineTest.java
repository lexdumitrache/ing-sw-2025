package Model.Ship.Components;

import Model.Enums.*;
import Model.Ship.ShipBoard;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    @Test
    public void testSingleEngineConstructor() {
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        
        assertEquals(Card.ENGINE, engine.getType());
        assertFalse(engine.isDoubleEngine());
    }

    @Test
    public void testDoubleEngineConstructor() {
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, true);
        
        assertEquals(Card.ENGINE, engine.getType());
        assertTrue(engine.isDoubleEngine());
    }

    @Test
    public void testJsonConstructorSingleEngine() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ENGINE");
        json.addProperty("imagePath", "test.png");
        json.addProperty("isDoubleEngine", false);
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        Engine engine = new Engine(json);
        assertEquals(Card.ENGINE, engine.getType());
        assertFalse(engine.isDoubleEngine());
        assertEquals(ConnectorType.UNIVERSAL, engine.getConnectorAt(Side.FRONT));
        assertEquals(ConnectorType.SINGLE, engine.getConnectorAt(Side.REAR));
        assertEquals(ConnectorType.DOUBLE, engine.getConnectorAt(Side.LEFT));
        assertEquals(ConnectorType.NONE, engine.getConnectorAt(Side.RIGHT));
    }

    @Test
    public void testJsonConstructorDoubleEngine() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ENGINE");
        json.addProperty("imagePath", "test.png");
        json.addProperty("isDoubleEngine", true);
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        Engine engine = new Engine(json);
        assertEquals(Card.ENGINE, engine.getType());
        assertTrue(engine.isDoubleEngine());
    }

    @Test
    public void testJsonConstructorMissingIsDouble() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "ENGINE");
        json.addProperty("imagePath", "test.png");
        
        JsonObject connectors = new JsonObject();
        connectors.addProperty("front", "UNIVERSAL");
        connectors.addProperty("rear", "SINGLE");
        connectors.addProperty("left", "DOUBLE");
        connectors.addProperty("right", "NONE");
        json.add("connectors", connectors);
        
        assertThrows(RuntimeException.class, () -> new Engine(json));
    }

    @Test
    public void testAddedSingleEngineToShip() {
        Engine singleEngine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        ShipBoard ship = new ShipBoard();
        singleEngine.setShipBoard(ship);
        
        int initialSingleEngines = ship.getCondensedShip().getEngines().getSingleEngines();
        singleEngine.added();
        
        assertTrue(ship.getCondensedShip().getEnginesList().contains(singleEngine));
        assertEquals(initialSingleEngines + 1, ship.getCondensedShip().getEngines().getSingleEngines());
        
        assertThrows(RuntimeException.class, singleEngine::added);
    }

    @Test
    public void testAddedDoubleEngineToShip() {
        Engine doubleEngine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, true);
        ShipBoard ship = new ShipBoard();
        doubleEngine.setShipBoard(ship);
        
        int initialDoubleEngines = ship.getCondensedShip().getEngines().getDoubleEngines();
        doubleEngine.added();
        
        assertTrue(ship.getCondensedShip().getEnginesList().contains(doubleEngine));
        assertEquals(initialDoubleEngines + 1, ship.getCondensedShip().getEngines().getDoubleEngines());
    }

    @Test
    public void testRemovedSingleEngineFromShip() {
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        ShipBoard ship = new ShipBoard();
        engine.setShipBoard(ship);
        
        engine.added();
        int singleEnginesAfterAdd = ship.getCondensedShip().getEngines().getSingleEngines();
        
        engine.removed();
        
        assertFalse(ship.getCondensedShip().getEnginesList().contains(engine));
        assertEquals(singleEnginesAfterAdd - 1, ship.getCondensedShip().getEngines().getSingleEngines());
        
        assertThrows(RuntimeException.class, engine::removed);
    }

    @Test
    public void testRemovedDoubleEngineFromShip() {
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, true);
        ShipBoard ship = new ShipBoard();
        engine.setShipBoard(ship);
        
        engine.added();
        int doubleEnginesAfterAdd = ship.getCondensedShip().getEngines().getDoubleEngines();
        
        engine.removed();
        
        assertFalse(ship.getCondensedShip().getEnginesList().contains(engine));
        assertEquals(doubleEnginesAfterAdd - 1, ship.getCondensedShip().getEngines().getDoubleEngines());
    }

    @Test
    public void testVisualize() {
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, true);
        assertDoesNotThrow(() -> engine.visualize());
    }

    @Test
    public void testRenderSmallSingleEngine() {
        Engine singleEngine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        
        String[] render = singleEngine.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("E1"));
    }

    @Test
    public void testRenderSmallDoubleEngine() {
        Engine doubleEngine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, true);
        
        String[] render = doubleEngine.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("E2"));
    }

    @Test
    public void testRenderBigSingleEngine() {
        Engine singleEngine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        
        String[] render = singleEngine.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
        assertTrue(render[2].contains("ENGINE"));
        assertTrue(render[1].contains("SINGLE"));
    }

    @Test
    public void testRenderBigDoubleEngine() {
        Engine doubleEngine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, true);
        
        String[] render = doubleEngine.renderBig();
        assertNotNull(render);
        assertEquals(6, render.length);
        assertTrue(render[2].contains("ENGINE"));
        assertTrue(render[1].contains("DOUBLE"));

    }

    @Test
    public void testRenderWithOrientations() {
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        
        engine.setOrientation(Direction.UP);
        String[] render = engine.renderSmall();
        assertTrue(render[1].contains(Direction.UP.getFreccia()));
        
        engine.setOrientation(Direction.DOWN);
        render = engine.renderSmall();
        assertTrue(render[1].contains(Direction.DOWN.getFreccia()));
        
        engine.setOrientation(Direction.LEFT);
        render = engine.renderSmall();
        assertTrue(render[1].contains(Direction.LEFT.getFreccia()));
        
        engine.setOrientation(Direction.RIGHT);
        render = engine.renderSmall();
        assertTrue(render[1].contains(Direction.RIGHT.getFreccia()));
    }

    @Test
    public void testRenderWithNoneConnectors() {
        Engine engine = new Engine(Card.ENGINE, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, ConnectorType.NONE, false);
        
        String[] smallRender = engine.renderSmall();
        assertNotNull(smallRender);
        assertEquals(3, smallRender.length);
        
        String[] bigRender = engine.renderBig();
        assertNotNull(bigRender);
        assertEquals(6, bigRender.length);
    }

    @Test
    public void testMultipleEnginesOnShip() {
        Engine engine1 = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        Engine engine2 = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, true);
        Engine engine3 = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        
        ShipBoard ship = new ShipBoard();
        engine1.setShipBoard(ship);
        engine2.setShipBoard(ship);
        engine3.setShipBoard(ship);
        
        engine1.added();
        engine2.added();
        engine3.added();
        
        assertEquals(2, ship.getCondensedShip().getEngines().getSingleEngines());
        assertEquals(1, ship.getCondensedShip().getEngines().getDoubleEngines());
        assertEquals(3, ship.getCondensedShip().getEnginesList().size());
    }
}