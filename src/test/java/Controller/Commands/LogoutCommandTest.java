package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogoutCommandTest {

    private Controller controller;
    private LogoutCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new LogoutCommand("Anna");
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
    }


    @Test
    public void testGetConstructor() {
        var constructor = LogoutCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
    }

    @Test
    public void testConstructorCreate() {
        var constructor = LogoutCommand.getConstructor();
        var args = java.util.Map.<String, String>of();
        Command created = constructor.create("Player1", args);

        assertNotNull(created);
        assertTrue(created instanceof LogoutCommand);
        assertEquals("Player1", created.getPlayerName());
    }

    @Test
    public void testWithNullPlayerName() {
        LogoutCommand nullCommand = new LogoutCommand(null);
        assertNull(nullCommand.getPlayerName());
    }

    @Test
    public void testWithEmptyPlayerName() {
        LogoutCommand emptyCommand = new LogoutCommand("");
        assertEquals("", emptyCommand.getPlayerName());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof LogoutCommand);
    }
}

