package Controller.AbandonedStation;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Controller.GamePhases.FlightPhase;

import java.util.List;

/**
 * Represents the state in which players decide whether to accept or skip the reward
 * from encountering an abandoned station during the flight phase.
 *
 * <p>Players take turns deciding whether to accept a reward of type {@code GOODS}.
 * If all players skip the reward, the game resumes the normal flight phase.
 * If a player accepts the reward, they receive credits, lose days and the game transitions
 * to the {@link AbandonedStationLandState} where additional consequences may occur.</p>
 */
public class AbandonedStationDecidingState extends State {


    /**
     * Constructs the state with the given context.
     *
     * @param context the game context used to access controller and shared state.
     */
    public AbandonedStationDecidingState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Called when a player chooses to skip the abandoned station reward.
     *
     * <p>If it is the current player's turn, they are removed from the decision pool.
     * If all players skip, the game returns to the {@link FlightPhase}.
     * Otherwise, the decision process continues with the next player.</p>
     *
     * @param playerName the name of the player who skips the reward
     */
    @Override
    public void skipReward(String playerName) throws InvalidParameters {
        Controller controller = context.getController();
        Player currentPlayer = controller.getModel().getPlayer(playerName);
        if(!currentPlayer.equals(context.getPlayers().getFirst())){  //se è il suo turno
            
            throw new InvalidParameters("It's not your turn to skip the reward.");
        }

        context.removePlayer(currentPlayer);
        if(context.getPlayers().isEmpty()){         //se skippano tutti....
            controller.getModel().setState(new FlightPhase(controller));
            
        }
        else{
            controller.getModel().setState(new AbandonedStationDecidingState(context));
            
        }

    }

    /**
     * Called when a player chooses to accept the reward from the abandoned station.
     *
     * <p>If the reward type is {@code GOODS} and the player has enough total crew members
     * to claim it, the player receives credits, lose days and the game transitions to the
     * {@link AbandonedStationLandState}.</p>
     *
     * <p>If it's not the player's turn, or if the player lacks sufficient crew members,
     * the reward is not granted and an error should be displayed.</p>
     *
     * @param playerName the name of the player accepting the reward
     * @param rewardType the type of reward selected; must be {@code GOODS} to be processed
     */
    @Override
    public void getReward(String playerName, RewardType rewardType) throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(rewardType != RewardType.GOODS){
            
            throw new InvalidParameters("It's not your turn to skip the reward.");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to take the reward.");
        }

        if(player.getShipBoard().getCondensedShip().getTotalCrew() >= context.getCrewmates()){
            player.deltaCredits(context.getCredits());
            controller.getModel().getFlightBoard().deltaFlightDays(player, -context.getDaysLost());
            controller.getModel().setState(new AbandonedStationLandState(context));
            
        } else {
            
            throw new InvalidContextualAction("The player doesn't have enough crew to take the reward"); //handle the situation where the player doesn't have enough crew)
        }
    }

    @Override
    public List<String> getAvailableCommands(){
        return List.of(
            "SkipReward",
            "GetGoodReward"
        );
    }
}
