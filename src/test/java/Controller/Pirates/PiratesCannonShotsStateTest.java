package Controller.Pirates;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Side;
import Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PiratesCannonShotsStateTest {

    private Controller controller;
    private Context context;
    private PiratesCannonShotsState state;
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
        context.addSpecialPlayer(player1);
        context.addSpecialPlayer(player2);
        
        // Add projectiles
        try {
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
            projectiles.add(new CannonShot(true, Side.FRONT));
            projectilesField.set(context, projectiles);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        state = new PiratesCannonShotsState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testThrowDices_Success() throws InvalidContextualAction, InvalidParameters {
        state.throwDices("Player1");
        
        assertFalse(controller.getModel().isError());
        // State should transition to either PiratesManageShotState or PiratesCannonShotsState
        assertTrue(controller.getModel().getState() instanceof PiratesManageShotState ||
                  controller.getModel().getState() instanceof PiratesCannonShotsState ||
                  controller.getModel().getState() instanceof FlightPhase);
    }



    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("ThrowDices"));
    }

    @Test
    void testThrowDices_LeftRightSide_ProjectileOutOfGrid() throws InvalidContextualAction, InvalidParameters {
        // Setup LEFT side projectile
        try {
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
            projectiles.add(new CannonShot(true, Side.LEFT));
            projectilesField.set(context, projectiles);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        // Test multiple times to eventually hit out-of-grid condition
        boolean outOfGridTested = false;
        for (int i = 0; i < 100 && !outOfGridTested; i++) {
            context = new Context(controller);
            context.addSpecialPlayer(player1);
            try {
                java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
                projectilesField.setAccessible(true);
                List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
                projectiles.add(new CannonShot(true, Side.LEFT));
                projectilesField.set(context, projectiles);
            } catch (Exception e) {
                fail("Failed to set projectiles: " + e.getMessage());
            }
            state = new PiratesCannonShotsState(context);
            
            state.throwDices("Player1");
            
            if (context.getProjectiles().isEmpty()) {
                outOfGridTested = true;
                assertFalse(controller.getModel().isError());
            }
        }
        assertTrue(outOfGridTested, "Out of grid condition should eventually occur");
    }

    @Test
    void testThrowDices_FrontRearSide_ProjectileOutOfGrid() throws InvalidContextualAction, InvalidParameters {
        // Test multiple times to eventually hit out-of-grid condition
        boolean outOfGridTested = false;
        for (int i = 0; i < 100 && !outOfGridTested; i++) {
            context = new Context(controller);
            context.addSpecialPlayer(player1);
            try {
                java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
                projectilesField.setAccessible(true);
                List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
                projectiles.add(new CannonShot(true, Side.REAR));
                projectilesField.set(context, projectiles);
            } catch (Exception e) {
                fail("Failed to set projectiles: " + e.getMessage());
            }
            state = new PiratesCannonShotsState(context);
            
            state.throwDices("Player1");
            
            if (context.getProjectiles().isEmpty()) {
                outOfGridTested = true;
                assertFalse(controller.getModel().isError());
            }
        }
        assertTrue(outOfGridTested, "Out of grid condition should eventually occur");
    }

    @Test
    void testThrowDices_LastProjectileRemoved_TransitionsToFlightPhase() throws InvalidContextualAction, InvalidParameters {
        // Test multiple times to eventually hit transition to FlightPhase
        boolean flightPhaseTested = false;
        for (int i = 0; i < 100 && !flightPhaseTested; i++) {
            context = new Context(controller);
            context.addSpecialPlayer(player1);
            try {
                java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
                projectilesField.setAccessible(true);
                List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
                projectiles.add(new CannonShot(true, Side.LEFT));
                projectilesField.set(context, projectiles);
            } catch (Exception e) {
                fail("Failed to set projectiles: " + e.getMessage());
            }
            state = new PiratesCannonShotsState(context);
            
            state.throwDices("Player1");
            
            if (controller.getModel().getState() instanceof FlightPhase) {
                flightPhaseTested = true;
                assertFalse(controller.getModel().isError());
            }
        }
        assertTrue(flightPhaseTested, "FlightPhase transition should eventually occur");
    }

    @Test
    void testThrowDices_ProjectileInGrid_TransitionsToManageShot() throws InvalidContextualAction, InvalidParameters {
        // Test multiple times to eventually hit transition to ManageShotState
        boolean manageShotTested = false;
        for (int i = 0; i < 100 && !manageShotTested; i++) {
            context = new Context(controller);
            context.addSpecialPlayer(player1);
            try {
                java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
                projectilesField.setAccessible(true);
                List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
                projectiles.add(new CannonShot(true, Side.FRONT));
                projectilesField.set(context, projectiles);
            } catch (Exception e) {
                fail("Failed to set projectiles: " + e.getMessage());
            }
            state = new PiratesCannonShotsState(context);
            
            state.throwDices("Player1");
            
            if (controller.getModel().getState() instanceof PiratesManageShotState) {
                manageShotTested = true;
                assertFalse(controller.getModel().isError());
            }
        }
        assertTrue(manageShotTested, "ManageShotState transition should eventually occur");
    }
}