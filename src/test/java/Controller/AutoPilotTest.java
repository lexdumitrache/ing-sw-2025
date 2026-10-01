package Controller;

import Controller.Enums.MatchLevel;
import Controller.GamePhases.FlightPhase;
import Controller.GamePhases.RewardsPhase;
import Controller.PreMatchLobby.LogInState;
import Controller.RealTimeBuilding.BuildingState;
import Controller.RealTimeBuilding.PlaceAlienState;
import Model.Player;
import TestUtils.TestStateManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The auto-pilot plays for disconnected players so that a game never waits for them forever.
 */
public class AutoPilotTest {

    @BeforeEach
    public void playImmediately() {
        Controller.setAutoPilotDelay(0);
    }

    @AfterEach
    public void restoreDelay() {
        Controller.setAutoPilotDelay(15000);
    }

    private static void disconnectEveryone(Controller controller) {
        for (Player player : controller.getModel().getPlayers()) {
            controller.playerDisconnected(player.getName());
        }
    }

    /**
     * Lets the auto-pilot play until it has nothing left to do.
     */
    private static void autoPilotUntilIdle(Controller controller) {
        for (int i = 0; i < 200 && controller.runAutoPilot(); i++) {
            // keep playing
        }
    }

    @Test
    public void testDisconnectingInTheLobbyRemovesThePlayer() {
        final Controller controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Anna");
        controller.getModel().addPlayer("Bob");
        assertInstanceOf(LogInState.class, controller.getModel().getState());

        controller.playerDisconnected("Bob");

        assertNull(controller.getModel().getPlayer("Bob"));
        assertFalse(controller.isDisconnected("Bob"));
    }

    @ParameterizedTest
    @EnumSource(MatchLevel.class)
    public void testAbsentPlayersFinishBuilding(MatchLevel level) {
        final Controller controller = TestStateManager.finishedBuildingAllValid(level).getController();
        disconnectEveryone(controller);

        assertTrue(controller.runAutoPilot());

        // the auto-pilot may go on and play the flight too: what matters is that building is over
        assertFalse(controller.getModel().getState() instanceof BuildingState,
                "absent players should have finished building");
    }

    @Test
    public void testAbsentPlayersFillCabinsWithHumans() throws Exception {
        final Controller controller = TestStateManager.finishedBuildingAllValid(MatchLevel.LEVEL2).getController();
        controller.finishBuilding("Anna", 1);
        controller.finishBuilding("Bob", 2);
        disconnectEveryone(controller);

        autoPilotUntilIdle(controller);

        assertFalse(controller.getModel().getState() instanceof PlaceAlienState, "crew placement should be over");
    }

    @Test
    public void testAbsentLeaderDrawsTheNextCard() {
        final Controller controller = TestStateManager.flightPhase2Players(MatchLevel.TRIAL).getController();
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
        final String leader = controller.getModel().getFlightBoard().getTurnOrder()[0].getName();

        controller.playerDisconnected(leader);
        autoPilotUntilIdle(controller);

        assertNotNull(controller.getModel().getCurrentCardImagePath(), "the absent leader should have drawn a card");
    }

    @Test
    public void testConnectedPlayersAreNotPlayedFor() {
        final Controller controller = TestStateManager.flightPhase2Players(MatchLevel.TRIAL).getController();

        assertFalse(controller.runAutoPilot(), "nobody is disconnected");
        assertNull(controller.getModel().getCurrentCardImagePath());
    }

    /**
     * If everyone leaves, the auto-pilot must be able to play the whole game to the end, whatever cards come up.
     */
    @ParameterizedTest
    @EnumSource(MatchLevel.class)
    public void testAbsentPlayersCanFinishAWholeGame(MatchLevel level) {
        for (int game = 0; game < 5; game++) {
            final Controller controller = TestStateManager.finishedBuildingAllValid(level).getController();
            disconnectEveryone(controller);

            autoPilotUntilIdle(controller);

            assertInstanceOf(RewardsPhase.class, controller.getModel().getState(),
                    "game " + game + " got stuck in " + controller.getModel().getState().getClass().getSimpleName());
        }
    }
}
