package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ThrowDicesCommandTest {

    private Controller controller;
    private ThrowDicesCommand command;

    @BeforeEach
    public void setUp() {
        // Removed TestStateManager initialization
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new ThrowDicesCommand("Anna");
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
    }



    @Test
    public void testGetConstructor() {
        var constructor = ThrowDicesCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
    }

    @Test
    public void testConstructorCreate() {
        var constructor = ThrowDicesCommand.getConstructor();
        var args = java.util.Map.<String, String>of();
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof ThrowDicesCommand);
        assertEquals("Player1", created.getPlayerName());
    }

    @Test
    public void testWithNullPlayerName() {
        ThrowDicesCommand nullCommand = new ThrowDicesCommand(null);
        assertNull(nullCommand.getPlayerName());
    }

    @Test
    public void testWithEmptyPlayerName() {
        ThrowDicesCommand emptyCommand = new ThrowDicesCommand("");
        assertEquals("", emptyCommand.getPlayerName());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof ThrowDicesCommand);
    }
}