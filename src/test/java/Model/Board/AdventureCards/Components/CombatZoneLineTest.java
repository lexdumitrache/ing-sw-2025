package Model.Board.AdventureCards.Components;

import Model.Board.AdventureCards.Penalties.CrewPenalty;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Penalties.GoodsPenalty;
import Model.Board.AdventureCards.Penalties.Penalty;
import Model.Enums.Criteria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CombatZoneLineTest {

    @Test
    public void testConstructorWithDaysPenalty() {
        DaysPenalty penalty = new DaysPenalty(2);
        CombatZoneLine line = new CombatZoneLine(Criteria.FIRE_POWER, penalty);
        
        assertEquals(Criteria.FIRE_POWER, line.getOrderingCriteria());
        assertEquals(penalty, line.getPenalty());
        assertEquals(2, ((DaysPenalty)line.getPenalty()).getAmount());
    }
    
    @Test
    public void testConstructorWithCrewPenalty() {
        CrewPenalty penalty = new CrewPenalty(3);
        CombatZoneLine line = new CombatZoneLine(Criteria.MAN_POWER, penalty);
        
        assertEquals(Criteria.MAN_POWER, line.getOrderingCriteria());
        assertEquals(penalty, line.getPenalty());
        assertEquals(3, ((CrewPenalty)line.getPenalty()).getAmount());
    }
    
    @Test
    public void testConstructorWithGoodsPenalty() {
        GoodsPenalty penalty = new GoodsPenalty(1);
        CombatZoneLine line = new CombatZoneLine(Criteria.ENGINE_POWER, penalty);
        
        assertEquals(Criteria.ENGINE_POWER, line.getOrderingCriteria());
        assertEquals(penalty, line.getPenalty());
        assertEquals(1, ((GoodsPenalty)line.getPenalty()).getAmount());
    }

    @Test
    public void testGettersConsistency() {
        DaysPenalty penalty = new DaysPenalty(5);
        CombatZoneLine line = new CombatZoneLine(Criteria.FIRE_POWER, penalty);
        
        assertSame(penalty, line.getPenalty());
        assertSame(Criteria.FIRE_POWER, line.getOrderingCriteria());
        
        // Test multiple calls return same values
        assertEquals(line.getOrderingCriteria(), line.getOrderingCriteria());
        assertEquals(line.getPenalty(), line.getPenalty());
    }

    @Test
    public void testAllCriteriaTypes() {
        Penalty penalty = new DaysPenalty(1);
        
        for (Criteria criteria : Criteria.values()) {
            CombatZoneLine line = new CombatZoneLine(criteria, penalty);
            assertEquals(criteria, line.getOrderingCriteria());
            assertEquals(penalty, line.getPenalty());
        }
    }
}