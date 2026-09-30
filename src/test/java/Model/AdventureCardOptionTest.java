package Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdventureCardOptionTest {

    @Test
    public void testGetters() {
        AdventureCardOption option = new AdventureCardOption();
        
        assertNull(option.getLabel());
        assertNull(option.getConditions());
        assertNull(option.getRewards());
        assertNull(option.getPenalties());
    }
}