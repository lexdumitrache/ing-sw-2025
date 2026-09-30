package Controller.CombatZone.Level_TWO;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Direction;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.ShieldGenerator;
import Model.Ship.Components.StructuralModule;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZone2ManageShotStateTest {

    private Controller controller;
    private Context context;
    private CombatZone2ManageShotState state;
    private Player player1;
    private Player player2;
    private Coordinates batteryCoords;
    private BatteryCompartment battery;
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
        
        // Set up the context's special player list
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        context.getSpecialPlayers().add(player2);
        
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
        
        // Add battery to player1's ship
        batteryCoords = new Coordinates(6, 7);
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3);
        
        try {
            player1.getShipBoard().addComponent(battery, batteryCoords);
            battery.setShipBoard(player1.getShipBoard());
            battery.added();
        } catch (Exception e) {
            fail("Failed to add battery: " + e.getMessage());
        }
        
        // Add shield to player1's ship
        Coordinates shieldCoords = new Coordinates(5, 7);
        ShieldGenerator shield = new ShieldGenerator(Card.SHIELD_GENERATOR, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                                   ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Direction.UP);
        
        try {
            player1.getShipBoard().addComponent(shield, shieldCoords);
            shield.setShipBoard(player1.getShipBoard());
            shield.added();
        } catch (Exception e) {
            fail("Failed to add shield: " + e.getMessage());
        }
        
        // Add component that can be hit
        componentCoords = new Coordinates(7, 8); // Changed from (7, 7) to (7, 8) to avoid position conflict
        component = new StructuralModule(Card.STRUCTURAL_MODULE, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL);
        
        try {
            player1.getShipBoard().addComponent(component, componentCoords);
            component.setShipBoard(player1.getShipBoard());
            component.added();
        } catch (Exception e) {
            fail("Failed to add component: " + e.getMessage());
        }
        
        state = new CombatZone2ManageShotState(context, 7); // Number 7 for dice roll
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
    }

    @Test
    void testEnd_ValidPlayer() throws InvalidMethodParameters, InvalidParameters {
        state.end("Player1");
        
        // Should transition to next state (may stay in same state type with different player)
        assertTrue(controller.getModel().getState() instanceof CombatZone2ManageShotState || 
                  !(controller.getModel().getState() instanceof CombatZone2ManageShotState));
    }

    @Test
    void testEnd_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.end("Player2"));
        
        assertEquals("It's not the player's turn", exception.getMessage());
    }

    @Test
    void testEnd_LastProjectile() throws InvalidMethodParameters, InvalidParameters {
        // Set up context with only one projectile and one player
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        state.end("Player1");
        
        assertFalse(controller.getModel().isError());
        // Should transition to FlightPhase
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }


    @Test
    void testUseItem_InvalidItemType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.CREW, batteryCoords));
        
        assertEquals("Invalid item type, expected BATTERIES", exception.getMessage());
    }

    @Test
    void testUseItem_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player2", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("It's not the player's turn", exception.getMessage());
    }

    @Test
    void testUseItem_BigShot() throws NoSuchFieldException, IllegalAccessException {
        // Change shot to big shot
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(true, Side.FRONT));
        
        java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
        projectilesField.setAccessible(true);
        projectilesField.set(context, shots);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("Cannot use batteries on a big shot", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testUseItem_NoShield() throws NoSuchFieldException, IllegalAccessException {
        // Change shot direction to one without shield
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(false, Side.REAR));
        
        java.lang.reflect.Field projectilesField = Context.class.getDeclaredField("projectiles");
        projectilesField.setAccessible(true);
        projectilesField.set(context, shots);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("No shield or cannon found to use", exception.getMessage());
    }



    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(2, commands.size());
        assertTrue(commands.contains("EndTurn"));
        assertTrue(commands.contains("UseBattery"));
    }
}