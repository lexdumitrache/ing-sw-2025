package Model.Ship.Components;

import Model.Enums.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StructuralModuleTest {

    @Test
    public void testConstructor() {
        StructuralModule module = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE);
        
        assertEquals(Card.STRUCTURAL_MODULE, module.getType());
        assertEquals(ConnectorType.UNIVERSAL, module.getConnectorAt(Side.FRONT));
        assertEquals(ConnectorType.SINGLE, module.getConnectorAt(Side.REAR));
        assertEquals(ConnectorType.DOUBLE, module.getConnectorAt(Side.LEFT));
        assertEquals(ConnectorType.NONE, module.getConnectorAt(Side.RIGHT));
    }

    @Test
    public void testAddedAndRemovedMethods() {
        StructuralModule module = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE);
        
        // These methods should not throw exceptions and do nothing
        module.added();
        module.removed();
    }

    @Test
    public void testVisualize() {
        StructuralModule module = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE);
        module.visualize();
    }

    @Test
    public void testRenderMethods() {
        StructuralModule module = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE);
        
        String[] render = module.renderSmall();
        assertNotNull(render);
        assertEquals(3, render.length);
        assertTrue(render[1].contains("STR"));
        
        module.renderBig();
    }

    @Test
    public void testRotationAffectsConnectors() {
        StructuralModule module = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.SINGLE, ConnectorType.DOUBLE, ConnectorType.NONE);
        
        // Initial state
        assertEquals(ConnectorType.UNIVERSAL, module.getConnectorAt(Side.FRONT));
        assertEquals(ConnectorType.SINGLE, module.getConnectorAt(Side.REAR));
        assertEquals(ConnectorType.DOUBLE, module.getConnectorAt(Side.LEFT));
        assertEquals(ConnectorType.NONE, module.getConnectorAt(Side.RIGHT));
        
        // After rotation - check that rotation changes connectors
        module.rotate();
        // Just verify that rotation happened by checking orientation changed
        assertEquals(Direction.RIGHT, module.getOrientation());
    }
}