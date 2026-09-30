package Controller.RealTimeBuilding;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.FlightBoard;
import Model.Exceptions.InvalidMethodParameters;
import Model.Game;
import Model.Player;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * State that manages the correction of invalid ships for Level 2
 * After the building phase, players with ships that violate rules
 * must remove components until they are compliant.
 */
 public class FixShipState extends State {

    /** List of players with invalid ships that need to be corrected */
    List<Player> playersWithInvalidShip = new ArrayList<>();

    /** Valid coordinates for component placement */
    Map<Integer, List<Integer>> validCoordinates = new HashMap<>();

    /**
     * Constructor that identifies players with invalid ships,
     * and put them in, playersWithInvalidShip list.
     * Initializes valid coordinates and validates all ships.
     *
     * @param controller The game controller
     */
    FixShipState(Controller controller) {

        super(controller);





        MatchLevel matchLevel=controller.getMatchLevel();
        if(matchLevel== MatchLevel.TRIAL){

            this.validCoordinates.put(5, List.of(7));
            this.validCoordinates.put(6, List.of(6,7,8));
            this.validCoordinates.put(7, List.of(5,6,7,8,9));
            this.validCoordinates.put(8, List.of(5,6,7,8,9));
            this.validCoordinates.put(9, List.of(5,6,8,9));
        }
        else if(matchLevel== MatchLevel.LEVEL2){

            this.validCoordinates.put(5, List.of(6,8));
            this.validCoordinates.put(6, List.of(5,6,7,8,9));
            this.validCoordinates.put(7, List.of(4,5,6,7,8,9,10));
            this.validCoordinates.put(8, List.of(4,5,6,7,8,9,10));
            this.validCoordinates.put(9, List.of(4,5,6,8,9,10));
        }
        else{
            throw new IllegalArgumentException("Invalid match level");
        }

        Game model= this.getController().getModel();
        FlightBoard flightBoard= model.getFlightBoard();





        List<Player> allPlayers= model.getPlayers();
        for (Player player : allPlayers) {
            ShipBoard shipBoard=player.getShipBoard();
            if(!shipBoard.validateShip()){
                playersWithInvalidShip.add(player);
                player.getShipBoard().setValid(false);
            }else{
                player.getShipBoard().setValid(true);
            }
        }

    }

    /**
     * Checks if all players have valid ships.
     * If the list of players with invalid ships is empty, returns true.
     *
     * @return true if all players have valid ships, false otherwise
     */
    public boolean allPlayersHaveValidShips() {
        return playersWithInvalidShip.isEmpty();
    }


    /**
     * Removes a component from the ship to correct rule violations.
     * If by removing the component the ship becomes valid,
     * the player is removed from the list of players with invalid ships.
     * The player receives a penalty (junk) for each removed component.
     *
     * @param name Player's name
     * @param coordinates Coordinates of the component to remove
     * @throws InvalidCommand If the player doesn't have an invalid ship
     * @throws InvalidParameters If coordinates are invalid or contain no component
     */
    @Override
    public void deleteComponent(String name, Coordinates coordinates) throws InvalidCommand, InvalidParameters {
        Player currrentPlayer = this.getController().getModel().getPlayer(name);
        if (currrentPlayer == null) {
            throw new InvalidParameters("Player not found");
        }
        if (!playersWithInvalidShip.contains(currrentPlayer)) {
            throw new InvalidCommand("Player not in the list of players with invalid ship");
        }
        ShipBoard shipBoard = currrentPlayer.getShipBoard();

        // Check if the coordinates are valid
        if (!validCoordinates.containsKey(coordinates.getI()) || !validCoordinates.get(coordinates.getI()).contains(coordinates.getJ())) {
            throw new InvalidParameters("Invalid coordinates");
        }
        // Check if the coordinates correspond to a valid component
        SpaceshipComponent component = shipBoard.getComponent(coordinates);
        if (component == null) {
            throw new InvalidParameters("No component found at the given coordinates");
        }

        // apply removed on the tile
        component.removed();
        // remove the tile from the ship matrix
        try {
            shipBoard.removeComponent(coordinates);
        } catch (InvalidMethodParameters e) {
            throw new RuntimeException("Either the map of ship valid position or the convertion between board and matrix coordinates failed, Strange bug!!!");
        }

        // add junk
        currrentPlayer.addJunk();

        //check if the ship is now valid and if so remove the player from the list, and set the ship as valid
        if (shipBoard.validateShip()) {
            playersWithInvalidShip.remove(currrentPlayer);
            shipBoard.setValid(true);

            // If all players have valid ships, change state to PlaceAlienState
            if (allPlayersHaveValidShips()) {

                PlaceAlienState placeAlienState= new PlaceAlienState(this.getController());


                if(placeAlienState.allPlayersHavePlacedAliens()){
                    //if no players can place aliens, set FlightPhase
                    this.getController().getModel().setState(new FlightPhase(this.getController()));
                }else{
                    this.getController().getModel().setState(placeAlienState);
                }
            }
        }



    }

    /**
     * Returns a list of available commands for this state.
     * In this case, the only command is "DeleteComponent".
     *
     * @return List of available commands
     */
    public List<String> getAvailableCommands(){
        return List.of( "DeleteComponent");
    }

}
