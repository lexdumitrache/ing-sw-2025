package Controller.CombatZone.Level_TWO;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZone2_P_BatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private CombatZone2_P_BatteryRemovalState state;
    private Player player1;
    private Player player2;
    private Coordinates batteryCoords;
    private BatteryCompartment battery;

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
        
        // Set days lost in context
        try {
            java.lang.reflect.Field daysLostField = Context.class.getDeclaredField("daysLost");
            daysLostField.setAccessible(true);
            daysLostField.set(context, 2);
        } catch (Exception e) {
            fail("Failed to set daysLost: " + e.getMessage());
        }
        
        // Add battery to player1's ship
        batteryCoords = new Coordinates(6, 7);
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3);
        
        try {
            player1.getShipBoard().addComponent(battery, batteryCoords);
        } catch (Exception e) {
            fail("Failed to add battery: " + e.getMessage());
        }
        
        // Create state with declared power and batteries to remove
        double declaredPower = 2.0;
        int batteriesToRemove = 1;
        state = new CombatZone2_P_BatteryRemovalState(context, declaredPower, batteriesToRemove);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }
    
    @Test
    void testConstructorWithWorst() {
        CombatZone2_P_BatteryRemovalState stateWithWorst = new CombatZone2_P_BatteryRemovalState(context, 2.0, 1, 1.0);
        assertNotNull(stateWithWorst);
        assertEquals(player1, stateWithWorst.getPlayerInTurn());
    }

    @Test
    void testUseItem_ValidBatteryRemoval() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Set up battery properly in condensed ship
        battery.setShipBoard(player1.getShipBoard());
        battery.added();
        
        // This should work without exception
        state.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
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
    void testUseItem_NullCoordinates() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, null));
        
        assertEquals("Coordinates cannot be null", exception.getMessage());
    }

    @Test
    void testUseItem_InvalidComponent() {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, invalidCoords));
        
        assertEquals("Invalid component type, expected BatteryCompartment", exception.getMessage());
    }

    @Test
    void testUseItem_MultipleBatteries() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Create state with 2 batteries to remove
        CombatZone2_P_BatteryRemovalState multiState = new CombatZone2_P_BatteryRemovalState(context, 2.0, 2);
        
        // Set up battery properly in condensed ship
        battery.setShipBoard(player1.getShipBoard());
        battery.added();
        
        // This should work without exception
        multiState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testUseItem_NegativeDeclaredPower() {
        CombatZone2_P_BatteryRemovalState negativeState = new CombatZone2_P_BatteryRemovalState(context, -1.0, 1);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("Declared power cannot be negative", exception.getMessage());
    }

    @Test
    void testUseItem_NegativeBatteries() {
        CombatZone2_P_BatteryRemovalState negativeState = new CombatZone2_P_BatteryRemovalState(context, 2.0, -1);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("Invalid number of batteries", exception.getMessage());
    }

    @Test
    void testUseItem_ZeroBatteries() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        CombatZone2_P_BatteryRemovalState zeroState = new CombatZone2_P_BatteryRemovalState(context, 2.0, 0);
        
        zeroState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should handle zero batteries case
        assertTrue(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testEnd_ValidPlayer() {
        // The end method is not implemented in this state, so it throws InvalidCommand
        InvalidCommand exception = assertThrows(InvalidCommand.class,
            () -> state.end("Player1"));
        
        assertEquals("Invalid command: end", exception.getMessage());
    }

    @Test
    void testEnd_WrongPlayer() {
        // The end method is not implemented in this state, so it throws InvalidCommand
        InvalidCommand exception = assertThrows(InvalidCommand.class,
            () -> state.end("Player2"));
        
        assertEquals("Invalid command: end", exception.getMessage());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("UseBattery"));
    }
}