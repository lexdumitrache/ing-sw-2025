package Controller.Commands;

import Controller.Controller;
import Controller.Enums.ComponentOrigin;
import Model.Enums.Direction;
import Model.Ship.Coordinates;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlaceComponentCommandTest {

    private Controller controller;
    private PlaceComponentCommand command;
    private Coordinates coordinates;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        coordinates = new Coordinates(5, 5);
        command = new PlaceComponentCommand("Anna", ComponentOrigin.HAND, coordinates, Direction.UP);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(ComponentOrigin.HAND, command.getOrigin());
        assertEquals(coordinates, command.getCoordinates());
        assertEquals(Direction.UP, command.getOrientation());
    }


    @Test
    public void testGetOrigin() {
        assertEquals(ComponentOrigin.HAND, command.getOrigin());
    }

    @Test
    public void testGetCoordinates() {
        assertEquals(coordinates, command.getCoordinates());
    }

    @Test
    public void testGetOrientation() {
        assertEquals(Direction.UP, command.getOrientation());
    }

    @Test
    public void testGetConstructor() {
        var constructor = PlaceComponentCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(4, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("origin"));
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        assertTrue(constructor.getArguments().contains("orientation"));
    }

    @Test
    public void testConstructorInvalidOrigin() {
        var constructor = PlaceComponentCommand.getConstructor();
        var args = java.util.Map.of(
            "origin", "INVALID",
            "row", "3",
            "column", "4",
            "orientation", "UP"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidRow() {
        var constructor = PlaceComponentCommand.getConstructor();
        var args = java.util.Map.of(
            "origin", "HAND",
            "row", "invalid",
            "column", "4",
            "orientation", "UP"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidColumn() {
        var constructor = PlaceComponentCommand.getConstructor();
        var args = java.util.Map.of(
            "origin", "HAND",
            "row", "3",
            "column", "invalid",
            "orientation", "UP"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidOrientation() {
        var constructor = PlaceComponentCommand.getConstructor();
        var args = java.util.Map.of(
            "origin", "HAND",
            "row", "3",
            "column", "4",
            "orientation", "INVALID"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testAllOrigins() {
        for (ComponentOrigin origin : ComponentOrigin.values()) {
            PlaceComponentCommand originCmd = new PlaceComponentCommand("Player", origin, coordinates, Direction.UP);
            assertEquals(origin, originCmd.getOrigin());
        }
    }

    @Test
    public void testAllDirections() {
        for (Direction direction : Direction.values()) {
            PlaceComponentCommand dirCmd = new PlaceComponentCommand("Player", ComponentOrigin.HAND, coordinates, direction);
            assertEquals(direction, dirCmd.getOrientation());
        }
    }

    @Test
    public void testWithNullCoordinates() {
        PlaceComponentCommand nullCommand = new PlaceComponentCommand("Anna", ComponentOrigin.HAND, null, Direction.UP);
        assertNull(nullCommand.getCoordinates());
    }

    @Test
    public void testWithNullOrigin() {
        PlaceComponentCommand nullCommand = new PlaceComponentCommand("Anna", null, coordinates, Direction.UP);
        assertNull(nullCommand.getOrigin());
    }

    @Test
    public void testWithNullOrientation() {
        PlaceComponentCommand nullCommand = new PlaceComponentCommand("Anna", ComponentOrigin.HAND, coordinates, null);
        assertNull(nullCommand.getOrientation());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof PlaceComponentCommand);
    }
}