package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.*;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LookDeckCommandTest {

    private Controller controller;
    private LookDeckCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new LookDeckCommand("Anna", 0);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(0, command.getIndex());
    }

    @Test
    public void testExecute() throws Exception {
        assertThrows(InvalidCommand.class, () -> command.execute(controller));
    }

    @Test
    public void testGetIndex() {
        assertEquals(0, command.getIndex());
    }

    @Test
    public void testGetConstructor() {
        var constructor = LookDeckCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("index"));
    }

    @Test
    public void testConstructorCreate() {
        var constructor = LookDeckCommand.getConstructor();
        var args = java.util.Map.of("index", "2");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof LookDeckCommand);
        LookDeckCommand lookCmd = (LookDeckCommand) created;
        assertEquals("Player1", lookCmd.getPlayerName());
        assertEquals(2, lookCmd.getIndex());
    }

    @Test
    public void testConstructorInvalidIndex() {
        var constructor = LookDeckCommand.getConstructor();
        var args = java.util.Map.of("index", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testWithNegativeIndex() {
        LookDeckCommand negCommand = new LookDeckCommand("Anna", -1);
        assertEquals(-1, negCommand.getIndex());
    }

    @Test
    public void testWithZeroIndex() {
        LookDeckCommand zeroCommand = new LookDeckCommand("Anna", 0);
        assertEquals(0, zeroCommand.getIndex());
    }

    @Test
    public void testWithLargeIndex() {
        LookDeckCommand largeCommand = new LookDeckCommand("Anna", 999);
        assertEquals(999, largeCommand.getIndex());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof LookDeckCommand);
    }
}