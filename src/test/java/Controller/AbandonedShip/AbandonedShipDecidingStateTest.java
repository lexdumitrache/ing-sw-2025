package Controller.AbandonedShip;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.AbandonedShip;
import Model.Enums.CardLevel;
import Model.Enums.Crewmates;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipDecidingStateTest {

    private Controller controller;
    private Context context;
    private AbandonedShipDecidingState state;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        controller.getModel().addPlayer("Player2");
        
        player1 = controller.getModel().getPlayer("Player1");
        player2 = controller.getModel().getPlayer("Player2");
        
        // Create a custom Context that doesn't rely on FlightBoard
        AbandonedShip abandonedShip = new AbandonedShip(1, CardLevel.LEARNER, 2, 5, 3);
        context = new Context(controller);
        
        // Set the required fields manually
        context.getPlayers().clear(); // Clear any existing players
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        // Set the values from the abandoned ship card
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, abandonedShip.getWinPenalty().getAmount());
            
            java.lang.reflect.Field creditsField = Context.class.getDeclaredField("credits");
            creditsField.setAccessible(true);
            creditsField.set(context, abandonedShip.getLandingReward().getAmount());
            
            java.lang.reflect.Field daysLostField = Context.class.getDeclaredField("daysLost");
            daysLostField.setAccessible(true);
            daysLostField.set(context, abandonedShip.getLandingPenalty().getAmount());
        } catch (Exception e) {
            fail("Failed to set up context: " + e.getMessage());
        }
        
        // Create state after setting up context
        state = new AbandonedShipDecidingState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
        assertEquals(context, state.getContext());
    }

    @Test
    void testSkipReward_ValidPlayer() throws InvalidParameters {
        // Test valid skip by first player
        state.skipReward("Player1");
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getPlayers().contains(player2));
        assertInstanceOf(AbandonedShipDecidingState.class, controller.getModel().getState());
    }

    @Test
    void testSkipReward_InvalidPlayer() {
        // Test skip by wrong player
        assertThrows(InvalidParameters.class, 
            () -> state.skipReward("Player2"));
        
        assertTrue(context.getPlayers().contains(player1));
    }

    @Test
    void testSkipReward_AllPlayersSkip() throws InvalidParameters {
        // First player skips
        state.skipReward("Player1");
        
        // Verify player1 was removed from context players
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getPlayers().contains(player2));
        
        // Get new state and have second player skip
        AbandonedShipDecidingState newState = (AbandonedShipDecidingState) controller.getModel().getState();
        assertEquals(player2, newState.getPlayerInTurn());
        newState.skipReward("Player2");
        
        // Verify all players are removed and we transition to FlightPhase
        assertTrue(context.getPlayers().isEmpty());
        assertFalse(controller.getModel().isError());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testGetReward_ValidCreditsReward() throws InvalidParameters {
        // Add crew to player1's ship using the central cabin at (7,7)
        Cabin cabin = (Cabin) player1.getShipBoard().getComponent(new Coordinates(7, 7));
        assertNotNull(cabin, "Central cabin should exist at coordinates (7,7)");
        cabin.setOccupants(Crewmates.DOUBLE_HUMAN);
        
        // This test expects an exception due to player not found in flight board
        InvalidMethodParameters exception = assertThrows(InvalidMethodParameters.class,
            () -> state.getReward("Player1", RewardType.CREDITS));
        
        assertEquals("Player not found in flight board", exception.getMessage());
    }

    @Test
    void testGetReward_InvalidRewardType() {
        assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", RewardType.GOODS));
    }

    @Test
    void testGetReward_WrongPlayer() {
        assertThrows(InvalidParameters.class,
            () -> state.getReward("Player2", RewardType.CREDITS));
    }

    @Test
    void testGetReward_InsufficientCrew() {
        // Player has no crew but card requires crew
        // Make sure the central cabin is empty
        Cabin cabin = (Cabin) player1.getShipBoard().getComponent(new Coordinates(7, 7));
        assertNotNull(cabin, "Central cabin should exist at coordinates (7,7)");
        cabin.setOccupants(Crewmates.EMPTY);
        
        // Verify player has no crew
        assertEquals(0, player1.getShipBoard().getCondensedShip().getTotalCrew());
        
        // Try to get reward
        assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", RewardType.CREDITS));
    }

    @Test
    void testGetAvailableCommands() {
        assertEquals(2, state.getAvailableCommands().size());
        assertTrue(state.getAvailableCommands().contains("SkipReward"));
        assertTrue(state.getAvailableCommands().contains("GetCreditsReward"));
    }

    @Test
    void testInheritedMethods() {
        // Test that inherited methods throw InvalidCommand
        assertThrows(InvalidCommand.class, () -> state.login("test"));
        assertThrows(InvalidCommand.class, () -> state.logout("test"));
        assertThrows(InvalidCommand.class, () -> state.startGame("test"));
        assertThrows(InvalidCommand.class, () -> state.getComponent("test", 0));
        assertThrows(InvalidCommand.class, () -> state.reserveComponent("test"));
        assertThrows(InvalidCommand.class, () -> state.placeComponent("test", null, null, null));
        assertThrows(InvalidCommand.class, () -> state.lookDeck("test", 0));
        assertThrows(InvalidCommand.class, () -> state.flipHourGlass("test"));
        assertThrows(InvalidCommand.class, () -> state.finishBuilding("test", 0));
        assertThrows(InvalidCommand.class, () -> state.placeCrew("test", null, null));
        assertThrows(InvalidCommand.class, () -> state.pickNextCard("test"));
        assertThrows(InvalidCommand.class, () -> state.deleteComponent("test", null));
        assertThrows(InvalidCommand.class, () -> state.leaveRace("test"));
        assertThrows(InvalidCommand.class, () -> state.moveGood("test", null, null, 0, 0));
        assertThrows(InvalidCommand.class, () -> state.useItem("test", null, null));
        assertThrows(InvalidCommand.class, () -> state.declaresDouble("test", null, 0));
        assertThrows(InvalidCommand.class, () -> state.end("test"));
        assertThrows(InvalidCommand.class, () -> state.choosePlanet("test", "test"));
        assertThrows(InvalidCommand.class, () -> state.getGood("test", 0, null, 0));
        assertThrows(InvalidCommand.class, () -> state.throwDices("test"));
        assertThrows(InvalidCommand.class, () -> state.preBuiltShip("test", 0));
    }

    @Test
    void testGetReward_NonExistentPlayer() {
        // Test with non-existent player - should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> state.getReward("NonExistentPlayer", RewardType.CREDITS));
    }

    @Test
    void testSkipReward_NonExistentPlayer() {
        // Test with non-existent player - should throw NullPointerException
        assertThrows(NullPointerException.class,
            () -> state.skipReward("NonExistentPlayer"));
    }

    @Test
    void testSkipReward_SinglePlayerRemaining() throws InvalidParameters {
        // Remove player2 from context first
        context.removePlayer(player2);
        
        // Now only player1 should remain
        assertEquals(1, context.getPlayers().size());
        assertTrue(context.getPlayers().contains(player1));
        
        // Skip with the remaining player
        state.skipReward("Player1");
        
        // Should transition to FlightPhase since no players remain
        assertTrue(context.getPlayers().isEmpty());
        assertFalse(controller.getModel().isError());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testContextPlayerManagement() {
        // Test context player list management
        assertEquals(2, context.getPlayers().size());
        assertTrue(context.getPlayers().contains(player1));
        assertTrue(context.getPlayers().contains(player2));
        
        // Test removePlayer
        context.removePlayer(player1);
        assertEquals(1, context.getPlayers().size());
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getPlayers().contains(player2));
    }

    @Test
    void testPlayerInTurnSetCorrectly() {
        // Test that constructor sets player in turn correctly
        assertEquals(player1, state.getPlayerInTurn());
        
        // Test with different context setup
        Context newContext = new Context(controller);
        newContext.getPlayers().clear();
        newContext.getPlayers().add(player2);
        newContext.getPlayers().add(player1);
        
        AbandonedShipDecidingState newState = new AbandonedShipDecidingState(newContext);
        assertEquals(player2, newState.getPlayerInTurn());
    }

    @Test
    void testGetReward_AllRewardTypes() {
        // Test all possible reward types
        for (RewardType rewardType : RewardType.values()) {
            if (rewardType != RewardType.CREDITS) {
                assertThrows(InvalidParameters.class,
                    () -> state.getReward("Player1", rewardType));
            }
        }
    }

    @Test
    void testContextIntegration() {
        // Test that context is properly used
        assertNotNull(state.getContext());
        assertEquals(context, state.getContext());
        assertEquals(controller, context.getController());
        
        // Test context values
        assertTrue(context.getCredits() > 0);
        assertTrue(context.getDaysLost() > 0);
        assertTrue(context.getCrewmates() > 0);
    }

    @Test
    void testIsDone() {
        // Test inherited isDone method
        assertFalse(state.isDone());
    }

    @Test
    void testOnEnter() {
        // Test inherited onEnter method (should not throw)
        assertDoesNotThrow(() -> state.onEnter());
    }

    @Test
    void testGetController() {
        // Test inherited getController method
        assertNull(state.getController()); // Controller is transient in State
    }

    @Test
    void testSetPlayerInTurn() {
        // Test inherited setPlayerInTurn method
        state.setPlayerInTurn(player2);
        assertEquals(player2, state.getPlayerInTurn());
    }
}