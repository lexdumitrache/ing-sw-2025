package Controller;

import Controller.Commands.Command;
import Controller.Commands.CommandConstructor;
import Controller.GamePhases.FlightPhase;
import Controller.GamePhases.RewardsPhase;
import Controller.PreMatchLobby.LogInState;
import Controller.PreMatchLobby.OffState;
import Model.Enums.Crewmates;
import Model.Enums.Good;
import Model.Game;
import Model.Player;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.Cabin;
import Model.Ship.Components.CargoHold;
import Model.Ship.Components.SpaceshipComponent;

import java.util.*;

/**
 * Plays for players who are disconnected, so the game never waits for them forever.
 * It always makes the most passive move the rules allow: ending its turn, declaring no extra power,
 * skipping rewards, accepting penalties. Moves are tried in that order and the game rules reject the
 * illegal ones, so the auto-pilot does not need to know the details of every card.
 */
final class AutoPilot {

    /** Upper bound of moves made in one call, so a move that changes nothing cannot loop forever. */
    private static final int MAX_MOVES = 50;

    /** Commands with no arguments that end a decision passively, best first. */
    private static final List<String> PASSIVE = List.of("End", "EndTurn", "SkipReward", "ThrowDices", "PickNextCard");

    /** Prints why moves are rejected (for debugging). */
    static boolean verbose = false;

    private AutoPilot() {}

    /**
     * Makes moves for the absent players the game is waiting on.
     *
     * @param controller the game
     * @param absent     names of the players to play for
     * @return true if at least one move was made
     */
    static boolean play(Controller controller, Set<String> absent) {
        boolean moved = false;
        for (int i = 0; i < MAX_MOVES; i++) {
            final List<String> waiting = waitingOn(controller, absent);
            boolean progress = false;
            for (String name : waiting) {
                if (makeMove(controller, name)) {
                    System.out.println("Auto-pilot played for " + name + " in game " + controller.getGameID());
                    progress = true;
                    break;
                }
            }
            if (!progress && skipStuckCard(controller, waiting)) {
                progress = true;
            }
            if (!progress) {
                break;
            }
            moved = true;
        }
        return moved;
    }

    /**
     * Safety net: if an absent player has the turn during a card and no move is legal (which only happens
     * because of a bug in that card), the rest of the card is skipped so the game does not freeze.
     *
     * @return true if the card was skipped
     */
    private static boolean skipStuckCard(Controller controller, List<String> waiting) {
        final State state = controller.getModel().getState();
        if (waiting.isEmpty() || state == null || state.getPlayerInTurn() == null) {
            return false;
        }
        System.err.println("Auto-pilot found no legal move for " + waiting.getFirst() + " in "
                + state.getClass().getSimpleName() + " (game " + controller.getGameID() + "): skipping the rest of this card");
        controller.getModel().setState(new FlightPhase(controller));
        return true;
    }

    /**
     * @return the absent players the current game state needs a move from
     */
    private static List<String> waitingOn(Controller controller, Set<String> absent) {
        final Game game = controller.getModel();
        final State state = game.getState();

        if (state == null || state instanceof LogInState || state instanceof OffState || state instanceof RewardsPhase) {
            return List.of();
        }

        if (state instanceof FlightPhase) {
            // between cards the leader draws the next one
            final Player[] order = game.getFlightBoard().getTurnOrder();
            return order.length > 0 && absent.contains(order[0].getName()) ? List.of(order[0].getName()) : List.of();
        }

        if (state.getPlayerInTurn() != null) {
            final String name = state.getPlayerInTurn().getName();
            return absent.contains(name) ? List.of(name) : List.of();
        }

        // phases where everyone acts at the same time (building, crew placement, fixing ships)
        final List<String> names = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            if (absent.contains(player.getName())) {
                names.add(player.getName());
            }
        }
        return names;
    }

    /**
     * Tries the passive moves for one player until the game accepts one.
     *
     * @return true if a move was accepted
     */
    private static boolean makeMove(Controller controller, String name) {
        for (Command command : candidates(controller, name)) {
            try {
                command.execute(controller);
                return true;
            } catch (Exception rejected) {
                // not legal right now: try the next move
                if (verbose) {
                    System.out.println("  rejected " + command.getClass().getSimpleName() + " for " + name + ": " + rejected);
                }
            }
        }
        return false;
    }

    /**
     * @return the moves to try for the player, most passive first
     */
    private static List<Command> candidates(Controller controller, String name) {
        final Game game = controller.getModel();
        final Player player = game.getPlayer(name);
        final List<String> available = game.getState().getAvailableCommands();
        final List<Command> moves = new ArrayList<>();
        if (player == null) {
            return moves;
        }

        final ShipBoard ship = player.getShipBoard();
        final boolean flying = game.getFlightBoard().getFlyingPlayers().contains(player);

        for (String passive : PASSIVE) {
            add(moves, available, passive, name);
        }
        // the declared amount is the total power: declaring the base power activates no double component
        final var condensed = ship.getCondensedShip();
        for (double power = condensed.getBasePower(); power <= condensed.getMaxPower(); power += 0.5) {
            add(moves, available, "DeclareFirePower", name, Double.toString(power));
        }
        for (double thrust = condensed.getBaseThrust(); thrust <= condensed.getMaxThrust(); thrust += 0.5) {
            add(moves, available, "DeclareEnginePower", name, Double.toString(thrust));
        }

        if (!flying && ship.getAllComponents().size() <= 1) {
            // never got to build (only the central cabin): a ready-made ship lets them play when they come back
            for (int index = 0; index < 3; index++) {
                add(moves, available, "PreBuiltShip", name, Integer.toString(index));
            }
        }
        if (!flying) {
            for (int position = 1; position <= 4; position++) {
                add(moves, available, "FinishBuilding", name, Integer.toString(position));
            }
        }

        for (SpaceshipComponent component : ship.getAllComponents()) {
            final Coordinates at = ship.getIndex(component);
            final String row = Integer.toString(at.getI()), col = Integer.toString(at.getJ());

            if (component instanceof Cabin cabin) {
                if (cabin.getOccupants() == Crewmates.EMPTY) {
                    add(moves, available, "PlaceHuman", name, row, col);
                } else {
                    add(moves, available, "UseCrew", name, row, col);
                }
            }
            if (component instanceof BatteryCompartment battery && battery.getBatteries() > 0) {
                add(moves, available, "UseBattery", name, row, col);
            }
            if (component instanceof CargoHold hold) {
                final Good[] goods = hold.getGoods();
                for (int i = 0; i < goods.length; i++) {
                    if (goods[i] != null) {
                        add(moves, available, "RemoveGood", name, row, col, Integer.toString(i));
                    }
                }
            }
        }

        // removing tiles is only ever asked to fix a broken ship; keep the central cabin as the last resort
        final List<SpaceshipComponent> components = new ArrayList<>(ship.getAllComponents());
        components.sort(Comparator.comparing(component -> isCentralCabin(ship, component)));
        if (flying) {
            // while building, removing tiles would just take the ship apart: a pre-built ship is used instead
            for (SpaceshipComponent component : components) {
                final Coordinates at = ship.getIndex(component);
                add(moves, available, "DeleteComponent", name, Integer.toString(at.getI()), Integer.toString(at.getJ()));
            }
        }

        // a few cards only end once something is taken
        if (!available.contains("End")) {
            for (int good = 0; good < 4; good++) {
                for (CargoHold hold : ship.getCondensedShip().getCargoHolds()) {
                    final Coordinates at = ship.getIndex(hold);
                    for (int slot = 0; slot < hold.getGoods().length; slot++) {
                        add(moves, available, "GetGood", name, Integer.toString(good),
                                Integer.toString(at.getI()), Integer.toString(at.getJ()), Integer.toString(slot));
                    }
                }
            }
        }
        add(moves, available, "GetCreditsReward", name);

        if (!flying) {
            // an absent player whose ship cannot fly gets a ready-made one
            for (int index = 0; index < 3; index++) {
                add(moves, available, "PreBuiltShip", name, Integer.toString(index));
            }
        }
        return moves;
    }

    private static boolean isCentralCabin(ShipBoard ship, SpaceshipComponent component) {
        final Coordinates at = ship.getIndex(component);
        return at.getI() == 7 && at.getJ() == 7;
    }

    /**
     * Adds the command if the current state allows it.
     */
    private static void add(List<Command> moves, List<String> available, String command, String name, String... values) {
        if (!available.contains(command)) {
            return;
        }
        final CommandConstructor constructor = CommandConstructor.getCommandConstructor(command);
        if (constructor == null) {
            return;
        }
        final List<String> arguments = constructor.getArguments();
        final Map<String, String> args = new HashMap<>();
        for (int i = 0; i < arguments.size() && i < values.length; i++) {
            args.put(arguments.get(i), values[i]);
        }
        try {
            moves.add(constructor.create(name, args));
        } catch (IllegalArgumentException ignored) {
            // malformed candidate: skip it
        }
    }
}
