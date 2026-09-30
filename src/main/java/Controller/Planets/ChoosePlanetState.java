package Controller.Planets;

import Controller.Context;
import Controller.Controller;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Components.Planet;
import Model.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This state allows the first player in turn to choose a planet to land on.
 * Each planet can only be selected once. Players who choose a valid, unoccupied planet
 * are removed from the turn order and added to the special players list.
 *
 * Once all players have made a decision or all available planets have been selected,
 * the game transitions to the {@link PlanetsLandState}.
 */
public class ChoosePlanetState extends State {
    /**
     * The list of planets that have been chosen by players.
     */
    List<Planet> chosenPlanets;

    /**
     * Constructs a new ChoosePlanetState.
     *
     * @param context The context of the game.
     */
    public ChoosePlanetState(Context context) {
        super(context);
        this.setPlayerInTurn(context.getPlayers().getFirst());
        this.chosenPlanets = new ArrayList<>();
    }

    /**
     * Constructs a new ChoosePlanetState with the specified context and chosen planets.
     *
     * @param context The context of the game.
     * @param choosenPlanets The list of planets that have been chosen by players so far.
     */
    public ChoosePlanetState(Context context, List<Planet> choosenPlanets) {
        super(context);
        this.chosenPlanets = choosenPlanets;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Called when a player selects a planet.
     * The method verifies that the player is allowed to choose,
     * and that the planet is valid, unoccupied, and not already selected.
     * If the choice is successful, the player is removed from the active list
     * and added to the special players list.
     *
     * When all players have chosen or all planets are taken,
     * the state changes to {@link PlanetsLandState}.
     *
     * @param playerName the name of the player making the choice
     * @param planetName       the name of the chosen planet
     */
    @Override
    public void choosePlanet(String playerName, String planetName) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to choose a planet");
        }

        Planet chosenPlanet = context.getPlanet(planetName);
        if(chosenPlanet == null) {
            
            throw new InvalidParameters("Planet not found");
        }

        if(chosenPlanet.isOccupied()) {
            
            throw new InvalidContextualAction("Planet is already occupied");
        }

        chosenPlanet.setOccupied();
        if(chosenPlanets.contains(chosenPlanet)){
            
            throw new InvalidContextualAction("Planet already chosen");
        } else {
            chosenPlanets.add(chosenPlanet);
        }

        if(context.getSpecialPlayers().contains(player)) {
            
            throw new InvalidContextualAction("Player already chosen a planet");
        }

        context.addSpecialPlayer(player);
        context.removePlayer(player);

        if(context.getPlayers().isEmpty() || new HashSet<>(chosenPlanets).containsAll(context.getPlanets())) { // Handle the case where all players have chosen a planet, or all the planets has been chosen
            controller.getModel().setState(new PlanetsLandState(context, chosenPlanets));
            
        } else {
            controller.getModel().setState(new ChoosePlanetState(context, chosenPlanets));
            
        }
    }

    /**
     * Called when a player decides to skip choosing a planet.
     * The player is removed from the list of active players,
     * and the state is refreshed.
     *
     * @param playerName the name of the player skipping the choice
     */
    @Override
    public void end(String playerName) throws InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn");
        }

        context.removePlayer(player);
        if (!context.getPlayers().isEmpty()) {
            controller.getModel().setState(new ChoosePlanetState(context, chosenPlanets));
            
        } else {
            if (context.getSpecialPlayers().isEmpty()) {
                controller.getModel().setState(new FlightPhase(controller));
                
            } else {
                controller.getModel().setState(new PlanetsLandState(context, chosenPlanets));
                
            }
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "ChoosePlanet", "End" );
    }
}
