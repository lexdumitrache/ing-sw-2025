package Controller.Smugglers;

import Controller.Context;
import Controller.Controller;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Good;
import Model.Player;
import Model.Ship.Components.CargoHold;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Model.Ship.GoodCounter;
import Controller.GamePhases.FlightPhase;
import Controller.State;

import java.util.List;

/**
 * This state manages the forced removal of goods from a player's ship during
 * the "Smugglers" event, if they failed to surpass the smugglers' power threshold.
 *
 * <p>Players must discard a number of goods equal to the required amount. If they lack
 * sufficient goods but have batteries, a secondary penalty is applied. Otherwise,
 * they suffer no additional effect.</p>
 */
public class SmugglersGoodsRemovalState extends State{
    /**
     * Context object that holds the game state and player information.
     */
   
    /**
     * The good counter that tracks the number of goods to be removed.
     */
    private GoodCounter goodCounter;


    /**
     * Constructor to initialize the state with the current game context.
     *
     * @param context the shared game context
     */
    public SmugglersGoodsRemovalState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }



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
        //se non trova nessun cargo hold con goods
        if(!availableGoods){
            if(player.getShipBoard().getCondensedShip().getTotalBatteries() > 0){
                controller.getModel().setState(new SecondSmugglersBatteryRemovalState(context));
                
            } else {
                context.removeSpecialPlayer(player);
                if(context.getSpecialPlayers().isEmpty()){
                    controller.getModel().setState(new FlightPhase(controller));
                    
                } else {
                    controller.getModel().setState(new SmugglersGoodsRemovalState(context));
                    
                }
            }
        }
    }



    /**
     * Handles the removal of a good from the player's cargo hold. If the player lacks
     * sufficient goods, but has batteries, they will instead face battery removal.
     * Otherwise, they are skipped with no penalty.
     *
     * <p>After a good is removed, the state updates based on whether the required
     * number has been discarded.</p>
     *
     * @param name the name of the player discarding the good
     * @param oldCoordinates the coordinates of the cargo hold from which the good is removed
     * @param newCoordinates unused in this context
     * @param oldIndex the index of the good in the cargo hold
     * @param newIndex unused in this context
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

        GoodCounter goodCounter = player.getShipBoard().getCondensedShip().goodToDiscard(context.getRequiredGoods());
        if(goodCounter.getRed() + goodCounter.getBlue() + goodCounter.getGreen() + goodCounter.getYellow() == 0){       //non ha abbastanza goods da scartare
            if(player.getShipBoard().getCondensedShip().getTotalBatteries() > 0){   //se almeno ha delle batterie
                controller.getModel().setState(new SecondSmugglersBatteryRemovalState(context));
                
            } else {    //se no non gli succede niente
                context.removeSpecialPlayer(player);
                if(context.getSpecialPlayers().isEmpty()){
                    controller.getModel().setState(new FlightPhase(controller));
                    
                } else {
                    controller.getModel().setState(new SmugglersGoodsRemovalState(context));
                    
                }
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
        context.removeRequiredGood();
        if(context.getRequiredGoods() == 0){
            context.removeSpecialPlayer(player);
            if(context.getSpecialPlayers().isEmpty()){
                controller.getModel().setState(new FlightPhase(controller));
                
            } else {
                context.setRequiredGoods(context.getDefaultAmount());
                controller.getModel().setState(new SmugglersGoodsRemovalState(context));
                
            }
        } else {
                controller.getModel().setState(new SmugglersGoodsRemovalState(context));
            
        }
    }
    @Override
    public List<String> getAvailableCommands(){
        return List.of(
            "RemoveGood"
        );
    }
}
