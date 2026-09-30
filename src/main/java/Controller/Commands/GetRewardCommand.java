package Controller.Commands;

import Controller.Controller;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Exceptions.InvalidMethodParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for claiming rewards from adventure cards.
 * Players can choose to claim goods or credits when eligible.
 */
public class GetRewardCommand extends Command {
    /** The type of reward to claim */
    private final RewardType rewardType;
    
    /**
     * Constructs a new GetRewardCommand.
     *
     * @param playerName the name of the player claiming the reward
     * @param rewardType the type of reward to claim
     */
    public GetRewardCommand(String playerName, RewardType rewardType) {
        super(playerName);
        this.rewardType = rewardType;
    }
    
    /**
     * Executes the get reward command by calling the controller's getReward method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        controller.getReward(getPlayerName(), rewardType);
    }
    
    /**
     * Gets the reward type.
     *
     * @return the reward type
     */
    public RewardType getRewardType() {
        return rewardType;
    }

    public static CommandConstructor getGoodsConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {

                return new GetRewardCommand(username, RewardType.GOODS);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }

    public static CommandConstructor getCreditsConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {

                return new GetRewardCommand(username, RewardType.CREDITS);
            }

            @Override
            public List<String> getArguments() {
                return List.of();
            }
        };
    }
}