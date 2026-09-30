package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChoosePlanetCommandTest {

    private Controller controller;
    private ChoosePlanetCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new ChoosePlanetCommand("Anna", "TestPlanet");
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals("TestPlanet", command.getPlanetName());
    }

    @Test
    public void testExecute() throws Exception {
        assertThrows(InvalidCommand.class, () -> command.execute(controller));
    }

    @Test
    public void testGetPlanetName() {
        assertEquals("TestPlanet", command.getPlanetName());
    }

    @Test
    public void testGetConstructor() {
        var constructor = ChoosePlanetCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("planetName"));
    }

    @Test
    public void testConstructorCreate() {
        var constructor = ChoosePlanetCommand.getConstructor();
        var args = java.util.Map.of("planetName", "Mars");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof ChoosePlanetCommand);
        assertEquals("Player1", created.getPlayerName());
        assertEquals("Mars", ((ChoosePlanetCommand) created).getPlanetName());
    }

    @Test
    public void testWithNullPlanetName() {
        ChoosePlanetCommand nullCommand = new ChoosePlanetCommand("Anna", null);
        assertNull(nullCommand.getPlanetName());
    }

    @Test
    public void testWithEmptyPlanetName() {
        ChoosePlanetCommand emptyCommand = new ChoosePlanetCommand("Anna", "");
        assertEquals("", emptyCommand.getPlanetName());
    }

    @Test
    public void testExecuteWithInvalidPlayer() {
        ChoosePlanetCommand invalidCommand = new ChoosePlanetCommand("NonExistent", "TestPlanet");
        assertThrows(Exception.class, () -> invalidCommand.execute(controller));
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}