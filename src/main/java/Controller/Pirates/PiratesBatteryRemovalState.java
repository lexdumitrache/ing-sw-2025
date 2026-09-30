package Controller.Pirates;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.Slavers.SlaversBatteryRemovalState;
import Controller.Slavers.SlaversCrewRemovalState;
import Controller.Slavers.SlaversPowerDeclarationState;
import Controller.Slavers.SlaversRewardsState;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Controller.State;

import java.util.List;

/**
 * Represents the state where a player removes batteries to power up weapons
 * during the pirates encounter phase.
 *
 * <p>This state occurs after a player declares an intent to use a certain amount of power
 * against pirates. The player must now remove batteries to match or exceed the pirates' power.</p>
 */
public class PiratesBatteryRemovalState extends State{
    /**
     * The context of the game, which contains information about the current state and players.
     */
   
    /**
     * The declared power of the player, which is the amount of power they intend to use.
     */
    private double declaredPower;
    /**
     * The actual power of the player, which is the amount of power they have used.
     */
    private int batteries;

    /**
     * Constructs a new PiratesBatteryRemovalState.
     *
     * @param context The context of the game.
     * @param declaredPower The declared power of the player.
     * @param batteries The number of batteries the player has to remove.
     */
    public PiratesBatteryRemovalState(Context context, double declaredPower, int batteries) {
        super(context);
        this.declaredPower = declaredPower;
        this.batteries = batteries;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Called when a player attempts to use an item. This state handles the use of batteries only.
     * One battery is removed from the specified coordinates and added to the player's power.
     *
     * <p>After all declared batteries have been removed, the player's total power is compared to
     * the pirates' power. Depending on the result, the player may earn a reward, be safely removed
     * from the context, or be added to the list of players to be hit by pirates.</p>
     *
     * @param playerName  the name of the player using the item
     * @param itemType    the type of item being used (must be {@code ItemType.BATTERIES})
     * @param coordinates the coordinates of the battery compartment
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(itemType != ItemType.BATTERIES){
            
            throw new InvalidParameters("Invalid item type, expected BATTERIES");
        }

        if(declaredPower < 0){
            
            throw new InvalidParameters("Declared power cannot be negative");
        }

        if(coordinates == null){
            
            throw new InvalidParameters("Coordinates cannot be null");
        }

        if(batteries <= 0){
            
            throw new InvalidParameters("No batteries to remove");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }

        SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
        if(component == null || !player.getShipBoard().getCondensedShip().getBatteryCompartments().contains(component)) {   //non è un Battery
            
            throw new InvalidContextualAction("Invalid component type, expected BatteryCompartment");
        }

        BatteryCompartment compartment = (BatteryCompartment) component;
        compartment.removeBattery();
        batteries--;
        if(batteries == 0){
            if(declaredPower > context.getPower()){
                controller.getModel().setState(new PiratesRewardState(context));
                
            } else if(declaredPower == context.getPower()){
                context.removePlayer(player);
                if(context.getPlayers().isEmpty()){         //passati tutti
                    if (!context.getSpecialPlayers().isEmpty()) {
                        controller.getModel().setState(new PiratesCannonShotsState(context)); //tutti i giocatori gestiti
                        
                    } else {
                        controller.getModel().setState(new FlightPhase(controller));
                        
                    }
                }
                else{       //manca qualcuno da gestire
                    controller.getModel().setState(new PiratesPowerDeclarationState(context)); //manca qualcuno da gestire
                    
                }
            }
            else{
                context.removePlayer(player);
                context.addSpecialPlayer(player);
                if(context.getPlayers().isEmpty()){         //passati tutti
                    controller.getModel().setState(new PiratesCannonShotsState(context)); //tutti i giocatori gestiti
                    
                }
                else{       //manca qualcuno da gestire
                    controller.getModel().setState(new PiratesPowerDeclarationState(context)); //manca qualcuno da gestire
                    
                }
            }

        }
        else{       //rimuovi altra batteria
            controller.getModel().setState(new PiratesBatteryRemovalState(context, declaredPower, batteries));
            
        }
    }

    /**
     * Called when a player attempts to claim a reward directly using only base power (without additional batteries).
     *
     * @param playerName  the name of the player claiming the reward
     * @param rewardType  the type of reward being claimed (must be {@code RewardType.CREDITS})
     */
    @Override
    public void getReward(String playerName, RewardType rewardType) throws InvalidParameters {
        Controller controller = context.getController();
        if(rewardType != RewardType.CREDITS){
            
            throw new InvalidParameters("Invalid reward type, expected CREDITS");
        }
        if(declaredPower < 0){
            
            throw new InvalidParameters("Declared power cannot be negative");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }

        if(declaredPower > context.getPower() && batteries == 0){
            controller.getModel().setState(new PiratesRewardState(context));
            
        }

    }

    public List<String> getAvailableCommands(){
        return List.of( "UseBattery",
                        "GetCreditsReward" );
    }
}
