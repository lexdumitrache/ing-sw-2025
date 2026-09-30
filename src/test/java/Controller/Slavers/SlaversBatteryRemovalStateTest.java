package Controller.Slavers;

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

class SlaversBatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private SlaversBatteryRemovalState state;
    private Player player1;
    private BatteryCompartment battery;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        
        player1 = controller.getModel().getPlayer("Player1");
        
        context = new Context(controller);
        context.getPlayers().add(player1);
        
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, 5);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 5);
        
        try {
            player1.getShipBoard().addComponent(battery, new Coordinates(6, 7));
            player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
        } catch (Exception e) {
            fail("Failed to add components: " + e.getMessage());
        }
        
        state = new SlaversBatteryRemovalState(context, 6.0, 1);
    }

    @Test
    void testUseItem_Success() throws InvalidParameters, InvalidContextualAction {
        state.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SlaversRewardsState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_InvalidItemType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.CREW, new Coordinates(6, 7)));
        
        assertEquals("Item type must be BATTERIES", exception.getMessage());
    }

    @Test
    void testUseItem_NegativeDeclaredPower() {
        SlaversBatteryRemovalState negativeState = new SlaversBatteryRemovalState(context, -1.0, 1);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7)));
        
        assertEquals("Declared power cannot be negative", exception.getMessage());
    }

    @Test
    void testUseItem_NullCoordinates() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, null));
        
        assertEquals("Coordinates are null", exception.getMessage());
    }

    @Test
    void testUseItem_WrongPlayer() {
        controller.getModel().addPlayer("Player2");
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player2", ItemType.BATTERIES, new Coordinates(6, 7)));
        
        assertEquals("It's not your turn", exception.getMessage());
    }

    @Test
    void testGetReward_Success() throws InvalidParameters {
        SlaversBatteryRemovalState rewardState = new SlaversBatteryRemovalState(context, 6.0, 0);
        
        rewardState.getReward("Player1", RewardType.CREDITS);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SlaversRewardsState.class, controller.getModel().getState());
    }
    
    @Test
    void testGetReward_NotEnoughPower() throws InvalidParameters {
        // Test when power is equal to context power but batteries are not 0
        SlaversBatteryRemovalState rewardState = new SlaversBatteryRemovalState(context, 5.0, 1);
        
        rewardState.getReward("Player1", RewardType.CREDITS);
        
        // Should not change state since conditions aren't met
        // The original state is still the controller's state
        assertFalse(controller.getModel().getState() instanceof SlaversRewardsState);
    }
    
    @Test
    void testUseItem_NoBatteriesToRemove() {
        SlaversBatteryRemovalState noBatteriesState = new SlaversBatteryRemovalState(context, 6.0, 0);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> noBatteriesState.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7)));
        
        assertEquals("No batteries to remove", exception.getMessage());
    }
    
    @Test
    void testUseItem_NotBatteryComponent() throws InvalidParameters, InvalidContextualAction {
        // Use a different coordinate that doesn't have a battery component
        Coordinates coords = new Coordinates(8, 8);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, coords));
        
        assertEquals("Component is not a battery compartment", exception.getMessage());
    }
    
    @Test
    void testUseItem_EqualPower() throws InvalidParameters, InvalidContextualAction {
        // Test when declared power equals context power
        SlaversBatteryRemovalState equalPowerState = new SlaversBatteryRemovalState(context, 5.0, 1);
        
        // Add a player to special players to avoid NoSuchElementException
        context.addSpecialPlayer(player1);
        
        equalPowerState.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        // Player should be removed from context players
        assertFalse(context.getPlayers().contains(player1));
        
        // If players list is empty, should transition to SlaversCrewRemovalState
        assertTrue(controller.getModel().getState() instanceof SlaversCrewRemovalState);
    }
    
    @Test
    void testUseItem_LessPower() throws InvalidParameters, InvalidContextualAction {
        // Test when declared power is less than context power
        SlaversBatteryRemovalState lessPowerState = new SlaversBatteryRemovalState(context, 4.0, 1);
        
        lessPowerState.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        // Player should be removed from context players and added to special players
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getSpecialPlayers().contains(player1));
        
        // If players list is empty, should transition to SlaversCrewRemovalState
        assertTrue(controller.getModel().getState() instanceof SlaversCrewRemovalState);
    }
    
    @Test
    void testUseItem_MoreBatteriesToRemove() throws InvalidParameters, InvalidContextualAction {
        // Test when there are still more batteries to remove after this one
        SlaversBatteryRemovalState moreBatteriesState = new SlaversBatteryRemovalState(context, 6.0, 2);
        
        moreBatteriesState.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        // Should create a new state with one less battery to remove
        assertTrue(controller.getModel().getState() instanceof SlaversBatteryRemovalState);
        SlaversBatteryRemovalState newState = (SlaversBatteryRemovalState) controller.getModel().getState();
        
        // Test the new state by using another battery
        newState.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7));
        assertInstanceOf(SlaversRewardsState.class, controller.getModel().getState());
    }
    
    @Test
    void testUseItem_PlayersRemaining() throws InvalidParameters, InvalidContextualAction {
        // Add a second player to test the branch where players remain after removing one
        controller.getModel().addPlayer("Player2");
        Player player2 = controller.getModel().getPlayer("Player2");
        context.getPlayers().add(player2);
        
        // Test when declared power equals context power
        SlaversBatteryRemovalState equalPowerState = new SlaversBatteryRemovalState(context, 5.0, 1);
        
        equalPowerState.useItem("Player1", ItemType.BATTERIES, new Coordinates(6, 7));
        
        assertFalse(controller.getModel().isError());
        // Player1 should be removed, but Player2 remains
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getPlayers().contains(player2));
        
        // Should transition to SlaversPowerDeclarationState for the next player
        assertTrue(controller.getModel().getState() instanceof SlaversPowerDeclarationState);
    }

    @Test
    void testGetReward_InvalidRewardType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getReward("Player1", RewardType.GOODS));
        
        assertEquals("Reward type must be CREDITS", exception.getMessage());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(2, commands.size());
        assertTrue(commands.contains("UseBattery"));
        assertTrue(commands.contains("GetCreditsReward"));
    }
}