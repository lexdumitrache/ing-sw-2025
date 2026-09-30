package Controller.RealTimeBuilding;

import Controller.Controller;
import Controller.Exceptions.*;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Exceptions.InvalidMethodParameters;
import Model.Game;
import Model.Player;

import java.util.List;
import java.util.Map;

/**
 * Transitional state when the building phase timer has expired.
 * Players who haven't finished yet must choose in real-time a starting position.
 *
 *
 */
public class HourGlassFinishedState extends State {

    /** Map of players who have already finished */
    Map<Integer, Player> finishedPlayers;

    /**
     * Constructor that receives the map of already finished players, form BuildingState.
     *
     * @param controller The game controller
     * @param finishedPlayerss Map of players who have already finished
     */
    public HourGlassFinishedState(Controller controller, Map<Integer, Player> finishedPlayerss) {
        super(controller);
        this.finishedPlayers = finishedPlayerss;
    }

    /**
     * Allows remaining players to choose a starting position
     * after the timer expires.
     *
     * @param name Player's name
     * @param position Desired starting position
     * @throws InvalidCommand If the player has already finished
     * @throws InvalidParameters If the position is already occupied
     */
    public void finishBuilding(String name, int position) throws InvalidCommand, InvalidParameters {
        Game model= this.getController().getModel();

        Player currentPlayer = model.getPlayer(name);
        if (currentPlayer == null) {
            throw new InvalidParameters("Player not found");
        }
        if (finishedPlayers.containsValue(currentPlayer)) {
            throw new InvalidCommand("Player already finished");
        }

//        if(this.getController().getMatchLevel()== MatchLevel.TRIAL){
//            if(!currentPlayer.getShipBoard().validateShip()){
//                throw new InvalidCommand("Ship not valid");
//            }
//        }
//  non dovrebe mai trovarsi in questo stato se è in trial

//        SpaceshipComponent oldTile= currentPlayer.getShipBoard().getActiveComponent();
//        model.addComponent(oldTile);
//        currentPlayer.getShipBoard().setActiveComponent(null);


        if(finishedPlayers.containsKey(position)){
            throw new InvalidParameters("Position already occupied");
        }

        try {
            model.getFlightBoard().setStartingPositions(currentPlayer, position);
        } catch (InvalidMethodParameters e) {
            throw new InvalidParameters("Invalid starting position, must be btween 1 and 4");
        }

        int penalty=currentPlayer.getShipBoard().getReservedComponents().size();
        for(int i=0; i<penalty; i++){
            currentPlayer.addJunk();
        }

        finishedPlayers.put(position, currentPlayer);

        if(finishedPlayers.size()==model.getPlayers().size()){
            FixShipState fixShipState= new FixShipState(this.getController());
            this.getController().getModel().setState(fixShipState);

            if(fixShipState.allPlayersHaveValidShips()){
                //if all players already have valid ships, set PlaceAlienState
                PlaceAlienState placeAlienState= new PlaceAlienState(this.getController());
                this.getController().getModel().setState(placeAlienState);

                if(placeAlienState.allPlayersHavePlacedAliens()){
                    //if no players can place aliens, set FlightPhase
                    this.getController().getModel().setState(new FlightPhase(this.getController()));
                }

            }
        }


    }

    /**
     * Returns a list of available commands for this state.
     * In this case, the only command is "FinishBuilding".
     *
     * @return List of available commands
     */
    public List<String> getAvailableCommands(){
        return List.of(
            "FinishBuilding"
        );
    }
}
