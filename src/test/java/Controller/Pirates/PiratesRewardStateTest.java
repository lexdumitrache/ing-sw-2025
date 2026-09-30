package Controller.Pirates;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Board.CardDeck;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PiratesRewardStateTest {

    private Controller controller;
    private Context context;
    private PiratesRewardState state;
    private Player player1;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        
        player1 = controller.getModel().getPlayer("Player1");
        
        // Set starting position for player in flight board
        try {
            controller.getModel().getFlightBoard().setStartingPositions(player1, 4);
        } catch (Exception e) {
            fail("Failed to set starting position: " + e.getMessage());
        }
        
        // Mock the card deck to prevent NullPointerException
        try {
            java.lang.reflect.Field deckField = controller.getModel().getFlightBoard().getClass().getDeclaredField("upcomingCardDeck");
            deckField.setAccessible(true);
            deckField.set(controller.getModel().getFlightBoard(), new CardDeck(new ArrayList<AdventureCardFilip>()));
        } catch (Exception e) {
            // If reflection fails, continue without deck setup
        }
        
        context = new Context(controller);
        context.getPlayers().add(player1);
        
        // Set credits and days using reflection
        try {
            java.lang.reflect.Field creditsField = Context.class.getDeclaredField("credits");
            creditsField.setAccessible(true);
            creditsField.set(context, 10);
            
            java.lang.reflect.Field daysField = Context.class.getDeclaredField("daysLost");
            daysField.setAccessible(true);
            daysField.set(context, 2);
        } catch (Exception e) {
            fail("Failed to set context fields: " + e.getMessage());
        }
        
        state = new PiratesRewardState(context);
    }



    @Test
    void testGetReward_InvalidRewardType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", RewardType.GOODS));
        
        assertEquals("Invalid reward type, expected CREDITS", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testGetReward_WrongPlayer() {
        controller.getModel().addPlayer("Player2");
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player2", RewardType.CREDITS));
        
        assertEquals("It's not the player's turn", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }



    @Test
    void testSkipReward_WrongPlayer() {
        controller.getModel().addPlayer("Player2");
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.skipReward("Player2"));
        
        assertEquals("It's not the player's turn", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testGetReward_WithSpecialPlayers() throws InvalidMethodParameters, InvalidParameters {
        context.addSpecialPlayer(controller.getModel().getPlayer("Player1"));
        
        state.getReward("Player1", RewardType.CREDITS);
        
        assertTrue(controller.getModel().getState() instanceof PiratesCannonShotsState);
    }

    @Test
    void testSkipReward_WithSpecialPlayers() throws InvalidParameters {
        context.addSpecialPlayer(controller.getModel().getPlayer("Player1"));
        
        state.skipReward("Player1");
        
        assertTrue(controller.getModel().getState() instanceof PiratesCannonShotsState);
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(2, commands.size());
        assertTrue(commands.contains("GetCreditsReward"));
        assertTrue(commands.contains("SkipReward"));
    }

    @Test
    void testGetReward_WithNoSpecialPlayers_TransitionsToFlightPhase() throws InvalidMethodParameters, InvalidParameters {
        state.getReward("Player1", RewardType.CREDITS);
        
        assertFalse(controller.getModel().getState() instanceof FlightPhase);
        assertFalse(controller.getModel().isError());
    }



    @Test
    void testGetReward_StateChangeConfirmed() throws InvalidMethodParameters, InvalidParameters {
        // Test with special players - should transition to PiratesCannonShotsState
        context.addSpecialPlayer(controller.getModel().getPlayer("Player1"));
        state.getReward("Player1", RewardType.CREDITS);
        assertTrue(controller.getModel().getState() instanceof PiratesCannonShotsState);
        
        // Reset and test without special players - should not transition to FlightPhase
        setUp();
        state.getReward("Player1", RewardType.CREDITS);
        assertFalse(controller.getModel().getState() instanceof FlightPhase);
    }

    @Test
    void testSkipReward_StateChangeConfirmed() throws InvalidParameters {
        // Test with special players - should transition to PiratesCannonShotsState
        context.addSpecialPlayer(controller.getModel().getPlayer("Player1"));
        state.skipReward("Player1");
        assertTrue(controller.getModel().getState() instanceof PiratesCannonShotsState);
        
        // Reset and test without special players - should not transition to FlightPhase
        setUp();
        state.skipReward("Player1");
        assertFalse(controller.getModel().getState() instanceof FlightPhase);
    }
}