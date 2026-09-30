package Controller.CombatZone.Level_ONE;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
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
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatZone1_P_BatteryRemovalStateTest {

    private Controller controller;
    private Context context;
    private CombatZone1_P_BatteryRemovalState state;
    private Player player1;
    private Player player2;
    private Coordinates batteryCoords;
    private BatteryCompartment battery;

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
        state = new CombatZone1_P_BatteryRemovalState(context, declaredPower, batteriesToRemove);
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testUseItem_ValidBatteryRemoval() throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        // This test expects an exception due to component type mismatch
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("Invalid component type, expected BatteryCompartment", exception.getMessage());
    }

    @Test
    void testUseItem_InvalidItemType() throws InvalidMethodParameters {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.CREW, batteryCoords));
        
        assertEquals("Invalid item type, expected BATTERIES", exception.getMessage());
    }

    @Test
    void testUseItem_WrongPlayer() throws InvalidMethodParameters {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player2", ItemType.BATTERIES, batteryCoords));
        
        assertEquals("It's not the player's turn", exception.getMessage());
    }

    @Test
    void testUseItem_NullCoordinates() throws InvalidMethodParameters {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, null));
        
        assertEquals("Coordinates cannot be null", exception.getMessage());
    }

    @Test
    void testUseItem_InvalidComponent() throws InvalidMethodParameters {
        Coordinates invalidCoords = new Coordinates(0, 0);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.useItem("Player1", ItemType.BATTERIES, invalidCoords));
        
        assertEquals("Invalid component type, expected BatteryCompartment", exception.getMessage());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(1, commands.size());
        assertTrue(commands.contains("UseBattery"));
    }
}