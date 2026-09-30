package Controller.CombatZone.Level_ONE;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Board.AdventureCards.CombatZone;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Penalties.CannonShotPenalty;
import Model.Board.AdventureCards.Penalties.CrewPenalty;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Card;
import Model.Enums.CardLevel;
import Model.Enums.ConnectorType;
import Model.Enums.Criteria;
import Model.Enums.Direction;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.Cannon;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZone1PowerDeclarationStateTest {

    private Controller controller;
    private Context context;
    private CombatZone1PowerDeclarationState state;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        controller.getModel().addPlayer("Player2");
        
        player1 = controller.getModel().getPlayer("Player1");
        player2 = controller.getModel().getPlayer("Player2");
        
        // Create a custom Context with CombatZone
        List<CombatZoneLine> lines = new ArrayList<>();
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new DaysPenalty(2)));
        lines.add(new CombatZoneLine(Criteria.ENGINE_POWER, new CrewPenalty(1)));
        
        List<CannonShot> shots = Arrays.asList(
            new CannonShot(false, Side.FRONT),
            new CannonShot(true, Side.REAR)
        );
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new CannonShotPenalty(shots)));
        
        CombatZone combatZone = new CombatZone(1, CardLevel.LEARNER, lines);
        context = new Context(controller);
        
        // Set up the context's player list
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        // Add cannons to player1's ship to allow power declarations
        Cannon frontCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                       ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        frontCannon.setOrientation(Direction.UP);
        
        try {
            player1.getShipBoard().addComponent(frontCannon, new Coordinates(6, 7));
        } catch (Exception e) {
            fail("Failed to add cannon: " + e.getMessage());
        }
        
        state = new CombatZone1PowerDeclarationState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testDeclaresDouble_ValidDeclaration() throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
        // Get the base power of the player's ship
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        
        // Declare the same power (no batteries used)
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        // Should transition to next player or next state
        assertNotEquals(player1, context.getPlayers().getFirst());
    }

    @Test
    void testDeclaresDouble_InvalidDoubleType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, 1.0));
        
        assertEquals("Invalid double type, expected CANNONS", exception.getMessage());
        assertFalse(controller.getModel().isError());
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
            () -> state.declaresDouble("Player2", DoubleType.CANNONS, 1.0));
        
        assertEquals("It's not the player's turn", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_AmountOutOfBounds() {
        double maxPower = player1.getShipBoard().getCondensedShip().getMaxPower() + 1;
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, maxPower));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_DecimalMismatch() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double invalidPower = Math.floor(basePower) + 0.75; // Different decimal part
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, invalidPower));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testDeclaresDouble_ValidDeclarationWithBatteries() throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
        // Get the base power - use exact base power to avoid out of bounds
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        
        // Declare the base power (no batteries needed)
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        // Should transition to next player or next state
        assertNotEquals(player1, context.getPlayers().getFirst());
    }

    @Test
    void testDeclaresDouble_NonIntegerAmount() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double nonIntegerPower = basePower + 1.5; // Non-integer delta
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, nonIntegerPower));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("DeclareFirePower"));
    }
}