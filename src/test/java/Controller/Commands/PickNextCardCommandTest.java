package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PickNextCardCommandTest {

    private Controller controller;
    private PickNextCardCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new PickNextCardCommand("Anna");
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
    }

    @Test
    public void testExecute() {
        // Execute should not throw exceptions due to try-catch
        assertDoesNotThrow(() -> command.execute(controller));
    }

    @Test
    public void testGetConstructor() {
        var constructor = PickNextCardCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
    }

    @Test
    public void testConstructorCreate() {
        var constructor = PickNextCardCommand.getConstructor();
        var args = java.util.Map.<String, String>of();
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof PickNextCardCommand);
        assertEquals("Player1", created.getPlayerName());
    }

    @Test
    public void testWithNullPlayerName() {
        PickNextCardCommand nullCommand = new PickNextCardCommand(null);
        assertNull(nullCommand.getPlayerName());
    }

    @Test
    public void testWithEmptyPlayerName() {
        PickNextCardCommand emptyCommand = new PickNextCardCommand("");
        assertEquals("", emptyCommand.getPlayerName());
    }



    @Test
    public void testExecuteHandlesExceptions() {
        // Test that execute method handles all expected exceptions
        Controller invalidController = new Controller(MatchLevel.TRIAL, 999);
        assertDoesNotThrow(() -> command.execute(invalidController));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof PickNextCardCommand);
    }
}