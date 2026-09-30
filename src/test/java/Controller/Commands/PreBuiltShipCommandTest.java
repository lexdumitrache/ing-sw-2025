package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PreBuiltShipCommandTest {

    private Controller controller;
    private PreBuiltShipCommand command;

    @BeforeEach
    public void setUp() {
        // Removed TestStateManager initialization
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new PreBuiltShipCommand("Anna", 0);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(0, command.getIndex());
    }



    @Test
    public void testGetIndex() {
        assertEquals(0, command.getIndex());
    }

    @Test
    public void testGetConstructor() {
        var constructor = PreBuiltShipCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("index"));
    }

    @Test
    public void testConstructorCreate() {
        var constructor = PreBuiltShipCommand.getConstructor();
        var args = java.util.Map.of("index", "2");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof PreBuiltShipCommand);
        PreBuiltShipCommand shipCmd = (PreBuiltShipCommand) created;
        assertEquals("Player1", shipCmd.getPlayerName());
        assertEquals(2, shipCmd.getIndex());
    }

    @Test
    public void testConstructorInvalidIndex() {
        var constructor = PreBuiltShipCommand.getConstructor();
        var args = java.util.Map.of("index", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testWithNegativeIndex() {
        PreBuiltShipCommand negCommand = new PreBuiltShipCommand("Anna", -1);
        assertEquals(-1, negCommand.getIndex());
    }

    @Test
    public void testWithZeroIndex() {
        PreBuiltShipCommand zeroCommand = new PreBuiltShipCommand("Anna", 0);
        assertEquals(0, zeroCommand.getIndex());
    }

    @Test
    public void testWithLargeIndex() {
        PreBuiltShipCommand largeCommand = new PreBuiltShipCommand("Anna", 999);
        assertEquals(999, largeCommand.getIndex());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof PreBuiltShipCommand);
    }
}