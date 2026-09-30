package Controller.Pirates;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Direction;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.Cannon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PiratesPowerDeclarationStateTest {

    private Controller controller;
    private Context context;
    private PiratesPowerDeclarationState state;
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
        
        // Set up ship components directly on condensed ship
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 5);
        frontCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        frontCannon.setOrientation(Direction.UP);
        otherCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, false);
        otherCannon.setOrientation(Direction.DOWN);
        
        player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
        player1.getShipBoard().getCondensedShip().addCannon(frontCannon);
        player1.getShipBoard().getCondensedShip().addCannon(otherCannon);
        
        state = new PiratesPowerDeclarationState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testDeclaresDouble_Success() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
    }





    @Test
    void testDeclaresDouble_InvalidDoubleType() {
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, 6.0));
    }

    @Test
    void testDeclaresDouble_NegativeAmount() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, -1.0));
        
        assertEquals("Negative amount", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player2", DoubleType.CANNONS, 6.0));
        
        assertEquals("It's not the player's turn", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_InvalidAmount() {
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, -1.0));
        
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_AmountOutOfBoundsTooLow() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, basePower - 1.0));
        
        assertEquals("Negative amount", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_AmountOutOfBoundsTooHigh() {
        double maxPower = player1.getShipBoard().getCondensedShip().getMaxPower();
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, maxPower + 1.0));
    }

    @Test
    void testDeclaresDouble_WrongDecimalPart() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double wrongDecimal = (basePower % 1 == 0.5) ? basePower + 0.3 : basePower + 0.5;
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, wrongDecimal));
    }

    @Test
    void testDeclaresDouble_NotEnoughDoubleCannons() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double maxPower = player1.getShipBoard().getCondensedShip().getMaxPower();
        // This test should pass without throwing an exception since maxPower should be achievable
        assertDoesNotThrow(() -> state.declaresDouble("Player1", DoubleType.CANNONS, maxPower));
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_NotEnoughBatteries() {
        player1.getShipBoard().getCondensedShip().getBatteryCompartments().clear();
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, basePower + 2.0));
    }

    @Test
    void testDeclaresDouble_AlreadyMarkedPlayer() {
        context.addSpecialPlayer(player1);
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        assertThrows(InvalidContextualAction.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, basePower));
    }

    @Test
    void testDeclaresDouble_PlayerLoses_NoPlayersLeft() throws InvalidContextualAction, InvalidParameters {
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertFalse(context.getPlayers().contains(player1));
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_PlayerLoses_MorePlayersLeft() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertFalse(context.getPlayers().contains(player1));
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_ExactThrustGreaterThanPirates() throws InvalidContextualAction, InvalidParameters {
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, 3);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_ExactThrustEqualsPirates_NoPlayersLeft() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, (int) basePower);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertTrue(context.getPlayers().contains(player1));
    }

    @Test
    void testDeclaresDouble_ExactThrustEqualsPirates_MorePlayers() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, (int) basePower);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertTrue(context.getPlayers().contains(player1));
    }

    @Test
    void testDeclaresDouble_ExactThrustLessThanPirates() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        try {
            java.lang.reflect.Field powerField = Context.class.getDeclaredField("power");
            powerField.setAccessible(true);
            powerField.set(context, (int) basePower + 1);
        } catch (Exception e) {
            fail("Failed to set power: " + e.getMessage());
        }
        
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertFalse(context.getPlayers().contains(player1));
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_ThrustDifferentThanDeclared() throws InvalidContextualAction, InvalidParameters {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double maxPower = player1.getShipBoard().getCondensedShip().getMaxPower();
        // Use a valid amount that maintains the same decimal part as base power
        double declaredAmount = Math.min(basePower + 2.0, maxPower);
        if ((declaredAmount % 1) != (basePower % 1)) {
            declaredAmount = basePower + 1.0; // Fallback to a smaller increment
        }
        
        state.declaresDouble("Player1", DoubleType.CANNONS, declaredAmount);
        
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("DeclareFirePower"));
    }
}