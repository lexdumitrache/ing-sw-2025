package Controller.CombatZone.Level_TWO;

import Controller.CombatZone.Level_ONE.CombatZone1CannonShotsState;
import Controller.CombatZone.Level_ONE.CombatZone1ManageShotState;
import Controller.Context;
import Controller.Controller;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Projectiles.Projectile;
import Model.Enums.Side;
import Model.Player;

import java.util.List;
import java.util.Random;

/**
 * Represents the state in which a player may throw the dice to determine where the shot is aimed.
 *
 * <p>This state allows a player to roll dice to determine the outcome of cannon shots,
 * and if the shot is valid, it transitions to the next state to manage the shot.</p>
 */
public class CombatZone2CannonShotsState extends State {

    public CombatZone2CannonShotsState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    /**
     * Executes the dice roll for a cannon shot during the combat zone.
     * <p>
     * Validates that projectiles are available and that it is the correct player's turn before rolling two dice.
     * The sum of the dice determines the trajectory of the next projectile. If the result falls outside the valid range
     * for the projectile's side, the shot is considered out of bounds and the projectile is discarded.
     * The game state is then updated based on whether additional projectiles remain or not.
     *
     * @param playerName the name of the player attempting to roll the dice
     * @throws InvalidContextualAction if there are no projectiles available to shoot
     * @throws InvalidParameters if the action is attempted by a player out of turn
     */
    @Override
    public void throwDices(String playerName) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(context.getProjectiles().isEmpty()) {
            
            throw new InvalidContextualAction("No projectiles available for cannon shots.");
        }
        Player player = controller.getModel().getPlayer(playerName);
        if (!player.equals(context.getSpecialPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }
        Random rand = new Random();
        int dado1 = controller.getModel().rollDice(); // numero tra 1 e 6
        int dado2 = controller.getModel().rollDice(); // numero tra 1 e 6
        int number = dado1 + dado2;

        context.setDiceNumber(number);


        Projectile shot = context.getProjectiles().getFirst();
        boolean out = false;
        if(shot.getSide() == Side.LEFT || shot.getSide() == Side.RIGHT) {
            if(number > 9 || number < 5){      //fuori dalla griglia
                out = true;
            }
        } else {
            if(number > 10 || number < 4){     //fuori dalla griglia
                out = true;
            }
        }
        if(out) {
            context.removeProjectile(shot);
            if(context.getProjectiles().isEmpty()) {
                controller.getModel().setState(new FlightPhase(controller));
                
            } else {
                controller.getModel().setState(new CombatZone2CannonShotsState(context));
                
            }
        } else {
            controller.getModel().setState(new CombatZone2ManageShotState(context, number));
            
        }

    }

    public List<String> getAvailableCommands(){
        return List.of(
            "ThrowDices"
        );
    }
}
