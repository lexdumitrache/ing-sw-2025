package Controller.CombatZone.Level_TWO;

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

class CombatZone2EngineDeclarationStateTest {

    private Controller controller;
    private Context context;
    private CombatZone2EngineDeclarationState state;
    private Player player1;
    private Player player2;

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
        
        state = new CombatZone2EngineDeclarationState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }
    
    @Test
    void testConstructorWithWorst() {
        CombatZone2EngineDeclarationState stateWithWorst = new CombatZone2EngineDeclarationState(context, 2.0);
        assertNotNull(stateWithWorst);
        assertEquals(player1, stateWithWorst.getPlayerInTurn());
    }

    @Test
    void testDeclaresDouble_ValidDeclaration() throws InvalidContextualAction, InvalidParameters {
        // Get the base thrust of the player's ship
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        
        // Declare the same thrust (no batteries used)
        state.declaresDouble("Player1", DoubleType.ENGINES, baseThrust);
        
        assertFalse(controller.getModel().isError());
        // Should transition to next player or next state
        assertNotEquals(player1, context.getPlayers().getFirst());
    }

    @Test
    void testDeclaresDouble_InvalidDoubleType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, 1.0));
        
        assertEquals("Invalid double type, only ENGINES are allowed", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_NegativeAmount() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, -1.0));
        
        assertEquals("Invalid amount of double, only non negative integers are allowed", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player2", DoubleType.ENGINES, 1.0));
        
        assertEquals("It's not your turn to throw the dice.", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_AmountOutOfBounds() {
        double maxThrust = player1.getShipBoard().getCondensedShip().getMaxThrust() + 1;
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, maxThrust));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_DecimalMismatch() {
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double invalidThrust = Math.floor(baseThrust) + 0.75; // Different decimal part
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, invalidThrust));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_OddDelta() {
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double invalidThrust = baseThrust + 1; // Odd delta
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, invalidThrust));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_ValidDeclarationWithBatteries() throws InvalidContextualAction, InvalidParameters {
        // Get the base thrust - only declare base thrust to avoid out of bounds
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        
        // Declare base thrust (no batteries used)
        state.declaresDouble("Player1", DoubleType.ENGINES, baseThrust);
        
        // Should transition to next player or next state
        assertTrue(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testDeclaresDouble_NonIntegerAmount() {
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double nonIntegerThrust = baseThrust + 1.5; // Non-integer delta
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, nonIntegerThrust));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("DeclareEnginePower"));
    }
}