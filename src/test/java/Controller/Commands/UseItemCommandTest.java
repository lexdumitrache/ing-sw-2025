package Controller.Commands;

import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.*;
import Model.Ship.Coordinates;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UseItemCommandTest {

    private Controller controller;
    private UseItemCommand command;
    private Coordinates coordinates;

    @BeforeEach
    public void setUp() {
        // Removed TestStateManager initialization
        controller = new Controller(MatchLevel.TRIAL, 1);
        coordinates = new Coordinates(5, 5);
        command = new UseItemCommand("Anna", ItemType.BATTERIES, coordinates);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(ItemType.BATTERIES, command.getItemType());
        assertEquals(coordinates, command.getCoordinates());
    }

    @Test
    public void testExecute() {
        assertThrows(InvalidCommand.class, () -> command.execute(controller));
    }

    @Test
    public void testGetItemType() {
        assertEquals(ItemType.BATTERIES, command.getItemType());
    }

    @Test
    public void testGetCoordinates() {
        assertEquals(coordinates, command.getCoordinates());
    }

    @Test
    public void testCrewCommand() {
        UseItemCommand crewCommand = new UseItemCommand("Anna", ItemType.CREW, coordinates);
        assertEquals(ItemType.CREW, crewCommand.getItemType());
    }

    @Test
    public void testGetBatteriesConstructor() {
        var constructor = UseItemCommand.getBatteriesConstructor();
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
    }

    @Test
    public void testGetCrewConstructor() {
        var constructor = UseItemCommand.getCrewConstructor();
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
    }

    @Test
    public void testBatteriesConstructorCreate() {
        var constructor = UseItemCommand.getBatteriesConstructor();
        var args = java.util.Map.of("row", "3", "column", "4");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof UseItemCommand);
        UseItemCommand useCmd = (UseItemCommand) created;
        assertEquals("Player1", useCmd.getPlayerName());
        assertEquals(ItemType.BATTERIES, useCmd.getItemType());
        assertEquals(3, useCmd.getCoordinates().getI());
        assertEquals(4, useCmd.getCoordinates().getJ());
    }

    @Test
    public void testCrewConstructorCreate() {
        var constructor = UseItemCommand.getCrewConstructor();
        var args = java.util.Map.of("row", "1", "column", "2");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof UseItemCommand);
        UseItemCommand useCmd = (UseItemCommand) created;
        assertEquals("Player1", useCmd.getPlayerName());
        assertEquals(ItemType.CREW, useCmd.getItemType());
        assertEquals(1, useCmd.getCoordinates().getI());
        assertEquals(2, useCmd.getCoordinates().getJ());
    }

    @Test
    public void testBatteriesConstructorInvalidRow() {
        var constructor = UseItemCommand.getBatteriesConstructor();
        var args = java.util.Map.of("row", "invalid", "column", "4");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testBatteriesConstructorInvalidColumn() {
        var constructor = UseItemCommand.getBatteriesConstructor();
        var args = java.util.Map.of("row", "3", "column", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testCrewConstructorInvalidRow() {
        var constructor = UseItemCommand.getCrewConstructor();
        var args = java.util.Map.of("row", "invalid", "column", "2");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testCrewConstructorInvalidColumn() {
        var constructor = UseItemCommand.getCrewConstructor();
        var args = java.util.Map.of("row", "1", "column", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testAllItemTypes() {
        for (ItemType type : ItemType.values()) {
            UseItemCommand typeCmd = new UseItemCommand("Player", type, coordinates);
            assertEquals(type, typeCmd.getItemType());
        }
    }

    @Test
    public void testWithNullItemType() {
        UseItemCommand nullCommand = new UseItemCommand("Anna", null, coordinates);
        assertNull(nullCommand.getItemType());
    }

    @Test
    public void testWithNullCoordinates() {
        UseItemCommand nullCommand = new UseItemCommand("Anna", ItemType.BATTERIES, null);
        assertNull(nullCommand.getCoordinates());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof UseItemCommand);
    }
}