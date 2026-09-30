package Controller.Commands;

import Controller.Controller;
import Controller.Exceptions.*;
import Model.Ship.Coordinates;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MoveGoodCommandTest {

    private Controller controller;
    private MoveGoodCommand command;
    private Coordinates oldCoords;
    private Coordinates newCoords;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        oldCoords = new Coordinates(5, 5);
        newCoords = new Coordinates(6, 6);
        command = new MoveGoodCommand("Anna", oldCoords, newCoords, 0, 1);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(oldCoords, command.getOldCoordinates());
        assertEquals(newCoords, command.getNewCoordinates());
        assertEquals(0, command.getOldIndex());
        assertEquals(1, command.getNewIndex());
    }

    @Test
    public void testExecute() {
        // The controller starts in LogInState which doesn't support moveGoods
        assertThrows(InvalidCommand.class, () -> command.execute(controller));
    }

    @Test
    public void testGetOldCoordinates() {
        assertEquals(oldCoords, command.getOldCoordinates());
    }

    @Test
    public void testGetNewCoordinates() {
        assertEquals(newCoords, command.getNewCoordinates());
    }

    @Test
    public void testGetOldIndex() {
        assertEquals(0, command.getOldIndex());
    }

    @Test
    public void testGetNewIndex() {
        assertEquals(1, command.getNewIndex());
    }

    @Test
    public void testGetConstructor() {
        var constructor = MoveGoodCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(6, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("oldRow"));
        assertTrue(constructor.getArguments().contains("oldColumn"));
        assertTrue(constructor.getArguments().contains("newRow"));
        assertTrue(constructor.getArguments().contains("newColumn"));
        assertTrue(constructor.getArguments().contains("oldIndex"));
        assertTrue(constructor.getArguments().contains("newIndex"));
    }

    @Test
    public void testGetRemoveConstructor() {
        var constructor = MoveGoodCommand.getRemoveConstructor();
        assertNotNull(constructor);
        assertEquals(3, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        assertTrue(constructor.getArguments().contains("index"));
    }

    @Test
    public void testConstructorCreate() {
        var constructor = MoveGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "oldRow", "1",
            "oldColumn", "2",
            "newRow", "3",
            "newColumn", "4",
            "oldIndex", "0",
            "newIndex", "1"
        );
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertInstanceOf(MoveGoodCommand.class, created);
        MoveGoodCommand moveCmd = (MoveGoodCommand) created;
        assertEquals("Player1", moveCmd.getPlayerName());
        // Compare coordinate values instead of object references
        assertEquals(1, moveCmd.getOldCoordinates().getI());
        assertEquals(2, moveCmd.getOldCoordinates().getJ());
        assertEquals(3, moveCmd.getNewCoordinates().getI());
        assertEquals(4, moveCmd.getNewCoordinates().getJ());
        assertEquals(0, moveCmd.getOldIndex());
        assertEquals(1, moveCmd.getNewIndex());
    }

    @Test
    public void testRemoveConstructorCreate() {
        var constructor = MoveGoodCommand.getRemoveConstructor();
        var args = java.util.Map.of(
            "row", "5",
            "oldColumn", "6",
            "index", "2"
        );
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertInstanceOf(MoveGoodCommand.class, created);
        MoveGoodCommand moveCmd = (MoveGoodCommand) created;
        assertEquals("Player1", moveCmd.getPlayerName());
        // Compare coordinate values instead of object references
        assertEquals(5, moveCmd.getOldCoordinates().getI());
        assertEquals(6, moveCmd.getOldCoordinates().getJ());
        assertNull(moveCmd.getNewCoordinates());
        assertEquals(2, moveCmd.getOldIndex());
        assertEquals(2, moveCmd.getNewIndex());
    }

    @Test
    public void testConstructorInvalidOldRow() {
        var constructor = MoveGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "oldRow", "invalid",
            "oldColumn", "2",
            "newRow", "3",
            "newColumn", "4",
            "oldIndex", "0",
            "newIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidOldColumn() {
        var constructor = MoveGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "oldRow", "1",
            "oldColumn", "invalid",
            "newRow", "3",
            "newColumn", "4",
            "oldIndex", "0",
            "newIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidNewRow() {
        var constructor = MoveGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "oldRow", "1",
            "oldColumn", "2",
            "newRow", "invalid",
            "newColumn", "4",
            "oldIndex", "0",
            "newIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidNewColumn() {
        var constructor = MoveGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "oldRow", "1",
            "oldColumn", "2",
            "newRow", "3",
            "newColumn", "invalid",
            "oldIndex", "0",
            "newIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidOldIndex() {
        var constructor = MoveGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "oldRow", "1",
            "oldColumn", "2",
            "newRow", "3",
            "newColumn", "4",
            "oldIndex", "invalid",
            "newIndex", "1"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testConstructorInvalidNewIndex() {
        var constructor = MoveGoodCommand.getConstructor();
        var args = java.util.Map.of(
            "oldRow", "1",
            "oldColumn", "2",
            "newRow", "3",
            "newColumn", "4",
            "oldIndex", "0",
            "newIndex", "invalid"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testRemoveConstructorInvalidRow() {
        var constructor = MoveGoodCommand.getRemoveConstructor();
        var args = java.util.Map.of(
            "row", "invalid",
            "oldColumn", "6",
            "index", "2"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testRemoveConstructorInvalidColumn() {
        var constructor = MoveGoodCommand.getRemoveConstructor();
        var args = java.util.Map.of(
            "row", "5",
            "oldColumn", "invalid",
            "index", "2"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testRemoveConstructorInvalidIndex() {
        var constructor = MoveGoodCommand.getRemoveConstructor();
        var args = java.util.Map.of(
            "row", "5",
            "oldColumn", "6",
            "index", "invalid"
        );
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testWithNullCoordinates() {
        MoveGoodCommand nullCommand = new MoveGoodCommand("Anna", null, null, 0, 1);
        assertNull(nullCommand.getOldCoordinates());
        assertNull(nullCommand.getNewCoordinates());
    }

    @Test
    public void testWithNegativeIndices() {
        MoveGoodCommand negCommand = new MoveGoodCommand("Anna", oldCoords, newCoords, -1, -2);
        assertEquals(-1, negCommand.getOldIndex());
        assertEquals(-2, negCommand.getNewIndex());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof MoveGoodCommand);
    }
}