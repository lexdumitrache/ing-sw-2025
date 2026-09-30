package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeleteComponentCommandTest {

    private Controller controller;
    private DeleteComponentCommand command;
    private Coordinates coordinates;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        coordinates = new Coordinates(5, 5);
        command = new DeleteComponentCommand("Anna", coordinates);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(coordinates, command.getCoordinates());
    }


    @Test
    public void testGetCoordinates() {
        assertEquals(coordinates, command.getCoordinates());
    }

    @Test
    public void testGetConstructor() {
        var constructor = DeleteComponentCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
    }

    @Test
    public void testConstructorCreate() {
        var constructor = DeleteComponentCommand.getConstructor();
        var args = java.util.Map.of("row", "3", "column", "4");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof DeleteComponentCommand);
        DeleteComponentCommand deleteCmd = (DeleteComponentCommand) created;
        assertEquals("Player1", deleteCmd.getPlayerName());
        assertEquals(3, deleteCmd.getCoordinates().getI());
        assertEquals(4, deleteCmd.getCoordinates().getJ());
    }

    @Test
    public void testConstructorInvalidRow() {
        var constructor = DeleteComponentCommand.getConstructor();
        var args = java.util.Map.of("row", "invalid", "column", "4");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidColumn() {
        var constructor = DeleteComponentCommand.getConstructor();
        var args = java.util.Map.of("row", "3", "column", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testWithNullCoordinates() {
        DeleteComponentCommand nullCommand = new DeleteComponentCommand("Anna", null);
        assertNull(nullCommand.getCoordinates());
    }

    @Test
    public void testWithNegativeCoordinates() {
        Coordinates negCoords = new Coordinates(-1, -1);
        DeleteComponentCommand negCommand = new DeleteComponentCommand("Anna", negCoords);
        assertEquals(negCoords, negCommand.getCoordinates());
    }

    @Test
    public void testWithZeroCoordinates() {
        Coordinates zeroCoords = new Coordinates(0, 0);
        DeleteComponentCommand zeroCommand = new DeleteComponentCommand("Anna", zeroCoords);
        assertEquals(zeroCoords, zeroCommand.getCoordinates());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}