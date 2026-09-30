package Controller.PreMatchLobby;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;
import Controller.RealTimeBuilding.BuildingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LogInState functionality.
 * Tests login, logout, and game start operations.
 */
public class LogInStateTest {

    private Controller controller;
    private LogInState loginState;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 100);
        loginState = (LogInState) controller.getModel().getState();
    }

    // Login Tests

    @Test
    @DisplayName("Should reject duplicate player names")
    void testDuplicatePlayerNameRejection() throws Exception {
        // First login should succeed
        loginState.login("Player1");

        // Second login with same name should fail
        InvalidParameters exception = assertThrows(InvalidParameters.class,
                () -> loginState.login("Player1"));
        assertTrue(exception.getMessage().contains("Player with this name already connected"));
    }

    @Test
    @DisplayName("Should reject login when game has 4 players")
    void testMaxPlayersRejection() throws Exception {
        // Add 4 players
        loginState.login("Player1");
        loginState.login("Player2");
        loginState.login("Player3");
        loginState.login("Player4");

        assertEquals(4, controller.getModel().getPlayers().size());

        // Fifth player should be rejected
        InvalidParameters exception = assertThrows(InvalidParameters.class,
                () -> loginState.login("Player5"));
        assertTrue(exception.getMessage().contains("Game is full"));
    }

    // Logout Tests

    @Test
    @DisplayName("Should transition to OffState when all players logout")
    void testTransitionToOffStateWhenEmpty() throws Exception {
        // Add some players
        loginState.login("Player1");
        loginState.login("Player2");

        // Remove all players
        loginState.logout("Player1");
        loginState.logout("Player2");

        // Should be in OffState
        assertTrue(controller.getModel().getState() instanceof OffState);
    }

    @Test
    @DisplayName("Should remain in LogInState if players remain after logout")
    void testRemainInLogInStateWithPlayers() throws Exception {
        // Add players
        loginState.login("Player1");
        loginState.login("Player2");
        loginState.login("Player3");

        // Remove one player
        loginState.logout("Player2");

        // Should still be in LogInState
        assertTrue(controller.getModel().getState() instanceof LogInState);
        assertEquals(2, controller.getModel().getPlayers().size());
    }

    // Start Game Tests

    @Test
    @DisplayName("Should reject game start with less than 2 players")
    void testRejectStartWithInsufficientPlayers() throws Exception {
        // Add only one player
        loginState.login("Player1");

        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> loginState.startGame("Player1"));
        assertTrue(exception.getMessage().contains("Not enough players to start the game"));
    }

    @Test
    @DisplayName("Should allow only host (first player) to start game")
    void testOnlyHostCanStartGame() throws Exception {
        // Add players
        loginState.login("Host");
        loginState.login("Player2");

        // Non-host tries to start
        InvalidParameters exception = assertThrows(InvalidParameters.class,
                () -> loginState.startGame("Player2"));
        assertTrue(exception.getMessage().contains("Only the admin can start the game"));
    }

    @Test
    @DisplayName("Should transition to BuildingState when game starts successfully")
    void testSuccessfulGameStart() throws Exception {
        // Add players
        loginState.login("Host");
        loginState.login("Player2");

        // Host starts the game
        loginState.startGame("Host");

        // Should be in BuildingState
        assertTrue(controller.getModel().getState() instanceof BuildingState);
    }

    // Edge Cases

    @Test
    @DisplayName("Should handle logout of non-existent player")
    void testLogoutNonExistentPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
                () -> loginState.logout("NonExistent"));
        assertTrue(exception.getMessage().contains("Player with this name not connected"));
    }

    @Test
    @DisplayName("Should maintain correct admin after first player logout")
    void testAdminTransferAfterLogout() throws Exception {
        // Add players
        loginState.login("FirstAdmin");
        loginState.login("SecondPlayer");
        loginState.login("ThirdPlayer");

        // First admin logs out
        loginState.logout("FirstAdmin");

        // Second player should now be admin
        assertEquals("SecondPlayer", controller.getModel().getPlayers().get(0).getName());

        // Second player should be able to start game
        assertDoesNotThrow(() -> loginState.startGame("SecondPlayer"));
    }

    @Test
    @DisplayName("Should handle rapid login/logout sequences")
    void testRapidLoginLogoutSequence() throws Exception {
        // Rapid operations
        loginState.login("Player1");
        loginState.login("Player2");
        loginState.logout("Player1");
        loginState.login("Player3");
        loginState.logout("Player2");
        loginState.login("Player1"); // Same name as before, should work now

        assertEquals(2, controller.getModel().getPlayers().size());
        assertNotNull(controller.getModel().getPlayer("Player1"));
        assertNotNull(controller.getModel().getPlayer("Player3"));
    }
}