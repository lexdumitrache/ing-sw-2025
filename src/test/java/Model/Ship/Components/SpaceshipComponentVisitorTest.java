package Model.Ship.Components;

import Model.Enums.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpaceshipComponentVisitorTest {

    private static class TestVisitor implements SpaceshipComponentVisitor {
        private String lastVisited = "";

        @Override
        public void visit(Cabin cabin) {
            lastVisited = "Cabin";
        }

        @Override
        public void visit(Cannon cannon) {
            lastVisited = "Cannon";
        }

        @Override
        public void visit(Engine engine) {
            lastVisited = "Engine";
        }

        @Override
        public void visit(BatteryCompartment battery) {
            lastVisited = "BatteryCompartment";
        }

        @Override
        public void visit(CargoHold cargo) {
            lastVisited = "CargoHold";
        }

        @Override
        public void visit(ShieldGenerator shield) {
            lastVisited = "ShieldGenerator";
        }

        @Override
        public void visit(StructuralModule structural) {
            lastVisited = "StructuralModule";
        }

        @Override
        public void visit(AlienLifeSupport support) {
            lastVisited = "AlienLifeSupport";
        }

        public String getLastVisited() {
            return lastVisited;
        }
    }

    @Test
    public void testVisitorInterface() {
        TestVisitor visitor = new TestVisitor();
        
        Cabin cabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Crewmates.EMPTY);
        visitor.visit(cabin);
        assertEquals("Cabin", visitor.getLastVisited());
        
        Cannon cannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        visitor.visit(cannon);
        assertEquals("Cannon", visitor.getLastVisited());
        
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, false);
        visitor.visit(engine);
        assertEquals("Engine", visitor.getLastVisited());
        
        BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 3);
        visitor.visit(battery);
        assertEquals("BatteryCompartment", visitor.getLastVisited());
        
        CargoHold cargo = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, 2, false);
        visitor.visit(cargo);
        assertEquals("CargoHold", visitor.getLastVisited());
        
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, Direction.UP);
        visitor.visit(shield);
        assertEquals("ShieldGenerator", visitor.getLastVisited());
        
        StructuralModule structural = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE);
        visitor.visit(structural);
        assertEquals("StructuralModule", visitor.getLastVisited());
        
        AlienLifeSupport support = new AlienLifeSupport(Card.ALIEN_LIFE_SUPPORT, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE, AlienColor.BROWN);
        visitor.visit(support);
        assertEquals("AlienLifeSupport", visitor.getLastVisited());
    }
}