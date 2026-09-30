package Controller.Commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CommandConstructor {
    /**
     * Method that creates a new Command based on a map of argName->argValue
     *
     * @param username
     * @param args     the map
     * @return Command
     * @throws IllegalArgumentException if there is something wrong with the arguments
     */
    Command create(String username, Map<String, String> args) throws IllegalArgumentException;

    /**
     *
     * @return list of argument names
     */
    List<String> getArguments();

    /**
     * Map that holds all commands that exist
     */
    Map<String, CommandConstructor> commandConstructors = new HashMap<>();

    static Map<String, CommandConstructor> getCommandConstructors(){
        if(commandConstructors.isEmpty()) {
            commandConstructors.putAll(Map.of(
                    "ChoosePlanet", ChoosePlanetCommand.getConstructor(),
                    "DeclareFirePower", DeclaresDoubleCommand.getCannonConstructor(),
                    "DeclareEnginePower", DeclaresDoubleCommand.getEngineConstructor(),
                    "DeleteComponent", DeleteComponentCommand.getConstructor(),
                    "End", EndCommand.getConstructor(),
                    "FinishBuilding", FinishBuildingCommand.getConstructor(),
                    "FlipHourGlass", FlipHourGlassCommand.getConstructor(),
                    "GetComponent", GetComponentCommand.getConstructor(),
                    "GetGood", GetGoodCommand.getConstructor(),
                    "GetGoodReward", GetRewardCommand.getGoodsConstructor()
            ));

            commandConstructors.putAll(Map.of(
                    "GetCreditsReward", GetRewardCommand.getCreditsConstructor(),
                    "LeaveRace", LeaveRaceCommand.getConstructor(),
                    "Leave", LogoutCommand.getConstructor(),
                    "LookDeck", LookDeckCommand.getConstructor(),
                    "MoveGood", MoveGoodCommand.getConstructor(),
                    "PickNextCard", PickNextCardCommand.getConstructor(),
                    "PlaceBrownAlien", PlaceCrewCommand.getBrownConstructor(),
                    "PlaceComponent", PlaceComponentCommand.getConstructor(),
                    "PlacePurpleAlien", PlaceCrewCommand.getPurpleConstructor(),
                    "PlaceHuman", PlaceCrewCommand.getHumanConstructor()
            ));

            commandConstructors.putAll(Map.of(
                    "ReserveComponent", ReserveComponentCommand.getConstructor(),
                    "SkipReward", SkipRewardCommand.getConstructor(),
                    "StartGame", StartGameCommand.getConstructor(),
                    "ThrowDices", ThrowDicesCommand.getConstructor(),
                    "UseBattery", UseItemCommand.getBatteriesConstructor(),
                    "UseCrew", UseItemCommand.getCrewConstructor(),
                    "PreBuiltShip", PreBuiltShipCommand.getConstructor(),
                    "RemoveGood", MoveGoodCommand.getRemoveConstructor()
            ));
        }


        return commandConstructors;
    }

    static CommandConstructor getCommandConstructor(String command) {
        return getCommandConstructors().get(command);
    }

}
