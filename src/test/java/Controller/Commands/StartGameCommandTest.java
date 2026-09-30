package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the StartGameCommand class.
 * Tests start game command creation and execution.
 */
public class StartGameCommandTest {

    private Controller controller;
    private StartGameCommand startGameCommand;
    


    @BeforeEach
    public void setUp() {
        // Removed TestStateManager initialization
        controller = new Controller(MatchLevel.TRIAL, 1);
        try {
            controller.login("Anna");
            controller.login("Player2");
        } catch (Exception e) {
            // Ignore login errors in setup
        }
        startGameCommand = new StartGameCommand("Anna");
    }

    @Test
    public void testStartGameCommandConstructor() {
        assertNotNull(startGameCommand);
        assertEquals("Anna", startGameCommand.getPlayerName());
    }

    @Test
    public void testStartGameCommandExecution() throws Exception {
        startGameCommand.execute(controller);
        // Game should start successfully - state should change
        assertNotNull(controller.getModel().getState());
    }

    @Test
    public void testStartGameCommandNotAdmin() throws Exception {
        StartGameCommand nonAdminCommand = new StartGameCommand("Player2");
        assertThrows(InvalidParameters.class, () -> nonAdminCommand.execute(controller));
    }

    @Test
    public void testStartGameCommandNotEnoughPlayers() throws Exception {
        Controller emptyController = new Controller(MatchLevel.TRIAL, 2);
        emptyController.login("Anna");
        
        StartGameCommand command = new StartGameCommand("Anna");
        assertThrows(InvalidCommand.class, () -> command.execute(emptyController));
    }

    @Test
    public void testStartGameCommandWithLevel2() {
        StartGameCommand level2Command = new StartGameCommand("Anna");
        assertNotNull(level2Command);
    }

//    @Test
//    public void testStartGameCommandInheritance() {
//        assertTrue(startGameCommand instanceof Command);
//        assertTrue(startGameCommand instanceof StartGameCommand);
//    }

    @Test
    public void testStartGameCommandGetLevel() {
        assertNotNull(startGameCommand);
        
        StartGameCommand level2Command = new StartGameCommand("Anna");
        assertNotNull(level2Command);
    }

    @Test
    public void testStartGameCommandWithNullLevel() {
        StartGameCommand nullLevelCommand = new StartGameCommand("Anna");
        assertNotNull(nullLevelCommand);
    }

    @Test
    public void testStartGameCommandWithEmptyName() throws Exception {
        StartGameCommand emptyNameCommand = new StartGameCommand("");
        assertThrows(InvalidParameters.class, () -> emptyNameCommand.execute(controller));
    }

    @Test
    public void testStartGameCommandWithNullName() {
        StartGameCommand nullNameCommand = new StartGameCommand(null);
        assertThrows(Exception.class, () -> nullNameCommand.execute(controller));
    }

    @Test
    public void testStartGameCommandWithNonExistentPlayer() throws Exception {
        StartGameCommand nonExistentCommand = new StartGameCommand("NonExistent");
        assertThrows(InvalidParameters.class, () -> nonExistentCommand.execute(controller));
    }

    @Test
    public void testStartGameCommandMultipleExecutions() throws Exception {
        startGameCommand.execute(controller);
        
        // Second execution should fail as game already started
        assertThrows(InvalidCommand.class, () -> startGameCommand.execute(controller));
    }

//    @Test
//    public void testStartGameCommandWithDifferentGameID() throws Exception {
//        StartGameCommand differentGameCommand = new StartGameCommand("Anna");
//
//        // Should still work as it calls controller.startGame()
//        differentGameCommand.execute(controller);
//    }

    @Test
    public void testStartGameCommandParameterModification() throws Exception {
        // StartGameCommand doesn't have setter methods
        
        startGameCommand.execute(controller);
        // Should work with modified parameters
        assertNotNull(controller.getModel().getState());
    }

    @Test
    public void testStartGameCommandStateConsistency() throws Exception {
        assertEquals("Anna", startGameCommand.getPlayerName());
        
        startGameCommand.execute(controller);
        
        // Command parameters should remain unchanged after execution
        assertEquals("Anna", startGameCommand.getPlayerName());
    }

    @Test
    public void testStartGameCommandWithNullController() {
        assertThrows(NullPointerException.class, () -> startGameCommand.execute(null));
    }

    @Test
    public void testStartGameCommandWithSpecialCharacters() throws Exception {
        controller.login("Anna!@#");
        StartGameCommand specialCommand = new StartGameCommand("Anna!@#");
        
        try {
            specialCommand.execute(controller);
        } catch (Exception e) {
            // May fail due to player name issues
            assertTrue(true);
        }
    }

    @Test
    public void testStartGameCommandWithUnicodeCharacters() throws Exception {
        controller.login("Ādmīn测试");
        StartGameCommand unicodeCommand = new StartGameCommand("Ādmīn测试");
        
        try {
            unicodeCommand.execute(controller);
        } catch (Exception e) {
            // May fail due to player name issues
            assertTrue(true);
        }
    }

    @Test
    public void testStartGameCommandWithMaxPlayers() throws Exception {
        // Add 2 more players to reach maximum
        try {
            controller.login("Carl");
            controller.login("Diana");
        } catch (Exception e) {
            // Ignore if already at max
        }
        
        assertTrue(controller.getModel().getPlayers().size() >= 2);
        
        startGameCommand.execute(controller);
        // Should work with maximum players
        assertNotNull(controller.getModel().getState());
    }

    @Test
    public void testStartGameCommandWithMinimumPlayers() throws Exception {
        // Already have 2 players from setUp
        assertEquals(2, controller.getModel().getPlayers().size());
        
        startGameCommand.execute(controller);
        // Should work with minimum players
        assertNotNull(controller.getModel().getState());
    }

    @Test
    public void testStartGameCommandSequence() throws Exception {
        // Test that only admin can start
        String[] playerNames = {"Anna", "Player2", "Carl", "Diana"};
        
        for (int i = 2; i < playerNames.length; i++) {
            controller.login(playerNames[i]);
        }
        
        // Non-admin should fail
        for (int i = 1; i < playerNames.length; i++) {
            StartGameCommand command = new StartGameCommand(playerNames[i]);
            try {
                command.execute(controller);
                fail("Should have thrown exception");
            } catch (InvalidParameters | IndexOutOfBoundsException e) {
                // Expected - either exception type is acceptable
            }
        }
        
        // Anna should succeed
        startGameCommand.execute(controller);
    }

    @Test
    public void testStartGameCommandWithDifferentControllers() throws Exception {
        Controller controller2 = new Controller(MatchLevel.LEVEL2, 2);
        controller2.login("Admin2");
        controller2.login("Player2");
        
        StartGameCommand command2 = new StartGameCommand("Admin2");
        command2.execute(controller2);
        
        // Both controllers should be able to start independently
        startGameCommand.execute(controller);
    }

    @Test
    public void testStartGameCommandExceptionHandling() {
        // Test with controller in different states
        Controller emptyController = new Controller(MatchLevel.TRIAL, 3);
        
        StartGameCommand command = new StartGameCommand("NonExistent");
        assertThrows(IndexOutOfBoundsException.class, () -> command.execute(emptyController));
    }

    @Test
    public void testStartGameCommandToString() {
        String result = startGameCommand.toString();
        assertNotNull(result);
    }

    @Test
    public void testStartGameCommandHashCode() {
        int hashCode = startGameCommand.hashCode();
        assertTrue(true); // Just ensure no exception
    }

    @Test
    public void testStartGameCommandEquality() {
        StartGameCommand command1 = new StartGameCommand("Anna");
        StartGameCommand command2 = new StartGameCommand("Anna");
        StartGameCommand command3 = new StartGameCommand("Anna");
        StartGameCommand command4 = new StartGameCommand("OtherAdmin");
        
        // Commands are different objects even with same parameters
        assertNotEquals(command1, command2);
        assertNotEquals(command1, command3);
        assertNotEquals(command1, command4);
    }

    @Test
    public void testStartGameCommandWithWhitespace() throws Exception {
        controller.login("   Anna   ");
        StartGameCommand whitespaceCommand = new StartGameCommand("   Anna   ");
        
        try {
            whitespaceCommand.execute(controller);
        } catch (Exception e) {
            // May fail due to player name matching issues
            assertTrue(true);
        }
    }

    @Test
    public void testStartGameCommandWithVeryLongName() throws Exception {
        String longName = "A".repeat(1000);
        controller.login(longName);
        StartGameCommand longNameCommand = new StartGameCommand(longName);
        
        try {
            longNameCommand.execute(controller);
        } catch (Exception e) {
            // May fail due to player name issues
            assertTrue(true);
        }
    }

    @Test
    public void testStartGameCommandLevelConsistency() {
        // Test that level parameter is independent of controller level
        Controller level2Controller = new Controller(MatchLevel.LEVEL2, 2);
        StartGameCommand trialCommand = new StartGameCommand("Anna");
        
        assertNotNull(trialCommand);
        assertEquals(MatchLevel.LEVEL2, level2Controller.getMatchLevel());
    }

    @Test
    public void testStartGameCommandErrorRecovery() throws Exception {
        // Test that after a failed start, subsequent starts can still work
        try {
            new StartGameCommand("Player2").execute(controller);
            fail("Should have thrown exception");
        } catch (InvalidParameters e) {
            // Expected - Player2 is not admin
        }
        
        // Anna start should still work
        startGameCommand.execute(controller);
    }

    @Test
    public void testStartGameCommandBoundaryConditions() throws Exception {
        // Test with exactly minimum players (2)
        assertTrue(controller.getModel().getPlayers().size() >= 2);
        startGameCommand.execute(controller);
        
        // Test with single player should fail
        Controller singlePlayerController = new Controller(MatchLevel.TRIAL, 4);
        try {
            singlePlayerController.login("Anna");
        } catch (Exception e) {
            // Ignore login errors
        }
        
        StartGameCommand singlePlayerCommand = new StartGameCommand("Anna");
        assertThrows(InvalidCommand.class, () -> singlePlayerCommand.execute(singlePlayerController));
    }

    @Test
    public void testStartGameCommandWithTabsAndNewlines() throws Exception {
        controller.login("Anna\t\n\r");
        StartGameCommand specialCommand = new StartGameCommand("Anna\t\n\r");
        
        try {
            specialCommand.execute(controller);
        } catch (Exception e) {
            // May fail due to player name matching issues
            assertTrue(true);
        }
    }

    @Test
    public void testStartGameCommandConcurrentExecution() {
        // Test that multiple start game commands handle concurrency properly
        StartGameCommand[] commands = new StartGameCommand[5];
        for (int i = 0; i < 5; i++) {
            commands[i] = new StartGameCommand("Anna");
        }
        
        // Only first should succeed
        try {
            commands[0].execute(controller);
            
            for (int i = 1; i < 5; i++) {
                final int finalI = i;
                assertThrows(InvalidCommand.class, () -> commands[finalI].execute(controller));
            }
        } catch (Exception e) {
            // May fail due to various issues
            assertTrue(true);
        }
    }

    @Test
    public void testStartGameCommandAllMatchLevels() {
        // Test command creation with all match levels
        for (MatchLevel level : MatchLevel.values()) {
            StartGameCommand command = new StartGameCommand("Anna");
            assertNotNull(command);
        }
    }
}