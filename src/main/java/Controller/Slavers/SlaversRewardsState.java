package Controller.Slavers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Controller.GamePhases.FlightPhase;

import java.util.List;

/**
 * This state manages the reward phase during the "Slavers" event, allowing players
 * who successfully surpassed the slavers' power to claim their reward (Credits),
 * or to skip it entirely.
 */
public class SlaversRewardsState extends State{
    /**
     * Constructor to initialize the state with the current game context.
     *
     * @param context the shared game context
     */
    public SlaversRewardsState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Allows the current player to claim their reward if it's of type {@code CREDITS}.
     * Upon claiming, the player's credits are increased by the amount specified in the context.
     * The player is then removed from the list of players yet to act, and the game proceeds:
     * <ul>
     *     <li>If special players remain, transition to {@link SlaversCrewRemovalState}</li>
     *     <li>Otherwise, the game continues to the {@link FlightPhase}</li>
     * </ul>
     *
     * @param playerName  the name of the player claiming the reward
     * @param rewardType  the type of reward to be claimed (must be {@code CREDITS})
     */
    @Override
    public void getReward(String playerName, RewardType rewardType) throws InvalidMethodParameters, InvalidParameters {
        Controller controller = context.getController();
        if (rewardType != RewardType.CREDITS) {
            
            throw new InvalidParameters("Invalid reward type, expected CREDITS");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if (!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }

        player.deltaCredits(context.getCredits());

        controller.getModel().getFlightBoard().deltaFlightDays(player, -context.getDaysLost());

        context.removePlayer(player);

        if(!context.getSpecialPlayers().isEmpty()){
            controller.getModel().setState(new SlaversCrewRemovalState(context));
            
        } else {
            controller.getModel().setState(new FlightPhase(controller));
            
        }
    }

    /**
     * Allows the current player to skip their reward.
     * The player is simply removed from the remaining players list.
     * The game proceeds similarly to {@link #getReward}, based on whether any special players remain.
     *
     * @param playerName the name of the player skipping the reward
     */
    @Override
    public void skipReward(String playerName) throws InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if (!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }
        context.removePlayer(player);

        if(!context.getSpecialPlayers().isEmpty()){
            controller.getModel().setState(new SlaversCrewRemovalState(context));
            
        } else {
            controller.getModel().setState(new FlightPhase(controller));
            
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "GetCreditsReward", "SkipReward" );
    }
}
