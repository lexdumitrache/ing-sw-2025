package Controller.Commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

public class CommandConstructorTest {

    @Test
    public void testGetCommandConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        assertNotNull(constructors);
        assertFalse(constructors.isEmpty());
    }

    @Test
    public void testAllCommandsPresent() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        
        // Test that all expected commands are present
        String[] expectedCommands = {
            "ChoosePlanet", "DeclareFirePower", "DeclareEnginePower", "DeleteComponent",
            "End", "FinishBuilding", "FlipHourGlass", "GetComponent", "GetGood",
            "GetGoodReward", "GetCreditsReward", "LeaveRace", "Leave", "LookDeck",
            "MoveGood", "PickNextCard", "PlaceBrownAlien", "PlaceComponent",
            "PlacePurpleAlien", "PlaceHuman", "ReserveComponent", "SkipReward",
            "StartGame", "ThrowDices", "UseBattery", "UseCrew", "PreBuiltShip",
            "RemoveGood"
        };
        
        for (String command : expectedCommands) {
            assertTrue(constructors.containsKey(command), "Missing command: " + command);
            assertNotNull(constructors.get(command), "Null constructor for: " + command);
        }
        
        assertEquals(expectedCommands.length, constructors.size());
    }

    @Test
    public void testConstructorsSingleton() {
        Map<String, CommandConstructor> constructors1 = CommandConstructor.getCommandConstructors();
        Map<String, CommandConstructor> constructors2 = CommandConstructor.getCommandConstructors();
        
        assertSame(constructors1, constructors2);
    }

    @Test
    public void testChoosePlanetConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("ChoosePlanet");
        
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("planetName"));
        
        Command command = constructor.create("Player1", Map.of("planetName", "Mars"));
        assertTrue(command instanceof ChoosePlanetCommand);
    }

    @Test
    public void testDeclareFirePowerConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("DeclareFirePower");
        
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("amount"));
        
        Command command = constructor.create("Player1", Map.of("amount", "2.0"));
        assertTrue(command instanceof DeclaresDoubleCommand);
    }

    @Test
    public void testDeclareEnginePowerConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("DeclareEnginePower");
        
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("amount"));
        
        Command command = constructor.create("Player1", Map.of("amount", "1.5"));
        assertTrue(command instanceof DeclaresDoubleCommand);
    }

    @Test
    public void testDeleteComponentConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("DeleteComponent");
        
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        
        Command command = constructor.create("Player1", Map.of("row", "3", "column", "4"));
        assertTrue(command instanceof DeleteComponentCommand);
    }

    @Test
    public void testEndConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("End");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof EndCommand);
    }

    @Test
    public void testFinishBuildingConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("FinishBuilding");
        
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("position"));
        
        Command command = constructor.create("Player1", Map.of("position", "1"));
        assertTrue(command instanceof FinishBuildingCommand);
    }

    @Test
    public void testFlipHourGlassConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("FlipHourGlass");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof FlipHourGlassCommand);
    }

    @Test
    public void testGetComponentConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("GetComponent");
        
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("index"));
        
        Command command = constructor.create("Player1", Map.of("index", "0"));
        assertTrue(command instanceof GetComponentCommand);
    }

    @Test
    public void testGetGoodConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("GetGood");
        
        assertNotNull(constructor);
        assertEquals(4, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("goodIndex"));
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        assertTrue(constructor.getArguments().contains("cargoHoldIndex"));
        
        Command command = constructor.create("Player1", Map.of(
            "goodIndex", "0", "row", "5", "column", "5", "cargoHoldIndex", "0"));
        assertTrue(command instanceof GetGoodCommand);
    }

    @Test
    public void testGetGoodRewardConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("GetGoodReward");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof GetRewardCommand);
    }

    @Test
    public void testGetCreditsRewardConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("GetCreditsReward");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof GetRewardCommand);
    }

    @Test
    public void testLeaveRaceConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("LeaveRace");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof LeaveRaceCommand);
    }

    @Test
    public void testLeaveConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("Leave");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof LogoutCommand);
    }

    @Test
    public void testLookDeckConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("LookDeck");
        
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("index"));
        
        Command command = constructor.create("Player1", Map.of("index", "0"));
        assertTrue(command instanceof LookDeckCommand);
    }

    @Test
    public void testMoveGoodConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("MoveGood");
        
        assertNotNull(constructor);
        assertEquals(6, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of(
            "oldRow", "1", "oldColumn", "2", "newRow", "3", 
            "newColumn", "4", "oldIndex", "0", "newIndex", "1"));
        assertTrue(command instanceof MoveGoodCommand);
    }

    @Test
    public void testPickNextCardConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("PickNextCard");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof PickNextCardCommand);
    }

    @Test
    public void testPlaceBrownAlienConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("PlaceBrownAlien");
        
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        
        Command command = constructor.create("Player1", Map.of("row", "7", "column", "7"));
        assertTrue(command instanceof PlaceCrewCommand);
    }

    @Test
    public void testPlaceComponentConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("PlaceComponent");
        
        assertNotNull(constructor);
        assertEquals(4, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("origin"));
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        assertTrue(constructor.getArguments().contains("orientation"));
        
        Command command = constructor.create("Player1", Map.of(
            "origin", "HAND", "row", "5", "column", "5", "orientation", "UP"));
        assertTrue(command instanceof PlaceComponentCommand);
    }

    @Test
    public void testPlacePurpleAlienConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("PlacePurpleAlien");
        
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        
        Command command = constructor.create("Player1", Map.of("row", "7", "column", "7"));
        assertTrue(command instanceof PlaceCrewCommand);
    }

    @Test
    public void testPlaceHumanConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("PlaceHuman");
        
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        
        Command command = constructor.create("Player1", Map.of("row", "7", "column", "7"));
        assertTrue(command instanceof PlaceCrewCommand);
    }

    @Test
    public void testReserveComponentConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("ReserveComponent");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof ReserveComponentCommand);
    }

    @Test
    public void testSkipRewardConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("SkipReward");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof SkipRewardCommand);
    }

    @Test
    public void testStartGameConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("StartGame");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof StartGameCommand);
    }

    @Test
    public void testThrowDicesConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("ThrowDices");
        
        assertNotNull(constructor);
        assertEquals(0, constructor.getArguments().size());
        
        Command command = constructor.create("Player1", Map.of());
        assertTrue(command instanceof ThrowDicesCommand);
    }

    @Test
    public void testUseBatteryConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("UseBattery");
        
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        
        Command command = constructor.create("Player1", Map.of("row", "5", "column", "5"));
        assertTrue(command instanceof UseItemCommand);
    }

    @Test
    public void testUseCrewConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("UseCrew");
        
        assertNotNull(constructor);
        assertEquals(2, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        
        Command command = constructor.create("Player1", Map.of("row", "7", "column", "7"));
        assertTrue(command instanceof UseItemCommand);
    }

    @Test
    public void testPreBuiltShipConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("PreBuiltShip");
        
        assertNotNull(constructor);
        assertEquals(1, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("index"));
        
        Command command = constructor.create("Player1", Map.of("index", "0"));
        assertTrue(command instanceof PreBuiltShipCommand);
    }

    @Test
    public void testRemoveGoodConstructor() {
        Map<String, CommandConstructor> constructors = CommandConstructor.getCommandConstructors();
        CommandConstructor constructor = constructors.get("RemoveGood");
        
        assertNotNull(constructor);
        assertEquals(3, constructor.getArguments().size());
        assertTrue(constructor.getArguments().contains("row"));
        assertTrue(constructor.getArguments().contains("column"));
        assertTrue(constructor.getArguments().contains("index"));
        
        Command command = constructor.create("Player1", Map.of("row", "5", "oldColumn", "6", "index", "0"));
        assertTrue(command instanceof MoveGoodCommand);
    }
}