package Controller.RealTimeBuilding;

import Controller.Controller;
import Controller.Enums.ComponentOrigin;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.*;
import Controller.State;
import Model.Board.CardDeck;
import Model.Board.FlightBoard;
import Model.Enums.Direction;
import Model.Exceptions.InvalidMethodParameters;
import Model.Game;
import Model.Player;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Model.Board.Timer;
import Controller.GamePhases.FlightPhase;
import Model.Ship.ShipBoard;
import View.Client.Actions.Action;
import View.Client.ClientState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * State that manages the real-time ship building phase.
 * During this phase, players simultaneously assemble their ships
 * by drawing components from a common pool and placing them on their boards.
 *
 * The class coordinates:
 * - Component drawing and placement
 * - Timer management (hourglass)
 * - Component reservation (for advanced levels)
 * - Assembly completion
 *
 */
public class BuildingState extends State {


    /** Map of players who have finished assembly with their starting position */
    private Map<Integer, Player> finishedPlayers = new HashMap<>();

    /** Valid coordinates for component placement based on game level, key is the row i, values are the corresponding valid columns */
    private Map<Integer, List<Integer>> validCoordinates = new HashMap<>();


    /**
     * Constructor that initializes the building state.
     * Sets up valid coordinates based on match level (Trial or Level 2).
     *
     * @param controller The game controller
     * @throws IllegalArgumentException if the match level is invalid
     */
    public BuildingState(Controller controller) {
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

        controller.setQueuedAction(ClientState::net_Start);
    }

    /**
     * Unbooks the deck for a player if they have booked one.
     * This is called when a player draws a component or reserves a component.
     *
     * @param player The player whose deck booking is to be removed
     */
    private void unbookDeck(String player){
        Map<String, CardDeck> bookedDecks=this.getController().getModel().getFlightBoard().getBookedDecks();
        if(bookedDecks.containsKey(player)){
            //remove the entry
            bookedDecks.remove(player);
        }
    }

    /**
     * Allows a player to draw a component from the common pool.
     * The component becomes the player's active component.
     *
     * @param name Player's name
     * @param index Index of the component in the pool
     * @throws InvalidCommand If the player has already finished or timer has expired
     * @throws InvalidParameters If the index is invalid or player doesn't exist
     */
    @Override
    public void getComponent(String name, int index) throws InvalidCommand, InvalidParameters {
        Game model= this.getController().getModel();
        //if the timer is null it's a trial game, so no timer is present, and no needs to go to HourGlassFinishedState
        Timer timer= model.getFlightBoard().getTimer();
        if(timer!=null && timer.getPhase()== Timer.Phase.LAST_PHASE && timer.getTimeLeft()==0.0f){
            this.getController().getModel().setState(new HourGlassFinishedState(this.getController(), finishedPlayers));
        }
        else{
            Player currentPlayer = this.getController().getModel().getPlayer(name);
            if (currentPlayer == null) {
                throw new InvalidParameters("Player not found, strange bug!!!");
            }
            unbookDeck(name);
            if (finishedPlayers.containsValue(currentPlayer)) {
                throw new InvalidCommand("Player already finished");
            }
            if (index < 0 || index >= this.getController().getModel().getTiles().length) {
                throw new InvalidParameters("Invalid index");
            }

            SpaceshipComponent selectedTile = this.getController().getModel().getTiles()[index];

            if(selectedTile == null) {
                throw new InvalidParameters("Component not found");
            }

            selectedTile.setVisible();

            this.getController().getModel().getTiles()[index] = null;

            SpaceshipComponent oldTile= currentPlayer.getShipBoard().getActiveComponent();
            if (oldTile!=null) {
                model.addComponent(oldTile);
            }
            currentPlayer.getShipBoard().setActiveComponent(selectedTile);

        }


    }

    /**
     * Allows a player to reserve their active component.
     * Only available in advanced levels (not Trial).
     *
     * @param name Player's name
     * @throws InvalidCommand If it's a Trial deck, player has finished,
     *                       or has already reserved 2 components
     * @throws InvalidParameters If the player doesn't exist
     */
    @Override
    public void reserveComponent(String name) throws InvalidCommand, InvalidParameters {
        if(this.getController().getMatchLevel()==MatchLevel.TRIAL){
            throw new InvalidCommand("Trial deck");
        }
        //if the timer is null it's a trial game, so no timer is present, and no needs to go to HourGlassFinishedState
        Timer timer= this.getController().getModel().getFlightBoard().getTimer();
        if(timer!=null && timer.getPhase()== Timer.Phase.LAST_PHASE && timer.getTimeLeft()==0.0f){
            this.getController().getModel().setState(new HourGlassFinishedState(this.getController(), finishedPlayers));
        }
        else{
            Player currentPlayer = this.getController().getModel().getPlayer(name);
            if (currentPlayer == null) {
                throw new InvalidParameters("Player not found");
            }
            unbookDeck(name);
            if (finishedPlayers.containsValue(currentPlayer)) {
                throw new InvalidCommand("Player already finished");
            }
            SpaceshipComponent selectedTile = currentPlayer.getShipBoard().getActiveComponent();
            if (selectedTile == null) {
                throw new InvalidCommand("Component not found");
            }
            try {
                currentPlayer.getShipBoard().reserveComponent(selectedTile);
                currentPlayer.getShipBoard().setActiveComponent(null);
            } catch (IllegalStateException e) {
                throw new InvalidCommand("Already reserved 2 tiles");
            }
        }
    }
    /**
     * Places a component on the player's board.
     * The component can come from hand or reserved components.
     *
     * @param name Player's name
     * @param origin Component origin (hand or reserved)
     * @param coordinates Coordinates where to place the component
     * @param orientation Component orientation
     * @throws InvalidCommand If player has finished or has no component in the selected origin
     * @throws InvalidParameters If coordinates are invalid or already occupied
     */
    @Override
    public void placeComponent(String name, ComponentOrigin origin, Coordinates coordinates, Direction orientation) throws InvalidCommand, InvalidParameters {
        Timer timer= this.getController().getModel().getFlightBoard().getTimer();
        Game model= this.getController().getModel();
        //if the timer is null it's a trial game, so no timer is present, and no needs to go to HourGlassFinishedState
        if(timer!=null && timer.getPhase()== Timer.Phase.LAST_PHASE && timer.getTimeLeft()==0.0f){
            this.getController().getModel().setState(new HourGlassFinishedState(this.getController(), finishedPlayers));
        }
        else{
            Player currentPlayer = this.getController().getModel().getPlayer(name);
            if (currentPlayer == null) {
                throw new InvalidParameters("Player not found");
            }
            unbookDeck(name);
            if (finishedPlayers.containsValue(currentPlayer)) {
                throw new InvalidCommand("Player already finished");
            }
            if(!validCoordinates.containsKey(coordinates.getI()) || !validCoordinates.get(coordinates.getI()).contains(coordinates.getJ())){
                throw new InvalidParameters("Invalid coordinates");
            }
            if(currentPlayer.getShipBoard().getComponent(coordinates)!=null){
                throw new InvalidParameters("Coordinates already occupied");
            }

            //inside the switch case is also checked if the component is not adjacent to a tile already placed, since it's not valid
            //it can be checked only after the component is selected, since the connectors and the orientation are not known before




            /*
            must be careful that when placing a element from reserved active tile goes back to the table
            Before doing anything the orientation must be setted,
             */

            SpaceshipComponent activeTile;
            List<SpaceshipComponent> reservedComponents = currentPlayer.getShipBoard().getReservedComponents();
            SpaceshipComponent activeComponent = currentPlayer.getShipBoard().getActiveComponent();

            switch (origin) {
                case HAND:
                activeTile= activeComponent;
                    if (activeTile == null) {
                        throw new InvalidCommand("No active component");
                    }
                    if(!currentPlayer.getShipBoard().isAdjacentToExistingComponent(coordinates)){
                        throw new InvalidParameters("Invalid position, must be adjacent to existing components");
                    }
                    currentPlayer.getShipBoard().setActiveComponent(null);
                    break;

                case FIRST_RESERVED:
                    if (reservedComponents.isEmpty()) {
                        throw new InvalidCommand("No active component");
                    }

                    activeTile= reservedComponents.getFirst();
                    if(!currentPlayer.getShipBoard().isAdjacentToExistingComponent(coordinates)){
                        throw new InvalidParameters("Invalid position, must be adjacent to existing components");
                    }
                    currentPlayer.getShipBoard().removeReservedComponent(0);

                    if (activeComponent!=null) {
                        model.addComponent(activeComponent);
                        currentPlayer.getShipBoard().setActiveComponent(null);
                    }


                    break;

                case SECOND_RESERVED:
                    if (reservedComponents.size()<2) {
                        throw new InvalidCommand("No active component");
                    }

                    activeTile= reservedComponents.get(1);

                    if(!currentPlayer.getShipBoard().isAdjacentToExistingComponent(coordinates)){
                        throw new InvalidParameters("Invalid position, must be adjacent to existing components");
                    }
                    currentPlayer.getShipBoard().removeReservedComponent(1);
                    if (activeComponent!=null) {
                        model.addComponent(activeComponent);
                        currentPlayer.getShipBoard().setActiveComponent(null);
                    }


                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + origin);


            }

            while (activeTile.getOrientation()!=orientation) {
                activeTile.rotate();
            }

            activeTile.setShipBoard(currentPlayer.getShipBoard());
            activeTile.added();
            try {
                currentPlayer.getShipBoard().addComponent(activeTile, coordinates);
            } catch (InvalidMethodParameters e) {
                throw new RuntimeException("Either the map of ship valid position or the convertion between board and matrix coordinates failed, Strange bug!!!");
            }

        }
    }

    /**
     * Allows viewing one of the predictable adventure card decks.
     * Only available in advanced levels.
     *
     * @param name Player's name
     * @param index Index of the deck to view (1-3)
     * @throws InvalidCommand If it's a Trial deck or player hasn't placed components yet
     * @throws InvalidParameters If the index is invalid
     */
    public void lookDeck(String name, int index) throws InvalidCommand, InvalidParameters {

        index-=1;
        if(this.getController().getMatchLevel()==MatchLevel.TRIAL){
            throw new InvalidCommand("Trial deck");
        }
        Game model= this.getController().getModel();
        Timer timer= model.getFlightBoard().getTimer();
        //if the timer is null it's a trial game, so no timer is present, and no needs to go to HourGlassFinishedState
        if(timer!=null && timer.getPhase()== Timer.Phase.LAST_PHASE && timer.getTimeLeft()==0.0f){
            this.getController().getModel().setState(new HourGlassFinishedState(this.getController(), finishedPlayers));
        }
        else{

            Player currentPlayer = this.getController().getModel().getPlayer(name);
            if (currentPlayer == null) {
                throw new InvalidParameters("Player not found");
            }
            unbookDeck(name);
            if (finishedPlayers.containsValue(currentPlayer)) {
                throw new InvalidCommand("Player already finished");
            }
            if(currentPlayer.getShipBoard().isEmpty()){
                throw new InvalidCommand("No tiles placed yet");
            }
            if(index < 0 || index >= 3) {
                throw new InvalidParameters("Invalid index");
            }




            FlightBoard flightBoard= this.getController().getModel().getFlightBoard();
            CardDeck deck=flightBoard.getPeekableCardDeck(index);
            Map<String, CardDeck> bookedDecks=flightBoard.getBookedDecks();

            if(bookedDecks.values().contains(deck)){
                throw new InvalidParameters("Booked deck");
            }else{
                SpaceshipComponent oldTile= currentPlayer.getShipBoard().getActiveComponent();
                if (oldTile != null) {
                    model.addComponent(oldTile);
                    currentPlayer.getShipBoard().setActiveComponent(null);
                }
                bookedDecks.put(name, deck);

                this.getController().setQueuedAction((Action) state -> state.net_ViewCardDeck(name));
            }



        }

    }

    /**
     * Flips the hourglass to proceed to the next phase.
     * Only players who have finished can flip the last hourglass.
     *
     * @param name Player's name
     * @throws InvalidCommand If it's a Trial deck, hourglass isn't finished,
     *                       or player cannot flip it (e.g. hasn't finished,
     *                       and want to flip the last hourglass)
     * @throws InvalidParameters If the player doesn't exist
     */
    public void flipHourGlass(String name) throws InvalidCommand, InvalidParameters {

        if(this.getController().getMatchLevel()==MatchLevel.TRIAL){
            throw new InvalidCommand("Trial deck");
        }

        Game model= this.getController().getModel();
        Timer timer= model.getFlightBoard().getTimer();
        //if the timer is null it's a trial game, so no timer is present, and no needs to go to HourGlassFinishedState
        if(timer!=null && timer.getPhase()==Timer.Phase.LAST_PHASE && timer.getTimeLeft()==0.0f){
            this.getController().getModel().setState(new HourGlassFinishedState(this.getController(), finishedPlayers));
        }
        else{
            Player currentPlayer = this.getController().getModel().getPlayer(name);
            if (currentPlayer == null) {
                throw new InvalidParameters("Player not found");
            }
            unbookDeck(name);

//            SpaceshipComponent oldTile= currentPlayer.getShipBoard().getActiveComponent();
//            model.addComponent(oldTile);
//            currentPlayer.getShipBoard().setActiveComponent(null);

            assert timer!=null: "Timer is null in Level2 error in FlightBoard builder";

            if(timer.getTimeLeft()!=0.0f){

                throw new InvalidCommand("Hourglass not finished");
            }
            if(timer.getPhase()== Timer.Phase.MIDDLE_PHASE && !finishedPlayers.containsValue(currentPlayer)){
                throw new InvalidCommand("Last HourGlass can be flipped only by players that already finished building");
            }

            timer.nextPhase();



        }

    }

    /**
     * Allows a player to choose a pre-built ship from the available options.
     * The ship is set as the player's ship board.
     *
     * @param name Player's name
     * @param index Index of the pre-built ship (0-2)
     * @throws InvalidCommand If player has already finished or ship is invalid (Trial)
     * @throws InvalidParameters If the index is invalid or player doesn't exist
     */
    @Override
    public void preBuiltShip(String name, int index) throws InvalidCommand, InvalidParameters {

        Game model = this.getController().getModel();
        Player currentPlayer = model.getPlayer(name);

        if(currentPlayer == null) {
            throw new InvalidParameters("Player not found");
        }
        unbookDeck(name);

        try {
            ShipBoard shipBoard= model.getPreBuiltShip(index);
            currentPlayer.setShipBoard(shipBoard);
        } catch (Exception e) {
            throw new InvalidParameters("Invalid ship index");
        }

    }

    /**
     * Finishes assembly for a player and places them on the starting grid.
     * If there are components reserved not placed,
     * they are converted into junk (penalty).
     *
     * @param name Player's name
     * @param position Desired starting position (1-4)
     * @throws InvalidCommand If player has already finished or ship is invalid (Trial)
     * @throws InvalidParameters If position is already occupied
     */
    public void finishBuilding(String name, int position) throws InvalidCommand, InvalidParameters {
        Game model= this.getController().getModel();
//        Timer timer= model.getFlightBoard().getTimer();
//        //if the timer is null it's a trial game, so no timer is present, and no needs to go to HourGlassFinishedState
//        if(timer!=null && timer.getPhase()== Timer.Phase.LAST_PHASE && timer.getTimeLeft()==0.0f){
//            this.getController().getModel().setState(new HourGlassFinishedState(this.getController(), finishedPlayers));
//        }
//        else{
            Player currentPlayer = this.getController().getModel().getPlayer(name);
            if (currentPlayer == null) {
                throw new InvalidParameters("Player not found");
            }
            unbookDeck(name);
            if (finishedPlayers.containsValue(currentPlayer)) {
                throw new InvalidCommand("Player already finished");
            }
            if(this.getController().getMatchLevel()==MatchLevel.TRIAL){
                if(!currentPlayer.getShipBoard().validateShip()){
                    throw new InvalidCommand("Ship not valid");
                }
            }
            if(finishedPlayers.containsKey(position)){
                throw new InvalidParameters("Position already occupied");
            }

            if(position<1 || position>model.getPlayers().size()){
                throw new InvalidParameters("Invalid position, must be between 1 and "+model.getPlayers().size());
            }

            try {
                model.getFlightBoard().setStartingPositions(currentPlayer, position);
            } catch (InvalidMethodParameters e) {
                throw new InvalidParameters("Invalid starting position, must be btween 1 and"+model.getPlayers().size());
            }

            SpaceshipComponent oldTile= currentPlayer.getShipBoard().getActiveComponent();
            if (oldTile != null) {
                model.addComponent(oldTile);
                currentPlayer.getShipBoard().setActiveComponent(null);
            }


            int penalty=currentPlayer.getShipBoard().getReservedComponents().size();
            for(int i=0; i<penalty; i++){
                currentPlayer.addJunk();
            }

            finishedPlayers.put(position, currentPlayer);

            if(finishedPlayers.size()==model.getPlayers().size()){
                MatchLevel matchLevel=this.getController().getMatchLevel();

                if (matchLevel == MatchLevel.TRIAL) {
                    //if it's a trial game set at frist PlaceAlienState that will populate cabins autonomously
                    // then, skip FixShipState, since in Trial mode you cannot finish building unless your ship is valid.
                    State state=new PlaceAlienState(this.getController());
                    this.getController().getModel().setState(new FlightPhase(this.getController()));
                } else {
                    //if it's a level2 game, set FixShipState that will allow players to fix their ships
                    FixShipState fixShipState= new FixShipState(this.getController());

                    if(fixShipState.allPlayersHaveValidShips()){

                        //if all players already have valid ships, checks if they can place aliens
                        PlaceAlienState placeAlienState= new PlaceAlienState(this.getController());

                        if(placeAlienState.allPlayersHavePlacedAliens()){
                            //if no players can place aliens, set FlightPhase
                            this.getController().getModel().setState(new FlightPhase(this.getController()));
                        }else{
                            //if at least one player can place aliens, stay in PlaceAlienState
                            this.getController().getModel().setState(placeAlienState);
                        }

                    }else{
                        //if not all players have valid ships, goes in FixShipState
                        this.getController().getModel().setState(fixShipState);
                    }

                }

            }





//        }

    }

    /**
     * Deletes a component from the player's ship board.
     * Only available in Trial games.
     *
     * @param name Player's name
     * @param coordinates Coordinates of the component to delete
     * @throws InvalidCommand If it's not a Trial game
     * @throws InvalidParameters If the player doesn't exist or coordinates are invalid
     */
    @Override
    public void deleteComponent(String name, Coordinates coordinates) throws InvalidCommand, InvalidParameters {

        if(!(this.getController().getMatchLevel()==MatchLevel.TRIAL)){
            throw new InvalidCommand("Delete component command is available only in Trial Games");
        }

        Game model=this.getController().getModel();
        Player currrentPlayer = model.getPlayer(name);
        if (currrentPlayer == null) {
            throw new InvalidParameters("Player not found");
        }
        ShipBoard shipBoard = currrentPlayer.getShipBoard();

        // Check if the coordinates are valid
        if (!validCoordinates.containsKey(coordinates.getI()) || !validCoordinates.get(coordinates.getI()).contains(coordinates.getJ())) {
            throw new InvalidParameters("Invalid coordinates");
        }

        if(coordinates.getI()==7&&coordinates.getJ()==7){
            throw new InvalidParameters("Central Cabin can't be removed");
        }
        // Check if the coordinates correspond to a valid component
        SpaceshipComponent component = shipBoard.getComponent(coordinates);
        if (component == null) {
            throw new InvalidParameters("No component found at the given coordinates");
        }



        try {
            shipBoard.removeComponent(coordinates);
        } catch (InvalidMethodParameters e) {
            throw new RuntimeException("Either the map of ship valid position or the convertion between board and matrix coordinates failed, Strange bug!!!");
        }

        //Put the component back into the tiles in the table;
        SpaceshipComponent[] allTiles = model.getTiles();
        boolean put=false;
        int length=allTiles.length;
        for(int i=0; (i<length)&&!put; i++){
            if(allTiles[i]==null){
                put=true;
                allTiles[i]=component;
            }
        }








    }

    /**
     * Returns the list of available commands in this state.
     * This includes commands for building, reserving, and placing components,
     * as well as managing the hourglass and viewing decks.
     * * @return List of available commands
     */
    @Override
    public List<String> getAvailableCommands(){
        return List.of("FinishBuilding", "GetComponent", "PlaceComponent", "ReserveComponent", "LookDeck", "FlipHourGlass", "DeleteComponent", "PreBuiltShip");
    }
}
