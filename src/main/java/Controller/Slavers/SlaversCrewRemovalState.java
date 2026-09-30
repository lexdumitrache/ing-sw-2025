package Controller.Slavers;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidParameters;
import Model.Enums.Crewmates;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.Coordinates;
import Controller.State;
import Controller.GamePhases.FlightPhase;

import java.util.List;

/**
 * This state handles the forced removal of crewmates from players' ships during
 * the "Slavers" encounter, specifically for players who failed to surpass the slavers' power.
 * Players must remove crew members until they have no remaining required removals.
 */
public class SlaversCrewRemovalState extends State {



    /**
     * Constructor to initialize the state with the current game context.
     *
     * @param context the shared game context
     */
    public SlaversCrewRemovalState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());

    }

    /**
     * Allows the current player to remove a crew member from one of their cabins.
     * The player must be the current one in turn, and a valid {@link Cabin} must be targeted.
     *
     * After a crew member is removed, if more removals are required, the state refreshes.
     * Otherwise, the player is removed from the special players list and:
     * <ul>
     *     <li>If all players have been processed, the game transitions to {@link FlightPhase}</li>
     *     <li>Otherwise, the state remains to process the next player</li>
     * </ul>
     *
     * @param playerName  the name of the player attempting to remove a crew member
     * @param itemType    the item type used (must be {@code CREW})
     * @param coordinates the coordinates of the cabin containing the crew member
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates) throws InvalidParameters {
        Controller controller = context.getController();
        if(itemType != ItemType.CREW){
            
            throw new InvalidParameters("Invalid item type, expected CREW");
        }

        if(coordinates == null){
            
            throw new InvalidParameters("Invalid coordinates");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(player == context.getSpecialPlayers().getFirst()){
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

                //se devo ancora togliere gente
                if (context.getCrewmates() > 0) {
                    //se non ho più crew (atterraggio anticipato a fine carta)
                    if (player.getShipBoard().getCondensedShip().getTotalCrew() > 0) {
                        controller.getModel().setState(new SlaversCrewRemovalState(context));
                        
                    } else {
                        context.removeSpecialPlayer(player);
                        if(context.getSpecialPlayers().isEmpty()){         //passati tutti
                            controller.getModel().setState(new FlightPhase(controller)); //tutti i giocatori gestiti
                            
                        }
                        else{   //manca qualcuno da gestire
                            context.setCrewmates(context.getDefaultAmount());
                            controller.getModel().setState(new SlaversCrewRemovalState(context)); //manca qualcuno da gestire
                            
                        }
                    }
                } //se non devo più togliere gente
                else {
                    context.removeSpecialPlayer(player);
                    if(context.getSpecialPlayers().isEmpty()){         //passati tutti
                        controller.getModel().setState(new FlightPhase(controller)); //tutti i giocatori gestiti
                        
                    }
                    else{       //manca qualcuno da gestire
                        context.setCrewmates(context.getDefaultAmount());
                        controller.getModel().setState(new SlaversCrewRemovalState(context)); //manca qualcuno da gestire
                        
                    }
                }
            }
            else{
                context.removeSpecialPlayer(player);
                if(context.getSpecialPlayers().isEmpty()){         //passati tutti
                    controller.getModel().setState(new FlightPhase(controller)); //tutti i giocatori gestiti
                    
                }
                else{       //manca qualcuno da gestire
                    context.setCrewmates(context.getDefaultAmount());
                    controller.getModel().setState(new SlaversCrewRemovalState(context)); //manca qualcuno da gestire
                    
                }
            }
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "UseCrew" );
    }
}
