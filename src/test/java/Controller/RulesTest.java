package Controller;

import Controller.Enums.ItemType;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.GamePhases.RewardsPhase;
import Controller.Pirates.PiratesManageShotState;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Board.AdventureCards.Projectiles.Projectile;
import Model.Board.Timer;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Good;
import Model.Enums.Side;
import Model.Player;
import Model.Ship.CondensedShip;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.CargoHold;
import TestUtils.TestStateManager;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Rules checked against the Galaxy Trucker rulebook.
 */
public class RulesTest {

    /* ---------------------------------------------------------------- shields */

    /**
     * A light cannon shot from the right side, with one player hit and a battery to power the shield.
     */
    private static PiratesManageShotState shotFromTheRight(Controller controller, Player player) throws Exception {
        final Context context = new Context(controller);
        context.addSpecialPlayer(player);

        final java.lang.reflect.Field projectiles = Context.class.getDeclaredField("projectiles");
        projectiles.setAccessible(true);
        final List<Projectile> shots = new ArrayList<>();
        shots.add(new CannonShot(false, Side.RIGHT));
        projectiles.set(context, shots);

        final BatteryCompartment battery = new BatteryCompartment(Card.BATTERY_COMPARTMENT, ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 2);
        player.getShipBoard().addComponent(battery, new Coordinates(6, 7));
        player.getShipBoard().getCondensedShip().addBatteryCompartment(battery);

        return new PiratesManageShotState(context, 7, 0);
    }

    @Test
    public void testShieldCoveringTheRightStopsAShotFromTheRight() throws Exception {
        final Controller controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Anna");
        final Player anna = controller.getModel().getPlayer("Anna");
        final PiratesManageShotState state = shotFromTheRight(controller, anna);

        // a shield generator pointing up covers the front and the right (east) side
        anna.getShipBoard().getCondensedShip().getShields().incrementEastShields();

        assertDoesNotThrow(() -> state.useItem("Anna", ItemType.BATTERIES, new Coordinates(6, 7)));
    }

    @Test
    public void testShieldCoveringTheLeftDoesNotStopAShotFromTheRight() throws Exception {
        final Controller controller = new Controller(MatchLevel.TRIAL, 1);
        controller.getModel().addPlayer("Anna");
        final Player anna = controller.getModel().getPlayer("Anna");
        final PiratesManageShotState state = shotFromTheRight(controller, anna);

        anna.getShipBoard().getCondensedShip().getShields().incrementWestShields();

        assertThrows(InvalidParameters.class, () -> state.useItem("Anna", ItemType.BATTERIES, new Coordinates(6, 7)));
    }

    /* ---------------------------------------------------------------- aliens */

    /**
     * "If your engine strength without the alien is 0, you don't get this bonus": with only double engines,
     * powering one of them makes the strength 2, so the brown alien adds 2 more.
     */
    @Test
    public void testBrownAlienCountsWithOnlyDoubleEngines() {
        final CondensedShip ship = new ShipBoard().getCondensedShip();
        ship.getEngines().setDoubleEngines(1);
        ship.getAliens().setBrownAlien(true);

        assertEquals(0, ship.getBaseThrust(), "without powered engines the alien does not help");
        assertEquals(4, ship.getMaxThrust(), "one double engine (2) + brown alien (2)");
        assertEquals(2, ship.declaredDoublesStrength(4, true), "declaring 4 means powering one double engine");
        assertEquals(-1, ship.declaredDoublesStrength(2, true), "2 cannot be reached: powering an engine also adds the alien");
    }

    @Test
    public void testBrownAlienBonusWithSingleEngines() {
        final CondensedShip ship = new ShipBoard().getCondensedShip();
        ship.getEngines().setSingleEngines(1);
        ship.getEngines().setDoubleEngines(1);
        ship.getAliens().setBrownAlien(true);

        assertEquals(3, ship.getBaseThrust(), "one single engine (1) + brown alien (2)");
        assertEquals(5, ship.getMaxThrust());
        assertEquals(2, ship.declaredDoublesStrength(5, true));
    }

    /* ---------------------------------------------------------------- giving up */

    /**
     * "Add up the standard price of all your goods and then take half that many credits. (Round up.)"
     */
    @Test
    public void testRetiredPlayerSellsGoodsAtHalfTheTotalRoundedUp() throws Exception {
        final Controller controller = TestStateManager.flightPhase2Players(MatchLevel.TRIAL).getController();
        final Player retired = controller.getModel().getFlightBoard().getTurnOrder()[1];

        // a yellow (3) and a blue (1) good: half of 4 is 2 (selling each good at half price rounded up would give 3)
        final CargoHold hold = new CargoHold(Card.CARGO_HOLD, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL,
                ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, 2, false);
        hold.addGood(Good.YELLOW);
        hold.addGood(Good.BLUE);
        for (CargoHold existing : new ArrayList<>(retired.getShipBoard().getCondensedShip().getCargoHolds())) {
            retired.getShipBoard().getCondensedShip().removeCargoHold(existing);
        }
        retired.getShipBoard().getCondensedShip().addCargoHold(hold);

        controller.getModel().getState().leaveRace(retired.getName());
        assertInstanceOf(FlightPhase.class, controller.getModel().getState());
        final int before = retired.getCredits();

        controller.getModel().setState(new RewardsPhase(controller));

        assertEquals(before + 2 - retired.getJunk(), retired.getCredits());
    }

    /* ---------------------------------------------------------------- hourglass */

    /**
     * "Start the timer on this space of the flight board. The player who starts the timer should simultaneously say 'Go!'"
     */
    @Test
    public void testHourglassStartsWhenBuildingStarts() {
        final Controller controller = TestStateManager.finishedBuildingAllValid(MatchLevel.LEVEL2).getController();
        final Timer timer = controller.getModel().getFlightBoard().getTimer();

        assertEquals(Timer.Phase.START_PHASE, timer.getPhase());
        assertTrue(timer.getTimeLeft() > 0);
    }
}
