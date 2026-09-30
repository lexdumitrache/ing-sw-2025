package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GetComponentCommandTest {

    private Controller controller;
    private GetComponentCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new GetComponentCommand("Anna", 0);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(0, command.getIndex());
    }

    @Test
    public void testExecute() {
        // The controller starts in LogInState which doesn't support getComponent
        assertThrows(InvalidCommand.class, () -> command.execute(controller));
    }

    @Test
    public void testGetIndex() {
        assertEquals(0, command.getIndex());
    }

    @Test
    public void testGetConstructor() {
        var constructor = GetComponentCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("index"));
    }

    @Test
    public void testConstructorCreate() {
        var constructor = GetComponentCommand.getConstructor();
        var args = java.util.Map.of("index", "5");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof GetComponentCommand);
        GetComponentCommand getCmd = (GetComponentCommand) created;
        assertEquals("Player1", getCmd.getPlayerName());
        assertEquals(5, getCmd.getIndex());
    }

    @Test
    public void testConstructorInvalidIndex() {
        var constructor = GetComponentCommand.getConstructor();
        var args = java.util.Map.of("index", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testWithNegativeIndex() {
        GetComponentCommand negCommand = new GetComponentCommand("Anna", -1);
        assertEquals(-1, negCommand.getIndex());
    }

    @Test
    public void testWithZeroIndex() {
        GetComponentCommand zeroCommand = new GetComponentCommand("Anna", 0);
        assertEquals(0, zeroCommand.getIndex());
    }

    @Test
    public void testWithLargeIndex() {
        GetComponentCommand largeCommand = new GetComponentCommand("Anna", 999);
        assertEquals(999, largeCommand.getIndex());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof GetComponentCommand);
    }
}