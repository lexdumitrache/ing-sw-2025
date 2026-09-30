package Model.Board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Timer class.
 * Tests phase progression, time management, and edge cases.
 */
public class TimerTest {

    @Test
    public void testConstructor() {
        Timer timer = new Timer();
        assertEquals(Timer.Phase.NOT_USED, timer.getPhase());
        assertEquals(0.0f, timer.getTimeLeft(), 0.001f);
    }
    
    @Test
    public void testNextPhase() {
        Timer timer = new Timer();
        assertEquals(Timer.Phase.NOT_USED, timer.getPhase());
        
        // Since time left is 0, we should be able to move to the next phase
        assertTrue(timer.nextPhase());
        assertEquals(Timer.Phase.START_PHASE, timer.getPhase());
        
        // Time left should now be greater than 0
        assertTrue(timer.getTimeLeft() > 0);
        
        // We shouldn't be able to move to the next phase until time is up
        assertFalse(timer.nextPhase());
    }
    
    @Test
    public void testPhaseProgression() {
        Timer timer = new Timer();
        
        // Test complete phase progression
        timer.nextPhase(); // Move to START_PHASE
        assertEquals(Timer.Phase.START_PHASE, timer.getPhase());
        
        // Use reflection to simulate time expiration
        expireTimer(timer);
        assertTrue(timer.nextPhase());
        assertEquals(Timer.Phase.MIDDLE_PHASE, timer.getPhase());
        
        // Move to LAST_PHASE
        expireTimer(timer);
        assertTrue(timer.nextPhase());
        assertEquals(Timer.Phase.LAST_PHASE, timer.getPhase());
        
        // Cannot advance beyond LAST_PHASE
        expireTimer(timer);
        assertFalse(timer.nextPhase());
        assertEquals(Timer.Phase.LAST_PHASE, timer.getPhase());
    }
    
    @Test
    public void testGetTimeLeft() {
        Timer timer = new Timer();
        assertEquals(0.0f, timer.getTimeLeft(), 0.001f);
        
        timer.nextPhase();
        assertTrue(timer.getTimeLeft() > 0);
        
        // Time should decrease as time passes
        float firstReading = timer.getTimeLeft();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Sleep interrupted");
        }
        float secondReading = timer.getTimeLeft();
        
        assertTrue(secondReading < firstReading);
    }
    
    /**
     * Tests timer behavior when time expires naturally.
     */
    @Test
    public void testTimeExpiration() {
        Timer timer = new Timer();
        timer.nextPhase();
        
        // Verify timer starts with positive time
        float initialTime = timer.getTimeLeft();
        assertTrue(initialTime > 0);
        
        // Expire the timer
        expireTimer(timer);
        
        // Time should be 0 or very close to 0
        assertTrue(timer.getTimeLeft() <= 0.1f);
        
        // Should be able to advance to next phase
        assertTrue(timer.nextPhase());
    }
    
    /**
     * Tests all timer phases exist and are in correct order.
     */
    @Test
    public void testAllPhases() {
        Timer.Phase[] phases = Timer.Phase.values();
        assertEquals(4, phases.length);
        assertEquals(Timer.Phase.NOT_USED, phases[0]);
        assertEquals(Timer.Phase.START_PHASE, phases[1]);
        assertEquals(Timer.Phase.MIDDLE_PHASE, phases[2]);
        assertEquals(Timer.Phase.LAST_PHASE, phases[3]);
    }
    
    /**
     * Tests timer integration with FlightBoard setUpcomingCardDeck logic.
     */
    @Test
    public void testTimerForFlightBoardIntegration() {
        Timer timer = new Timer();
        
        // Initial state - should not be ready for upcoming deck
        assertFalse(isReadyForUpcomingDeck(timer));
        
        // Move through phases
        timer.nextPhase(); // START_PHASE
        assertFalse(isReadyForUpcomingDeck(timer));
        
        expireTimer(timer);
        timer.nextPhase(); // MIDDLE_PHASE
        assertFalse(isReadyForUpcomingDeck(timer));
        
        expireTimer(timer);
        timer.nextPhase(); // LAST_PHASE
        assertFalse(isReadyForUpcomingDeck(timer)); // Still has time
        
        // Only when in LAST_PHASE with no time left
        expireTimer(timer);
        assertTrue(isReadyForUpcomingDeck(timer));
    }
    
    /**
     * Tests timer behavior with multiple rapid calls.
     */
    @Test
    public void testRapidNextPhaseCalls() {
        Timer timer = new Timer();
        
        // First call should succeed
        assertTrue(timer.nextPhase());
        assertEquals(Timer.Phase.START_PHASE, timer.getPhase());
        
        // Rapid subsequent calls should fail until time expires
        for (int i = 0; i < 10; i++) {
            assertFalse(timer.nextPhase());
            assertEquals(Timer.Phase.START_PHASE, timer.getPhase());
        }
        
        // After expiring, should advance
        expireTimer(timer);
        assertTrue(timer.nextPhase());
        assertEquals(Timer.Phase.MIDDLE_PHASE, timer.getPhase());
    }
    
    /**
     * Tests timer precision and consistency.
     */
    @Test
    public void testTimerPrecision() {
        Timer timer = new Timer();
        timer.nextPhase();
        
        // Multiple readings should be consistent and decreasing
        float[] readings = new float[5];
        for (int i = 0; i < readings.length; i++) {
            readings[i] = timer.getTimeLeft();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                fail("Sleep interrupted");
            }
        }
        
        // Each reading should be less than the previous (time decreases)
        for (int i = 1; i < readings.length; i++) {
            assertTrue(readings[i] <= readings[i-1], 
                "Time should decrease: " + readings[i-1] +" -> " + readings[i]);
        }
    }
    
    /**
     * Helper method to simulate timer expiration using reflection.
     */
    private void expireTimer(Timer timer) {
        try {
            java.lang.reflect.Field targetTimeField = Timer.class.getDeclaredField("targetTime");
            targetTimeField.setAccessible(true);
            targetTimeField.set(timer, System.currentTimeMillis() - 1000); // Set to past time
        } catch (Exception e) {
            fail("Failed to expire timer: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to check if timer is ready for upcoming deck (simulates FlightBoard logic).
     */
    private boolean isReadyForUpcomingDeck(Timer timer) {
        return timer.getPhase() == Timer.Phase.LAST_PHASE && timer.getTimeLeft() <= 0.0f;
    }
}