package Controller.Commands;

import Controller.Controller;
import Model.Ship.Coordinates;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GetGoodCommandTest {

    private Controller controller;
    private GetGoodCommand command;
    private Coordinates coordinates;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        coordinates = new Coordinates(5, 5);
        command = new GetGoodCommand("Anna", 0, coordinates, 0);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(0, command.getGoodIndex());
        assertEquals(coordinates, command.getCoordinates());
        assertEquals(0, command.getCargoHoldIndex());
    }



    @Test
    public void testGetGoodIndex() {
        assertEquals(0, command.getGoodIndex());
    }

    @Test
    public void testGetCoordinates() {
        assertEquals(coordinates, command.getCoordinates());
    }

    @Test
    public void testGetCargoHoldIndex() {
        assertEquals(0, command.getCargoHoldIndex());
    }

    @Test
    public void testGetConstructor() {
        var constructor = GetGoodCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(4, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("goodIndex"));
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        assertTrue(constructor.getArguments().contains("cargoHoldIndex"));
    }


    @Test
    public void testConstructorInvalidGoodIndex() {
        var constructor = GetGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "goodIndex", "invalid",
            "row", "3",
            "column", "4",
            "cargoHoldIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidRow() {
        var constructor = GetGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "goodIndex", "2",
            "row", "invalid",
            "column", "4",
            "cargoHoldIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidColumn() {
        var constructor = GetGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "goodIndex", "2",
            "row", "3",
            "column", "invalid",
            "cargoHoldIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidCargoHoldIndex() {
        var constructor = GetGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "goodIndex", "2",
            "row", "3",
            "column", "4",
            "cargoHoldIndex", "invalid"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }



    @Test
    public void testWithNullCoordinates() {
        GetGoodCommand nullCommand = new GetGoodCommand("Anna", 0, null, 0);
        assertNull(nullCommand.getCoordinates());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof GetGoodCommand);
    }
}