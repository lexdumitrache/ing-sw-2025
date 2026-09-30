package Controller.CombatZone.Level_TWO;

import Controller.Context;
import Controller.Controller;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.Smugglers.SecondSmugglersBatteryRemovalState;
import Controller.Smugglers.SmugglersGoodsRemovalState;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Enums.Good;
import Model.Player;
import Model.Ship.Components.CargoHold;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Model.Ship.GoodCounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the state in which a player must remove goods from their ship
 * during the combat zone.
 *
 * <p>This state allows a player to remove goods from their ship's cargo holds,
 * and if all required goods are removed, it transitions to the next state.</p>
 */
public class CombatZone2GoodsRemovalState extends State {

    private GoodCounter goodCounter;

    private int amount;


    public CombatZone2GoodsRemovalState(Context context) {
        super(context);
        this.amount = context.getRequiredGoods();
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    public CombatZone2GoodsRemovalState(Context context, int amount) {
        super(context);
        this.amount = amount;
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    /**
     * Executes logic upon entering the current game state during the second combat zone phase.
     * <p>
     * Checks whether the current special player has any goods in their cargo holds. If not,
     * and the player has remaining batteries, transitions to a battery removal state.
     * If there are no batteries either, the player is removed from the special players list.
     * The game then transitions to either the next player's goods removal state or the flight phase,
     * depending on whether special players remain.
     */

    @Override
    public void onEnter(){
        Controller controller = context.getController();
        Player player = context.getSpecialPlayers().getFirst();
        boolean availableGoods = false;
        for (CargoHold cargoHold : player.getShipBoard().getCondensedShip().getCargoHolds()) {
            if (!cargoHold.isEmpty()) {
                availableGoods = true;
                break;
            }
        }
        if(!availableGoods){
            if(player.getShipBoard().getCondensedShip().getTotalBatteries() > 0){
                controller.getModel().setState(new SecondCombatZone2BatteryRemovalState(context, amount));
                
            } else {
                context.removeSpecialPlayer(player);
                if(context.getSpecialPlayers().isEmpty()){
                    controller.getModel().setState(new FlightPhase(controller));
                    
                } else {
                    controller.getModel().setState(new CombatZone2GoodsRemovalState(context));
                    
                }
            }
        }
    }

    /**
     * Remove a good from one cargo hold during the combat zone phase.
     * It ingores the newCoordinates and newIndex parameters as they are not used in this context.
     * <p>
     * Validates the player's turn, coordinates, and indices before proceeding with the removal of goods.
     * If the player has insufficient goods, it transitions to a battery removal state or cannon shots state.
     * If the good is successfully removed, it updates the game state accordingly.
     *
     * @param name the name of the player performing the action
     * @param oldCoordinates the coordinates of the cargo hold from which to remove the good
     * @param newCoordinates not used in this context
     * @param oldIndex the index of the good to be removed
     * @param newIndex not used in this context
     * @throws InvalidContextualAction if the action is not valid in the current context
     * @throws InvalidParameters if any parameters are invalid for this action
     */
    @Override
    public void moveGood(String name, Coordinates oldCoordinates, Coordinates newCoordinates, int oldIndex, int newIndex) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(name);
        if(!player.equals(context.getSpecialPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn");
        }

        if(oldCoordinates == null){
            
            throw new InvalidParameters("Invalid coordinates");
        }

        if(oldIndex < 0){
            
            throw new InvalidParameters("Invalid index");
        }

        GoodCounter goodCounter = player.getShipBoard().getCondensedShip().goodToDiscard(amount);
        if(goodCounter.getRed() + goodCounter.getBlue() + goodCounter.getGreen() + goodCounter.getYellow() == 0){       //non ha abbastanza goods da scartare
            if(player.getShipBoard().getCondensedShip().getTotalBatteries() > 0){   //se almeno ha delle batterie
                controller.getModel().setState(new SecondCombatZone2BatteryRemovalState(context, amount));
                
            } else {    //se no non gli succede niente

                int numPlayers = controller.getModel().getFlightBoard().getTurnOrder().length;
                Player currentPlayer = controller.getModel().getFlightBoard().getTurnOrder()[0];
                for(int i = 0; i<numPlayers; i++){
                    Player nextPlayer = controller.getModel().getFlightBoard().getTurnOrder()[(i+1)];
                    if(nextPlayer.getShipBoard().getCondensedShip().getTotalCrew() > currentPlayer.getShipBoard().getCondensedShip().getTotalCrew()){
                        currentPlayer = nextPlayer;
                    }
                }
                context.addSpecialPlayer(currentPlayer);
                controller.getModel().setState(new CombatZone2CannonShotsState(context));
                


            }
            return;
        }

        SpaceshipComponent component = player.getShipBoard().getComponent(oldCoordinates);

        if(component == null || !player.getShipBoard().getCondensedShip().getCargoHolds().contains(component)) {
            
            throw new InvalidContextualAction("Not a valid cargo hold");
        }
        CargoHold cargoHold = (CargoHold) component;
        Good selectedGood = cargoHold.getGoods()[oldIndex];
        if(selectedGood == null) {
            
            throw new InvalidParameters("The selected good is not found");
        }
        boolean done = goodCounter.removeGood(selectedGood);
        if(!done) {
            
            throw new InvalidContextualAction("Need to remove another type of good");
        }
        cargoHold.removeGood(oldIndex);
        amount--;
        if(amount == 0){
            List<Player> allPlayers= new ArrayList<>(Arrays.asList(controller.getModel().getFlightBoard().getTurnOrder()));
            context.setPlayers(allPlayers);
            controller.getModel().setState(new CombatZone2CannonShotsState(context));
            
        } else {
            controller.getModel().setState(new CombatZone2GoodsRemovalState(context, amount));
            
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "RemoveGood");
    }
}
