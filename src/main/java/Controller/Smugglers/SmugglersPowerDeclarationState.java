package Controller.Smugglers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Enums.Direction;
import Model.Player;
import Controller.State;

import java.util.List;

/**
 * This state handles the declaration of cannon power by players during the
 * "Smugglers" event. Players must declare how many cannons (and corresponding batteries)
 * they intend to use to surpass the smugglers' power threshold.
 */
public class SmugglersPowerDeclarationState extends State {
    /**
     * Constructor to initialize the state with the current game context.
     *
     * @param context the shared game context
     */
    public SmugglersPowerDeclarationState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Allows the current player to declare how many cannons they will use against the smugglers.
     * The declaration is only valid if the player declares {@link DoubleType#CANNONS}, has enough
     * cannon power and batteries, and hasn't already made a declaration.
     *
     * <p>If the total declared power (base + amount) is not enough to beat the smugglers' power,
     * the player is moved to {@link SmugglersBatteryRemovalState} to use batteries.</p>
     * <p>If the power is insufficient and the player is not already in the special players list,
     * the player is added to the losers. Once all players are handled, the state transitions to
     * {@link SmugglersGoodsRemovalState}.</p>
     *
     * @param playerName the name of the player making the declaration
     * @param doubleType the type of resource being declared (must be {@code CANNONS})
     * @param amount the number of cannons the player wants to use
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
                
                throw new InvalidContextualAction("Player already declared power");
            }
            context.addSpecialPlayer(player);
            context.removePlayer(player);
            if(context.getPlayers().isEmpty()){
                controller.getModel().setState(new SmugglersGoodsRemovalState(context));  //tutti i giocatori gestiti
                
            }else{
                controller.getModel().setState(new SmugglersPowerDeclarationState(context)); //manca qualcuno da gestire
                
            }
        }else{
            //se non devi rimovere batterie
            if(amount == player.getShipBoard().getCondensedShip().getBasePower()){
                //se vinci
                if(amount > context.getPower()){
                    controller.getModel().setState(new SmugglersLandState(context));
                    
                    //se pareggia:
                } else if(amount == context.getPower()){
                    context.removePlayer(player);
                    if(context.getPlayers().isEmpty()){         //passati tutti
                        if (!context.getSpecialPlayers().isEmpty()) {
                            controller.getModel().setState(new SmugglersGoodsRemovalState(context)); //tutti i giocatori gestiti
                            
                        }else{
                            controller.getModel().setState(new FlightPhase(controller));
                        }
                    }
                    else{       //manca qualcuno da gestire
                        controller.getModel().setState(new SmugglersPowerDeclarationState(context)); //manca qualcuno da gestire
                        
                    }
                }
                //se perde:
                else{
                    context.removePlayer(player);
                    context.addSpecialPlayer(player);
                    if(context.getPlayers().isEmpty()){         //passati tutti
                        controller.getModel().setState(new SmugglersGoodsRemovalState(context)); //tutti i giocatori gestiti
                        
                    }
                    else{       //manca qualcuno da gestire
                        controller.getModel().setState(new SmugglersPowerDeclarationState(context)); //manca qualcuno da gestire
                        
                    }
                }
            } else {
                controller.getModel().setState(new SmugglersBatteryRemovalState(context, amount, batteries)); //rimuovi batteria
                
            }

        }
    }
    @Override
    public List<String> getAvailableCommands(){
        return List.of(
            "DeclareFirePower"
        );
    }
}
