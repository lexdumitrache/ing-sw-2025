package Controller.AbandonedStation;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.AbandonedStation;
import Model.Enums.CardLevel;
import Model.Enums.Crewmates;
import Model.Enums.Good;
import Model.Player;
import Model.Ship.Components.Cabin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedStationDecidingStateTest {

    private Controller controller;
    private Context context;
    private AbandonedStationDecidingState state;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        controller.getModel().addPlayer("Player2");
        
        player1 = controller.getModel().getPlayer("Player1");
        player2 = controller.getModel().getPlayer("Player2");
        
        // Create a custom Context
        List<Good> goods = Arrays.asList(Good.RED, Good.BLUE);
        AbandonedStation abandonedStation = new AbandonedStation(1, CardLevel.LEARNER, 1, goods, 2);
        context = new Context(controller);
        
        // Set the required fields manually
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        // Set the values from the abandoned station card
        try {
            java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
            crewmatesField.setAccessible(true);
            crewmatesField.set(context, abandonedStation.getCrew());
            
            java.lang.reflect.Field daysLostField = Context.class.getDeclaredField("daysLost");
            daysLostField.setAccessible(true);
            daysLostField.set(context, abandonedStation.getLandingPenalty().getAmount());
            
            java.lang.reflect.Field creditsField = Context.class.getDeclaredField("credits");
            creditsField.setAccessible(true);
            creditsField.set(context, 5); // Set some credits for reward
            
            java.lang.reflect.Field goodsField = Context.class.getDeclaredField("goods");
            goodsField.setAccessible(true);
            goodsField.set(context, goods);
        } catch (Exception e) {
            fail("Failed to set up context: " + e.getMessage());
        }
        
        state = new AbandonedStationDecidingState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
        assertEquals(context, state.getContext());
    }

    @Test
    void testConstructorWithDifferentPlayerOrder() {
        // Test constructor with different player order in context
        context.getPlayers().clear();
        context.getPlayers().add(player2);
        context.getPlayers().add(player1);
        
        AbandonedStationDecidingState newState = new AbandonedStationDecidingState(context);
        
        assertEquals(player2, newState.getPlayerInTurn());
        assertEquals(context, newState.getContext());
    }

    @Test
    void testSkipReward_ValidPlayer() throws InvalidParameters {
        state.skipReward("Player1");
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getPlayers().contains(player2));
        assertInstanceOf(AbandonedStationDecidingState.class, controller.getModel().getState());
    }

    @Test
    void testErrorStateManagement() throws Exception {
        // Test that error state is properly managed across different scenarios
        
        // First, cause an error
        try {
            state.skipReward("Player2");
        } catch (InvalidParameters e) {
            // Error occurred as expected
        }
        
        // Then perform a valid operation and verify error is cleared
        state.skipReward("Player1");
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testSkipReward_InvalidPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class, 
            () -> state.skipReward("Player2"));
        
        assertEquals("It's not your turn to skip the reward.", exception.getMessage());
    }

    @Test
    void testSkipReward_NullPlayerName() {
        assertThrows(NullPointerException.class,
            () -> state.skipReward(null));
    }

    @Test
    void testSkipReward_AllPlayersSkip() throws InvalidParameters {
        state.skipReward("Player1");
        
        AbandonedStationDecidingState newState = (AbandonedStationDecidingState) controller.getModel().getState();
        newState.skipReward("Player2");
        
        assertFalse(controller.getModel().isError());
        assertTrue(context.getPlayers().isEmpty());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testSkipReward_MultiplePlayersSequential() throws InvalidParameters {
        // Add a third player to test more complex scenarios
        controller.getModel().addPlayer("Player3");
        Player player3 = controller.getModel().getPlayer("Player3");
        context.getPlayers().add(player3);
        
        // First player skips
        state.skipReward("Player1");
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getPlayers().contains(player2));
        assertTrue(context.getPlayers().contains(player3));
        
        // Second player skips
        AbandonedStationDecidingState newState = (AbandonedStationDecidingState) controller.getModel().getState();
        newState.skipReward("Player2");
        assertFalse(context.getPlayers().contains(player2));
        assertTrue(context.getPlayers().contains(player3));
        
        // Third player skips - should transition to FlightPhase
        AbandonedStationDecidingState finalState = (AbandonedStationDecidingState) controller.getModel().getState();
        finalState.skipReward("Player3");
        assertTrue(context.getPlayers().isEmpty());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testGetReward_ValidGoodsReward() throws Exception {
        // Setup flight board with player
        controller.getModel().getFlightBoard().setStartingPositions(player1, 4);
        controller.getModel().getFlightBoard().setStartingPositions(player2, 3);
        
        // Ensure player has enough crew
        Cabin cabin = (Cabin) player1.getShipBoard().getComponent(new Model.Ship.Coordinates(7, 7));
        cabin.setOccupants(Crewmates.DOUBLE_HUMAN);
        
        int initialCredits = player1.getCredits();
        int initialDays = controller.getModel().getFlightBoard().getTotalDistance(player1);
        
        state.getReward("Player1", RewardType.GOODS);
        
        assertFalse(controller.getModel().isError());
        assertEquals(initialCredits + context.getCredits(), player1.getCredits());
        assertInstanceOf(AbandonedStationLandState.class, controller.getModel().getState());
    }

    @Test
    void testGetReward_InvalidRewardType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", RewardType.CREDITS));
        
        assertEquals("It's not your turn to skip the reward.", exception.getMessage());
    }

    @Test
    void testStateTransitionAfterSkip() throws InvalidParameters {
        // Test that state properly transitions and maintains context
        int initialPlayerCount = context.getPlayers().size();
        
        state.skipReward("Player1");
        
        assertEquals(initialPlayerCount - 1, context.getPlayers().size());
        assertInstanceOf(AbandonedStationDecidingState.class, controller.getModel().getState());
        
        // Verify the new state has the same context
        AbandonedStationDecidingState newState = (AbandonedStationDecidingState) controller.getModel().getState();
        assertEquals(context, newState.getContext());
        assertEquals(player2, newState.getPlayerInTurn());
    }

    @Test
    void testGetReward_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player2", RewardType.GOODS));
        
        assertEquals("It's not your turn to take the reward.", exception.getMessage());
    }

    @Test
    void testGetReward_EmptyPlayerName() {
        assertThrows(NullPointerException.class,
            () -> state.getReward("", RewardType.GOODS));
    }

    @Test
    void testSkipReward_EmptyPlayerName() {
        assertThrows(NullPointerException.class,
            () -> state.skipReward(""));
    }

    @Test
    void testGetReward_InsufficientCrew() throws Exception {
        // Setup flight board
        controller.getModel().getFlightBoard().setStartingPositions(player1, 4);
        
        // Make sure cabin is empty
        Cabin cabin = (Cabin) player1.getShipBoard().getComponent(new Model.Ship.Coordinates(7, 7));
        cabin.setOccupants(Crewmates.EMPTY);
        
        // Verify player has no crew
        assertEquals(0, player1.getShipBoard().getCondensedShip().getTotalCrew());
        
        // Try to get reward
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.getReward("Player1", RewardType.GOODS));
        
        assertEquals("The player doesn't have enough crew to take the reward", exception.getMessage());
        assertFalse(controller.getModel().isError()); // Note: error is set to false in this case
    }

    @Test
    void testGetReward_ValidGoodsRewardWithExactCrew() throws Exception {
        // Setup flight board
        controller.getModel().getFlightBoard().setStartingPositions(player1, 4);
        controller.getModel().getFlightBoard().setStartingPositions(player2, 3);
        
        // Set crew to exactly match requirement
        Cabin cabin = (Cabin) player1.getShipBoard().getComponent(new Model.Ship.Coordinates(7, 7));
        cabin.setOccupants(Crewmates.DOUBLE_HUMAN); // This gives 2 crew
        
        // Set context crewmates to 2 (exact match)
        java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
        crewmatesField.setAccessible(true);
        crewmatesField.set(context, 2);
        
        state.getReward("Player1", RewardType.GOODS);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(AbandonedStationLandState.class, controller.getModel().getState());
    }

    @Test
    void testGetReward_PlayerNotFoundInModel() {
        assertThrows(NullPointerException.class,
            () -> state.getReward("NonExistentPlayer", RewardType.GOODS));
    }

    @Test
    void testSkipReward_PlayerNotFoundInModel() {
        assertThrows(NullPointerException.class,
            () -> state.skipReward("NonExistentPlayer"));
    }

    @Test
    void testSkipReward_SinglePlayerSkipsAll() throws InvalidParameters {
        // Remove player2 from context to test single player scenario
        context.removePlayer(player2);
        
        state.skipReward("Player1");
        
        assertFalse(controller.getModel().isError());
        assertTrue(context.getPlayers().isEmpty());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testGetReward_NullRewardType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", null));
        
        assertEquals("It's not your turn to skip the reward.", exception.getMessage());
    }

    @Test
    void testGetReward_CreditsRewardType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", RewardType.CREDITS));
        
        assertEquals("It's not your turn to skip the reward.", exception.getMessage());
    }



    @Test
    void testGetReward_InsufficientCrewBoundaryCase() throws Exception {
        // Setup flight board
        controller.getModel().getFlightBoard().setStartingPositions(player1, 4);
        
        // Set crew to one less than required
        Cabin cabin = (Cabin) player1.getShipBoard().getComponent(new Model.Ship.Coordinates(7, 7));
        cabin.setOccupants(Crewmates.SINGLE_HUMAN); // This gives 1 crew
        
        // Set context crewmates to 2 (one more than player has)
        java.lang.reflect.Field crewmatesField = Context.class.getDeclaredField("crewmates");
        crewmatesField.setAccessible(true);
        crewmatesField.set(context, 2);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.getReward("Player1", RewardType.GOODS));
        
        assertEquals("The player doesn't have enough crew to take the reward", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testContextGettersAndSetters() {
        assertEquals(context, state.getContext());
        assertEquals(player1, state.getPlayerInTurn());
        
        // Test setting different player in turn
        state.setPlayerInTurn(player2);
        assertEquals(player2, state.getPlayerInTurn());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(2, commands.size());
        assertTrue(commands.contains("SkipReward"));
        assertTrue(commands.contains("GetGoodReward"));
    }
}