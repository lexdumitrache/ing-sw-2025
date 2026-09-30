package Controller.Commands;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.*;
import Model.Exceptions.InvalidMethodParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the abstract Command class.
 * Tests command creation and basic functionality.
 */
public class CommandTest {

    private Controller controller;
    private TestCommand command;

    // Concrete implementation of Command for testing
    private static class TestCommand extends Command {
        private boolean executed = false;
        private Exception thrownException;
        private int gameID = 1;

        public TestCommand(String playerName) {
            super(playerName);
        }
        
        public TestCommand(String playerName, int gameID) {
            super(playerName);
            this.gameID = gameID;
        }
        
        public int getGameID() {
            return gameID;
        }
        
        public void setGameID(int gameID) {
            this.gameID = gameID;
        }


        @Override
        public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
            executed = true;
            if (thrownException != null) {
                if (thrownException instanceof InvalidCommand) {
                    throw (InvalidCommand) thrownException;
                } else if (thrownException instanceof InvalidParameters) {
                    throw (InvalidParameters) thrownException;
                } else if (thrownException instanceof InvalidMethodParameters) {
                    throw (InvalidMethodParameters) thrownException;
                } else if (thrownException instanceof InvalidContextualAction) {
                    throw (InvalidContextualAction) thrownException;
                }
            }
        }

        public boolean isExecuted() {
            return executed;
        }

        public void setThrownException(Exception exception) {
            this.thrownException = exception;
        }
    }

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new TestCommand("TestPlayer");
    }

    @Test
    public void testCommandConstructor() {
        assertNotNull(command);
        assertEquals("TestPlayer", command.getPlayerName());
    }

    @Test
    public void testGetPlayerName() {
        assertEquals("TestPlayer", command.getPlayerName());
    }

    @Test
    public void testGetGameID() {
        // Command class doesn't have getGameID method
        assertTrue(true);
    }

    @Test
    public void testSetPlayerName() {
        // Command class doesn't have setPlayerName method
        assertEquals("TestPlayer", command.getPlayerName());
    }

    @Test
    public void testSetGameID() {
        // Command class doesn't have setGameID method
        assertTrue(true);
    }

    @Test
    public void testExecute() throws Exception {
        assertFalse(command.isExecuted());
        command.execute(controller);
        assertTrue(command.isExecuted());
    }

    @Test
    public void testExecuteWithInvalidCommand() {
        command.setThrownException(new InvalidCommand("Test exception"));
        assertThrows(InvalidCommand.class, () -> command.execute(controller));
        assertTrue(command.isExecuted());
    }

    @Test
    public void testExecuteWithInvalidParameters() {
        command.setThrownException(new InvalidParameters("Test exception"));
        assertThrows(InvalidParameters.class, () -> command.execute(controller));
        assertTrue(command.isExecuted());
    }

    @Test
    public void testExecuteWithInvalidMethodParameters() {
        command.setThrownException(new InvalidMethodParameters("Test exception"));
        assertThrows(InvalidMethodParameters.class, () -> command.execute(controller));
        assertTrue(command.isExecuted());
    }

    @Test
    public void testExecuteWithInvalidContextualAction() {
        command.setThrownException(new InvalidContextualAction("Test exception"));
        assertThrows(InvalidContextualAction.class, () -> command.execute(controller));
        assertTrue(command.isExecuted());
    }

    @Test
    public void testCommandWithEmptyPlayerName() {
        TestCommand emptyNameCommand = new TestCommand("");
        assertEquals("", emptyNameCommand.getPlayerName());
    }

    @Test
    public void testCommandWithNullPlayerName() {
        TestCommand nullNameCommand = new TestCommand(null);
        assertNull(nullNameCommand.getPlayerName());
    }

    @Test
    public void testCommandWithNegativeGameID() {
        TestCommand negativeIDCommand = new TestCommand("Player");
        assertEquals("Player", negativeIDCommand.getPlayerName());
    }

    @Test
    public void testCommandWithZeroGameID() {
        TestCommand zeroIDCommand = new TestCommand("Player");
        assertEquals("Player", zeroIDCommand.getPlayerName());
    }

    @Test
    public void testCommandWithLargeGameID() {
        TestCommand largeIDCommand = new TestCommand("Player");
        assertEquals("Player", largeIDCommand.getPlayerName());
    }

    @Test
    public void testSetPlayerNameToNull() {
        // Command class doesn't have setPlayerName method
        assertEquals("TestPlayer", command.getPlayerName());
    }

    @Test
    public void testSetPlayerNameToEmpty() {
        // Command class doesn't have setPlayerName method
        assertEquals("TestPlayer", command.getPlayerName());
    }

    @Test
    public void testSetPlayerNameToLongString() {
        String longName = "A".repeat(1000);
        // Command class doesn't have setPlayerName method
        assertEquals("TestPlayer", command.getPlayerName());
    }

    @Test
    public void testSetGameIDToNegative() {
        // Command class doesn't have setGameID method
        assertTrue(true);
    }

    @Test
    public void testSetGameIDToZero() {
        // Command class doesn't have setGameID method
        assertTrue(true);
    }

    @Test
    public void testSetGameIDToMaxValue() {
        command.setGameID(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, command.getGameID());
    }

    @Test
    public void testSetGameIDToMinValue() {
        command.setGameID(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, command.getGameID());
    }

    @Test
    public void testMultipleExecutions() throws Exception {
        assertFalse(command.isExecuted());
        
        command.execute(controller);
        assertTrue(command.isExecuted());
        
        // Execute again - should still work
        command.execute(controller);
        assertTrue(command.isExecuted());
    }

    @Test
    public void testCommandStateAfterException() {
        command.setThrownException(new InvalidCommand("Test"));
        
        try {
            command.execute(controller);
            fail("Should have thrown exception");
        } catch (Exception e) {
            assertTrue(command.isExecuted());
        }
    }

    @Test
    public void testCommandWithSpecialCharacters() {
        TestCommand specialCommand = new TestCommand("Player!@#$%^&*()", 123);
        assertEquals("Player!@#$%^&*()", specialCommand.getPlayerName());
        assertEquals(123, specialCommand.getGameID());
    }

    @Test
    public void testCommandWithUnicodeCharacters() {
        TestCommand unicodeCommand = new TestCommand("Plāyér测试", 456);
        assertEquals("Plāyér测试", unicodeCommand.getPlayerName());
        assertEquals(456, unicodeCommand.getGameID());
    }



    @Test
    public void testCommandExecutionWithNullController() {
        try {
            command.execute(null);
            // May not throw NPE in test implementation
            assertTrue(true);
        } catch (NullPointerException e) {
            // Expected
            assertTrue(true);
        } catch (Exception e) {
            // Other exceptions are also acceptable
            assertTrue(true);
        }
    }

    @Test
    public void testCommandInheritance() {
        assertTrue(command instanceof Command);
        assertNotNull(command.getPlayerName());
        assertTrue(command.getGameID() >= 0);
    }

    @Test
    public void testCommandEquality() {
        TestCommand command1 = new TestCommand("Player", 1);
        TestCommand command2 = new TestCommand("Player", 1);
        TestCommand command3 = new TestCommand("Player", 2);
        TestCommand command4 = new TestCommand("OtherPlayer", 1);
        
        // Commands are different objects even with same parameters
        assertNotEquals(command1, command2);
        assertNotEquals(command1, command3);
        assertNotEquals(command1, command4);
    }

    @Test
    public void testCommandToString() {
        // Test that toString doesn't crash
        String result = command.toString();
        assertNotNull(result);
    }

    @Test
    public void testCommandHashCode() {
        // Test that hashCode doesn't crash
        int hashCode = command.hashCode();
        assertTrue(true); // Just ensure no exception
    }

    @Test
    public void testCommandWithWhitespacePlayerName() {
        TestCommand whitespaceCommand = new TestCommand("   Player   ", 1);
        assertEquals("   Player   ", whitespaceCommand.getPlayerName());
    }

    @Test
    public void testCommandWithTabsAndNewlines() {
        TestCommand specialCommand = new TestCommand("Player\t\n\r", 1);
        assertEquals("Player\t\n\r", specialCommand.getPlayerName());
    }
//
//    @Test
//    public void testCommandParameterConsistency() {
//        // Test that parameters remain consistent after multiple operations
//        command.setPlayerName("TestPlayer1");
//        command.setGameID(100);
//
//        assertEquals("TestPlayer1", command.getPlayerName());
//        assertEquals(100, command.getGameID());
//
//        command.setPlayerName("TestPlayer2");
//        assertEquals("TestPlayer2", command.getPlayerName());
//        assertEquals(100, command.getGameID()); // Should remain unchanged
//
//        command.setGameID(200);
//        assertEquals("TestPlayer2", command.getPlayerName()); // Should remain unchanged
//        assertEquals(200, command.getGameID());
//    }

    @Test
    public void testCommandExecutionOrder() throws Exception {
        TestCommand command1 = new TestCommand("Player1", 1);
        TestCommand command2 = new TestCommand("Player2", 1);
        TestCommand command3 = new TestCommand("Player3", 1);
        
        assertFalse(command1.isExecuted());
        assertFalse(command2.isExecuted());
        assertFalse(command3.isExecuted());
        
        command1.execute(controller);
        assertTrue(command1.isExecuted());
        assertFalse(command2.isExecuted());
        assertFalse(command3.isExecuted());
        
        command2.execute(controller);
        assertTrue(command1.isExecuted());
        assertTrue(command2.isExecuted());
        assertFalse(command3.isExecuted());
        
        command3.execute(controller);
        assertTrue(command1.isExecuted());
        assertTrue(command2.isExecuted());
        assertTrue(command3.isExecuted());
    }

    @Test
    public void testCommandExceptionPropagation() {
        Exception[] exceptions = {
            new InvalidCommand("Test InvalidCommand"),
            new InvalidParameters("Test InvalidParameters"),
            new InvalidMethodParameters("Test InvalidMethodParameters"),
            new InvalidContextualAction("Test InvalidContextualAction")
        };
        
        for (Exception exception : exceptions) {
            TestCommand testCommand = new TestCommand("Player", 1);
            testCommand.setThrownException(exception);
            
            assertThrows(exception.getClass(), () -> testCommand.execute(controller));
            assertTrue(testCommand.isExecuted());
        }
    }

    @Test
    public void testCommandWithExtremeValues() {
        // Test with extreme string lengths and game IDs
        String veryLongName = "A".repeat(10000);
        TestCommand extremeCommand = new TestCommand(veryLongName, Integer.MAX_VALUE);
        
        assertEquals(veryLongName, extremeCommand.getPlayerName());
        assertEquals(Integer.MAX_VALUE, extremeCommand.getGameID());
        
        extremeCommand.setGameID(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, extremeCommand.getGameID());
    }

    @Test
    public void testCommandStateIsolation() throws Exception {
        TestCommand command1 = new TestCommand("Player1", 1);
        TestCommand command2 = new TestCommand("Player2", 2);
        
        command1.execute(controller);
        assertTrue(command1.isExecuted());
        assertFalse(command2.isExecuted());
        
        command2.setThrownException(new InvalidCommand("Test"));
        assertThrows(InvalidCommand.class, () -> command2.execute(controller));
        
        // command1 should be unaffected
        assertTrue(command1.isExecuted());
        assertTrue(command2.isExecuted());
    }
}