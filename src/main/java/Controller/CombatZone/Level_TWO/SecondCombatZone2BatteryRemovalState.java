package Controller.CombatZone.Level_TWO;

import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Controller.Context;
import Controller.State;

import java.util.List;

/**
 * Represents the state in which a player may remove batteries from their ship
 * during the  combat zone because the player does not have enough goods.
 *
 * <p>This state allows a player to remove batteries from their ship's battery compartments,
 * and if all batteries are removed, it transitions to the next state based on the remaining batteries.</p>
 */
public class SecondCombatZone2BatteryRemovalState extends State {

   
    private int amount;

    public SecondCombatZone2BatteryRemovalState(Context context, int amount) {
        super(context);
        this.amount = amount;
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
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
     * @throws InvalidParameters if any parameters are invalid
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(itemType != ItemType.BATTERIES){
            
            throw new InvalidParameters("Invalid item type, expected BATTERIES");
        }

        if(amount < 0){
            
            throw new InvalidParameters("Declared power cannot be negative");
        }

        if(coordinates == null){
            
            throw new InvalidParameters("Coordinates cannot be null");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getSpecialPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }

        SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
        if(component == null || !player.getShipBoard().getCondensedShip().getBatteryCompartments().contains(component)) {   //non è un Battery
            
            throw new InvalidContextualAction("Invalid component type, expected BatteryCompartment");
        }

        BatteryCompartment compartment = (BatteryCompartment) component;
        compartment.removeBattery();
        amount--;
        if(amount == 0){
            int numPlayers = controller.getModel().getFlightBoard().getTurnOrder().length;
            Player currentPlayer = controller.getModel().getFlightBoard().getTurnOrder()[0];
            for(int i = 0; i<numPlayers-1; i++){
                Player nextPlayer = controller.getModel().getFlightBoard().getTurnOrder()[(i+1)];
                if(nextPlayer.getShipBoard().getCondensedShip().getTotalCrew() > currentPlayer.getShipBoard().getCondensedShip().getTotalCrew()){
                    currentPlayer = nextPlayer;
                }
            }
            context.addSpecialPlayer(currentPlayer);
            controller.getModel().setState(new CombatZone2CannonShotsState(context));
            
        }
        else{       //rimuovi altra batteria
            controller.getModel().setState(new SecondCombatZone2BatteryRemovalState(context, amount));
            
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "UseBattery");
    }
}
