package Controller.RealTimeBuilding;

import Controller.Controller;
import Controller.Enums.CrewType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.*;
import Controller.State;
import Controller.GamePhases.FlightPhase;
import Model.Board.FlightBoard;
import Model.Enums.Crewmates;
import Model.Game;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.CondensedShip;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class that tracks which types of aliens a player can still insert.
 * Each player can insert at most one brown alien and one purple alien.
 */
 class CanInsertAliens implements Serializable {

    /** Indicates if the player can still insert a brown alien */
    private boolean canInstertBrown;

    /** Indicates if the player can still insert a purple alien */
    private boolean canInsertPurple;

    /**
     * Constructor that initializes insertion possibilities.
     *
     * @param canInstertBrown If can insert a brown alien
     * @param canInsertPurple If can insert a purple alien
     */
    public CanInsertAliens(boolean canInstertBrown, boolean canInsertPurple) {
        this.canInstertBrown = canInstertBrown;
        this.canInsertPurple = canInsertPurple;
    }

    /**
     * Checks if can insert a brown alien.
     *
     * @return true if can insert a brown alien
     */
    public boolean getCanInstertBrown() {
        return canInstertBrown;
    }

    /**
     * Checks if can insert a purple alien.
     *
     * @return true if can insert a purple alien
     */
    public boolean getCanInsertPurple() {
        return canInsertPurple;
    }

    /**
     * Records the insertion of a brown alien.
     * Prevents further brown alien insertions.
     */
    public void insertedBrown() {
        this.canInstertBrown = false;
    }

    /**
     * Records the insertion of a purple alien.
     * Prevents further purple alien insertions.
     */
    public void insertedPurple() {
        this.canInsertPurple = false;
    }
}

/**
 * State that manages the placement of aliens in cabins.
 */
 public class PlaceAlienState extends State {


    /** Map tracking which aliens each player can still insert */
    Map<Player, CanInsertAliens> playersAlienAvailability = new HashMap<>();

    /**
     * Fills all empty cabins with human crew.
     *
     * @param condensedShip The condensed ship to fill
     */
    private void fillCabinsWithHumans(CondensedShip condensedShip) {
        List<Cabin> cabins = condensedShip.getCabins();
        for (Cabin cabin : cabins) {
            if (cabin.getOccupants() == Crewmates.EMPTY) {
                cabin.setOccupants(Crewmates.DOUBLE_HUMAN);
            }
        }

    }

    /**
     * Constructor that determines which players can insert aliens.
     * Checks for appropriate life support modules through CondensedShip class.
     *
     * @param controller The game controller
     */
    public PlaceAlienState(Controller controller) {
        super(controller);

        Game model= this.getController().getModel();
        FlightBoard flightBoard= model.getFlightBoard();

        flightBoard.setUpcomingCardDeck();

        List<Player> players = model.getPlayers();
        MatchLevel matchLevel = controller.getMatchLevel();
        for (Player player : players) {
            CondensedShip condensedShip = player.getShipBoard().getCondensedShip();

            if (matchLevel== MatchLevel.LEVEL2) {

                //Since the central cabin can only contain humans, we need to set it to cannot contain purple or brown aliens
                SpaceshipComponent centralTile = player.getShipBoard().getComponent(new Coordinates(7,7));
                if((centralTile != null) && condensedShip.getCabins().contains(centralTile)) {
                    Cabin centralCabin = (Cabin) centralTile;
                    centralCabin.setCanContainPurple(0);
                    centralCabin.setCanContainBrown(0);
                }else{
                    throw new RuntimeException("Bug: Central tile not found by PlaceAlienState for player, either null or not a cabin");
                }


                boolean localCanContainBrown = condensedShip.canContainBrown();
                boolean localCanContainPurple = condensedShip.canContainPurple();

                if (localCanContainBrown || localCanContainPurple) {
                    playersAlienAvailability.put(player, new CanInsertAliens(localCanContainBrown, localCanContainPurple));
                    player.getShipBoard().setValid(false);
                }else{
                    fillCabinsWithHumans(condensedShip);
                    player.getShipBoard().setValid(true);
                }

            }else{
                fillCabinsWithHumans(condensedShip);
                player.getShipBoard().setValid(true);
            }

        }



    }

    /**
     * Checks if all players have placed their aliens.
     * @return true if all players have placed their aliens, false otherwise
     */
    public boolean allPlayersHavePlacedAliens() {
        // Check if all players have placed their aliens
        return playersAlienAvailability.isEmpty();
    }

    /**
     * Places an alien in a specific cabin.
     * The cabin must have appropriate life support modules.
     *
     * @param name Player's name
     * @param coordinates Cabin coordinates
     * @param type Type of alien to insert
     * @throws InvalidCommand If player has already placed that alien type
     *                       or cabin cannot contain it
     * @throws InvalidParameters If coordinates don't correspond to a valid cabin
     */
    @Override
    public void placeCrew(String name, Coordinates coordinates, CrewType type) throws InvalidCommand, InvalidParameters {
        Player currentPlayer = this.getController().getModel().getPlayer(name);
        if (currentPlayer == null) {
            throw new InvalidParameters("Player not found");
        }
        if (!playersAlienAvailability.containsKey(currentPlayer)) {
            throw new InvalidCommand("Already placed aliens");
        }
        if (type == CrewType.BROWN_ALIEN && !playersAlienAvailability.get(currentPlayer).getCanInstertBrown()) {
            throw new InvalidCommand("Already placed brown alien");
        }
        if (type == CrewType.PURPLE_ALIEN && !playersAlienAvailability.get(currentPlayer).getCanInsertPurple()) {
            throw new InvalidCommand("Already placed purple alien");
        }
        // Check if the coordinates correspond to a cabin
        ShipBoard shipBoard= currentPlayer.getShipBoard();
        SpaceshipComponent shipComponent = shipBoard.getComponent(coordinates);
        if (shipComponent == null || !(shipBoard.getCondensedShip().getCabins().contains(shipComponent))) {
            throw new InvalidParameters("Invalid coordinates, not a cabin");
        }



        //Check if the cabin can contain the alien type specified
        Cabin cabin = (Cabin) shipComponent;
        if (type == CrewType.BROWN_ALIEN && !cabin.getCanContainBrown()) {
            throw new InvalidCommand("Cabin cannot contain brown alien");
        }
        if (type == CrewType.PURPLE_ALIEN && !cabin.getCanContainPurple()) {
            throw new InvalidCommand("Cabin cannot contain purple alien");
        }
        // and it's not already occupied
        if (cabin.getOccupants() != Crewmates.EMPTY) {
            throw new InvalidCommand("Cabin already occupied");
        }


        // insert the alien in the cabin
        // and update the playersAlienAvailability map
        CanInsertAliens canInsertAliens = playersAlienAvailability.get(currentPlayer);

        if (type == CrewType.BROWN_ALIEN) {
            cabin.setOccupants(Crewmates.BROWN_ALIEN);
            shipBoard.getCondensedShip().getAliens().setBrownAlien(true);
            canInsertAliens.insertedBrown();
        } else if (type == CrewType.PURPLE_ALIEN) {
            cabin.setOccupants(Crewmates.PURPLE_ALIEN);
            shipBoard.getCondensedShip().getAliens().setPurpleAlien(true);
            canInsertAliens.insertedPurple();
        }
        // if both aliens are placed, remove the player from the map, and fill all remaining cabins with humans, furthermore set the ship as valid

        if (!canInsertAliens.getCanInstertBrown() && !canInsertAliens.getCanInsertPurple()) {
            playersAlienAvailability.remove(currentPlayer);
            fillCabinsWithHumans(shipBoard.getCondensedShip());
            shipBoard.setValid(true);
        }

        //If a Player have placed all crew
        boolean havePlacedAllCrew = true;
        List<Cabin> allCabins = shipBoard.getCondensedShip().getCabins();
        int length=allCabins.size();

        for(int i=0;(i<length)&&havePlacedAllCrew;i++){
            Cabin currentCabin = allCabins.get(i);
            if (currentCabin.getOccupants() == Crewmates.EMPTY) {
                havePlacedAllCrew = false;
            }

        }

        if(havePlacedAllCrew){
            playersAlienAvailability.remove(currentPlayer);
            shipBoard.setValid(true);
        }

        // If the map is empty, change the state to the next one
        if (playersAlienAvailability.isEmpty()) {
            this.getController().getModel().setState(new FlightPhase(this.getController()));
        }


    }

    /**
     * Returns a list of available commands for this state.
     * In this case, the only commands are "PlaceHuman", "PlaceBrownAlien", and "PlacePurpleAlien".
     *
     * @return List of available commands
     */
    public List<String> getAvailableCommands(){
        return List.of( "PlaceHuman", "PlaceBrownAlien", "PlacePurpleAlien");
    }



}
