package Controller.CombatZone.Level_TWO;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Player;
import Model.Ship.Components.StructuralModule;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZone2CheckShipStateTest {

    private Controller controller;
    private Context context;
    private CombatZone2CheckShipState state;
    private Player player1;
    private Player player2;
    private Coordinates componentCoords;
    private StructuralModule component;

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
        
        // Add component to player1's ship
        componentCoords = new Coordinates(6, 7);
        component = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL);
        
        try {
            player1.getShipBoard().addComponent(component, componentCoords);
        } catch (Exception e) {
            fail("Failed to add component: " + e.getMessage());
        }
        
        state = new CombatZone2CheckShipState(context);
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
        assertFalse(controller.getModel().getState() instanceof CombatZone2CheckShipState);
    }







    @Test
    void testDeleteComponent_LastProjectile() throws InvalidMethodParameters, InvalidParameters {
        // Set up context with only one projectile and one player
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        
        try {
            List<CannonShot> shots = new ArrayList<>();
            shots.add(new CannonShot(false, Side.FRONT));
            
            java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
            projectilesField.setAccessible(true);
            projectilesField.set(context, shots);
        } catch (Exception e) {
            fail("Failed to set projectiles: " + e.getMessage());
        }
        
        state.deleteComponent("Player1", componentCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to FlightPhase
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }


}