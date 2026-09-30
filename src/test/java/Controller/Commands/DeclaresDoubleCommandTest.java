package Controller.Commands;

import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeclaresDoubleCommandTest {

    private Controller controller;
    private DeclaresDoubleCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new DeclaresDoubleCommand("Anna", DoubleType.ENGINES, 2.0);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(DoubleType.ENGINES, command.getDoubleType());
        assertEquals(2.0, command.getAmount());
    }

    @Test
    public void testExecute() throws Exception {
        assertThrows(Exception.class, () -> command.execute(controller));
    }

    @Test
    public void testGetDoubleType() {
        assertEquals(DoubleType.ENGINES, command.getDoubleType());
    }

    @Test
    public void testGetAmount() {
        assertEquals(2.0, command.getAmount());
    }

    @Test
    public void testCannonConstructor() {
        DeclaresDoubleCommand cannonCommand = new DeclaresDoubleCommand("Player", DoubleType.CANNONS, 1.5);
        assertEquals(DoubleType.CANNONS, cannonCommand.getDoubleType());
        assertEquals(1.5, cannonCommand.getAmount());
    }

    @Test
    public void testGetCannonConstructor() {
        var constructor = DeclaresDoubleCommand.getCannonConstructor();
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("amount"));
    }

    @Test
    public void testGetEngineConstructor() {
        var constructor = DeclaresDoubleCommand.getEngineConstructor();
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("amount"));
    }

    @Test
    public void testCannonConstructorCreate() {
        var constructor = DeclaresDoubleCommand.getCannonConstructor();
        var args = java.util.Map.of("amount", "3.0");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof DeclaresDoubleCommand);
        DeclaresDoubleCommand doubleCmd = (DeclaresDoubleCommand) created;
        assertEquals("Player1", doubleCmd.getPlayerName());
        assertEquals(DoubleType.CANNONS, doubleCmd.getDoubleType());
        assertEquals(3.0, doubleCmd.getAmount());
    }

    @Test
    public void testEngineConstructorCreate() {
        var constructor = DeclaresDoubleCommand.getEngineConstructor();
        var args = java.util.Map.of("amount", "2.5");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof DeclaresDoubleCommand);
        DeclaresDoubleCommand doubleCmd = (DeclaresDoubleCommand) created;
        assertEquals("Player1", doubleCmd.getPlayerName());
        assertEquals(DoubleType.ENGINES, doubleCmd.getDoubleType());
        assertEquals(2.5, doubleCmd.getAmount());
    }

    @Test
    public void testCannonConstructorInvalidAmount() {
        var constructor = DeclaresDoubleCommand.getCannonConstructor();
        var args = java.util.Map.of("amount", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testEngineConstructorInvalidAmount() {
        var constructor = DeclaresDoubleCommand.getEngineConstructor();
        var args = java.util.Map.of("amount", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testZeroAmount() {
        DeclaresDoubleCommand zeroCommand = new DeclaresDoubleCommand("Player", DoubleType.ENGINES, 0.0);
        assertEquals(0.0, zeroCommand.getAmount());
    }

    @Test
    public void testNegativeAmount() {
        DeclaresDoubleCommand negativeCommand = new DeclaresDoubleCommand("Player", DoubleType.CANNONS, -1.0);
        assertEquals(-1.0, negativeCommand.getAmount());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}