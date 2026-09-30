package Controller.MeteorsSwarm;

import Controller.CombatZone.Level_ONE.CombatZone1ManageShotState;
import Controller.Context;
import Controller.Controller;
import Controller.Exceptions.InvalidContextualAction;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Projectiles.Projectile;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;

import java.util.List;
import java.util.Random;

/**
 * Represents the game state where a meteor storm is initiated.
 *
 * <p>During this state, a player rolls dice to determine the row or column for a meteor event.
 * All players are marked as involved in the special event, and the game transitions
 * to {@link ManageMeteorState} to handle the meteor impact resolution.</p>
 */
public class MeteorsState extends State {
    public MeteorsState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Executes the dice roll for a meteor storm event.
     * <p>
     * Validates that projectiles are available and that it is the correct player's turn before rolling two dice.
     * The sum of the dice determines the trajectory of the meteor. If the result falls outside the valid range
     * for the projectile's side, the meteor is considered out of bounds and removed.
     * The game state is then updated based on whether additional projectiles remain or not.
     *
     * @param playerName the name of the player attempting to roll the dice
     * @throws InvalidContextualAction if there are no projectiles available to shoot
     * @throws InvalidMethodParameters if the action is attempted by a player out of turn
     */
    @Override
    public void throwDices(String playerName) throws InvalidMethodParameters, InvalidContextualAction {
        Controller controller = context.getController();
        if(context.getProjectiles().isEmpty()) {
            
            throw new InvalidContextualAction("No projectiles available.");
        }
        Player player = controller.getModel().getPlayer(playerName);
        if (!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidMethodParameters("It's not your turn to throw the dice.");
        }
        Random rand = new Random();
        int dado1 = rand.nextInt(6) + 1; // numero tra 1 e 6
        int dado2 = rand.nextInt(6) + 1; // numero tra 1 e 6
        int number = dado1 + dado2;

        context.setDiceNumber(number);

        for(Player p : controller.getModel().getFlightBoard().getTurnOrder()){
            context.addSpecialPlayer(p);
        }

        Projectile meteor = context.getProjectiles().getFirst();
        boolean out = false;
        if(meteor.getSide() == Side.LEFT || meteor.getSide() == Side.RIGHT) {
            if(number > 9 || number < 5){      //fuori dalla griglia
                out = true;
            }
        } else {
            if(number > 10 || number < 4){     //fuori dalla griglia
                out = true;
            }
        }
        if(out) {
            context.removeProjectile(meteor);
            if(context.getProjectiles().isEmpty()) {
                controller.getModel().setState(new FlightPhase(controller));
                
            } else {
                controller.getModel().setState(new MeteorsState(context));
                
            }
        } else {
            controller.getModel().setState(new ManageMeteorState(context, number));
            
        }

    }

    public List<String> getAvailableCommands(){
        return List.of(
            "ThrowDices"
        );
    }
}
