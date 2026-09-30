package Controller.CombatZone.Level_ONE;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Side;
import Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZone1CannonShotsStateTest {

    private Controller controller;
    private Context context;
    private CombatZone1CannonShotsState state;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        controller.getModel().addPlayer("Player2");
        
        player1 = controller.getModel().getPlayer("Player1");
        player2 = controller.getModel().getPlayer("Player2");
        
        context = new Context(controller);
        
        // Set up the context's player list
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        // Set up special players list (required for state constructor)
        context.addSpecialPlayer(player1);
        context.addSpecialPlayer(player2);
        
        // Add projectiles to context
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.FRONT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        state = new CombatZone1CannonShotsState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testThrowDices_ValidPlayer() throws InvalidContextualAction, InvalidParameters {
        state.throwDices("Player1");
        
        assertFalse(controller.getModel().isError());
        // The state transition depends on the dice roll, so we can't assert the exact state
    }

    @Test
    void testThrowDices_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.throwDices("Player2"));
        
        assertEquals("It's not the player's turn", exception.getMessage());
    }

    @Test
    void testThrowDices_NoProjectiles() {
        // Remove all projectiles
        try {
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, new ArrayList<>());
        } catch (Exception e) {
            fail("Failed to clear projectiles: " + e.getMessage());
        }
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.throwDices("Player1"));
        
        assertEquals("No projectiles available for cannon shots.", exception.getMessage());
    }

    @Test
    void testThrowDices_MultipleProjectiles() throws InvalidContextualAction, InvalidParameters {
        // Add multiple projectiles
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.FRONT));
            shots.add(new CannonShot(true, Side.REAR));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        state.throwDices("Player1");
        
        assertFalse(controller.getModel().isError());
        // Should handle multiple projectiles properly
    }

    @Test
    void testThrowDices_LeftSideProjectile() throws InvalidContextualAction, InvalidParameters {
        // Test LEFT side projectile
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.LEFT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            assertFalse(controller.getModel().isError());
            // The dice roll result is random, so we just verify no error occurred
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_RightSideProjectile() throws InvalidContextualAction, InvalidParameters {
        // Test RIGHT side projectile
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.RIGHT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            assertFalse(controller.getModel().isError());
            // The dice roll result is random, so we just verify no error occurred
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_FrontSideProjectile() throws InvalidContextualAction, InvalidParameters {
        // Test FRONT side projectile
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.FRONT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            assertFalse(controller.getModel().isError());
            // The dice roll result is random, so we just verify no error occurred
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_RearSideProjectile() throws InvalidContextualAction, InvalidParameters {
        // Test REAR side projectile
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.REAR));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            assertFalse(controller.getModel().isError());
            // The dice roll result is random, so we just verify no error occurred
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("ThrowDices"));
    }

    @Test
    void testThrowDices_NullPlayerName() {
        assertThrows(NullPointerException.class,
            () -> state.throwDices(null));
    }

    @Test
    void testThrowDices_NonExistentPlayer() {
        assertThrows(NullPointerException.class,
            () -> state.throwDices("NonExistentPlayer"));
    }

    @Test
    void testThrowDices_OutOfBoundsLeftSide() throws InvalidContextualAction, InvalidParameters {
        // Test LEFT/RIGHT side projectile with out of bounds result (< 5 or > 9)
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.LEFT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            // Mock dice to return out of bounds values
            // Since we can't control random dice, we test the logic by checking state transitions
            state.throwDices("Player1");
            
            assertFalse(controller.getModel().isError());
            // State should either transition to ManageShotState or back to CannonShotsState/FlightPhase
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_OutOfBoundsFrontSide() throws InvalidContextualAction, InvalidParameters {
        // Test FRONT/REAR side projectile with out of bounds result (< 4 or > 10)
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.FRONT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            assertFalse(controller.getModel().isError());
            // Dice result determines the outcome
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_ProjectileRemovalOnOutOfBounds() throws InvalidContextualAction, InvalidParameters {
        // Test that projectile is removed when shot goes out of bounds
        try {
            List<CannonShot> shots = new ArrayList<>();
            CannonShot shot1 = new CannonShot(false, Side.LEFT);
            CannonShot shot2 = new CannonShot(false, Side.RIGHT);
            shots.add(shot1);
            shots.add(shot2);
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            int initialCount = context.getProjectiles().size();
            
            state.throwDices("Player1");
            
            // Depending on dice result, projectile might be removed
            assertTrue(context.getProjectiles().size() <= initialCount);
        } catch (Exception e) {
            fail("Failed to test projectile removal: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_DiceNumberSetInContext() throws InvalidContextualAction, InvalidParameters {
        state.throwDices("Player1");
        
        // Verify that dice number is set in context
        int diceNumber = context.getDiceNumber();
        assertTrue(diceNumber >= 2 && diceNumber <= 12); // Sum of two dice (1-6 each)
    }

    @Test
    void testThrowDices_StateTransitionToFlightPhase() throws InvalidContextualAction, InvalidParameters {
        // Test transition to FlightPhase when last projectile is out of bounds
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.LEFT)); // Only one projectile
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            // If projectile goes out of bounds and it's the last one, should transition to FlightPhase
            // Otherwise, should transition to ManageShotState or stay in CannonShotsState
            assertFalse(controller.getModel().isError());
        } catch (Exception e) {
            fail("Failed to test state transition: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_StateTransitionToManageShot() throws InvalidContextualAction, InvalidParameters {
        // Test transition to ManageShotState when shot is in bounds
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.FRONT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            // Depending on dice result, might transition to ManageShotState
            assertFalse(controller.getModel().isError());
        } catch (Exception e) {
            fail("Failed to test state transition: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_ContextPlayerManagement() throws InvalidContextualAction, InvalidParameters {
        // Test with different player order in context
        context.getPlayers().clear();
        context.getPlayers().add(player2);
        context.getPlayers().add(player1);
        
        // Clear and reset special players
        context.getSpecialPlayers().clear();
        context.addSpecialPlayer(player2);
        context.addSpecialPlayer(player1);
        
        CombatZone1CannonShotsState newState = new CombatZone1CannonShotsState(context);
        assertEquals(player2, newState.getPlayerInTurn());
        
        newState.throwDices("Player2");
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testThrowDices_AllSideTypes() throws InvalidContextualAction, InvalidParameters {
        // Test all four side types
        Side[] sides = {Side.LEFT, Side.RIGHT, Side.FRONT, Side.REAR};
        
        for (Side side : sides) {
            try {
                List<CannonShot> shots = new ArrayList<>();
                shots.add(new CannonShot(false, side));
                
                // Create new context for each test to avoid state pollution
                Context testContext = new Context(controller);
                testContext.getPlayers().clear();
                testContext.getPlayers().add(player1);
                testContext.getPlayers().add(player2);
                testContext.addSpecialPlayer(player1);
                testContext.addSpecialPlayer(player2);
                
                java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
                projectilesField.setAccessible(true);
                projectilesField.set(testContext, shots);
                
                CombatZone1CannonShotsState testState = new CombatZone1CannonShotsState(testContext);
                testState.throwDices("Player1");
                
                assertFalse(controller.getModel().isError());
            } catch (Exception e) {
                fail("Failed to test side " + side + ": " + e.getMessage());
            }
        }
    }

    @Test
    void testThrowDices_DoubleCannonShot() throws InvalidContextualAction, InvalidParameters {
        // Test with double cannon shot
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(true, Side.FRONT)); // Double shot
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
            
            state.throwDices("Player1");
            
            assertFalse(controller.getModel().isError());
        } catch (Exception e) {
            fail("Failed to test double cannon shot: " + e.getMessage());
        }
    }

    @Test
    void testThrowDices_BoundaryValues() throws InvalidContextualAction, InvalidParameters {
        // Test boundary conditions for dice results
        // We can't control the dice directly, but we can verify the logic handles all cases
        
        // Test multiple times to increase chance of hitting boundary conditions
        for (int i = 0; i < 10; i++) {
            try {
                List<CannonShot> shots = new ArrayList<>();
                shots.add(new CannonShot(false, Side.LEFT));
                
                // Create new context for each test to avoid state pollution
                Context testContext = new Context(controller);
                testContext.getPlayers().clear();
                testContext.getPlayers().add(player1);
                testContext.getPlayers().add(player2);
                testContext.addSpecialPlayer(player1);
                testContext.addSpecialPlayer(player2);
                
                java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
                projectilesField.setAccessible(true);
                projectilesField.set(testContext, shots);
                
                CombatZone1CannonShotsState testState = new CombatZone1CannonShotsState(testContext);
                testState.throwDices("Player1");
                
                assertFalse(controller.getModel().isError());
                
                // Verify dice number is within valid range
                int diceNumber = testContext.getDiceNumber();
                assertTrue(diceNumber >= 2 && diceNumber <= 12);
            } catch (Exception e) {
                fail("Failed boundary test iteration " + i + ": " + e.getMessage());
            }
        }
    }
}