package Controller.RealTimeBuilding;

import Controller.Controller;
import Controller.Enums.CrewType;
import Controller.Enums.MatchLevel;
import Controller.GamePhases.FlightPhase;
import Model.Enums.Crewmates;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.ShipBoard;
import TestUtils.TestStateManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceAlienStateTest {

    /**
     * A player who does not want aliens fills the cabins with humans; once everyone is done the flight starts.
     */
    @Test
    public void testPlacingHumansInEveryCabinStartsTheFlight() throws Exception {
        final Controller controller = TestStateManager.finishedBuildingAllValid(MatchLevel.LEVEL2).getController();
        controller.finishBuilding("Anna", 1);
        controller.finishBuilding("Bob", 2);

        if (!(controller.getModel().getState() instanceof PlaceAlienState)) {
            // no ship can host aliens: cabins were filled automatically
            assertInstanceOf(FlightPhase.class, controller.getModel().getState());
            return;
        }

        for (Player player : controller.getModel().getPlayers()) {
            final ShipBoard ship = player.getShipBoard();
            for (Cabin cabin : ship.getCondensedShip().getCabins()) {
                if (cabin.getOccupants() == Crewmates.EMPTY && controller.getModel().getState() instanceof PlaceAlienState) {
                    controller.placeCrew(player.getName(), ship.getIndex(cabin), CrewType.HUMAN);
                    assertEquals(Crewmates.DOUBLE_HUMAN, cabin.getOccupants(), "placing humans must fill the cabin");
                }
            }
        }

        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
    }
}
