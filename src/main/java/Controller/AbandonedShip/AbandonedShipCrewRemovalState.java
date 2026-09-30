package Controller.AbandonedShip;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Enums.Crewmates;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Controller.GamePhases.FlightPhase;

import java.util.List;


/**
 * Represents the state where players can remove crew members from their ship
 * after encountering an abandoned ship during the flight phase.
 *
 * <p>This state handles the logic for removing crew members from the ship.
 * If a player has crew members, they can remove them from the cabin.
 * If no crew members are left, the game transitions to the flight phase.</p>
 *
 * <p>Expected usage within a game state machine, with transitions depending on
 * player interactions.</p>
 */
public class AbandonedShipCrewRemovalState extends State {

    /**
     * Constructs the state with the given context.
     *
     * @param context the game context used to access controller and shared state.
     */
    public AbandonedShipCrewRemovalState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Called when a player wants to remove crew members from their ship.
     *
     * <p>If the item type is CREW, the player can remove crew members from the cabin.
     * If there are no crew members left required by the card, the game transitions to the flight phase.</p>
     *
     * <p>If the player does not have enough crew members, the method should return an error
     *
     * @param playerName the name of the player removing crewmates.
     * @param itemType   the type of item being used.
     * @param coordinates the coordinates of the cabin where crew members are removed.
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(itemType != ItemType.CREW){
            
            throw new InvalidParameters("Invalid item type for crew removal.");
        }

        if(coordinates == null){
            
            throw new InvalidParameters("Coordinates cannot be null.");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to remove crew members.");
        }


        if(context.getCrewmates() > 0){
            SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
            if(component == null || !player.getShipBoard().getCondensedShip().getCabins().contains(component)) {   //non è un Battery
                
                throw new InvalidContextualAction("Invalid component type, expected Cabin");
            }
            Cabin cabin = (Cabin) component;
            switch (cabin.getOccupants()){
                case SINGLE_HUMAN, BROWN_ALIEN, PURPLE_ALIEN:
                    cabin.setOccupants(Crewmates.EMPTY);
                    break;
                case DOUBLE_HUMAN:
                    cabin.setOccupants(Crewmates.SINGLE_HUMAN);
                    break;
                default:
                    break;
            }
            context.removeCrewmate();

            if(context.getCrewmates() > 0){
                controller.getModel().setState(new AbandonedShipCrewRemovalState(context));
                
            } else {
                // No more crew members to remove, transition to flight phase
                controller.getModel().setState(new FlightPhase(controller));
                
            }


        }
        else{
            controller.getModel().setState(new FlightPhase(controller));
            
        }
    }

    @Override
    public List<String> getAvailableCommands(){
        return List.of( "UseCrew");
    }
}
