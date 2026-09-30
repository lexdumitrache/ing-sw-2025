package Controller.Planets;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.RewardType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Board.AdventureCards.Components.Planet;
import Model.Board.AdventureCards.Rewards.Goods;
import Model.Enums.Good;
import Model.Player;
import Model.Ship.Components.CargoHold;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Controller.State;
import Controller.GamePhases.FlightPhase;

import java.util.Iterator;
import java.util.List;

/**
 * This state allows players to collect goods from the planets they landed on during the {@link ChoosePlanetState}.
 * Players can select goods to store in their cargo holds or rearrange their existing goods before ending the phase.
 * Once all players and planets are handled, the game transitions back to the {@link FlightPhase}.
 */
public class PlanetsLandState extends State {
    /**
     * The context of the game, which contains information about the current state and players.
     */
   
    /**
     * The list of planets that have been chosen by players.
     */
    List<Planet> chosenPlanets;

    /**
     * Constructs a new PlanetsLandState.
     *
     * @param context The context of the game.
     */
    public PlanetsLandState(Context context, List<Planet> chosenPlanets) {
        super(context);
        this.chosenPlanets = chosenPlanets;
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    /**
     * Allows a player to collect a good from the current planet and store it in a cargo hold.
     * Only the first special player can perform this action.
     * The good must be placed in a valid cargo hold component.
     *
     * If the good is successfully added, the planet and player are removed from their respective lists.
     * The game continues until all chosen planets and special players are processed.
     *
     * @param playerName     the name of the player collecting the good
     * @param goodIndex      the index of the good in the planet's reward list
     * @param coordinates    the coordinates of the cargo hold on the player's ship
     * @param CargoHoldIndex the index within the cargo hold where the good should be placed
     */
    @Override
    public void getGood(String playerName, int goodIndex, Coordinates coordinates, int CargoHoldIndex) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);

        if(context.getSpecialPlayers().getFirst() != player) {
            
            throw new InvalidParameters("It's not your turn to collect goods");
        }

        SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
        if(component == null || !player.getShipBoard().getCondensedShip().getCargoHolds().contains(component)) {   //non è un CargoHold
            
            throw new InvalidParameters("The selected component is not a cargo hold");
        }

        CargoHold cargoHold = (CargoHold) component;
        Planet planet = chosenPlanets.getFirst();
        if(planet == null) {
            
            throw new InvalidParameters("The planet is nto found");
        }
        if(!planet.isOccupied()){
            
            throw new InvalidContextualAction("The planet is not occupied");
        }

//        Goods rewards = planet.getLandingReward();
//        if(!rewards.iterator().hasNext()){
//            
//            throw new InvalidContextualAction("There are no goods to collect from this planet");
//        }

        if(CargoHoldIndex < 0 || CargoHoldIndex >= cargoHold.getCapacity()) {
            
            throw new InvalidParameters("Invalid cargo hold index");
        }

        if(goodIndex < 0 ) {
            
            throw new InvalidParameters("Invalid good index");
        }

        Good selectedGood = null;   //da correggere

        int corrente = 0;
        while (planet.getLandingReward().iterator().hasNext()) {
            selectedGood = planet.getLandingReward().iterator().next();
            if (corrente == goodIndex) {
                break;
            }
            corrente++;
        }


        if(selectedGood == null) {
            
            throw new InvalidParameters("The selected good is not found");
        }

        boolean done = cargoHold.addGoodAt(selectedGood, CargoHoldIndex);
        if (!done) {
            
            throw new InvalidContextualAction("The good cannot be added to the cargo hold");
        }

        Iterator<Good> iterator = planet.getLandingReward().iterator();
        while (iterator.hasNext()) {
            Good currentGood = iterator.next();
            if (currentGood.equals(selectedGood)) {
                iterator.remove(); // Usa lo stesso iterator del loop
                break;
            }
        }

        controller.getModel().setState(new PlanetsLandState(context, chosenPlanets));
        

    }

    /**
     * Allows a player to move a good from one cargo hold to another.
     * Both source and destination must be valid cargo holds.
     *
     * @param name          the name of the player performing the move
     * @param oldCoordinates the coordinates of the current cargo hold
     * @param newCoordinates the coordinates of the target cargo hold
     * @param oldIndex       the index of the good in the original cargo hold
     * @param newIndex       the index in the target cargo hold where the good should be placed
     */
    @Override
    public void moveGood(String name, Coordinates oldCoordinates, Coordinates newCoordinates, int oldIndex, int newIndex) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(name);
        if(!player.equals(context.getSpecialPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn");
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
            
            throw new InvalidParameters("Invalid cargo hold index.");
        }

        Good selectedGood = oldCargoHold.getGoods()[oldIndex];
        if(selectedGood == null) {
            
            throw new InvalidParameters("The selected good is not found");
        }
        boolean done = newCargoHold.addGoodAt(selectedGood, newIndex);
        if (!done) {
            
            throw new InvalidContextualAction("The good cannot be added to the cargo hold");
        }
        oldCargoHold.removeGood(oldIndex);
        controller.getModel().setState(new PlanetsLandState(context, chosenPlanets));
        
    }

    /**
     * Called when a player ends their interaction with the current planet.
     * Immediately transitions to the {@link FlightPhase}.
     *
     * @param playerName the name of the player ending the phase
     */
    @Override
    public void end(String playerName) throws InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getSpecialPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn");
        }
        context.removeSpecialPlayer(player);
        chosenPlanets.removeFirst();
        if(chosenPlanets.isEmpty() && context.getSpecialPlayers().isEmpty()) {      //dovrei controllare anche le non conformità delle due liste
            controller.getModel().setState(new FlightPhase(controller));     //finiti pianeti occupati
            
        } else {
            controller.getModel().setState(new PlanetsLandState(context, chosenPlanets));
            
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "GetGood",
                        "MoveGood",
                        "End");
    }
}
