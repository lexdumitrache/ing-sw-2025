package Controller.CombatZone.Level_ONE;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Enums.Crewmates;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.Coordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the state in which a player may remove crew members from their ship
 * during the combat zone phase of the game.
 *
 * <p>This state allows a player to remove crew members from their ship's cabins,
 * and if all crew members are removed, it transitions to the next state based on the declared power.</p>
 */
public class CombatZone1CrewRemovalState extends State {
   

    public CombatZone1CrewRemovalState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    /**
     * Allows a player to remove a crewmate on a specific cabin during the crew removal phase of combat.
     * <p>
     * Validates the item type, player turn, coordinates, and crew count before proceeding.
     * If a valid Cabin is found at the given coordinates, a crew member is removed from it.
     * When all required crew members have been removed, updates the special player list and transitions to the next game state.
     * If crew members are still required, transitions to the appropriate crew removal state to continue the process.
     *
     * @param playerName the name of the player attempting to remove a crew member
     * @param itemType the type of item being used (must be {@code ItemType.CREW})
     * @param coordinates the coordinates of the cabin from which the crew member is to be removed
     * @throws InvalidContextualAction if there are not enough crew members to remove
     * @throws InvalidParameters if it is not the player's turn or if no cabin exists at the given coordinates
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(itemType != ItemType.CREW){
            
            throw new InvalidParameters("Invalid item type for crew removal.");
        }
        Player player = controller.getModel().getPlayer(playerName);
        if(player.equals(context.getSpecialPlayers().getFirst())){
            if(player.getShipBoard().getCondensedShip().getTotalCrew() < context.getCrewmates()){
                
                throw new InvalidContextualAction("The player doesn't have enough crew"); //handle the situation where the player doesn't have enough crew
            }
            if(context.getCrewmates() > 0){
                Cabin cabin = (Cabin) player.getShipBoard().getComponent(coordinates);
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

                if(context.getCrewmates() == 0){
                    List<Player> allPlayers= new ArrayList<>(Arrays.asList(controller.getModel().getFlightBoard().getTurnOrder()));
                    context.setPlayers(allPlayers);
                    controller.getModel().setState(new CombatZone1PowerDeclarationState(context));
                    
                } else {
                    controller.getModel().setState(new CombatZone1CrewRemovalState(context));
                    
                }
            }
            else{
                List<Player> allPlayers= new ArrayList<>(Arrays.asList(controller.getModel().getFlightBoard().getTurnOrder()));
                context.setPlayers(allPlayers);
                controller.getModel().setState(new CombatZone1PowerDeclarationState(context));
                
            }
        } else {
            
            throw new InvalidParameters("It's not your turn to remove crew members.");
        }
    }

    public List<String> getAvailableCommands(){
        return List.of(
            "UseCrew"
        );
    }

}
