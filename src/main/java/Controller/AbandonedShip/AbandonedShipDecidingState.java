package Controller.AbandonedShip;

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
 * Represents the state where players decide whether to accept or skip the reward
 * from encountering an abandoned ship during the flight phase.
 *
 * <p>This state handles the logic for players making decisions about whether to take
 * a reward or skip it. If all players skip, the game continues with the flight phase.
 * If a player chooses to take the reward, they gain credits, lose days and the state transitions
 * to a new state for crew removal.</p>
 *
 * <p>Expected usage within a game state machine, with transitions depending on
 * player interactions.</p>
 */

public class AbandonedShipDecidingState extends State {


    /**
     * Constructs the state with the given context.
     *
     * @param context the game context used to access controller and shared state.
     */
    public AbandonedShipDecidingState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Called when a player decides to skip the abandoned ship reward.
     *
     * <p>If it's the player's turn, they are removed from the decision pool.
     * If all players skip, the game returns to the normal flight phase.
     * Otherwise, the next player is prompted.</p>
     *
     * @param playerName the name of the player choosing to skip the reward.
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
            controller.getModel().setState(new AbandonedShipDecidingState(context));
            
        }

    }

    /**
     * Called when a player decides to accept the abandoned ship reward.
     *
     * <p>The player receives credits from the context and the game transitions
     * to the {@link AbandonedShipCrewRemovalState}.</p>
     *
     * @param playerName the name of the player choosing to take the reward.
     * @param rewardType the type of reward chosen (has to be credits).
     */
    @Override
    public void getReward(String playerName, RewardType rewardType) throws InvalidMethodParameters, InvalidParameters {

        Controller controller = context.getController();
        if(rewardType != RewardType.CREDITS){
            
            throw new InvalidParameters("Invalid reward type. Only credits are accepted.");
        }
        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to take the reward.");
        }
        if(player.getShipBoard().getCondensedShip().getTotalCrew() < context.getCrewmates()){
            
            throw new InvalidParameters("The player doesn't have enough crew");
        }
        player.deltaCredits(context.getCredits());

        controller.getModel().getFlightBoard().deltaFlightDays(player, -context.getDaysLost());

        controller.getModel().setState(new AbandonedShipCrewRemovalState(context));
        
    }

    @Override
    public List<String> getAvailableCommands(){
        return List.of(
            "SkipReward",
            "GetCreditsReward"
        );
    }
}
