package Controller.Exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for all Controller exceptions.
 * Tests exception creation, inheritance, and message handling.
 */
public class ExceptionsTest {

    @Test
    public void testInvalidCommandException() {
        // Test constructor with message
        String message = "Test invalid command";
        InvalidCommand exception = new InvalidCommand(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        
        // Test inheritance
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof InvalidCommand);
    }

    @Test
    public void testInvalidParametersException() {
        // Test constructor with message
        String message = "Test invalid parameters";
        InvalidParameters exception = new InvalidParameters(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        
        // Test inheritance
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof InvalidParameters);
    }

    @Test
    public void testInvalidContextualActionException() {
        // Test constructor with message
        String message = "Test invalid contextual action";
        InvalidContextualAction exception = new InvalidContextualAction(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        
        // Test inheritance
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof InvalidContextualAction);
    }

    @Test
    public void testExceptionWithNullMessage() {
        // Test that exceptions handle null messages properly
        InvalidCommand exception1 = new InvalidCommand(null);
        assertNull(exception1.getMessage());
        
        InvalidParameters exception2 = new InvalidParameters(null);
        assertNull(exception2.getMessage());
        
        InvalidContextualAction exception3 = new InvalidContextualAction(null);
        assertNull(exception3.getMessage());
    }

    @Test
    public void testExceptionWithEmptyMessage() {
        // Test that exceptions handle empty messages properly
        String emptyMessage = "";
        
        InvalidCommand exception1 = new InvalidCommand(emptyMessage);
        assertEquals(emptyMessage, exception1.getMessage());
        
        InvalidParameters exception2 = new InvalidParameters(emptyMessage);
        assertEquals(emptyMessage, exception2.getMessage());
        
        InvalidContextualAction exception3 = new InvalidContextualAction(emptyMessage);
        assertEquals(emptyMessage, exception3.getMessage());
    }

    @Test
    public void testExceptionWithLongMessage() {
        // Test that exceptions handle very long messages
        String longMessage = "A".repeat(10000);
        
        InvalidCommand exception1 = new InvalidCommand(longMessage);
        assertEquals(longMessage, exception1.getMessage());
        
        InvalidParameters exception2 = new InvalidParameters(longMessage);
        assertEquals(longMessage, exception2.getMessage());
        
        InvalidContextualAction exception3 = new InvalidContextualAction(longMessage);
        assertEquals(longMessage, exception3.getMessage());
    }

    @Test
    public void testExceptionWithSpecialCharacters() {
        // Test that exceptions handle special characters in messages
        String specialMessage = "Test!@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        InvalidCommand exception1 = new InvalidCommand(specialMessage);
        assertEquals(specialMessage, exception1.getMessage());
        
        InvalidParameters exception2 = new InvalidParameters(specialMessage);
        assertEquals(specialMessage, exception2.getMessage());
        
        InvalidContextualAction exception3 = new InvalidContextualAction(specialMessage);
        assertEquals(specialMessage, exception3.getMessage());
    }

    @Test
    public void testExceptionWithUnicodeCharacters() {
        // Test that exceptions handle unicode characters in messages
        String unicodeMessage = "Test测试Тест🎮";
        
        InvalidCommand exception1 = new InvalidCommand(unicodeMessage);
        assertEquals(unicodeMessage, exception1.getMessage());
        
        InvalidParameters exception2 = new InvalidParameters(unicodeMessage);
        assertEquals(unicodeMessage, exception2.getMessage());
        
        InvalidContextualAction exception3 = new InvalidContextualAction(unicodeMessage);
        assertEquals(unicodeMessage, exception3.getMessage());
    }

    @Test
    public void testExceptionWithNewlinesAndTabs() {
        // Test that exceptions handle newlines and tabs in messages
        String formattedMessage = "Line 1\nLine 2\tTabbed";
        
        InvalidCommand exception1 = new InvalidCommand(formattedMessage);
        assertEquals(formattedMessage, exception1.getMessage());
        
        InvalidParameters exception2 = new InvalidParameters(formattedMessage);
        assertEquals(formattedMessage, exception2.getMessage());
        
        InvalidContextualAction exception3 = new InvalidContextualAction(formattedMessage);
        assertEquals(formattedMessage, exception3.getMessage());
    }

    @Test
    public void testExceptionThrowing() {
        // Test that exceptions can be thrown and caught properly
        String message = "Test exception throwing";
        
        // Test InvalidCommand
        assertThrows(InvalidCommand.class, () -> {
            throw new InvalidCommand(message);
        });
        
        try {
            throw new InvalidCommand(message);
        } catch (InvalidCommand e) {
            assertEquals(message, e.getMessage());
        }
        
        // Test InvalidParameters
        assertThrows(InvalidParameters.class, () -> {
            throw new InvalidParameters(message);
        });
        
        try {
            throw new InvalidParameters(message);
        } catch (InvalidParameters e) {
            assertEquals(message, e.getMessage());
        }
        
        // Test InvalidContextualAction
        assertThrows(InvalidContextualAction.class, () -> {
            throw new InvalidContextualAction(message);
        });
        
        try {
            throw new InvalidContextualAction(message);
        } catch (InvalidContextualAction e) {
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void testExceptionCatchingAsException() {
        // Test that custom exceptions can be caught as generic Exception
        String message = "Test generic catching";
        
        try {
            throw new InvalidCommand(message);
        } catch (Exception e) {
            assertTrue(e instanceof InvalidCommand);
            assertEquals(message, e.getMessage());
        }
        
        try {
            throw new InvalidParameters(message);
        } catch (Exception e) {
            assertTrue(e instanceof InvalidParameters);
            assertEquals(message, e.getMessage());
        }
        
        try {
            throw new InvalidContextualAction(message);
        } catch (Exception e) {
            assertTrue(e instanceof InvalidContextualAction);
            assertEquals(message, e.getMessage());
        }
    }

    @Test
    public void testExceptionStackTrace() {
        // Test that exceptions have proper stack traces
        try {
            throw new InvalidCommand("Test stack trace");
        } catch (InvalidCommand e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            assertNotNull(stackTrace);
            assertTrue(stackTrace.length > 0);
            assertEquals("testExceptionStackTrace", stackTrace[0].getMethodName());
        }
    }

    @Test
    public void testExceptionCause() {
        // Test exception chaining
        Exception cause = new RuntimeException("Root cause");
        
        InvalidCommand exception1 = new InvalidCommand("Wrapper exception");
        exception1.initCause(cause);
        assertEquals(cause, exception1.getCause());
        
        InvalidParameters exception2 = new InvalidParameters("Wrapper exception");
        exception2.initCause(cause);
        assertEquals(cause, exception2.getCause());
        
        InvalidContextualAction exception3 = new InvalidContextualAction("Wrapper exception");
        exception3.initCause(cause);
        assertEquals(cause, exception3.getCause());
    }

    @Test
    public void testExceptionSuppressed() {
        // Test suppressed exceptions
        InvalidCommand mainException = new InvalidCommand("Main exception");
        InvalidParameters suppressedException = new InvalidParameters("Suppressed exception");
        
        mainException.addSuppressed(suppressedException);
        
        Throwable[] suppressed = mainException.getSuppressed();
        assertEquals(1, suppressed.length);
        assertEquals(suppressedException, suppressed[0]);
    }

    @Test
    public void testExceptionToString() {
        // Test toString method
        String message = "Test toString";
        
        InvalidCommand exception1 = new InvalidCommand(message);
        String toString1 = exception1.toString();
        assertNotNull(toString1);
        assertTrue(toString1.contains("InvalidCommand"));
        assertTrue(toString1.contains(message));
        
        InvalidParameters exception2 = new InvalidParameters(message);
        String toString2 = exception2.toString();
        assertNotNull(toString2);
        assertTrue(toString2.contains("InvalidParameters"));
        assertTrue(toString2.contains(message));
        
        InvalidContextualAction exception3 = new InvalidContextualAction(message);
        String toString3 = exception3.toString();
        assertNotNull(toString3);
        assertTrue(toString3.contains("InvalidContextualAction"));
        assertTrue(toString3.contains(message));
    }

    @Test
    public void testExceptionEquality() {
        // Test that exceptions with same message are not equal (different instances)
        String message = "Test equality";
        
        InvalidCommand exception1 = new InvalidCommand(message);
        InvalidCommand exception2 = new InvalidCommand(message);
        
        assertNotEquals(exception1, exception2);
        assertEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    public void testExceptionHashCode() {
        // Test that hashCode doesn't crash
        InvalidCommand exception1 = new InvalidCommand("Test");
        InvalidParameters exception2 = new InvalidParameters("Test");
        InvalidContextualAction exception3 = new InvalidContextualAction("Test");
        
        int hash1 = exception1.hashCode();
        int hash2 = exception2.hashCode();
        int hash3 = exception3.hashCode();
        
        // Just ensure no exception is thrown
        assertTrue(true);
    }

    @Test
    public void testExceptionSerialization() {
        // Test that exceptions can be serialized (implement Serializable)
        InvalidCommand exception1 = new InvalidCommand("Test serialization");
        InvalidParameters exception2 = new InvalidParameters("Test serialization");
        InvalidContextualAction exception3 = new InvalidContextualAction("Test serialization");
        
        // Exceptions should be serializable through Exception class
        assertTrue(exception1 instanceof java.io.Serializable);
        assertTrue(exception2 instanceof java.io.Serializable);
        assertTrue(exception3 instanceof java.io.Serializable);
    }

    @Test
    public void testExceptionInheritanceHierarchy() {
        // Test complete inheritance hierarchy
        InvalidCommand exception1 = new InvalidCommand("Test message");
        assertTrue(exception1 instanceof InvalidCommand);
        assertTrue(exception1 instanceof Exception);
        assertTrue(exception1 instanceof Throwable);
        assertTrue(exception1 instanceof Object);
        
        InvalidParameters exception2 = new InvalidParameters("Test message");
        assertTrue(exception2 instanceof InvalidParameters);
        assertTrue(exception2 instanceof Exception);
        assertTrue(exception2 instanceof Throwable);
        assertTrue(exception2 instanceof Object);
        
        InvalidContextualAction exception3 = new InvalidContextualAction("Test message");
        assertTrue(exception3 instanceof InvalidContextualAction);
        assertTrue(exception3 instanceof Exception);
        assertTrue(exception3 instanceof Throwable);
        assertTrue(exception3 instanceof Object);
    }

    @Test
    public void testExceptionClassNames() {
        // Test that class names are correct
        InvalidCommand exception1 = new InvalidCommand("Test message");
        assertEquals("InvalidCommand", exception1.getClass().getSimpleName());
        assertEquals("Controller.Exceptions.InvalidCommand", exception1.getClass().getName());
        
        InvalidParameters exception2 = new InvalidParameters("Test message");
        assertEquals("InvalidParameters", exception2.getClass().getSimpleName());
        assertEquals("Controller.Exceptions.InvalidParameters", exception2.getClass().getName());
        
        InvalidContextualAction exception3 = new InvalidContextualAction("Test message");
        assertEquals("InvalidContextualAction", exception3.getClass().getSimpleName());
        assertEquals("Controller.Exceptions.InvalidContextualAction", exception3.getClass().getName());
    }

    @Test
    public void testExceptionMultipleCatch() {
        // Test that exceptions can be caught in multiple catch blocks
        String message = "Test multiple catch";
        
        try {
            throw new InvalidCommand(message);
        } catch (InvalidCommand e) {
            assertEquals(message, e.getMessage());
        } catch (Exception e) {
            fail("Should have been caught by InvalidCommand catch block");
        }
        
        try {
            throw new InvalidParameters(message);
        } catch (InvalidParameters e) {
            assertEquals(message, e.getMessage());
        } catch (Exception e) {
            fail("Should have been caught by InvalidParameters catch block");
        }
        
        try {
            throw new InvalidContextualAction(message);
        } catch (InvalidContextualAction e) {
            assertEquals(message, e.getMessage());
        } catch (Exception e) {
            fail("Should have been caught by InvalidContextualAction catch block");
        }
    }

    @Test
    public void testExceptionRethrow() {
        // Test that exceptions can be rethrown
        String message = "Test rethrow";
        
        assertThrows(InvalidCommand.class, () -> {
            try {
                throw new InvalidCommand(message);
            } catch (InvalidCommand e) {
                assertEquals(message, e.getMessage());
                throw e; // Rethrow
            }
        });
        
        assertThrows(InvalidParameters.class, () -> {
            try {
                throw new InvalidParameters(message);
            } catch (InvalidParameters e) {
                assertEquals(message, e.getMessage());
                throw e; // Rethrow
            }
        });
        
        assertThrows(InvalidContextualAction.class, () -> {
            try {
                throw new InvalidContextualAction(message);
            } catch (InvalidContextualAction e) {
                assertEquals(message, e.getMessage());
                throw e; // Rethrow
            }
        });
    }

    @Test
    public void testExceptionFinally() {
        // Test that finally blocks execute with exceptions
        boolean[] finallyExecuted = {false, false, false};
        
        try {
            throw new InvalidCommand("Test finally");
        } catch (InvalidCommand e) {
            // Caught
        } finally {
            finallyExecuted[0] = true;
        }
        
        try {
            throw new InvalidParameters("Test finally");
        } catch (InvalidParameters e) {
            // Caught
        } finally {
            finallyExecuted[1] = true;
        }
        
        try {
            throw new InvalidContextualAction("Test finally");
        } catch (InvalidContextualAction e) {
            // Caught
        } finally {
            finallyExecuted[2] = true;
        }
        
        assertTrue(finallyExecuted[0]);
        assertTrue(finallyExecuted[1]);
        assertTrue(finallyExecuted[2]);
    }
}