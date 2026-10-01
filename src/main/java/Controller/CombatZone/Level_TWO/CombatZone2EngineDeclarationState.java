package Controller.CombatZone.Level_TWO;

import Controller.CombatZone.Level_ONE.CombatZone1CrewRemovalState;
import Controller.CombatZone.Level_ONE.CombatZone1EngineDeclarationState;
import Controller.CombatZone.Level_ONE.CombatZone1_E_BatteryRemovalState;
import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Player;

import java.util.List;

/**
 * Represents the state in which a player declares engine power during the combat zone phase of the game.
 *
 * <p>This state allows a player to declare the amount of engine power they wish to use,
 * and if the declaration is valid, it transitions to the next state for battery removal or crew removal.</p>
 */
public class CombatZone2EngineDeclarationState extends State {

    public CombatZone2EngineDeclarationState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * @deprecated the lowest declaration is now tracked by the context; worst is ignored
     */
    @Deprecated
    public CombatZone2EngineDeclarationState(Context context, double worst) {
        this(context);
    }

    /**
     * Allows a player to declare their total engine during the combat zone phase.
     * Validates the double type, amount, and player's turn before proceeding with the declaration.
     *
     * <p>If the declaration is valid, it transitions to the next state for battery removal or crew removal.</p>
     *
     * @param playerName the name of the player declaring the engine power
     * @param doubleType the type of double engine being declared (must be {@code DoubleType.ENGINES})
     * @param amount the amount of engine power being declared
     * @throws InvalidContextualAction if it is not the player's turn to declare engine power
     * @throws InvalidParameters if the double type is invalid or the amount is out of bounds
     */
    @Override
    public void declaresDouble(String playerName, DoubleType doubleType, double amount) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(doubleType != DoubleType.ENGINES){
            
            throw new InvalidParameters("Invalid double type, only ENGINES are allowed");
        }

        if(amount < 0){
            
            throw new InvalidParameters("Invalid amount of double, only non negative integers are allowed");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to throw the dice.");
        }


        int batteries = 0;
        double minPower = player.getShipBoard().getCondensedShip().getBaseThrust();
        double maxPower = player.getShipBoard().getCondensedShip().getMaxThrust();

        if (amount < minPower || amount > maxPower) {
            
            throw new InvalidParameters("Declared amount is out of bounds");
        }


        if ((amount % 1) != (minPower % 1)) {
            
            throw new InvalidParameters("Declared amount must match the ship's base power decimal part");
        }

        int delta = player.getShipBoard().getCondensedShip().declaredDoublesStrength(amount, true);

        if (delta < 0) {

            throw new InvalidParameters("Declared amount is out of bounds");

        }

        if(delta % 2 != 0) {
            
            throw new InvalidParameters("Cannot reach the declared amount with double engines");
        }

        int doubleRequired = delta / 2;

        if(player.getShipBoard().getCondensedShip().getEngines().getDoubleEngines()>=doubleRequired){
            batteries = doubleRequired;
        } else {
            
            throw new InvalidParameters("Not enough double engines to declare this amount");
        }

        if (amount == player.getShipBoard().getCondensedShip().getBaseThrust()) {
            // no double engine activated: the declaration is final
            finishDeclaration(context, player, amount);
        } else {
            controller.getModel().setState(new CombatZone2_E_BatteryRemovalState(context, amount, batteries));
        }
    }

    /**
     * Records a final engine power declaration. When everyone has declared, the weakest player loses goods.
     */
    static void finishDeclaration(Context context, Player player, double power) {
        final Controller controller = context.getController();
        context.recordDeclaration(player, power);
        context.removePlayer(player);
        if (context.getPlayers().isEmpty()) {
            controller.getModel().setState(new CombatZone2GoodsRemovalState(context));
        } else {
            controller.getModel().setState(new CombatZone2EngineDeclarationState(context));
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "DeclareEnginePower");
    }
}
