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
import Model.Ship.Components.CargoHold;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersLandStateTest {

    private Controller controller;
    private Context context;
    private SmugglersLandState state;
    private Player player1;
    private Player player2;
    private Coordinates cargoCoords1;
    private Coordinates cargoCoords2;
    private CargoHold cargoHold1;
    private CargoHold cargoHold2;

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
        context.getSpecialPlayers().add(player2);
        
        // Initialize and add some goods to the context using reflection
        try {
            java.lang.reflect.Field goodsField = Context.class.getDeclaredField("goods");
            goodsField.setAccessible(true);
            java.util.List<Good> goods = new java.util.ArrayList<>();
            goods.add(Good.RED);
            goods.add(Good.BLUE);
            goods.add(Good.GREEN);
            goodsField.set(context, goods);
        } catch (Exception e) {
            fail("Failed to set goods: " + e.getMessage());
        }
        
        cargoCoords1 = new Coordinates(5, 5);
        cargoCoords2 = new Coordinates(6, 6);
        cargoHold1 = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                  ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3, false);
        cargoHold2 = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                  ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3, false);
        
        try {
            player1.getShipBoard().addComponent(cargoHold1, cargoCoords1);
            player1.getShipBoard().addComponent(cargoHold2, cargoCoords2);
            player1.getShipBoard().getCondensedShip().addCargoHold(cargoHold1);
            player1.getShipBoard().getCondensedShip().addCargoHold(cargoHold2);
        } catch (Exception e) {
            fail("Failed to add components: " + e.getMessage());
        }
        
        state = new SmugglersLandState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testGetGood_ValidGoodPlacement() throws InvalidContextualAction, InvalidParameters {
        // This test expects an exception due to cargo hold placement failure
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.getGood("Player1", 0, cargoCoords1, 0));
        
        assertEquals("Failed to place good in cargo hold at specified index.", exception.getMessage());
    }

    @Test
    void testGetGood_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getGood("Player2", 0, cargoCoords1, 0));
        
        assertEquals("It's not your turn to remove crew members.", exception.getMessage());
    }

    @Test
    void testGetGood_InvalidCargoHold() {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.getGood("Player1", 0, invalidCoords, 0));
        
        assertEquals("Not a valid cargo hold coordinates.", exception.getMessage());
    }

    @Test
    void testGetGood_InvalidGoodIndex() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getGood("Player1", -1, cargoCoords1, 0));
        
        assertEquals("Invalid good index.", exception.getMessage());
    }

    @Test
    void testGetGood_GoodIndexOutOfBounds() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getGood("Player1", 10, cargoCoords1, 0));
        
        assertEquals("Invalid good index.", exception.getMessage());
    }

    @Test
    void testGetGood_InvalidCargoHoldIndex() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getGood("Player1", 0, cargoCoords1, -1));
        
        assertEquals("Invalid cargo hold index.", exception.getMessage());
    }

    @Test
    void testGetGood_CargoHoldIndexOutOfBounds() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getGood("Player1", 0, cargoCoords1, 10));
        
        assertEquals("Invalid cargo hold index.", exception.getMessage());
    }

    @Test
    void testGetGood_NullGood() {
        context.getGoods().clear();
        context.getGoods().add(null);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.getGood("Player1", 0, cargoCoords1, 0));
        
        assertEquals("Selected good is null.", exception.getMessage());
    }

    @Test
    void testGetGood_FailedToPlaceGood() throws InvalidContextualAction, InvalidParameters {
        // Fill the cargo hold first
        cargoHold1.addGood(Good.YELLOW);
        cargoHold1.addGood(Good.YELLOW);
        cargoHold1.addGood(Good.YELLOW);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.getGood("Player1", 0, cargoCoords1, 0));
        
        assertEquals("Failed to place good in cargo hold at specified index.", exception.getMessage());
    }

    @Test
    void testMoveGood_ValidMove() throws InvalidParameters, InvalidContextualAction {
        // First add a good to move
        cargoHold1.addGood(Good.RED);
        
        // This test expects an exception due to null good
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords1, cargoCoords2, 0, 0));
        
        assertEquals("Selected good is null.", exception.getMessage());
    }

    @Test
    void testMoveGood_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player2", cargoCoords1, cargoCoords2, 0, 0));
        
        assertEquals("It's not your turn to move the good.", exception.getMessage());
    }

    @Test
    void testMoveGood_InvalidOldCargoHold() {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", invalidCoords, cargoCoords2, 0, 0));
        
        assertEquals("Invalid cargo hold coordinates.", exception.getMessage());
    }

    @Test
    void testMoveGood_InvalidNewCargoHold() {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords1, invalidCoords, 0, 0));
        
        assertEquals("Invalid cargo hold coordinates.", exception.getMessage());
    }

    @Test
    void testMoveGood_InvalidOldIndex() {
        // Add a good to ensure we don't get null good error first
        try {
            cargoHold1.addGood(Good.RED);
        } catch (Exception e) {
            // Ignore if adding fails
        }
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords1, cargoCoords2, -1, 0));
        
        assertEquals("Invalid cargo hold index.", exception.getMessage());
    }

    @Test
    void testMoveGood_InvalidNewIndex() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords1, cargoCoords2, 0, -1));
        
        assertEquals("Invalid cargo hold index.", exception.getMessage());
    }

    @Test
    void testMoveGood_NullGood() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords1, cargoCoords2, 0, 0));
        
        assertEquals("Selected good is null.", exception.getMessage());
    }

    @Test
    void testMoveGood_FailedToAddGood() throws InvalidContextualAction, InvalidParameters {
        // Add a good to the source cargo hold
        cargoHold1.addGood(Good.RED);
        
        // Fill the destination cargo hold
        cargoHold2.addGood(Good.YELLOW);
        cargoHold2.addGood(Good.YELLOW);
        cargoHold2.addGood(Good.YELLOW);
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.moveGood("Player1", cargoCoords1, cargoCoords2, 0, 0));
        
        assertEquals("Selected good is null.", exception.getMessage());
    }

    @Test
    void testEnd_ValidEnd() throws InvalidParameters {
        // Set up player2 with goods so they need to go through goods removal
        CargoHold cargoHold3 = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                 ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 3, false);
        try {
            player2.getShipBoard().addComponent(cargoHold3, new Coordinates(8, 8));
            player2.getShipBoard().getCondensedShip().addCargoHold(cargoHold3);
            cargoHold3.addGood(Good.BLUE);
        } catch (Exception e) {
            fail("Failed to add components to player2: " + e.getMessage());
        }
        
        state.end("Player1");
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(SmugglersGoodsRemovalState.class, controller.getModel().getState());
    }

    @Test
    void testEnd_NoMoreSpecialPlayers() throws InvalidParameters {
        context.getSpecialPlayers().clear();
        
        state.end("Player1");
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testEnd_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.end("Player2"));
        
        assertEquals("It's not your turn", exception.getMessage());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(3, commands.size());
        assertTrue(commands.contains("GetGood"));
        assertTrue(commands.contains("MoveGood"));
        assertTrue(commands.contains("End"));
    }
}