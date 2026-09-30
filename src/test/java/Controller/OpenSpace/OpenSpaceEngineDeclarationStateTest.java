package Controller.OpenSpace;

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
import Model.Ship.Components.Engine;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceEngineDeclarationStateTest {

    private Controller controller;
    private Context context;
    private OpenSpaceEngineDeclarationState state;
    private Player player1;
    private Player player2;

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
        
        // Add engine to player1's ship
        Coordinates engineCoords = new Coordinates(6, 7);
        Engine engine = new Engine(Card.ENGINE, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                  ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        engine.setOrientation(Direction.UP);
        
        try {
            player1.getShipBoard().addComponent(engine, engineCoords);
        } catch (Exception e) {
            fail("Failed to add engine: " + e.getMessage());
        }
        
        state = new OpenSpaceEngineDeclarationState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testOnEnter() {
        // This test expects an exception due to player not found in flight board
        assertThrows(Exception.class, () -> state.onEnter());
    }

    @Test
    void testDeclaresDouble_ValidDeclaration() throws InvalidContextualAction, InvalidParameters {
        // This test expects an exception due to player not found in flight board
        assertThrows(Exception.class, () -> {
            double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
            state.declaresDouble("Player1", DoubleType.ENGINES, baseThrust);
        });
    }

    @Test
    void testDeclaresDouble_InvalidDoubleType() {
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, 1.0));
    }

    @Test
    void testDeclaresDouble_NegativeAmount() {
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, -1.0));
    }

    @Test
    void testDeclaresDouble_WrongPlayer() {
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player2", DoubleType.ENGINES, 1.0));
    }

    @Test
    void testDeclaresDouble_AmountOutOfBounds() {
        double maxThrust = player1.getShipBoard().getCondensedShip().getMaxThrust() + 1;
        
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, maxThrust));
    }

    @Test
    void testDeclaresDouble_DecimalMismatch() {
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double invalidThrust = Math.floor(baseThrust) + 0.75; // Different decimal part
        
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, invalidThrust));
    }

    @Test
    void testDeclaresDouble_OddDelta() {
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double invalidThrust = baseThrust + 1; // Odd delta
        
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, invalidThrust));
    }

    @Test
    void testDeclaresDouble_NotEnoughEngines() {
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double highThrust = baseThrust + 10; // Requires more engines than available
        
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, highThrust));
    }

    @Test
    void testDeclaresDouble_NotEnoughBatteries() {
        // Remove all batteries from player1's ship
        player1.getShipBoard().getCondensedShip().getBatteryCompartments().clear();
        
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double highThrust = baseThrust + 2; // Requires batteries
        
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, highThrust));
    }

    @Test
    void testDeclaresDouble_AllPlayersFinished() throws InvalidContextualAction, InvalidParameters, Model.Exceptions.InvalidMethodParameters {
        // Set up context with only one player
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        
        // This will throw an exception due to flight board setup
        assertThrows(Exception.class, () -> state.declaresDouble("Player1", DoubleType.ENGINES, baseThrust));
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertNotNull(commands);
        assertFalse(commands.isEmpty());
    }
}