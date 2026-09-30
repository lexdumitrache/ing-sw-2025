package Controller.OpenSpace;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Enums.Direction;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Controller.GamePhases.FlightPhase;

import java.util.List;

/**
 * Represents the state where a player must remove batteries from their ship to confirm
 * the use of double engines during the open space phase.
 *
 * <p>This state is reached after a player declares the intent to use a number of double engines,
 * and must now pay the cost in batteries.</p>
 */
public class OpenSpaceBatteryRemovalState extends State {
    /**
     * The context in which this state operates, providing access to the game controller.
     */
   
    /**
     * The number of batteries that must be removed to confirm the use of double engines.
     */
    private double declaredPower;

    private int batteries;

    /**
     * Constructs an OpenSpaceBatteryRemovalState with the specified context and declared power.
     *
     * @param context      The context providing access to the current game context.
     * @param declaredPower The number of batteries that must be removed.
     */
    public OpenSpaceBatteryRemovalState(Context context, double declaredPower, int batteries) {
        super(context);
        this.declaredPower = declaredPower;
        this.batteries = batteries;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }


    /**
     * Called when a player uses an item during this state.
     * Only battery usage is handled, and the coordinates must point to a valid battery compartment.
     *
     * <p>Each call removes one battery from the specified compartment. When all batteries
     * required by the declared power have been removed, the player is removed from the open space phase,
     * and the game either proceeds to the next player or returns to the flight phase.</p>
     *
     * <p>If the item is not a battery, the coordinates are null, the declared power is negative,
     * or the targeted component is not a valid battery compartment, the method should crate an error.</p>
     *
     * @param playerName  the name of the player using the item
     * @param itemType    the type of item being used (must be {@code ItemType.BATTERIES})
     * @param coordinates the coordinates of the battery compartment to remove a battery from
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidMethodParameters, InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();

        if(itemType != ItemType.BATTERIES){
            
            throw new InvalidParameters("Invalid item type, only BATTERIES are allowed");
        }

        if(coordinates == null){
            
            throw new InvalidParameters("Invalid coordinates type, coordinates is null");
        }

        if(declaredPower < 0){
            
            throw new InvalidParameters("Invalid declared power type, declared power is negative");
        }

        if(batteries < 0){
            
            throw new InvalidParameters("Invalid batteries type, batteries negative");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to throw the dice.");
        }

        if (batteries > 0) {
            SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
            if(component == null || !player.getShipBoard().getCondensedShip().getBatteryCompartments().contains(component)) {   //non è un Battery
                
                throw new InvalidContextualAction("Invalid component type, only BATTERY COMPARTMENT are allowed");
            }

            BatteryCompartment compartment = (BatteryCompartment) component;
            compartment.removeBattery();
            batteries--;
        }
        if(batteries == 0){

            controller.getModel().getFlightBoard().deltaFlightDays(player, (int) declaredPower);

            context.removePlayer(player);
            if(context.getPlayers().isEmpty()){         //passati tutti
                controller.getModel().setState(new FlightPhase(controller));
                
            }
            else{       //manca qualcuno da gestire
                controller.getModel().setState(new OpenSpaceEngineDeclarationState(context));
                
            }
        }
        else{       //rimuovi altra batteria
            controller.getModel().setState(new OpenSpaceBatteryRemovalState(context, declaredPower, batteries));
            
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "UseBattery" );
    }

}
