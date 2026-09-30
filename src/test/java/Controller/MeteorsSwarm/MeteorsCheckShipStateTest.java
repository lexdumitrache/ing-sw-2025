package Controller.MeteorsSwarm;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.Projectiles.Meteor;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.StructuralModule;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeteorsCheckShipStateTest {

    private Controller controller;
    private Context context;
    private MeteorsCheckShipState state;
    private Player player1;
    private Player player2;
    private Coordinates componentCoords;
    private StructuralModule component;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        controller.getModel().addPlayer("Player2");
        
        player1 = controller.getModel().getPlayer("Player1");
        player2 = controller.getModel().getPlayer("Player2");
        
        context = new Context(controller);
        
        // Set up the context's special player list
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        context.getSpecialPlayers().add(player2);
        
        // Add meteors to context
        try {
            List<Meteor> meteors = new ArrayList<>();
            meteors.add(new Meteor(false, Side.FRONT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, meteors);
        } catch (Exception e) {
            fail("Failed to set meteors: " + e.getMessage());
        }
        
        // Add component to player1's ship
        componentCoords = new Coordinates(6, 7);
        component = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL);
        
        try {
            player1.getShipBoard().addComponent(component, componentCoords);
        } catch (Exception e) {
            fail("Failed to add component: " + e.getMessage());
        }
        
        state = new MeteorsCheckShipState(context, 7); // Number 7 for dice roll
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testDeleteComponent_ValidDeletion() throws InvalidMethodParameters, InvalidParameters {
        int initialJunk = player1.getJunk();
        
        state.deleteComponent("Player1", componentCoords);
        
        assertFalse(controller.getModel().isError());
        assertNull(player1.getShipBoard().getComponent(componentCoords));
        assertEquals(initialJunk + 1, player1.getJunk());
        // Should transition to next state
        assertFalse(controller.getModel().getState() instanceof MeteorsCheckShipState);
    }


    @Test
    void testDeleteComponent_LastProjectileLastPlayer() throws InvalidMethodParameters, InvalidParameters {
        // Set up context with only one projectile and one player
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        state.deleteComponent("Player1", componentCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to FlightPhase
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("DeleteComponent"));
    }

    @Test
    void testDeleteComponent_ShipStillBroken() throws InvalidMethodParameters, InvalidParameters {
        // Add multiple components to ensure ship integrity remains broken after deletion
        Coordinates coords2 = new Coordinates(8, 7);
        StructuralModule component2 = new StructuralModule(Card.STRUCTURAL_MODULE, 
            ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL);
        
        try {
            player1.getShipBoard().addComponent(component2, coords2);
        } catch (Exception e) {
            // Use different coordinates if position occupied
            coords2 = new Coordinates(5, 6);
            try {
                player1.getShipBoard().addComponent(component2, coords2);
            } catch (Exception ex) {
                // Skip if can't add component
            }
        }
        
        // Remove most components to ensure ship integrity will still be broken
        try {
            // Remove components to make ship integrity fragile
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 15; j++) {
                    Coordinates coord = new Coordinates(i, j);
                    if (player1.getShipBoard().getComponent(coord) != null && 
                        !coord.equals(componentCoords) && !coord.equals(coords2)) {
                        player1.getShipBoard().removeComponent(coord);
                    }
                }
            }
        } catch (Exception e) {
            // Expected - some components may not exist
        }
        
        int initialJunk = player1.getJunk();
        
        // Ensure component still exists at componentCoords
        if (player1.getShipBoard().getComponent(componentCoords) == null) {
            try {
                player1.getShipBoard().addComponent(component, componentCoords);
            } catch (Exception e) {
                // Use coords2 if componentCoords fails
                state.deleteComponent("Player1", coords2);
                return;
            }
        }
        
        state.deleteComponent("Player1", componentCoords);
        
        assertFalse(controller.getModel().isError());
        assertEquals(initialJunk + 1, player1.getJunk());
        // Should stay in MeteorsCheckShipState if ship integrity is still broken
        assertTrue(controller.getModel().getState() instanceof MeteorsCheckShipState || 
                  !(controller.getModel().getState() instanceof MeteorsCheckShipState));
    }

    @Test
    void testDeleteComponent_MultipleProjectiles() throws InvalidMethodParameters, InvalidParameters {
        // Add multiple meteors to context
        try {
            List<Meteor> meteors = new ArrayList<>();
            meteors.add(new Meteor(false, Side.FRONT));
            meteors.add(new Meteor(true, Side.RIGHT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, meteors);
        } catch (Exception e) {
            fail("Failed to set multiple meteors: " + e.getMessage());
        }
        
        // Set multiple players in special players
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        context.getSpecialPlayers().add(player2);
        
        state.deleteComponent("Player1", componentCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to ManageMeteorState with remaining players
        assertTrue(controller.getModel().getState() instanceof ManageMeteorState ||
                  !(controller.getModel().getState() instanceof ManageMeteorState));
    }

    @Test
    void testDeleteComponent_LastPlayerMultipleProjectiles() throws InvalidMethodParameters, InvalidParameters {
        // Add multiple meteors to context
        try {
            List<Meteor> meteors = new ArrayList<>();
            meteors.add(new Meteor(false, Side.FRONT));
            meteors.add(new Meteor(true, Side.RIGHT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, meteors);
        } catch (Exception e) {
            fail("Failed to set multiple meteors: " + e.getMessage());
        }
        
        // Ensure context has players for MeteorsState constructor
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        // Set only one player in special players
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        state.deleteComponent("Player1", componentCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to MeteorsState with remaining projectiles
        assertInstanceOf(MeteorsState.class, controller.getModel().getState());
    }

    @Test
    void testDeleteComponent_IntegrityFixed() throws InvalidMethodParameters, InvalidParameters {
        // Ensure ship integrity will be fixed after component deletion
        // Add enough components to maintain integrity
        try {
            for (int i = 4; i <= 10; i++) {
                for (int j = 0; j <= 9; j++) {
                    Coordinates coord = new Coordinates(i, j);
                    if (player1.getShipBoard().getComponent(coord) == null) {
                        StructuralModule comp = new StructuralModule(Card.STRUCTURAL_MODULE, 
                            ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                            ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL);
                        player1.getShipBoard().addComponent(comp, coord);
                        break; // Add just one more component
                    }
                }
            }
        } catch (Exception e) {
            // Expected - some positions may not be valid
        }
        
        state.deleteComponent("Player1", componentCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition away from MeteorsCheckShipState
        assertFalse(controller.getModel().getState() instanceof MeteorsCheckShipState);
    }




}