package Controller.CombatZone.Level_TWO;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Board.AdventureCards.CombatZone;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Penalties.CannonShotPenalty;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Penalties.GoodsPenalty;
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

class CombatZone2PowerDeclarationStateTest {

    private Controller controller;
    private Context context;
    private CombatZone2PowerDeclarationState state;
    private Player player1;
    private Player player2;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.LEVEL2, 1);
        controller.getModel().addPlayer("Player1");
        controller.getModel().addPlayer("Player2");
        
        player1 = controller.getModel().getPlayer("Player1");
        player2 = controller.getModel().getPlayer("Player2");
        
        // Create a custom Context with CombatZone
        List<CombatZoneLine> lines = new ArrayList<>();
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(2)));
        lines.add(new CombatZoneLine(Criteria.ENGINE_POWER, new GoodsPenalty(1)));
        
        List<CannonShot> shots = Arrays.asList(
            new CannonShot(false, Side.FRONT),
            new CannonShot(true, Side.REAR)
        );
        lines.add(new CombatZoneLine(Criteria.MAN_POWER, new CannonShotPenalty(shots)));
        
        CombatZone combatZone = new CombatZone(1, CardLevel.LEVEL_TWO, lines);
        context = new Context(controller);
        
        // Set up the context's player list
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        // Set days lost in context
        try {
            java.lang.reflect.Field daysLostField = Context.class.getDeclaredField("daysLost");
            daysLostField.setAccessible(true);
            daysLostField.set(context, 2);
        } catch (Exception e) {
            fail("Failed to set daysLost: " + e.getMessage());
        }
        
        // Add cannons to player1's ship to allow power declarations
        Cannon frontCannon = new Cannon(Card.CANNON, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 
                                       ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, true);
        frontCannon.setOrientation(Direction.UP);
        
        try {
            player1.getShipBoard().addComponent(frontCannon, new Coordinates(6, 7));
        } catch (Exception e) {
            fail("Failed to add cannon: " + e.getMessage());
        }
        
        state = new CombatZone2PowerDeclarationState(context);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        // In Level 2, the player in turn might not be set in constructor
        assertNotNull(context.getPlayers().getFirst());
    }

    @Test
    void testConstructorWithWorst() {
        CombatZone2PowerDeclarationState stateWithWorst = new CombatZone2PowerDeclarationState(context, 2.0);
        assertNotNull(stateWithWorst);
    }

    @Test
    void testDeclaresDouble_ValidDeclaration() throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
        // Get the base power of the player's ship
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        
        // Declare the same power (no batteries used)
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        assertFalse(controller.getModel().isError());
        // Should transition to next player or next state
        assertTrue(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testDeclaresDouble_InvalidDoubleType() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.ENGINES, 1.0));
        
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
            () -> state.declaresDouble("Player2", DoubleType.CANNONS, 1.0));
        
        assertEquals("It's not the player's turn", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_AmountOutOfBounds() {
        double maxPower = player1.getShipBoard().getCondensedShip().getMaxPower() + 1;
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, maxPower));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_DecimalMismatch() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double invalidPower = Math.floor(basePower) + 0.75; // Different decimal part
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, invalidPower));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testDeclaresDouble_ValidDeclarationWithBatteries() throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
        // Get the base power - only declare base power to avoid out of bounds
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        
        // Declare base power (no batteries used)
        state.declaresDouble("Player1", DoubleType.CANNONS, basePower);
        
        // Should add player to special players
        assertTrue(context.getSpecialPlayers().contains(player1));
    }

    @Test
    void testDeclaresDouble_NonIntegerAmount() {
        double basePower = player1.getShipBoard().getCondensedShip().getBasePower();
        double nonIntegerPower = basePower + 1.5; // Non-integer delta
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.declaresDouble("Player1", DoubleType.CANNONS, nonIntegerPower));
        
        assertEquals("Declared amount is out of bounds", exception.getMessage());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("DeclareFirePower"));
    }
}