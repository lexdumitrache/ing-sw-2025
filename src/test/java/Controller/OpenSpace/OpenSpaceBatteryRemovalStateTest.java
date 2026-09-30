package Controller.OpenSpace;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
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

class OpenSpaceBatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private OpenSpaceBatteryRemovalState state;
    private Player player1;
    private Player player2;
    private Coordinates batteryCoords;
    private BatteryCompartment battery;

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
        
        // Add battery to player1's ship
        batteryCoords = new Coordinates(6, 7);
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3);
        
        try {
            player1.getShipBoard().addComponent(battery, batteryCoords);
            // Add the battery to the condensedShip's battery compartments list
            player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
            
            // Add players to the flight board
            controller.getModel().getFlightBoard().updatePosition(player1, 0);
            controller.getModel().getFlightBoard().updatePosition(player2, 0);
        } catch (Exception e) {
            fail("Failed to add battery: " + e.getMessage());
        }
        
        // Create state with declared power and batteries to remove
        double declaredPower = 2.0;
        int batteriesToRemove = 1;
        state = new OpenSpaceBatteryRemovalState(context, declaredPower, batteriesToRemove);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }



    @Test
    void testUseItem_InvalidItemType() {
        assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.CREW, batteryCoords));
    }

    @Test
    void testUseItem_WrongPlayer() {
        assertThrows(InvalidParameters.class,
            () -> state.useItem("Player2", ItemType.BATTERIES, batteryCoords));
    }

    @Test
    void testUseItem_NullCoordinates() {
        assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, null));
    }

    @Test
    void testUseItem_InvalidComponent() {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, invalidCoords));
    }

    @Test
    void testUseItem_MultipleBatteries() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Create state with 2 batteries to remove
        OpenSpaceBatteryRemovalState multiState = new OpenSpaceBatteryRemovalState(context, 2.0, 2);
        
        int initialBatteries = battery.getBatteries();
        
        multiState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertEquals(initialBatteries - 1, battery.getBatteries());
        // Should stay in battery removal state
        assertInstanceOf(OpenSpaceBatteryRemovalState.class, controller.getModel().getState());
    }



    @Test
    void testUseItem_NegativeDeclaredPower() {
        // Create state with negative declared power
        OpenSpaceBatteryRemovalState negativeState = new OpenSpaceBatteryRemovalState(context, -1.0, 1);
        
        assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords));
    }

    @Test
    void testUseItem_NegativeBatteries() {
        // Create state with negative batteries
        OpenSpaceBatteryRemovalState negativeState = new OpenSpaceBatteryRemovalState(context, 2.0, -1);
        
        assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords));
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("UseBattery"));
    }
}