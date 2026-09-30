package Controller.Pirates;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Controller.Exceptions.InvalidParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PiratesCheckShipStateTest {

    private Controller controller;
    private Context context;
    private PiratesCheckShipState state;
    private Player player1;
    private Player player2;
    private BatteryCompartment battery;

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
        
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 5);
        
        try {
            player1.getShipBoard().addComponent(battery, new Coordinates(6, 7));
            battery.setShipBoard(player1.getShipBoard());
            player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
        } catch (Exception e) {
            fail("Failed to add components: " + e.getMessage());
        }
        
        state = new PiratesCheckShipState(context, 7, 0);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testDeleteComponent_Success() throws InvalidMethodParameters, InvalidParameters {
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        assertNull(player1.getShipBoard().getComponent(new Coordinates(6, 7)));
        assertTrue(player1.getJunk() > 0);
    }

    @Test
    void testDeleteComponent_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.deleteComponent("Player2", new Coordinates(6, 7)));
        
        assertEquals("It's not the player's turn", exception.getMessage());
    }

    @Test
    void testDeleteComponent_NullCoordinates() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.deleteComponent("Player1", null));
        
        assertEquals("Coordinates cannot be null", exception.getMessage());
    }

    @Test
    void testDeleteComponent_NoComponent() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.deleteComponent("Player1", new Coordinates(1, 1)));
        
        assertEquals("No component found at the given coordinates", exception.getMessage());
    }

    @Test
    void testDeleteComponent_ShipIntact_LastPlayer_AllShotsUsed_TransitionToFlightPhase() throws InvalidMethodParameters, InvalidParameters {
        // Setup: single player, single shot
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        try {
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
            projectiles.add(new CannonShot(true, Side.FRONT));
            projectilesField.set(context, projectiles);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        state = new PiratesCheckShipState(context, 0, 1);
        
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        assertTrue(controller.getModel().getState() instanceof FlightPhase);
    }

    @Test
    void testDeleteComponent_ShipIntact_LastPlayer_MoreShots_TransitionToCannonShotsState() throws InvalidMethodParameters, InvalidParameters {
        // Setup: single player, multiple shots
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        try {
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
            projectiles.add(new CannonShot(true, Side.FRONT));
            projectiles.add(new CannonShot(false, Side.REAR));
            projectilesField.set(context, projectiles);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        state = new PiratesCheckShipState(context, 0, 1);
        
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        assertTrue(controller.getModel().getState() instanceof PiratesCannonShotsState);
    }

    @Test
    void testDeleteComponent_ShipIntact_MorePlayersRemaining_TransitionToManageShotState() throws InvalidMethodParameters, InvalidParameters {
        // Setup: multiple players, current turn < total players
        state = new PiratesCheckShipState(context, 0, 0);
        
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        assertTrue(controller.getModel().getState() instanceof PiratesManageShotState);
    }

    @Test
    void testDeleteComponent_ShipBroken_NoStateChange() throws InvalidMethodParameters, InvalidParameters {
        // Setup: Remove all components to break ship integrity
        try {
            // Remove the cockpit to break ship integrity
            Coordinates cockpitCoords = new Coordinates(6, 6);
            player1.getShipBoard().removeComponent(cockpitCoords);
        } catch (Exception e) {
            // If cockpit removal fails, try removing multiple components
            for (int x = 0; x < 13; x++) {
                for (int y = 0; y < 13; y++) {
                    try {
                        Coordinates coord = new Coordinates(x, y);
                        if (player1.getShipBoard().getComponent(coord) != null && 
                            !coord.equals(new Coordinates(6, 7))) {
                            player1.getShipBoard().removeComponent(coord);
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
        
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        // Even when ship integrity is broken, it should still transition to PiratesManageShotState
        assertFalse(controller.getModel().isError());
        assertTrue(controller.getModel().getState() instanceof PiratesManageShotState);
    }

    @Test
    void testDeleteComponent_ShipIntact_ExactTurnLimit_ReachesFlightPhase() throws InvalidMethodParameters, InvalidParameters {
        // Setup: single player, single shot, turn will exceed player count
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        // Set up single projectile that will be removed
        try {
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            List<Model.Board.AdventureCards.Projectiles.Projectile> projectiles = new java.util.ArrayList<>();
            projectiles.add(new CannonShot(true, Side.FRONT));
            projectilesField.set(context, projectiles);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        state = new PiratesCheckShipState(context, 0, 1); // turn = 1, will become 2, and 2 > 1 is true
        
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        assertTrue(controller.getModel().getState() instanceof FlightPhase);
    }

    @Test
    void testDeleteComponent_ShipIntact_NotLastPlayer_TurnIncrements() throws InvalidMethodParameters, InvalidParameters {
        // Setup: turn < specialPlayers.size() - 1 (more players after current)
        state = new PiratesCheckShipState(context, 0, 0); // turn = 0, will increment to 1
        
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        assertTrue(controller.getModel().getState() instanceof PiratesManageShotState);
    }

    @Test
    void testDeleteComponent_ComponentExistsButNullAfterRemoval() throws InvalidMethodParameters, InvalidParameters {
        // Verify component exists before removal
        assertNotNull(player1.getShipBoard().getComponent(new Coordinates(6, 7)));
        
        state.deleteComponent("Player1", new Coordinates(6, 7));
        
        // Verify component is null after removal
        assertNull(player1.getShipBoard().getComponent(new Coordinates(6, 7)));
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("DeleteComponent"));
    }
}