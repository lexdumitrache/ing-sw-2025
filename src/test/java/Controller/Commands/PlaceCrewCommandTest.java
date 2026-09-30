package Controller.Commands;

import Controller.Controller;
import Controller.Enums.CrewType;
import Controller.Exceptions.InvalidCommand;
import Model.Ship.Coordinates;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlaceCrewCommandTest {

    private Controller controller;
    private PlaceCrewCommand command;
    private Coordinates coordinates;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        coordinates = new Coordinates(7, 7);
        command = new PlaceCrewCommand("Anna", coordinates, CrewType.HUMAN);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(coordinates, command.getCoordinates());
        assertEquals(CrewType.HUMAN, command.getType());
    }

    @Test
    public void testExecute() {
        assertThrows(InvalidCommand.class, () -> command.execute(controller));
    }

    @Test
    public void testGetCoordinates() {
        assertEquals(coordinates, command.getCoordinates());
    }

    @Test
    public void testGetType() {
        assertEquals(CrewType.HUMAN, command.getType());
    }

    @Test
    public void testGetHumanConstructor() {
        var constructor = PlaceCrewCommand.getHumanConstructor();
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
    }

    @Test
    public void testGetBrownConstructor() {
        var constructor = PlaceCrewCommand.getBrownConstructor();
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
    }

    @Test
    public void testGetPurpleConstructor() {
        var constructor = PlaceCrewCommand.getPurpleConstructor();
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
    }

    @Test
    public void testHumanConstructorCreate() {
        var constructor = PlaceCrewCommand.getHumanConstructor();
        var args = java.util.Map.of("row", "5", "column", "6");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof PlaceCrewCommand);
        PlaceCrewCommand crewCmd = (PlaceCrewCommand) created;
        assertEquals("Player1", crewCmd.getPlayerName());
        assertEquals(5, crewCmd.getCoordinates().getI());
        assertEquals(6, crewCmd.getCoordinates().getJ());
        assertEquals(CrewType.HUMAN, crewCmd.getType());
    }

    @Test
    public void testBrownConstructorCreate() {
        var constructor = PlaceCrewCommand.getBrownConstructor();
        var args = java.util.Map.of("row", "3", "column", "4");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof PlaceCrewCommand);
        PlaceCrewCommand crewCmd = (PlaceCrewCommand) created;
        assertEquals("Player1", crewCmd.getPlayerName());
        assertEquals(3, crewCmd.getCoordinates().getI());
        assertEquals(4, crewCmd.getCoordinates().getJ());
        assertEquals(CrewType.BROWN_ALIEN, crewCmd.getType());
    }

    @Test
    public void testPurpleConstructorCreate() {
        var constructor = PlaceCrewCommand.getPurpleConstructor();
        var args = java.util.Map.of("row", "1", "column", "2");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof PlaceCrewCommand);
        PlaceCrewCommand crewCmd = (PlaceCrewCommand) created;
        assertEquals("Player1", crewCmd.getPlayerName());
        assertEquals(1, crewCmd.getCoordinates().getI());
        assertEquals(2, crewCmd.getCoordinates().getJ());
        assertEquals(CrewType.PURPLE_ALIEN, crewCmd.getType());
    }

    @Test
    public void testHumanConstructorInvalidRow() {
        var constructor = PlaceCrewCommand.getHumanConstructor();
        var args = java.util.Map.of("row", "invalid", "column", "6");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testHumanConstructorInvalidColumn() {
        var constructor = PlaceCrewCommand.getHumanConstructor();
        var args = java.util.Map.of("row", "5", "column", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testBrownConstructorInvalidRow() {
        var constructor = PlaceCrewCommand.getBrownConstructor();
        var args = java.util.Map.of("row", "invalid", "column", "4");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testBrownConstructorInvalidColumn() {
        var constructor = PlaceCrewCommand.getBrownConstructor();
        var args = java.util.Map.of("row", "3", "column", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testPurpleConstructorInvalidRow() {
        var constructor = PlaceCrewCommand.getPurpleConstructor();
        var args = java.util.Map.of("row", "invalid", "column", "2");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testPurpleConstructorInvalidColumn() {
        var constructor = PlaceCrewCommand.getPurpleConstructor();
        var args = java.util.Map.of("row", "1", "column", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testAllCrewTypes() {
        for (CrewType type : CrewType.values()) {
            PlaceCrewCommand typeCmd = new PlaceCrewCommand("Player", coordinates, type);
            assertEquals(type, typeCmd.getType());
        }
    }

    @Test
    public void testWithNullCoordinates() {
        PlaceCrewCommand nullCommand = new PlaceCrewCommand("Anna", null, CrewType.HUMAN);
        assertNull(nullCommand.getCoordinates());
    }

    @Test
    public void testWithNullType() {
        PlaceCrewCommand nullCommand = new PlaceCrewCommand("Anna", coordinates, null);
        assertNull(nullCommand.getType());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof PlaceCrewCommand);
    }
}