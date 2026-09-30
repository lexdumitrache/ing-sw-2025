package Controller.CombatZone.Level_TWO;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;

import java.util.List;

/**
 * Represents the state in which a player may remove batteries from their ship
 * during the combat zone.
 *
 * <p>This state allows a player to remove batteries from their ship's battery compartments,
 * and if all batteries are removed, it transitions to the next state based on the declared power.</p>
 */
public class CombatZone2_E_BatteryRemovalState extends State {
   
    private double declaredPower;
    private int batteries;
    /**
     * The worst declared power among the players, used to determine the player with the lowest power.
     */
    private double worst;

    public CombatZone2_E_BatteryRemovalState(Context context, double declaredPower, int batteries) {
        super(context);
        this.declaredPower = declaredPower;
        this.batteries = batteries;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    public CombatZone2_E_BatteryRemovalState(Context context, double declaredPower, int batteries, double worst) {
        super(context);
        this.declaredPower = declaredPower;
        this.batteries = batteries;
        this.worst = worst;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Handles the use of an item by a player during the combat zone phase, specifically for using batteries.
     * <p>
     * Validates the item type, player turn, declared power, battery count, and coordinates before applying effects.
     * If all batteries have been removed, updates the special player list and transitions to the next game state.
     * If batteries remain, updates the state to allow further battery removal.
     *
     * @param playerName the name of the player attempting to use the item
     * @param itemType the type of item being used (must be {@code ItemType.BATTERIES})
     * @param coordinates the coordinates of the component from which the battery is to be removed
     * @throws InvalidMethodParameters if any input is null, negative, or otherwise structurally invalid
     * @throws InvalidContextualAction if the action is not valid in the current context
     * @throws InvalidParameters if any parameters are invalid for this action
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
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

        if (batteries < 0) {
            
            throw new InvalidParameters(" Number of batteries cannot be negative");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }

        if (batteries > 0) {
            SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
            if(component == null && !player.getShipBoard().getCondensedShip().getBatteryCompartments().contains(component)) {   //non è un Battery
                
                throw new InvalidContextualAction("Invalid component type, only BATTERY COMPARTMENT are allowed");
            }

            BatteryCompartment compartment = (BatteryCompartment) component;
            compartment.removeBattery();
            batteries--;
        }
        if(batteries == 0){
            if(context.getSpecialPlayers().isEmpty()){
                context.addSpecialPlayer(player);
            } else {
                if(declaredPower < worst){
                    context.removeSpecialPlayer(context.getSpecialPlayers().getFirst());
                    context.addSpecialPlayer(player);
                    worst = declaredPower;
                }
            }
            context.removePlayer(player);
            if(context.getPlayers().isEmpty()){
                controller.getModel().setState(new CombatZone2GoodsRemovalState(context));
                
            } else {
                controller.getModel().setState(new CombatZone2EngineDeclarationState(context, worst));
                
            }
        }
        else{       //rimuovi altra batteria
            controller.getModel().setState(new CombatZone2_E_BatteryRemovalState(context, declaredPower, batteries));
            
        }
    }

    public List<String> getAvailableCommands(){
        return List.of(
            "UseBattery"
        );
    }
}
