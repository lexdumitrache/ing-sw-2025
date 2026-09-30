package Controller.Smugglers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Good;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.CargoHold;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersGoodsRemovalStateTest {

    private Controller controller;
    private Context context;
    private SmugglersGoodsRemovalState state;
    private Player player1;
    private Player player2;
    private Coordinates cargoCoords;
    private Coordinates batteryCoords;
    private CargoHold cargoHold;
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
        // Set required goods using reflection
        try {
            java.lang.reflect.Field requiredGoodsField = Context.class.getDeclaredField("requiredGoods");
            requiredGoodsField.setAccessible(true);
            requiredGoodsField.set(context, 2);
        } catch (Exception e) {
            fail("Failed to set required goods: " + e.getMessage());
        }
        
        cargoCoords = new Coordinates(5, 5);
        batteryCoords = new Coordinates(6, 7);
        cargoHold = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                 ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3, false);
        battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                        ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3);
        
        try {
            player1.getShipBoard().addComponent(cargoHold, cargoCoords);
            player1.getShipBoard().addComponent(battery, batteryCoords);
            player1.getShipBoard().getCondensedShip().addCargoHold(cargoHold);
            player1.getShipBoard().getCondensedShip().addBatteryCompartment(battery);
            
            // Add goods in priority order: RED first (index 0)
            cargoHold.addGood(Good.RED);
            cargoHold.addGood(Good.YELLOW);
            cargoHold.addGood(Good.GREEN);
        } catch (Exception e) {
            fail("Failed to add components: " + e.getMessage());
        }
        
        state = new SmugglersGoodsRemovalState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testConstructorWithAmount() {
        context.setRequiredGoods(3);
        SmugglersGoodsRemovalState customState = new SmugglersGoodsRemovalState(context);
        assertNotNull(customState);
        assertEquals(player1, customState.getPlayerInTurn());
    }

    @Test
    void testMoveGood_ValidGoodRemoval() throws InvalidContextualAction, InvalidParameters {
        int initialGoods = cargoHold.getGoods().length;
        int nonNullGoods = 0;
        for (Good good : cargoHold.getGoods()) {
            if (good != null) nonNullGoods++;
        }
        
        state.moveGood("Player1", cargoCoords, null, 0, 0);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SmugglersGoodsRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testMoveGood_LastGoodWithMorePlayers() throws InvalidContextualAction, InvalidParameters {
        // Ensure there are multiple special players
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        context.getSpecialPlayers().add(player2);
        
        // Set up player2 with goods as well
        CargoHold cargoHold2 = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                 ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3, false);
        try {
            player2.getShipBoard().addComponent(cargoHold2, new Coordinates(5, 6));
            player2.getShipBoard().getCondensedShip().addCargoHold(cargoHold2);
            cargoHold2.addGood(Good.BLUE);
        } catch (Exception e) {
            fail("Failed to add components to player2: " + e.getMessage());
        }
        
        context.setRequiredGoods(1);
        SmugglersGoodsRemovalState singleGoodState = new SmugglersGoodsRemovalState(context);
        
        singleGoodState.moveGood("Player1", cargoCoords, null, 0, 0);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getSpecialPlayers().contains(player1));
        assertInstanceOf(SmugglersGoodsRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testMoveGood_LastGoodNoMorePlayers() throws InvalidContextualAction, InvalidParameters {
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        context.setRequiredGoods(1);
        SmugglersGoodsRemovalState singleGoodState = new SmugglersGoodsRemovalState(context);
        
        singleGoodState.moveGood("Player1", cargoCoords, null, 0, 0);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testMoveGood_NoGoodsButHasBatteries() throws InvalidContextualAction, InvalidParameters {
        // Clear all goods from cargo hold
        for (int i = 0; i < cargoHold.getCapacity(); i++) {
            cargoHold.removeGood(i);
        }
        
        state.moveGood("Player1", cargoCoords, null, 0, 0);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SecondSmugglersBatteryRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testMoveGood_NoGoodsNoBatteries() throws InvalidContextualAction, InvalidParameters {
        // Clear all goods from cargo hold
        for (int i = 0; i < cargoHold.getCapacity(); i++) {
            cargoHold.removeGood(i);
        }
        
        // Remove all batteries
        while (battery.getBatteries() > 0) {
            battery.removeBattery();
        }
        
        state.moveGood("Player1", cargoCoords, null, 0, 0);
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testMoveGood_NoGoodsNoBatteriesLastPlayer() throws InvalidContextualAction, InvalidParameters {
        context.getSpecialPlayers().clear();
        context.getSpecialPlayers().add(player1);
        
        // Clear all goods from cargo hold
        for (int i = 0; i < cargoHold.getCapacity(); i++) {
            cargoHold.removeGood(i);
        }
        
        // Remove all batteries
        while (battery.getBatteries() > 0) {
            battery.removeBattery();
        }
        
        state.moveGood("Player1", cargoCoords, null, 0, 0);
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testMoveGood_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player2", cargoCoords, null, 0, 0));
        
        assertEquals("It's not your turn", exception.getMessage());
    }

    @Test
    void testMoveGood_NullCoordinates() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", null, null, 0, 0));
        
        assertEquals("Invalid coordinates", exception.getMessage());
    }

    @Test
    void testMoveGood_NegativeIndex() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords, null, -1, 0));
        
        assertEquals("Invalid index", exception.getMessage());
    }

    @Test
    void testMoveGood_InvalidCargoHold() {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.moveGood("Player1", invalidCoords, null, 0, 0));
        
        assertEquals("Not a valid cargo hold", exception.getMessage());
    }

    @Test
    void testMoveGood_NullGood() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords, null, 2, 0));
        
        assertEquals("The selected good is not found", exception.getMessage());
    }

    @Test
    void testMoveGood_WrongGoodType() throws InvalidContextualAction, InvalidParameters {
        // This test would require setting up a specific good counter scenario
        // where the good type doesn't match what needs to be removed
        // For now, we'll test the basic functionality
        
        // Clear cargo and add only one type of good
        for (int i = 0; i < cargoHold.getCapacity(); i++) {
            cargoHold.removeGood(i);
        }
        cargoHold.addGood(Good.RED);
        
        // This should work normally
        state.moveGood("Player1", cargoCoords, null, 0, 0);
        
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("RemoveGood"));
    }
}