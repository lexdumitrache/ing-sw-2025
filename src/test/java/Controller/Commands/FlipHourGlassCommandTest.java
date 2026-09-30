package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FlipHourGlassCommandTest {

    private Controller controller;
    private FlipHourGlassCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new FlipHourGlassCommand("Anna");
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
    }



    @Test
    public void testGetConstructor() {
        var constructor = FlipHourGlassCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
    }

    @Test
    public void testConstructorCreate() {
        var constructor = FlipHourGlassCommand.getConstructor();
        var args = java.util.Map.<String, String>of();
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof FlipHourGlassCommand);
        assertEquals("Player1", created.getPlayerName());
    }

    @Test
    public void testWithNullPlayerName() {
        FlipHourGlassCommand nullCommand = new FlipHourGlassCommand(null);
        assertNull(nullCommand.getPlayerName());
    }

    @Test
    public void testWithEmptyPlayerName() {
        FlipHourGlassCommand emptyCommand = new FlipHourGlassCommand("");
        assertEquals("", emptyCommand.getPlayerName());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof FlipHourGlassCommand);
    }
}