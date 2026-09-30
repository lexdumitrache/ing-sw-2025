package Controller.CombatZone.Level_ONE;

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

class CombatZone1_E_BatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private CombatZone1_E_BatteryRemovalState state;
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
        } catch (Exception e) {
            fail("Failed to add battery: " + e.getMessage());
        }
        
        // Create state with declared power and batteries to remove
        double declaredPower = 2.0;
        int batteriesToRemove = 1;
        state = new CombatZone1_E_BatteryRemovalState(context, declaredPower, batteriesToRemove);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }
    
    @Test
    void testConstructorWithWorst() {
        CombatZone1_E_BatteryRemovalState stateWithWorst = new CombatZone1_E_BatteryRemovalState(context, 2.0, 1, 1.0);
        assertNotNull(stateWithWorst);
        assertEquals(player1, stateWithWorst.getPlayerInTurn());
    }

    @Test
    void testUseItem_ValidBatteryRemoval() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Add battery to condensed ship so validation passes
        player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
        
        state.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should create new state with batteries-1 (from 1 to 0)
        assertInstanceOf(CombatZone1EngineDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_ContinueBatteryRemoval() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Create state with more than 1 battery to remove
        CombatZone1_E_BatteryRemovalState multiState = new CombatZone1_E_BatteryRemovalState(context, 2.0, 2);
        
        // Use valid coordinates with battery component - should succeed and continue battery removal
        multiState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should create new state with batteries-1 (from 2 to 1)
        assertInstanceOf(CombatZone1_E_BatteryRemovalState.class, controller.getModel().getState());
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
        CombatZone1_E_BatteryRemovalState multiState = new CombatZone1_E_BatteryRemovalState(context, 2.0, 2);
        
        // This should succeed and continue battery removal
        multiState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(CombatZone1_E_BatteryRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_NegativeDeclaredPower() {
        CombatZone1_E_BatteryRemovalState negativeState = new CombatZone1_E_BatteryRemovalState(context, -1.0, 1);
        
        assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords));
    }

    @Test
    void testUseItem_NegativeBatteries() {
        CombatZone1_E_BatteryRemovalState negativeState = new CombatZone1_E_BatteryRemovalState(context, 2.0, -1);
        
        assertThrows(InvalidParameters.class,
            () -> negativeState.useItem("Player1", ItemType.BATTERIES, batteryCoords));
    }

    @Test
    void testUseItem_ZeroBatteries() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        CombatZone1_E_BatteryRemovalState zeroState = new CombatZone1_E_BatteryRemovalState(context, 2.0, 0);
        
        zeroState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to EngineDeclarationState when batteries is 0 and there are still players left
        assertInstanceOf(CombatZone1EngineDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testUseItem_WithSpecialPlayersAndBetterScore() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Add a special player with worse score
        context.addSpecialPlayer(player2);
        CombatZone1_E_BatteryRemovalState stateWithWorst = new CombatZone1_E_BatteryRemovalState(context, 1.0, 0, 3.0);
        
        stateWithWorst.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should replace the special player since declared power (1.0) < worst (3.0)
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertFalse(context.getSpecialPlayers().contains(player2));
    }

    @Test
    void testUseItem_WithSpecialPlayersAndWorseScore() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Add a special player with better score
        context.addSpecialPlayer(player2);
        CombatZone1_E_BatteryRemovalState stateWithWorst = new CombatZone1_E_BatteryRemovalState(context, 5.0, 0, 1.0);
        
        stateWithWorst.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should not replace the special player since declared power (5.0) > worst (1.0)
        assertTrue(context.getSpecialPlayers().contains(player2));
        assertFalse(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("UseBattery"));
    }

    @Test
    void testUseItem_TransitionToEngineDeclarationState() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Add another player to context so it doesn't become empty
        context.getPlayers().add(player2);
        CombatZone1_E_BatteryRemovalState stateWithWorst = new CombatZone1_E_BatteryRemovalState(context, 2.0, 0, 1.0);
        
        stateWithWorst.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to EngineDeclarationState when players list is not empty
        assertInstanceOf(CombatZone1EngineDeclarationState.class, controller.getModel().getState());
    }
}