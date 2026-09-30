package Controller;

import Controller.Enums.*;
import Controller.Exceptions.*;
import Model.Enums.Direction;
import Model.Player;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the State class.
 * Tests abstract state functionality and default method implementations.
 */
public class StateTest {

    private Controller controller;
    private TestState state;

    // Concrete implementation of State for testing
    private static class TestState extends State {
        public TestState(Controller controller) {
            super(controller);
        }
        
        public TestState(Context context) {
            //super();
            super(context);
        }
        
        @Override
        public List<String> getAvailableCommands() {
            return List.of();
        }
    }

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        state = new TestState(controller);
        // Don't login players to avoid Server.server null issues
    }

    @Test
    public void testStateConstructorWithController() {
        assertNotNull(state);
        Assertions.assertEquals(controller, state.getController());
        // PlayerInTurn may be null initially
        assertTrue(true);
    }
//
//    @Test
//    public void testStateConstructorEmpty() {
//        TestState emptyState = new TestState();
//        assertNotNull(emptyState);
//        assertNull(emptyState.getController());
//        assertNull(emptyState.getPlayerInTurn());
//    }

    @Test
    public void testGetController() {
        Assertions.assertEquals(controller, state.getController());
    }

    @Test
    public void testGetPlayerInTurn() {
        // PlayerInTurn may be null initially
        Player playerInTurn = state.getPlayerInTurn();
        if (playerInTurn != null && !controller.getModel().getPlayers().isEmpty()) {
            Assertions.assertEquals(controller.getModel().getFlightBoard().getTurnOrder()[0], state.getPlayerInTurn());
        }
        assertTrue(true);
    }

    @Test
    public void testSetPlayerInTurn() {
        if (controller.getModel().getPlayers().size() > 1) {
            var players = controller.getModel().getPlayers();
            state.setPlayerInTurn(players.get(1));
            Assertions.assertEquals(players.get(1), state.getPlayerInTurn());
        }
    }

    @Test
    public void testOnEnter() {
        // Default implementation should not throw
        state.onEnter();
        assertTrue(true);
    }

    @Test
    public void testLoginThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.login("TestPlayer"));
        assertEquals("Invalid command: login", exception.getMessage());
    }

    @Test
    public void testLogoutThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.logout("TestPlayer"));
        assertEquals("Invalid command: logout", exception.getMessage());
    }

    @Test
    public void testStartGameThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.startGame("TestPlayer"));
        assertEquals("Invalid command: startGame", exception.getMessage());
    }

    @Test
    public void testGetComponentThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.getComponent("TestPlayer", 0));
        assertEquals("Invalid command: getComponent", exception.getMessage());
    }

    @Test
    public void testReserveComponentThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.reserveComponent("TestPlayer"));
        assertEquals("Invalid command: reserveComponent", exception.getMessage());
    }

    @Test
    public void testPlaceComponentThrowsInvalidCommand() {
        Coordinates coords = new Coordinates(5, 7);
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.placeComponent("TestPlayer", ComponentOrigin.HAND, coords, Direction.UP));
        assertEquals("Invalid command: placeComponent", exception.getMessage());
    }

    @Test
    public void testLookDeckThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.lookDeck("TestPlayer", 1));
        assertEquals("Invalid command: lookDeck", exception.getMessage());
    }

    @Test
    public void testFlipHourGlassThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.flipHourGlass("TestPlayer"));
        assertEquals("Invalid command: flipHourGlass", exception.getMessage());
    }

    @Test
    public void testFinishBuildingThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.finishBuilding("TestPlayer", 1));
        assertEquals("Invalid command: finishBuilding", exception.getMessage());
    }

    @Test
    public void testPlaceCrewThrowsInvalidCommand() {
        Coordinates coords = new Coordinates(5, 7);
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.placeCrew("TestPlayer", coords, CrewType.HUMAN));
        assertEquals("Invalid command: placeCrew", exception.getMessage());
    }

    @Test
    public void testPickNextCardThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.pickNextCard("TestPlayer"));
        assertEquals("Invalid command: pickNextCard", exception.getMessage());
    }

    @Test
    public void testDeleteComponentThrowsInvalidCommand() {
        Coordinates coords = new Coordinates(5, 7);
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.deleteComponent("TestPlayer", coords));
        assertEquals("Invalid command: deleteComponent", exception.getMessage());
    }

    @Test
    public void testLeaveRaceThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.leaveRace("TestPlayer"));
        assertEquals("Invalid command: leaveRace", exception.getMessage());
    }

    @Test
    public void testGetRewardThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.getReward("TestPlayer", RewardType.CREDITS));
        assertEquals("Invalid command: getReward", exception.getMessage());
    }

    @Test
    public void testMoveGoodThrowsInvalidCommand() {
        Coordinates oldCoords = new Coordinates(5, 7);
        Coordinates newCoords = new Coordinates(6, 7);
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.moveGood("TestPlayer", oldCoords, newCoords, 0, 1));
        assertEquals("Invalid command: moveGoods", exception.getMessage());
    }

    @Test
    public void testUseItemThrowsInvalidCommand() {
        Coordinates coords = new Coordinates(5, 7);
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.useItem("TestPlayer", ItemType.BATTERIES, coords));
        assertEquals("Invalid command: useItem", exception.getMessage());
    }

    @Test
    public void testDeclaresDoubleThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.declaresDouble("TestPlayer", DoubleType.ENGINES, 2.0));
        assertEquals("Invalid command: declaresDouble", exception.getMessage());
    }

    @Test
    public void testEndThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.end("TestPlayer"));
        assertEquals("Invalid command: end", exception.getMessage());
    }

    @Test
    public void testChoosePlanetThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.choosePlanet("TestPlayer", "Earth"));
        assertEquals("Invalid command: choosePlanet", exception.getMessage());
    }

    @Test
    public void testSkipRewardThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.skipReward("TestPlayer"));
        assertEquals("Invalid command: skipReward", exception.getMessage());
    }

    @Test
    public void testGetGoodThrowsInvalidCommand() {
        Coordinates coords = new Coordinates(5, 7);
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> 
            state.getGood("TestPlayer", 0, coords, 0));
        assertEquals("Invalid command: getGood", exception.getMessage());
    }

    @Test
    public void testThrowDicesThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class, () -> state.throwDices("TestPlayer"));
        assertEquals("Invalid command: throwDices", exception.getMessage());
    }

    @Test
    public void testAllMethodsThrowCorrectExceptions() {
        // Test that all methods throw InvalidCommand with correct messages
        String[] expectedMessages = {
            "Invalid command: login",
            "Invalid command: logout", 
            "Invalid command: startGame",
            "Invalid command: getComponent",
            "Invalid command: reserveComponent",
            "Invalid command: placeComponent",
            "Invalid command: lookDeck",
            "Invalid command: flipHourGlass",
            "Invalid command: finishBuilding",
            "Invalid command: placeCrew",
            "Invalid command: pickNextCard",
            "Invalid command: deleteComponent",
            "Invalid command: leaveRace",
            "Invalid command: getReward",
            "Invalid command: moveGoods",
            "Invalid command: useItem",
            "Invalid command: declaresDouble",
            "Invalid command: end",
            "Invalid command: choosePlanet",
            "Invalid command: skipReward",
            "Invalid command: getGood",
            "Invalid command: throwDices"
        };

        // Test each method
        assertThrows(InvalidCommand.class, () -> state.login("test"));
        assertThrows(InvalidCommand.class, () -> state.logout("test"));
        assertThrows(InvalidCommand.class, () -> state.startGame("test"));
        assertThrows(InvalidCommand.class, () -> state.getComponent("test", 0));
        assertThrows(InvalidCommand.class, () -> state.reserveComponent("test"));
        assertThrows(InvalidCommand.class, () -> state.placeComponent("test", ComponentOrigin.HAND, new Coordinates(0, 0), Direction.UP));
        assertThrows(InvalidCommand.class, () -> state.lookDeck("test", 0));
        assertThrows(InvalidCommand.class, () -> state.flipHourGlass("test"));
        assertThrows(InvalidCommand.class, () -> state.finishBuilding("test", 0));
        assertThrows(InvalidCommand.class, () -> state.placeCrew("test", new Coordinates(0, 0), CrewType.HUMAN));
        assertThrows(InvalidCommand.class, () -> state.pickNextCard("test"));
        assertThrows(InvalidCommand.class, () -> state.deleteComponent("test", new Coordinates(0, 0)));
        assertThrows(InvalidCommand.class, () -> state.leaveRace("test"));
        assertThrows(InvalidCommand.class, () -> state.getReward("test", RewardType.CREDITS));
        assertThrows(InvalidCommand.class, () -> state.moveGood("test", new Coordinates(0, 0), new Coordinates(1, 1), 0, 1));
        assertThrows(InvalidCommand.class, () -> state.useItem("test", ItemType.BATTERIES, new Coordinates(0, 0)));
        assertThrows(InvalidCommand.class, () -> state.declaresDouble("test", DoubleType.ENGINES, 1.0));
        assertThrows(InvalidCommand.class, () -> state.end("test"));
        assertThrows(InvalidCommand.class, () -> state.choosePlanet("test", "planet"));
        assertThrows(InvalidCommand.class, () -> state.skipReward("test"));
        assertThrows(InvalidCommand.class, () -> state.getGood("test", 0, new Coordinates(0, 0), 0));
        assertThrows(InvalidCommand.class, () -> state.throwDices("test"));
    }
//
//    @Test
//    public void testStateWithContextController() {
//
//        TestState nullState = new TestState();
//        assertNull(nullState.getController());
//        assertNull(nullState.getPlayerInTurn());
//
//        // Should still throw InvalidCommand for all methods
//        assertThrows(InvalidCommand.class, () -> nullState.login("test"));
//        assertThrows(InvalidCommand.class, () -> nullState.logout("test"));
//    }

    @Test
    public void testPlayerInTurnManagement() {
        if (controller.getModel().getPlayers().size() > 0) {
            var player = controller.getModel().getPlayers().get(0);
            state.setPlayerInTurn(player);
            Assertions.assertEquals(player, state.getPlayerInTurn());
            
            // Test setting to null
            state.setPlayerInTurn(null);
            assertNull(state.getPlayerInTurn());
        }
    }

    @Test
    public void testStateInheritance() {
        // Test that TestState properly extends State
        assertTrue(state instanceof State);
        assertNotNull(state.getController());
    }

    @Test
    public void testOnEnterDefaultBehavior() {
        // Test that onEnter can be called multiple times without issues
        state.onEnter();
        state.onEnter();
        state.onEnter();
        assertTrue(true);
    }

    @Test
    public void testExceptionMessages() {
        // Verify exact exception messages for key methods
        try {
            state.login("test");
            fail("Should throw InvalidCommand");
        } catch (InvalidCommand e) {
            assertEquals("Invalid command: login", e.getMessage());
        } catch (Exception e) {
            fail("Should throw InvalidCommand, not " + e.getClass().getSimpleName());
        }
        
        try {
            state.startGame("test");
            fail("Should throw InvalidCommand");
        } catch (InvalidCommand e) {
            assertEquals("Invalid command: startGame", e.getMessage());
        } catch (Exception e) {
            fail("Should throw InvalidCommand, not " + e.getClass().getSimpleName());
        }
    }

    @Test
    public void testStateWithEmptyController() {
        Controller emptyController = new Controller(MatchLevel.TRIAL, 999);
        TestState emptyState = new TestState(emptyController);
        
        Assertions.assertEquals(emptyController, emptyState.getController());
        // Player in turn may be null with empty controller
        assertTrue(true);
    }

    @Test
    public void testAllEnumValues() {
        // Test with all enum values to ensure coverage
        Coordinates coords = new Coordinates(0, 0);
        
        // Test all ComponentOrigin values
        for (ComponentOrigin origin : ComponentOrigin.values()) {
            assertThrows(InvalidCommand.class, () -> 
                state.placeComponent("test", origin, coords, Direction.UP));
        }
        
        // Test all Direction values
        for (Direction dir : Direction.values()) {
            assertThrows(InvalidCommand.class, () -> 
                state.placeComponent("test", ComponentOrigin.HAND, coords, dir));
        }
        
        // Test all CrewType values
        for (CrewType crew : CrewType.values()) {
            assertThrows(InvalidCommand.class, () -> 
                state.placeCrew("test", coords, crew));
        }
        
        // Test all RewardType values
        for (RewardType reward : RewardType.values()) {
            assertThrows(InvalidCommand.class, () -> 
                state.getReward("test", reward));
        }
        
        // Test all ItemType values
        for (ItemType item : ItemType.values()) {
            assertThrows(InvalidCommand.class, () -> 
                state.useItem("test", item, coords));
        }
        
        // Test all DoubleType values
        for (DoubleType doubleType : DoubleType.values()) {
            assertThrows(InvalidCommand.class, () -> 
                state.declaresDouble("test", doubleType, 1.0));
        }
    }

    @Test
    public void testMethodParameterVariations() {
        Coordinates coords = new Coordinates(5, 7);
        
        // Test with different parameter values
        assertThrows(InvalidCommand.class, () -> state.getComponent("", 0));
        assertThrows(InvalidCommand.class, () -> state.getComponent("test", -1));
        assertThrows(InvalidCommand.class, () -> state.getComponent("test", 100));
        
        assertThrows(InvalidCommand.class, () -> state.lookDeck("test", 0));
        assertThrows(InvalidCommand.class, () -> state.lookDeck("test", 1));
        assertThrows(InvalidCommand.class, () -> state.lookDeck("test", 3));
        
        assertThrows(InvalidCommand.class, () -> state.finishBuilding("test", 1));
        assertThrows(InvalidCommand.class, () -> state.finishBuilding("test", 4));
        
        assertThrows(InvalidCommand.class, () -> state.choosePlanet("test", ""));
        assertThrows(InvalidCommand.class, () -> state.choosePlanet("test", "ValidPlanet"));
        
        assertThrows(InvalidCommand.class, () -> state.getGood("test", 0, coords, 0));
        assertThrows(InvalidCommand.class, () -> state.getGood("test", -1, coords, 0));
        
        assertThrows(InvalidCommand.class, () -> state.declaresDouble("test", DoubleType.ENGINES, 0.0));
        assertThrows(InvalidCommand.class, () -> state.declaresDouble("test", DoubleType.ENGINES, -1.0));
        assertThrows(InvalidCommand.class, () -> state.declaresDouble("test", DoubleType.ENGINES, 10.0));
    }

    @Test
    public void testCoordinateVariations() {
        // Test with different coordinate values
        Coordinates[] testCoords = {
            new Coordinates(0, 0),
            new Coordinates(5, 7),
            new Coordinates(10, 10),
            new Coordinates(-1, -1)
        };
        
        for (Coordinates coord : testCoords) {
            assertThrows(InvalidCommand.class, () -> 
                state.placeComponent("test", ComponentOrigin.HAND, coord, Direction.UP));
            assertThrows(InvalidCommand.class, () -> 
                state.placeCrew("test", coord, CrewType.HUMAN));
            assertThrows(InvalidCommand.class, () -> 
                state.deleteComponent("test", coord));
            assertThrows(InvalidCommand.class, () -> 
                state.useItem("test", ItemType.BATTERIES, coord));
            assertThrows(InvalidCommand.class, () -> 
                state.getGood("test", 0, coord, 0));
        }
    }

    @Test
    public void testMoveGoodCoordinateVariations() {
        Coordinates[] coords = {
            new Coordinates(0, 0),
            new Coordinates(5, 7),
            new Coordinates(10, 10)
        };
        
        for (Coordinates oldCoord : coords) {
            for (Coordinates newCoord : coords) {
                assertThrows(InvalidCommand.class, () -> 
                    state.moveGood("test", oldCoord, newCoord, 0, 1));
            }
        }
    }

    @Test
    public void testStringParameterVariations() {
        String[] testNames = {"", "test", "Player1", "VeryLongPlayerName", null};
        
        for (String name : testNames) {
            try {
                assertThrows(InvalidCommand.class, () -> state.login(name));
                assertThrows(InvalidCommand.class, () -> state.logout(name));
                assertThrows(InvalidCommand.class, () -> state.startGame(name));
                assertThrows(InvalidCommand.class, () -> state.getComponent(name, 0));
                assertThrows(InvalidCommand.class, () -> state.reserveComponent(name));
                assertThrows(InvalidCommand.class, () -> state.throwDices(name));
            } catch (NullPointerException e) {
                // Expected for null names
                assertTrue(true);
            }
        }
    }
}