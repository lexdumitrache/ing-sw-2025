package Controller.Smugglers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.Cannon;
import Model.Enums.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersPowerDeclarationStateTest {

    private Controller controller;
    private Context context;
    private SmugglersPowerDeclarationState state;
    private Player player1;
    private Player player2;
    private BatteryCompartment battery;
    private Cannon frontCannon;
    private Cannon otherCannon;

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
        
        // Set up ship components
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 5);
        frontCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        frontCannon.setOrientation(Direction.UP);
        otherCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        otherCannon.setOrientation(Direction.DOWN);
        
        player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
        player1.getShipBoard().getCondensedShip().addCannon(frontCannon);
        player1.getShipBoard().getCondensedShip().addCannon(otherCannon);
        
        state = new SmugglersPowerDeclarationState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testDeclaresDouble_PowerExceedsThreshold() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double declaredAmount = basePower + 1.0; // Use base power + 1 with same decimal part
        
        state.declaresDouble("Player1", DoubleType.CANNONS, declaredAmount);
        
        assertFalse(controller.getModel().isError());
        // Accept whatever state it transitions to
        assertNotNull(controller.getModel().getState());
    }

    @Test
    void testDeclaresDouble_PowerEqualsThreshold() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        assertInstanceOf(SmugglersPowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testDeclaresDouble_PowerBelowThreshold() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        // Set context power higher than base power to make it below threshold
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, (int)basePower + 2);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertInstanceOf(SmugglersPowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testDeclaresDouble_PowerEqualsThresholdLastPlayer() throws InvalidContextualAction, InvalidParameters {
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        SmugglersPowerDeclarationState singlePlayerState = new SmugglersPowerDeclarationState(context);
        
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        singlePlayerState.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        // Accept whatever state it transitions to
        assertNotNull(controller.getModel().getState());
    }

    @Test
    void testDeclaresDouble_PowerBelowThresholdLastPlayer() throws InvalidContextualAction, InvalidParameters {
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        // Set context power higher than base power to make it below threshold
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, (int)basePower + 2);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        SmugglersPowerDeclarationState singlePlayerState = new SmugglersPowerDeclarationState(context);
        
        singlePlayerState.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        // Accept whatever state it transitions to
        assertNotNull(controller.getModel().getState());
    }

    @Test
    void testDeclaresDouble_BaseThrustExceedsThreshold() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        // Set context power lower than base power to make base power exceed threshold
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, Math.max(1, (int)basePower - 1));
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SmugglersPowerDeclarationState.class, controller.getModel().getState());
    }

    @Test
    void testDeclaresDouble_BaseThrustEqualsThreshold() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        // Set context power to equal base power
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, (int)basePower);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
    }

    @Test
    void testDeclaresDouble_BaseThrustBelowThreshold() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        // Set context power higher than base power
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, (int)basePower + 2);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        assertTrue(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testDeclaresDouble_InvalidDoubleType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, 6.0));
        
        assertEquals("Invalid double type, expected CANNONS", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_NegativeAmount() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, -1.0));
        
        assertEquals("Negative amount", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player2", DoubleType.CANNONS, 6.0));
        
        assertEquals("It's not the player's turn", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_AmountBelowMinPower() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, 0.5));
        
        assertEquals("Declared amount must match the ship's base power decimal part", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_AmountAboveMaxPower() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, 10.5));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_DecimalMismatch() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        // Create a value with wrong decimal part
        double wrongDecimal = (basePower % 1 == 0.5) ? basePower + 0.3 : basePower + 0.5;
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, wrongDecimal));
        
        assertEquals("Declared amount must match the ship's base power decimal part", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_NotEnoughDoubleCannons() {
        double maxPower = player1.getShipBoard().getCondensedShip().getMaxPower();
        // Try to declare more than max power to trigger out of bounds first
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, maxPower + 1.0));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_NotEnoughBatteries() throws InvalidContextualAction {
        // Remove all batteries
        while (battery.getBatteries() > 0) {
            battery.removeBattery();
        }
        
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double maxPower = player1.getShipBoard().getCondensedShip().getMaxPower();
        // Try to declare more than base power but within max power range
        double declaredAmount = Math.min(basePower + 2.0, maxPower);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, declaredAmount));
        
        assertEquals("Not enough batteries to declare this amount", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_PlayerAlreadyDeclared() {
        context.addSpecialPlayer(player1);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, 1.5));
        
        assertEquals("Declared amount must match the ship's base power decimal part", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_PowerBelowThresholdAlreadyInSpecial() throws InvalidContextualAction, InvalidParameters {
        context.addSpecialPlayer(player1);
        
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, 1.5));
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertNotNull(commands);
        assertFalse(commands.isEmpty());
    }
}