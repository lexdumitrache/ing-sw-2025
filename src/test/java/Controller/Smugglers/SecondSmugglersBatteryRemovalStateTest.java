package Controller.Smugglers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecondSmugglersBatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private SecondSmugglersBatteryRemovalState state;
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
        context.getSpecialPlayers().add(player1);
        context.getSpecialPlayers().add(player2);
        
        batteryCoords = new Coordinates(6, 7);
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3);
        
        try {
            player1.getShipBoard().addComponent(battery, batteryCoords);
            player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
        } catch (Exception e) {
            fail("Failed to add battery: " + e.getMessage());
        }
        
        context.setRequiredGoods(2);
        state = new SecondSmugglersBatteryRemovalState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testUseItem_ValidBatteryRemoval() throws InvalidContextualAction, InvalidParameters {
        int initialBatteries = battery.getBatteries();
        
        state.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertEquals(initialBatteries - 1, battery.getBatteries());
        assertInstanceOf(SecondSmugglersBatteryRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_LastBatteryWithMorePlayers() throws InvalidContextualAction, InvalidParameters {
        context.setRequiredGoods(1);
        SecondSmugglersBatteryRemovalState singleBatteryState = new SecondSmugglersBatteryRemovalState(context);
        
        singleBatteryState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // When required goods reaches 0 and there are more special players, it should go to SmugglersGoodsRemovalState
        // But if no special players remain, it goes to FlightPhase
        assertTrue(controller.getModel().getState() instanceof SmugglersGoodsRemovalState ||
                   controller.getModel().getState() instanceof FlightPhase);
        assertFalse(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testUseItem_LastBatteryNoMorePlayers() throws InvalidContextualAction, InvalidParameters {
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        context.setRequiredGoods(1);
        SecondSmugglersBatteryRemovalState singleBatteryState = new SecondSmugglersBatteryRemovalState(context);
        
        singleBatteryState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_InvalidItemType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.CREW, batteryCoords));
        
        assertEquals("Invalid item type, expected BATTERIES", exception.getMessage());
    }

    @Test
    void testUseItem_NullCoordinates() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, null));
        
        assertEquals("Invalid coordinates", exception.getMessage());
    }

    @Test
    void testUseItem_NegativeAmount() throws InvalidContextualAction, InvalidParameters {
        context.setRequiredGoods(-1);
        SecondSmugglersBatteryRemovalState negativeState = new SecondSmugglersBatteryRemovalState(context);
        
        // The implementation doesn't validate negative amounts, it just processes the removal
        negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testUseItem_ZeroAmount() throws InvalidContextualAction, InvalidParameters {
        context.setRequiredGoods(0);
        SecondSmugglersBatteryRemovalState zeroState = new SecondSmugglersBatteryRemovalState(context);
        
        // When required goods is 0, the player is immediately removed and state transitions
        zeroState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testUseItem_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player2", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("It's not your turn", exception.getMessage());
    }

    @Test
    void testUseItem_InvalidBatteryCompartment() {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, invalidCoords));
        
        assertEquals("Not a valid battery compartment", exception.getMessage());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("UseBattery"));
    }
}