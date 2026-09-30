package Controller.OpenSpace;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;

import java.util.List;

/**
 * Represents the state in which a player may declare the use of double engines
 * during the open space phase of the game.
 *
 * <p>This state allows a player (not first in turn order) to attempt using double engines,
 * which requires having a sufficient number of both batteries and double engine components.</p>
 */
public class OpenSpaceEngineDeclarationState extends State {

    /**
     * The context in which this state operates, providing access to the game controller.
     */
   

    /**
     * Constructs an OpenSpaceEngineDeclarationState with the specified context.
     *
     * @param context The context providing access to the current game context.
     */
    public OpenSpaceEngineDeclarationState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    @Override
    public void onEnter() {
        for(Player p : context.getPlayers()) {
            if (p.getShipBoard().getCondensedShip().getTotalBatteries() == 0 && p.getShipBoard().getCondensedShip().getBaseThrust() == 0) {
                context.getController().getModel().getFlightBoard().removePlayingPlayer(p);
            } else if (p.getShipBoard().getCondensedShip().getTotalBatteries() == 0 && p.getShipBoard().getCondensedShip().getAliens().hasBrownAlien() &&
            p.getShipBoard().getCondensedShip().getBaseThrust() == 2) {
                context.getController().getModel().getFlightBoard().removePlayingPlayer(p);
            }
        }
    }

    /**
     * Called when a player declares the use of double components, such as engines.
     * Only double engines are handled in this state.
     *
     * <p>If the declared type is not {@code DoubleType.ENGINES}, or the player is first
     * in the turn order, or the player lacks sufficient batteries or double engines,
     * the method returns without taking action.</p>
     *
     * <p>If the declaration is valid, the game transitions to the
     * {@link OpenSpaceBatteryRemovalState} to proceed with the battery deduction.</p>
     *
     * @param playerName the name of the player making the declaration
     * @param doubleType the type of double component being declared (must be ENGINES)
     * @param amount     the number of double engines the player wants to use
     */
    @Override
    public void declaresDouble(String playerName, DoubleType doubleType, double amount) throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
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

        if(amount == 2 && player.getShipBoard().getCondensedShip().getAliens().hasBrownAlien()){
            
            throw new InvalidParameters(" Cannot declare 2 double engines with a brown alien on board");
        }

        if (amount < minPower || amount > maxPower) {
            
            throw new InvalidParameters("Declared amount is out of bounds");
        }


        if ((amount % 1) != (minPower % 1)) {
            
            throw new InvalidParameters("Declared amount must match the ship's base power decimal part");
        }

        int delta = (int) (amount - minPower);

        if(delta % 2 != 0) {
            
            throw new InvalidParameters("Cannot reach the declared amount with double engines");
        }

        int doubleRequired = delta / 2;

        if(player.getShipBoard().getCondensedShip().getEngines().getDoubleEngines()>=doubleRequired){
            batteries = doubleRequired;
        } else {
            
            throw new InvalidParameters("Not enough double engines to declare this amount");
        }

        if(player.getShipBoard().getCondensedShip().getTotalBatteries() < batteries) {
            
            throw new InvalidParameters("Not enough batteries to declare this amount");
        }

        if(amount == player.getShipBoard().getCondensedShip().getBaseThrust()){
            controller.getModel().getFlightBoard().deltaFlightDays(player, (int) amount);
            context.removePlayer(player);
            if(context.getPlayers().isEmpty()){         //passati tutti
                controller.getModel().setState(new FlightPhase(controller));
                
            }
            else{       //manca qualcuno da gestire
                controller.getModel().setState(new OpenSpaceEngineDeclarationState(context));
                
            }

        } else {
            controller.getModel().setState(new OpenSpaceBatteryRemovalState(context, amount, batteries));
            
        }

    }

    public List<String> getAvailableCommands(){
        return List.of( "DeclareEnginePower" );
    }

}
