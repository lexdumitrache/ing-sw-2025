package Controller.Commands;

import Controller.Controller;
import Controller.Enums.RewardType;
import Controller.Enums.MatchLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GetRewardCommandTest {

    private Controller controller;
    private GetRewardCommand command;

    @BeforeEach
    public void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        command = new GetRewardCommand("Anna", RewardType.GOODS);
    }

    @Test
    public void testConstructor() {
        assertNotNull(command);
        assertEquals("Anna", command.getPlayerName());
        assertEquals(RewardType.GOODS, command.getRewardType());
    }



    @Test
    public void testGetRewardType() {
        assertEquals(RewardType.GOODS, command.getRewardType());
    }

    @Test
    public void testCreditsReward() {
        GetRewardCommand creditsCommand = new GetRewardCommand("Anna", RewardType.CREDITS);
        assertEquals(RewardType.CREDITS, creditsCommand.getRewardType());
    }

    @Test
    public void testGetGoodsConstructor() {
        var constructor = GetRewardCommand.getGoodsConstructor();
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
    }

    @Test
    public void testGetCreditsConstructor() {
        var constructor = GetRewardCommand.getCreditsConstructor();
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
    }

    @Test
    public void testGoodsConstructorCreate() {
        var constructor = GetRewardCommand.getGoodsConstructor();
        var args = java.util.Map.<String, String>of();
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof GetRewardCommand);
        GetRewardCommand rewardCmd = (GetRewardCommand) created;
        assertEquals("Player1", rewardCmd.getPlayerName());
        assertEquals(RewardType.GOODS, rewardCmd.getRewardType());
    }

    @Test
    public void testCreditsConstructorCreate() {
        var constructor = GetRewardCommand.getCreditsConstructor();
        var args = java.util.Map.<String, String>of();
        Command created = constructor.create("Player1", args);
        
        assertNotNull(created);
        assertTrue(created instanceof GetRewardCommand);
        GetRewardCommand rewardCmd = (GetRewardCommand) created;
        assertEquals("Player1", rewardCmd.getPlayerName());
        assertEquals(RewardType.CREDITS, rewardCmd.getRewardType());
    }

    @Test
    public void testWithNullRewardType() {
        GetRewardCommand nullCommand = new GetRewardCommand("Anna", null);
        assertNull(nullCommand.getRewardType());
    }

    @Test
    public void testExecuteWithNullController() {
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void testInheritance() {
        assertTrue(command instanceof Command);
        assertTrue(command instanceof GetRewardCommand);
    }

    @Test
    public void testAllRewardTypes() {
        for (RewardType type : RewardType.values()) {
            GetRewardCommand typeCommand = new GetRewardCommand("Player", type);
            assertEquals(type, typeCommand.getRewardType());
        }
    }
}