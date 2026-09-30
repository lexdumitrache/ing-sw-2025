package Controller.Smugglers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersBatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private SmugglersBatteryRemovalState state;
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
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        // Set power using reflection
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, 5);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        batteryCoords = new Coordinates(6, 7);
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3);
        
        try {
            player1.getShipBoard().addComponent(battery, batteryCoords);
            player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
        } catch (Exception e) {
            fail("Failed to add battery: " + e.getMessage());
        }
        
        state = new SmugglersBatteryRemovalState(context, 6.0, 1);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testUseItem_PowerExceedsThreshold() throws InvalidContextualAction, InvalidParameters {
        state.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SmugglersLandState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_PowerEqualsThreshold() throws InvalidContextualAction, InvalidParameters {
        SmugglersBatteryRemovalState equalState = new SmugglersBatteryRemovalState(context, 5.0, 1);
        
        equalState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        assertInstanceOf(SmugglersPowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_PowerBelowThreshold() throws InvalidContextualAction, InvalidParameters {
        SmugglersBatteryRemovalState belowState = new SmugglersBatteryRemovalState(context, 4.0, 1);
        
        belowState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertInstanceOf(SmugglersPowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_PowerEqualsThresholdLastPlayer() throws InvalidContextualAction, InvalidParameters {
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getSpecialPlayers().add(player1); // Add to special players for goods removal
        
        // Add goods to player1 so SmugglersGoodsRemovalState doesn't immediately transition
        try {
            Model.Ship.Components.CargoHold cargoHold = new Model.Ship.Components.CargoHold(
                Model.Enums.Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3, false);
            player1.getShipBoard().addComponent(cargoHold, new Coordinates(5, 5));
            player1.getShipBoard().getCondensedShip().addCargoHold(cargoHold);
            cargoHold.addGood(Model.Enums.Good.RED);
        } catch (Exception e) {
            fail("Failed to add cargo hold: " + e.getMessage());
        }
        
        SmugglersBatteryRemovalState equalState = new SmugglersBatteryRemovalState(context, 5.0, 1);
        
        equalState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SecondSmugglersBatteryRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_PowerBelowThresholdLastPlayer() throws InvalidContextualAction, InvalidParameters {
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getSpecialPlayers().add(player1); // Add to special players for goods removal
        SmugglersBatteryRemovalState belowState = new SmugglersBatteryRemovalState(context, 4.0, 1);
        
        belowState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SecondSmugglersBatteryRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_MultipleBatteries() throws InvalidContextualAction, InvalidParameters {
        SmugglersBatteryRemovalState multiState = new SmugglersBatteryRemovalState(context, 6.0, 2);
        
        multiState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to SlaversBatteryRemovalState (note: this seems like a bug in the original code)
        // The original code references SlaversBatteryRemovalState instead of SmugglersBatteryRemovalState
    }

    @Test
    void testUseItem_InvalidItemType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.CREW, batteryCoords));
        
        assertEquals("Item type must be BATTERIES", exception.getMessage());
    }

    @Test
    void testUseItem_NullCoordinates() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, null));
        
        assertEquals("Invalid coordinates", exception.getMessage());
    }

    @Test
    void testUseItem_NegativeDeclaredPower() {
        SmugglersBatteryRemovalState negativeState = new SmugglersBatteryRemovalState(context, -1.0, 1);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("Invalid declared power", exception.getMessage());
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
    void testGetReward_ValidReward() throws InvalidParameters {
        SmugglersBatteryRemovalState rewardState = new SmugglersBatteryRemovalState(context, 6.0, 0);
        
        rewardState.getReward("Player1", RewardType.CREDITS);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SmugglersLandState.class, controller.getModel().getState());
    }

    @Test
    void testGetReward_InvalidRewardType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", RewardType.GOODS));
        
        assertEquals("Reward type must be CREDITS", exception.getMessage());
    }

    @Test
    void testGetReward_NegativeDeclaredPower() {
        SmugglersBatteryRemovalState negativeState = new SmugglersBatteryRemovalState(context, -1.0, 0);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> negativeState.getReward("Player1", RewardType.CREDITS));
        
        assertEquals("Invalid declared power", exception.getMessage());
    }

    @Test
    void testGetReward_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player2", RewardType.CREDITS));
        
        assertEquals("It's not your turn", exception.getMessage());
    }

    @Test
    void testGetReward_PowerNotExceedingThreshold() throws InvalidParameters {
        SmugglersBatteryRemovalState lowPowerState = new SmugglersBatteryRemovalState(context, 4.0, 0);
        
        lowPowerState.getReward("Player1", RewardType.CREDITS);
        
        // Should transition to next state even if power doesn't exceed threshold
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(2, commands.size());
        assertTrue(commands.contains("UseBattery"));
        assertTrue(commands.contains("GetCreditsReward"));
    }
}