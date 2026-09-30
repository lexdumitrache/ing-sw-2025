package Controller.CombatZone.Level_ONE;

import Controller.Commands.Command;
import Controller.Commands.EndCommand;
import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Crewmates;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZone1CrewRemovalStateTest {

    private Controller controller;
    private Context context;
    private CombatZone1CrewRemovalState state;
    private Player player1;
    private Player player2;
    private Coordinates cabinCoords;
    private Cabin cabin;

    @BeforeEach
    void setUp() {
        try {
            controller = new Controller(MatchLevel.TRIAL, 1);
            controller.getModel().addPlayer("Player1");
            controller.getModel().addPlayer("Player2");
            
            player1 = controller.getModel().getPlayer("Player1");
            player2 = controller.getModel().getPlayer("Player2");
            
            // Set up flight board positions
            controller.getModel().getFlightBoard().setStartingPositions(player1, 1);
            controller.getModel().getFlightBoard().setStartingPositions(player2, 2);
            
            context = new Context(controller);
            
            // Set up the context's player list - ensure it's not empty
            context.getPlayers().clear();
            context.getPlayers().add(player1);
            context.getPlayers().add(player2);
            
            // Set up special players list for the state constructor
            context.addSpecialPlayer(player1);
            
            // Set crewmates in context
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 1);
            
            // Add cabin to player1's ship
            cabinCoords = new Coordinates(7, 7);
            cabin = (Cabin) player1.getShipBoard().getComponent(cabinCoords);
            if (cabin == null) {
                fail("Central cabin not found at coordinates (7,7)");
            }
            cabin.setOccupants(Crewmates.SINGLE_HUMAN);
            
            state = new CombatZone1CrewRemovalState(context);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testUseItem_ValidSingleHumanRemoval() throws InvalidContextualAction, InvalidParameters {
        cabin.setOccupants(Crewmates.SINGLE_HUMAN);
        
        // Set crewmates to 1 so it transitions to PowerDeclarationState
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 1);
        } catch (Exception e) {
            fail("Failed to set crewmates: " + e.getMessage());
        }
        
        // Ensure context has players for transition
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        state.useItem("Player1", ItemType.CREW, cabinCoords);
        
        assertFalse(controller.getModel().isError());
        assertEquals(Crewmates.EMPTY, cabin.getOccupants());
        // Should transition to PowerDeclarationState when crewmates reaches 0
        assertInstanceOf(CombatZone1PowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_ValidPurpleAlienRemoval() throws InvalidContextualAction, InvalidParameters {
        cabin.setOccupants(Crewmates.PURPLE_ALIEN);
        
        // Set crewmates to 1 so it transitions to PowerDeclarationState
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 1);
        } catch (Exception e) {
            fail("Failed to set crewmates: " + e.getMessage());
        }
        
        // Ensure context has players for transition
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        state.useItem("Player1", ItemType.CREW, cabinCoords);
        
        assertFalse(controller.getModel().isError());
        assertEquals(Crewmates.EMPTY, cabin.getOccupants());
        // Should transition to PowerDeclarationState when crewmates reaches 0
        assertInstanceOf(CombatZone1PowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_ZeroCrewmates() throws InvalidContextualAction, InvalidParameters {
        // Set crewmates to 0 initially
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 0);
        } catch (Exception e) {
            fail("Failed to set crewmates: " + e.getMessage());
        }
        
        // Ensure context has players for transition
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        state.useItem("Player1", ItemType.CREW, cabinCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to PowerDeclarationState when crewmates is already 0
        assertInstanceOf(CombatZone1PowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_EmptyCabin() {
        cabin.setOccupants(Crewmates.EMPTY);
        
        // Set crewmates to 1
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 1);
        } catch (Exception e) {
            fail("Failed to set crewmates: " + e.getMessage());
        }
        
        // This should throw an exception because the player doesn't have enough crew
        assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.CREW, cabinCoords));
    }

    @Test
    void testUseItem_ValidDoubleHumanRemoval() throws InvalidContextualAction, InvalidParameters {
        cabin.setOccupants(Crewmates.DOUBLE_HUMAN);
        
        // Set crewmates to 2 so it stays in CrewRemovalState after removing 1
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 2);
        } catch (Exception e) {
            fail("Failed to set crewmates: " + e.getMessage());
        }
        
        state.useItem("Player1", ItemType.CREW, cabinCoords);
        
        assertFalse(controller.getModel().isError());
        assertEquals(Crewmates.SINGLE_HUMAN, cabin.getOccupants());
        // Should stay in CrewRemovalState since we still have crew
        assertInstanceOf(CombatZone1CrewRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_ValidAlienRemoval() throws InvalidContextualAction, InvalidParameters {
        cabin.setOccupants(Crewmates.BROWN_ALIEN);
        
        // Set crewmates to 1 so it transitions to PowerDeclarationState
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 1);
        } catch (Exception e) {
            fail("Failed to set crewmates: " + e.getMessage());
        }
        
        // Ensure context has players for transition
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        state.useItem("Player1", ItemType.CREW, cabinCoords);
        
        assertFalse(controller.getModel().isError());
        assertEquals(Crewmates.EMPTY, cabin.getOccupants());
        // Should transition to PowerDeclarationState when crewmates reaches 0
        assertInstanceOf(CombatZone1PowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_InvalidItemType() {
        assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, cabinCoords));
    }

    @Test
    void testUseItem_WrongPlayer() {
        assertThrows(InvalidParameters.class,
            () -> state.useItem("Player2", ItemType.CREW, cabinCoords));
    }

    @Test
    void testUseItem_InsufficientCrew() {
        // Set required crew to more than player has
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, 10);
        } catch (Exception e) {
            fail("Failed to set crewmates: " + e.getMessage());
        }
        
        assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.CREW, cabinCoords));
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("UseCrew"));
    }

    @Test
    void testControllerServerIntegration_SendAll() {
        assertDoesNotThrow(() -> controller.sendAll());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testControllerGameID() {
        assertEquals(1, controller.getGameID());
        assertEquals(MatchLevel.TRIAL, controller.getMatchLevel());
    }

    @Test
    void testControllerCommandQueue() throws InterruptedException {
        Command testCommand = new EndCommand("Player1");
        controller.enqueueCommand(testCommand);
        
        Thread dequeueThread = new Thread(() -> {
            Command dequeuedCommand = controller.dequeueCommand();
            assertNotNull(dequeuedCommand);
            assertEquals("Player1", dequeuedCommand.getPlayerName());
        });
        
        dequeueThread.start();
        dequeueThread.join(1000);
    }

    @Test
    void testControllerErrorHandling() {
        assertFalse(controller.getModel().isError());
        assertThrows(InvalidCommand.class, 
            () -> controller.useItem("NonExistentPlayer", ItemType.CREW, cabinCoords));
    }

    @Test
    void testControllerPlayerManagement() {
        assertEquals(player1, controller.getModel().getPlayer("Player1"));
        assertEquals(player2, controller.getModel().getPlayer("Player2"));
        assertNull(controller.getModel().getPlayer("NonExistentPlayer"));
    }

    @Test
    void testControllerModelCloning() {
        Model.Game originalModel = controller.getModel();
        Model.Game clonedModel = originalModel.clone();
        
        assertNotSame(originalModel, clonedModel);
        assertEquals(originalModel.getPlayers().size(), clonedModel.getPlayers().size());
    }
}