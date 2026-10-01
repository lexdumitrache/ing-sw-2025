package Controller.CombatZone.Level_TWO;

import Controller.GamePhases.FlightPhase;
import Controller.CombatZone.Level_ONE.CombatZone1_P_BatteryRemovalState;
import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;

import java.util.List;

/**
 * Represents the state in which a player declares their firepower during the combat zone.
 *
 * <p>This state allows a player to declare the amount of firepower they wish to use,
 * and if the declaration is valid, it transitions to the next state for battery removal or cannon shots.</p>
 */
public class CombatZone2PowerDeclarationState extends State {

    public CombatZone2PowerDeclarationState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * @deprecated the lowest declaration is now tracked by the context; worst is ignored
     */
    @Deprecated
    public CombatZone2PowerDeclarationState(Context context, double worst) {
        this(context);
    }

    /**
     * Handles a player's declaration of fire power during the combat phase.
     * <p>
     * Validates the double type, the player's turn, and the declared amount against the ship's capabilities.
     * Calculates the number of batteries required based on the number of available front and other double cannons.
     * If the player has enough batteries and cannons to support the declaration, transitions to the appropriate game state.
     * Updates the special player list and tracks the worst declared power when applicable.
     *
     * @param playerName the name of the player declaring power
     * @param doubleType the type of double declaration (must be {@code DoubleType.CANNONS})
     * @param amount the total amount of power being declared
     * @throws InvalidMethodParameters if any parameters are structurally invalid (e.g., negative amount)
     * @throws InvalidContextualAction if the action is not allowed in the current game context
     * @throws InvalidParameters if the declaration violates game rules (e.g., out-of-bounds value, not enough cannons or batteries)
     */
    @Override
    public void declaresDouble(String playerName, DoubleType doubleType, double amount) throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {

        Controller controller = context.getController();
        if (doubleType != DoubleType.CANNONS) {
            
            throw new InvalidParameters("Invalid double type, expected CANNONS");
        }

        if (amount < 0) {
            
            throw new InvalidParameters("Negative amount");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if (!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }


        int batteries = 0;
        double minPower = player.getShipBoard().getCondensedShip().getBasePower();
        double maxPower = player.getShipBoard().getCondensedShip().getMaxPower();

        if (amount < minPower || amount > maxPower) {
            
            throw new InvalidParameters("Declared amount is out of bounds");
        }


        if ((amount % 1) != (minPower % 1)) {
            
            throw new InvalidParameters("Declared amount must match the ship's base power decimal part");
        }

        int delta = player.getShipBoard().getCondensedShip().declaredDoublesStrength(amount, false);

        if (delta < 0) {

            throw new InvalidParameters("Declared amount is out of bounds");

        }

        int frontCannons = player.getShipBoard().getCondensedShip().getTotalDoubleCannons().getFrontCannons();
        int otherCannons = player.getShipBoard().getCondensedShip().getTotalDoubleCannons().getOtherCannons();

        int doubleRequired = delta / 2;
        if (doubleRequired <= frontCannons) {
            batteries += doubleRequired;
            delta -= doubleRequired * 2;
        } else {
            batteries += frontCannons;
            delta -= doubleRequired * 2;
        }

        if (delta > 0) {

            if (delta <= otherCannons) {
                batteries += delta;
            } else {
                
                throw new InvalidParameters("Not enough double cannons to declare this amount");
            }

        }

        if(batteries > player.getShipBoard().getCondensedShip().getTotalBatteries()){
            
            throw new InvalidParameters("Not enough batteries to declare this amount");
        }
        if (amount == player.getShipBoard().getCondensedShip().getBasePower()) {
            // no double cannon activated: the declaration is final
            finishDeclaration(context, player, amount);
        } else {
            controller.getModel().setState(new CombatZone2_P_BatteryRemovalState(context, amount, batteries));
        }
    }

    /**
     * Records a final fire power declaration. When everyone has declared, the weakest player loses flight days
     * and the card moves on to the engine power line.
     */
    static void finishDeclaration(Context context, Player player, double power) throws InvalidMethodParameters {
        final Controller controller = context.getController();
        context.recordDeclaration(player, power);
        context.removePlayer(player);
        if (context.getPlayers().isEmpty()) {
            controller.getModel().getFlightBoard().deltaFlightDays(context.getSpecialPlayers().getFirst(), -context.getDaysLost());
            context.startNewLine();
            if (context.getPlayers().isEmpty()) {
                controller.getModel().setState(new FlightPhase(controller));
            } else {
                controller.getModel().setState(new CombatZone2EngineDeclarationState(context));
            }
        } else {
            controller.getModel().setState(new CombatZone2PowerDeclarationState(context));
        }
    }

    public List<String> getAvailableCommands(){
        return List.of(
            "DeclareFirePower"
        );
    }
}