package Controller.CombatZone.Level_TWO;

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

class CombatZone2_E_BatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private CombatZone2_E_BatteryRemovalState state;
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
        state = new CombatZone2_E_BatteryRemovalState(context, declaredPower, batteriesToRemove);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }
    
    @Test
    void testConstructorWithWorst() {
        CombatZone2_E_BatteryRemovalState stateWithWorst = new CombatZone2_E_BatteryRemovalState(context, 2.0, 1, 1.0);
        assertNotNull(stateWithWorst);
        assertEquals(player1, stateWithWorst.getPlayerInTurn());
    }

    @Test
    void testUseItem_ValidBatteryRemoval() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        state.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should transition to next state after valid battery removal
        assertInstanceOf(CombatZone2EngineDeclarationState.class, controller.getModel().getState());
    }






    @Test
    void testUseItem_MultipleBatteries() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // Create state with 2 batteries to remove
        CombatZone2_E_BatteryRemovalState multiState = new CombatZone2_E_BatteryRemovalState(context, 2.0, 2);
        
        multiState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should continue in battery removal state since more batteries need to be removed
        assertInstanceOf(CombatZone2_E_BatteryRemovalState.class, controller.getModel().getState());
    }



    @Test
    void testUseItem_ZeroBatteries() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        CombatZone2_E_BatteryRemovalState zeroState = new CombatZone2_E_BatteryRemovalState(context, 2.0, 0);
        
        zeroState.useItem("Player1", ItemType.BATTERIES, batteryCoords);
        
        assertFalse(controller.getModel().isError());
        // Should handle zero batteries case
        assertTrue(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("UseBattery"));
    }
}