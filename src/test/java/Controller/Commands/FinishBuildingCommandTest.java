package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.*;
import Controller.RealTimeBuilding.BuildingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FinishBuildingCommandTest {

    private Controller controller;
    private FinishBuildingCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Anna");
        command = new FinishBuildingCommand("Anna", 1);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(1, command.getPosition());
    }

    @Test
    public void testExecute() {
        controller.getModel().setState(new BuildingState(controller));
        controller.getModel().addPlayer("Player2");
        
        // First player finishes at position 1
        FinishBuildingCommand firstCommand = new FinishBuildingCommand("Player2", 1);
        try {
            firstCommand.execute(controller);
        } catch (Exception e) {
            // Ignore any exceptions from the first command
        }
        
        // Now Anna tries to finish at the same position 1 - should throw InvalidParameters
        assertThrows(InvalidParameters.class, () -> command.execute(controller));
    }

    @Test
    public void testGetPosition() {
        assertEquals(1, command.getPosition());
    }

    @Test
    public void testGetConstructor() {
        var constructor = FinishBuildingCommand.getConstructor();
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("position"));
    }

    @Test
    public void testConstructorCreate() {
        var constructor = FinishBuildingCommand.getConstructor();
        var args = java.util.Map.of("position", "3");
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof FinishBuildingCommand);
        FinishBuildingCommand finishCmd = (FinishBuildingCommand) created;
        assertEquals("Player1", finishCmd.getPlayerName());
        assertEquals(3, finishCmd.getPosition());
    }

    @Test
    public void testConstructorInvalidPosition() {
        var constructor = FinishBuildingCommand.getConstructor();
        var args = java.util.Map.of("position", "invalid");
        assertThrows(IllegalArgumentException.class, () -> constructor.create("Player1", args));
    }

    @Test
    public void testWithNegativePosition() {
        FinishBuildingCommand negCommand = new FinishBuildingCommand("Anna", -1);
        assertEquals(-1, negCommand.getPosition());
    }

    @Test
    public void testWithZeroPosition() {
        FinishBuildingCommand zeroCommand = new FinishBuildingCommand("Anna", 0);
        assertEquals(0, zeroCommand.getPosition());
    }

    @Test
    public void testWithLargePosition() {
        FinishBuildingCommand largeCommand = new FinishBuildingCommand("Anna", 999);
        assertEquals(999, largeCommand.getPosition());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof FinishBuildingCommand);
    }
}