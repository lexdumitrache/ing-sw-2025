package Controller.AbandonedStation;

import Controller.Context;
import Controller.Controller;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Enums.Good;
import Model.Player;
import Model.Ship.Components.CargoHold;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Controller.GamePhases.FlightPhase;

import java.util.List;


/**
 * Represents the state in which a player manages the goods obtained from an abandoned station.
 *
 * <p>During this state, the player can choose where to place goods in cargo holds,
 * move goods between cargo holds, or end their action. Each operation is validated
 * to ensure it is the player's turn and that the operations are legal according to the game rules.</p>
 */
public class AbandonedStationLandState extends State {


    /**
     * Constructs the state with the given context.
     *
     * @param context the game context used to access controller and shared state.
     */
    public AbandonedStationLandState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Places a selected good from the reward into a specific cargo hold at the given position.
     *
     * <p>The good is removed from the context once placed. If the specified component
     * is not a valid cargo hold or the insertion fails, no state transition occurs.</p>
     *
     * @param playerName     the name of the player placing the good
     * @param goodIndex      the index of the good in the context's list of goods
     * @param coordinates    the coordinates of the cargo hold on the player's ship
     * @param CargoHoldIndex the index within the cargo hold where the good should be placed
     */
    @Override
    public void getGood(String playerName, int goodIndex, Coordinates coordinates, int CargoHoldIndex) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to remove crew members.");
        }

        SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
        if(component == null || !player.getShipBoard().getCondensedShip().getCargoHolds().contains(component)) {
            //non è un CargoHold
            throw new InvalidContextualAction("Not a valid cargo hold coordinates.");
        }

        if(goodIndex<0 || goodIndex >= context.getGoods().size()) {
            
            throw new InvalidParameters("Good index is out of bounds.");
        }

        CargoHold cargoHold = (CargoHold) component;

        if(CargoHoldIndex < 0 || CargoHoldIndex >= cargoHold.getCapacity()) {
            
            throw new InvalidParameters("Cargo hold index is out of bounds.");
        }

        Good selectedGood = context.getGoods().get(goodIndex);
        if(selectedGood == null) {
            
            throw new InvalidParameters("Selected good is null.");
        }

        boolean done = cargoHold.addGoodAt(selectedGood, CargoHoldIndex);
        if (!done) {
            
            throw new InvalidContextualAction("Failed to place good in cargo hold at specified index.");
        }

        context.removeGood(selectedGood);
        controller.getModel().setState(new AbandonedStationLandState(context));
        
    }

    /**
     * Moves a good from one cargo hold to another within the player's ship.
     *
     * <p>If either coordinate does not refer to a valid cargo hold, or if the move fails,
     * no changes are made. On success, the good is moved and the state is refreshed.</p>
     *
     * @param name         the name of the player performing the move
     * @param oldCoordinates the coordinates of the source cargo hold
     * @param newCoordinates the coordinates of the target cargo hold
     * @param oldIndex     the index of the good in the source cargo hold
     * @param newIndex     the index where the good should be placed in the target cargo hold
     */
    @Override
    public void moveGood(String name, Coordinates oldCoordinates, Coordinates newCoordinates, int oldIndex, int newIndex) throws InvalidParameters, InvalidContextualAction {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(name);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to move the good.");
        }

        SpaceshipComponent oldComponent = player.getShipBoard().getComponent(oldCoordinates);
        SpaceshipComponent newComponent = player.getShipBoard().getComponent(newCoordinates);

        if(oldComponent == null || newComponent == null ||
                !player.getShipBoard().getCondensedShip().getCargoHolds().contains(oldComponent) ||
                !player.getShipBoard().getCondensedShip().getCargoHolds().contains(newComponent)) {
            
            throw new InvalidParameters("Invalid cargo hold coordinates.");
        }

        CargoHold oldCargoHold = (CargoHold) oldComponent;
        CargoHold newCargoHold = (CargoHold) newComponent;
        if(oldIndex < 0 || oldIndex >= oldCargoHold.getCapacity() || newIndex < 0 || newIndex >= newCargoHold.getCapacity()) {
            
            throw new InvalidParameters("Cargo hold index is out of bounds.");
        }

        Good selectedGood = oldCargoHold.getGoods()[oldIndex];
        if(selectedGood == null) {
            
            throw new InvalidParameters("Selected good is null.");
        }

        boolean done = newCargoHold.addGoodAt(selectedGood, newIndex);
        if (!done) {
            
            throw new InvalidContextualAction("Invalid cargo hold coordinates.");
        }

        oldCargoHold.removeGood(oldIndex);
        controller.getModel().setState(new AbandonedStationLandState(context));
        
    }

    /**
     * Ends the current player's turn and resumes the flight phase.
     *
     * @param playerName the name of the player ending their action
     */
    @Override
    public void end(String playerName) throws InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to pass.");
        }

        controller.getModel().setState(new FlightPhase(controller));
        
    }

    @Override
    public List<String> getAvailableCommands(){
        return List.of(
            "GetGood",
            "MoveGood",
            "End"
        );
    }
}
