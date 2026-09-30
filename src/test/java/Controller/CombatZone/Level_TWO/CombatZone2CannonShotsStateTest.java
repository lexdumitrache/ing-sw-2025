package Controller.CombatZone.Level_TWO;

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

class CombatZone2CannonShotsStateTest {

    private Controller controller;
    private Context context;
    private CombatZone2CannonShotsState state;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.LEVEL2, 1);
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
        
        state = new CombatZone2CannonShotsState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testThrowDices_ValidPlayer() throws InvalidContextualAction, InvalidParameters {
        // Test the method
        state.throwDices("Player1");
        
        assertFalse(controller.getModel().isError());
        // The state transition depends on the dice roll, so we can't assert the exact state
    }

    @Test
    void testThrowDices_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.throwDices("Player2"));
        
        assertEquals("It's not the player's turn", exception.getMessage());
        assertFalse(controller.getModel().isError());
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
        assertFalse(controller.getModel().isError());
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
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("ThrowDices"));
    }
}