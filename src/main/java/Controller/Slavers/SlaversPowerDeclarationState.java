package Controller.Slavers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Enums.Direction;
import Model.Player;

import java.util.List;

/**
 * This state handles the declaration of cannon power by players during the "Slavers" encounter.
 * Players may attempt to use double cannons and batteries to surpass the slavers' power threshold.
 * If successful, they avoid penalties or get rewards; otherwise, they proceed to a crew removal.
 */
public class SlaversPowerDeclarationState extends State {
    /**
     * Constructor to initialize the state with the current game context.
     *
     * @param context the shared game context
     */
    public SlaversPowerDeclarationState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Allows a player to declare the number of double cannons they wish to use.
     * This declaration is only valid if:
     * - The declaration type is {@code DoubleType.CANNONS}
     * - The player has enough double cannons and batteries
     * - The player is the current player in the turn order
     *
     * If the player's total power (base + declared cannons) exceeds the slavers' power,
     * they are added to the list of special players (effectively marked as handled),
     * and the state either loops or transitions to {@link SlaversCrewRemovalState}.
     *
     * Otherwise, the player proceeds to {@link SlaversBatteryRemovalState} to spend the declared batteries.
     *
     * @param playerName the name of the player declaring power
     * @param doubleType the type of declaration (should be CANNONS)
     * @param amount     the number of double cannons declared
     */
    @Override
    public void declaresDouble(String playerName, DoubleType doubleType, double amount) throws InvalidContextualAction, InvalidParameters {
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

        int delta = (int) (amount - minPower);

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


        if(context.getPower() > (amount)){
            if(context.getSpecialPlayers().contains(player)){
                
                throw new InvalidParameters("Player already declared power");
            }
            context.addSpecialPlayer(player);
            context.removePlayer(player);
            if(context.getPlayers().isEmpty()){
                controller.getModel().setState(new SlaversCrewRemovalState(context));  //tutti i giocatori gestiti
                
            }else{
                controller.getModel().setState(new SlaversPowerDeclarationState(context)); //manca qualcuno da gestire
                
            }
        }else{
            if(amount == player.getShipBoard().getCondensedShip().getBaseThrust()){
                if(amount > context.getPower()){
                    controller.getModel().setState(new SlaversRewardsState(context));
                } else if(amount == context.getPower()){
                    context.removePlayer(player);
                    if(context.getPlayers().isEmpty()){         //passati tutti
                        if (!context.getSpecialPlayers().isEmpty()) {
                            controller.getModel().setState(new SlaversCrewRemovalState(context)); //tutti i giocatori gestiti
                            
                        } else {
                            controller.getModel().setState(new FlightPhase(controller));
                        }
                    }
                    else{       //manca qualcuno da gestire
                        controller.getModel().setState(new SlaversPowerDeclarationState(context)); //manca qualcuno da gestire
                        
                    }
                }
                else{
                    context.removePlayer(player);
                    context.addSpecialPlayer(player);
                    if(context.getPlayers().isEmpty()){         //passati tutti
                        controller.getModel().setState(new SlaversCrewRemovalState(context)); //tutti i giocatori gestiti
                        
                    }
                    else{       //manca qualcuno da gestire
                        controller.getModel().setState(new SlaversPowerDeclarationState(context)); //manca qualcuno da gestire
                        
                    }
                }
            } else {
                controller.getModel().setState(new SlaversBatteryRemovalState(context, amount, batteries)); //usa batterie
                
            }

        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "DeclareFirePower" );
    }
}
