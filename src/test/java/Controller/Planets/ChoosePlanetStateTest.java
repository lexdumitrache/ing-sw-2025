package Controller.Planets;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Model.Board.AdventureCards.Components.Planet;
import Model.Enums.Good;
import Model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChoosePlanetStateTest {

    private Controller controller;
    private Context context;
    private ChoosePlanetState state;
    private Player player1;
    private Player player2;
    private Planet planet1;
    private Planet planet2;

    @BeforeEach
    void setUp() {
        controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Player1");
        controller.getModel().addPlayer("Player2");
        
        player1 = controller.getModel().getPlayer("Player1");
        player2 = controller.getModel().getPlayer("Player2");
        
        context = new Context(controller);
        
        // Set up the context's player list
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        context.getPlayers().add(player2);
        
        // Create planets
        planet1 = new Planet("Planet1", Arrays.asList(Good.RED, Good.BLUE));
        planet2 = new Planet("Planet2", Arrays.asList(Good.GREEN, Good.YELLOW));
        
        // Add planets to context
        try {
            java.lang.reflect.Field planetsField = Context.class.getDeclaredField("planets");
            planetsField.setAccessible(true);
            planetsField.set(context, Arrays.asList(planet1, planet2));
        } catch (Exception e) {
            fail("Failed to set planets: " + e.getMessage());
        }
        
        state = new ChoosePlanetState(context, new ArrayList<>());
    }

    @Test
    void testConstructor() {
        assertNotNull(state);
        assertEquals(player1, state.getPlayerInTurn());
    }

    @Test
    void testChoosePlanet_ValidChoice() throws InvalidContextualAction, InvalidParameters {
        state.choosePlanet("Player1", "Planet1");
        
        assertFalse(controller.getModel().isError());
        assertTrue(planet1.isOccupied());
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertFalse(context.getPlayers().contains(player1));
        // Should transition to next player
        assertInstanceOf(ChoosePlanetState.class, controller.getModel().getState());
    }

    @Test
    void testChoosePlanet_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.choosePlanet("Player2", "Planet1"));
        
        assertEquals("It's not your turn to choose a planet", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_InvalidPlanet() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.choosePlanet("Player1", "NonExistentPlanet"));
        
        assertEquals("Planet not found", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_OccupiedPlanet() throws InvalidContextualAction, InvalidParameters {
        // First occupy the planet
        planet1.setOccupied();
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.choosePlanet("Player1", "Planet1"));
        
        assertEquals("Planet is already occupied", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_AlreadyChosenPlanet() throws InvalidContextualAction, InvalidParameters {
        // First choose the planet
        state.choosePlanet("Player1", "Planet1");
        
        // Reset the state for the next player
        context.getPlayers().clear();
        context.getPlayers().add(player2);
        ChoosePlanetState newState = (ChoosePlanetState) controller.getModel().getState();
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> newState.choosePlanet("Player2", "Planet1"));
        
        assertEquals("Planet is already occupied", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_PlayerAlreadyChosen() throws InvalidContextualAction, InvalidParameters {
        // First add player to special players
        context.addSpecialPlayer(player1);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.choosePlanet("Player1", "Planet1"));
        
        assertEquals("Player already chosen a planet", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_AllPlayersChosen() throws InvalidContextualAction, InvalidParameters {
        // Set up context with only one player
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        
        state.choosePlanet("Player1", "Planet1");
        
        assertFalse(controller.getModel().isError());
        // Should transition to PlanetsLandState
        assertInstanceOf(PlanetsLandState.class, controller.getModel().getState());
    }

    @Test
    void testChoosePlanet_AllPlanetsChosen() throws InvalidContextualAction, InvalidParameters {
        // Choose all planets
        state.choosePlanet("Player1", "Planet1");
        
        // Reset the state for the next player
        context.getPlayers().clear();
        context.getPlayers().add(player2);
        ChoosePlanetState newState = (ChoosePlanetState) controller.getModel().getState();
        
        newState.choosePlanet("Player2", "Planet2");
        
        assertFalse(controller.getModel().isError());
        // Should transition to PlanetsLandState
        assertInstanceOf(PlanetsLandState.class, controller.getModel().getState());
    }

    @Test
    void testEnd_ValidPlayer() throws InvalidParameters {
        state.end("Player1");
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        // Should transition to next player
        assertInstanceOf(ChoosePlanetState.class, controller.getModel().getState());
    }

    @Test
    void testEnd_WrongPlayer() {
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.end("Player2"));
        
        assertEquals("It's not your turn", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testEnd_AllPlayersSkip() throws InvalidParameters {
        // Set up context with only one player
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        
        state.end("Player1");
        
        assertFalse(controller.getModel().isError());
        // Should transition to FlightPhase
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testGetAvailableCommands() {
        List<String> commands = state.getAvailableCommands();
        assertEquals(2, commands.size());
        assertTrue(commands.contains("ChoosePlanet"));
        assertTrue(commands.contains("End"));
    }

    @Test
    void testConstructorWithChosenPlanets() {
        List<Planet> chosenPlanets = new ArrayList<>();
        chosenPlanets.add(planet1);
        ChoosePlanetState stateWithChosen = new ChoosePlanetState(context, chosenPlanets);
        
        assertNotNull(stateWithChosen);
        assertEquals(player1, stateWithChosen.getPlayerInTurn());
    }

    @Test
    void testChoosePlanet_AlreadyChosenPlanetInList() throws InvalidContextualAction, InvalidParameters {
        // Create state with pre-chosen planets
        List<Planet> chosenPlanets = new ArrayList<>();
        chosenPlanets.add(planet1);
        ChoosePlanetState stateWithChosen = new ChoosePlanetState(context, chosenPlanets);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> stateWithChosen.choosePlanet("Player1", "Planet1"));
        
        assertEquals("Planet already chosen", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_NullPlanet() {
        // Test with planet name that doesn't exist in context
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.choosePlanet("Player1", "NonExistentPlanet"));
        
        assertEquals("Planet not found", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_EmptyPlanetsList() throws Exception {
        // Set empty planets list
        java.lang.reflect.Field planetsField = Context.class.getDeclaredField("planets");
        planetsField.setAccessible(true);
        planetsField.set(context, new ArrayList<>());
        
        InvalidParameters exception = assertThrows(InvalidParameters.class,
            () -> state.choosePlanet("Player1", "Planet1"));
        
        assertEquals("Planet not found", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_TransitionToPlanetsLandStateWhenAllPlanetsChosen() throws InvalidContextualAction, InvalidParameters {
        // Create context with 3 players and 2 planets to test all planets chosen scenario
        controller.getModel().addPlayer("Player3");
        Player player3 = controller.getModel().getPlayer("Player3");
        context.getPlayers().add(player3);
        
        // First player chooses planet1
        state.choosePlanet("Player1", "Planet1");
        
        // Get new state and have second player choose planet2
        ChoosePlanetState newState = (ChoosePlanetState) controller.getModel().getState();
        newState.choosePlanet("Player2", "Planet2");
        
        // Should transition to PlanetsLandState since all planets are chosen
        assertInstanceOf(PlanetsLandState.class, controller.getModel().getState());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_ContinueWithNextPlayerWhenPlanetsRemain() throws InvalidContextualAction, InvalidParameters {
        // Add a third planet
        Planet planet3 = new Planet("Planet3", Arrays.asList(Good.RED, Good.GREEN));
        try {
            java.lang.reflect.Field planetsField = Context.class.getDeclaredField("planets");
            planetsField.setAccessible(true);
            List<Planet> planets = new ArrayList<>(Arrays.asList(planet1, planet2, planet3));
            planetsField.set(context, planets);
        } catch (Exception e) {
            fail("Failed to set planets: " + e.getMessage());
        }
        
        state.choosePlanet("Player1", "Planet1");
        
        // Should continue with ChoosePlanetState since planets remain
        assertInstanceOf(ChoosePlanetState.class, controller.getModel().getState());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testEnd_ContinueWithNextPlayer() throws InvalidParameters {
        // Add third player to ensure continuation
        controller.getModel().addPlayer("Player3");
        Player player3 = controller.getModel().getPlayer("Player3");
        context.getPlayers().add(player3);
        
        state.end("Player1");
        
        assertFalse(controller.getModel().isError());
        assertFalse(context.getPlayers().contains(player1));
        assertInstanceOf(ChoosePlanetState.class, controller.getModel().getState());
    }

    @Test
    void testEnd_LastPlayerTransitionsToFlightPhase() throws InvalidParameters {
        // Remove all but one player
        context.getPlayers().clear();
        context.getPlayers().add(player1);
        
        state.end("Player1");
        
        assertFalse(controller.getModel().isError());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }

    @Test
    void testChoosePlanet_PlayerNotInModel() {
        // Test with player not in the model
        NullPointerException exception = assertThrows(NullPointerException.class,
            () -> state.choosePlanet("NonExistentPlayer", "Planet1"));
    }

    @Test
    void testEnd_PlayerNotInModel() {
        // Test with player not in the model
        NullPointerException exception = assertThrows(NullPointerException.class,
            () -> state.end("NonExistentPlayer"));
    }

    @Test
    void testChoosePlanet_SetOccupiedReturnsFalse() throws Exception {
        // Create a planet that's already occupied
        Planet occupiedPlanet = new Planet("OccupiedPlanet", Arrays.asList(Good.RED));
        occupiedPlanet.setOccupied(); // First call returns true
        
        // Add to context
        java.lang.reflect.Field planetsField = Context.class.getDeclaredField("planets");
        planetsField.setAccessible(true);
        List<Planet> planets = new ArrayList<>(Arrays.asList(planet1, planet2, occupiedPlanet));
        planetsField.set(context, planets);
        
        InvalidContextualAction exception = assertThrows(InvalidContextualAction.class,
            () -> state.choosePlanet("Player1", "OccupiedPlanet"));
        
        assertEquals("Planet is already occupied", exception.getMessage());
        assertFalse(controller.getModel().isError());
    }

    @Test
    void testChoosePlanet_EdgeCaseEmptyChosenPlanetsList() throws InvalidContextualAction, InvalidParameters {
        // Test with null chosen planets list (should be initialized as empty)
        ChoosePlanetState stateWithNullChosen = new ChoosePlanetState(context);
        
        // Initialize chosenPlanets to avoid NullPointerException
        java.lang.reflect.Field chosenPlanetsField;
        try {
            chosenPlanetsField = ChoosePlanetState.class.getDeclaredField("chosenPlanets");
            chosenPlanetsField.setAccessible(true);
            chosenPlanetsField.set(stateWithNullChosen, new ArrayList<>());
        } catch (Exception e) {
            fail("Failed to set chosenPlanets: " + e.getMessage());
        }
        
        // This should work normally
        stateWithNullChosen.choosePlanet("Player1", "Planet1");
        
        assertFalse(controller.getModel().isError());
        assertTrue(planet1.isOccupied());
    }

    @Test
    void testChoosePlanet_SpecialPlayersListHandling() throws InvalidContextualAction, InvalidParameters {
        // Ensure special players list is properly initialized
        assertNotNull(context.getSpecialPlayers());
        
        state.choosePlanet("Player1", "Planet1");
        
        assertTrue(context.getSpecialPlayers().contains(player1));
        assertFalse(context.getPlayers().contains(player1));
    }

    @Test
    void testStateTransitions_ComplexScenario() throws InvalidContextualAction, InvalidParameters {
        // Test complex scenario with multiple players and planets
        controller.getModel().addPlayer("Player3");
        Player player3 = controller.getModel().getPlayer("Player3");
        context.getPlayers().add(player3);
        
        Planet planet3 = new Planet("Planet3", Arrays.asList(Good.BLUE));
        try {
            java.lang.reflect.Field planetsField = Context.class.getDeclaredField("planets");
            planetsField.setAccessible(true);
            List<Planet> planets = new ArrayList<>(Arrays.asList(planet1, planet2, planet3));
            planetsField.set(context, planets);
        } catch (Exception e) {
            fail("Failed to set planets: " + e.getMessage());
        }
        
        // Player1 chooses planet1
        state.choosePlanet("Player1", "Planet1");
        ChoosePlanetState state2 = (ChoosePlanetState) controller.getModel().getState();
        
        // Player2 skips
        state2.end("Player2");
        ChoosePlanetState state3 = (ChoosePlanetState) controller.getModel().getState();
        
        // Player3 chooses planet2
        state3.choosePlanet("Player3", "Planet2");
        
        // Should be in PlanetsLandState since two planets are chosen and one player skipped
        assertInstanceOf(PlanetsLandState.class, controller.getModel().getState());
    }
}