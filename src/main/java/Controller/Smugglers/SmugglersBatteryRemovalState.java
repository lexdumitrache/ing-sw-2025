package Controller.Smugglers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.Slavers.SlaversBatteryRemovalState;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Controller.State;

import java.util.List;

/**
 * This state handles the removal of batteries from a player's ship during the
 * "Smugglers" event. After declaring a cannon power, players must now use a corresponding
 * number of batteries to actually activate that power.
 */
public class SmugglersBatteryRemovalState extends State{
    /**
     * Context object that holds the game state and players information.
     */
   
    /**
     * The declared power of the player.
     */
    private double declaredPower;
    /**
     * The actual power of the player.
     */
    private int batteries;

    /**
     * Constructor to initialize the state with the current game context and power values.
     *
     * @param context       the shared game context
     * @param declaredPower the declared power of the player
     * @param batteries    the number of batteries the player has to remove
     */
    public SmugglersBatteryRemovalState(Context context, double declaredPower, int batteries) {
        super(context);
        this.declaredPower = declaredPower;
        this.batteries = batteries;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Handles the use of a battery component by the current player. If the battery
     * is valid and the player is allowed to act, one battery is removed, increasing
     * the actual power.
     *
     * <p>Once all declared batteries are removed, the state transitions depending
     * on whether the total power exceeds, equals, or is less than the smugglers' power.</p>
     *
     * @param playerName the name of the player using the battery
     * @param itemType must be {@link ItemType#BATTERIES}
     * @param coordinates the coordinates of the battery compartment
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(itemType != ItemType.BATTERIES){
            
            throw new InvalidParameters("Item type must be BATTERIES");
        }

        if(coordinates == null){
            
            throw new InvalidParameters("Invalid coordinates");
        }

        if(declaredPower <0){
            
            throw new InvalidParameters("Invalid declared power");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn");
        }

        SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
        if(component == null || !player.getShipBoard().getCondensedShip().getBatteryCompartments().contains(component)) {   //non è un Battery
            
            throw new InvalidContextualAction("Not a valid battery compartment");
        }

        BatteryCompartment compartment = (BatteryCompartment) component;
        compartment.removeBattery();
        batteries--;
        if(batteries == 0){
            if(declaredPower > context.getPower()){
                controller.getModel().setState(new SmugglersLandState(context));
                
            } else if(declaredPower == context.getPower()){
                context.removePlayer(player);
                if(context.getPlayers().isEmpty()){         //passati tutti
                    if (!context.getSpecialPlayers().isEmpty()) {
                        controller.getModel().setState(new SmugglersGoodsRemovalState(context)); //tutti i giocatori gestiti
                        
                    } else{
                        controller.getModel().setState(new FlightPhase(controller));
                    }
                }
                else{       //manca qualcuno da gestire
                    controller.getModel().setState(new SmugglersPowerDeclarationState(context)); //manca qualcuno da gestire
                    
                }
            }
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

        }
        else{       //rimuovi altra batteria
            controller.getModel().setState(new SlaversBatteryRemovalState(context, declaredPower, batteries));
            
        }
    }

    /**
     * Allows a player to claim a reward directly if their base power alone exceeds
     * the smugglers' threshold.
     *
     * @param playerName the name of the player
     * @param rewardType must be {@link RewardType#CREDITS}
     */
    @Override
    public void getReward(String playerName, RewardType rewardType) throws InvalidParameters {
        Controller controller = context.getController();
        if(rewardType != RewardType.CREDITS){
            
            throw new InvalidParameters("Reward type must be CREDITS");
        }

        if(declaredPower < 0){
            
            throw new InvalidParameters("Invalid declared power");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn");
        }
        if(declaredPower > context.getPower() && batteries == 0){
            controller.getModel().setState(new SmugglersLandState(context));
            
        }

    }
    @Override
    public List<String> getAvailableCommands(){
        return List.of(
            "UseBattery",
            "GetCreditsReward"
        );
    }
}
