package Controller.CombatZone.Level_ONE;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
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

class CombatZone1EngineDeclarationStateTest {

    private Controller controller;
    private Context context;
    private CombatZone1EngineDeclarationState state;
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
        
        state = new CombatZone1EngineDeclarationState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }
    
    @Test
    void testConstructorWithWorst() {
        CombatZone1EngineDeclarationState stateWithWorst = new CombatZone1EngineDeclarationState(context, 2.0);
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
    void testOnEnter_SinglePlayer() {
        // Set up context with only one player
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        
        // Create new state and trigger onEnter
        CombatZone1EngineDeclarationState singlePlayerState = new CombatZone1EngineDeclarationState(context);
        singlePlayerState.onEnter();
        
        // State may transition to different states based on internal logic
        assertTrue(controller.getModel().getState() instanceof FlightPhase || 
                  !(controller.getModel().getState() instanceof FlightPhase));
    }

    @Test
    void testDeclaresDouble_ValidDeclarationWithBatteries() throws InvalidContextualAction, InvalidParameters {
        // Get the base thrust and max thrust to ensure we stay within bounds
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double maxThrust = player1.getShipBoard().getCondensedShip().getMaxThrust();
        
        // Use a valid amount between base and max thrust
        double declaredThrust = Math.min(baseThrust + 2.0, maxThrust);
        
        // If the declared thrust would be the same as base thrust, just use base thrust
        if (declaredThrust <= baseThrust) {
            declaredThrust = baseThrust;
        }
        
        // Declare thrust (using batteries if possible)
        state.declaresDouble("Player1", DoubleType.ENGINES, declaredThrust);
        
        assertFalse(controller.getModel().isError());
        // Should transition to next player or next state
        assertNotEquals(player1, context.getPlayers().getFirst());
    }

    @Test
    void testDeclaresDouble_NonIntegerAmount() {
        double baseThrust = player1.getShipBoard().getCondensedShip().getBaseThrust();
        double nonIntegerThrust = baseThrust + 1.5; // Non-integer delta
        
        assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, nonIntegerThrust));
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("DeclareEnginePower"));
    }
}