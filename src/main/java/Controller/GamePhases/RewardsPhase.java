package Controller.GamePhases;

import Controller.Controller;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidParameters;
import Controller.PreMatchLobby.OffState;
import Controller.State;
import Model.Enums.Good;
import Model.Exceptions.InvalidMethodParameters;
import Model.Game;
import Model.Player;
import View.Client.ClientState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the Rewards Phase of the game, where players receive credits based on their performance
 * and the most beautiful ship is rewarded.
 */
public class RewardsPhase extends State {
    public RewardsPhase(Controller controller) {
        super(controller);
        controller.setQueuedAction(ClientState::net_Reward);
    }

    /**
     * This method is called when the Rewards Phase starts.
     * It calculates and distributes credits to players based on their performance,
     * including rewards for the most beautiful ship and goods collected.
     */
    @Override
    public void onEnter() {

        if(this.getController().getMatchLevel() == MatchLevel.TRIAL) {
            for (int i = 0; i < getController().getModel().getFlightBoard().getTurnOrder().length; i++) {
                Player player = getController().getModel().getFlightBoard().getTurnOrder()[i];
                player.deltaCredits(4 - i);
            }
        } else {
            for (int i = 0; i < getController().getModel().getFlightBoard().getTurnOrder().length; i++) {
                Player player = getController().getModel().getFlightBoard().getTurnOrder()[i];
                player.deltaCredits(8 - i * 2);
            }
        }

        int minExposedConnectors = Integer.MAX_VALUE;
        List<Player> winnersOfMostBeautifulShip = new ArrayList<>();

        // Find the minimum number of exposed connectors
        for (Player player : this.getController().getModel().getFlightBoard().getTurnOrder()) {
            // FIXED: Use getTotalExposedConnectorCount() instead of getExposedConnectors().size()
            // This counts each individual exposed connector, not just positions with exposed connectors
            int exposedConnectorCount = player.getShipBoard().getTotalExposedConnectorCount();

            if (exposedConnectorCount < minExposedConnectors) {
                minExposedConnectors = exposedConnectorCount;
                winnersOfMostBeautifulShip.clear();
                winnersOfMostBeautifulShip.add(player);
            } else if (exposedConnectorCount == minExposedConnectors) {
                winnersOfMostBeautifulShip.add(player);
            }
        }

        // Award credits to all players with the minimum exposed connectors
        int rewardAmount = (this.getController().getMatchLevel() == MatchLevel.TRIAL) ? 2 : 4;
        for (Player winner : winnersOfMostBeautifulShip) {
            winner.deltaCredits(rewardAmount);
        }

        for(Player p : this.getController().getModel().getFlightBoard().getTurnOrder()){
            List<Good> allGoods = p.getShipBoard().getCondensedShip().getCargoHolds().stream()
                    .flatMap(cargo -> Arrays.stream(cargo.getGoods()))
                    .toList();
            for(Good g : allGoods){
                if (g != null) {
                    switch(g){
                        case RED -> p.deltaCredits(4);
                        case YELLOW -> p.deltaCredits(3);
                        case GREEN -> p.deltaCredits(2);
                        case BLUE -> p.deltaCredits(1);
                        default -> {
                            // Do nothing for unknown goods
                        }
                    }
                }
            }

            p.deltaCredits(-p.getJunk());
        }

        for(Player p : this.getController().getModel().getFlightBoard().getFinishedFlightPlayers()){
            List<Good> allGoods = p.getShipBoard().getCondensedShip().getCargoHolds().stream()
                    .flatMap(cargo -> Arrays.stream(cargo.getGoods()))
                    .toList();
            for(Good g : allGoods){
                if (g != null) {
                    switch(g){
                        case RED -> p.deltaCredits(2);
                        case YELLOW -> p.deltaCredits(2);
                        case GREEN -> p.deltaCredits(1);
                        case BLUE -> p.deltaCredits(1);
                        default -> {
                            // Do nothing for unknown goods
                        }
                    }
                }
            }

            p.deltaCredits(-p.getJunk());
        }

    }

    /**
     * Visualizes the rewards leaderboard, displaying players ranked by their credits.
     * The leaderboard includes a header, player names, their credits, and a winner announcement.
     */
    public void visualize() {
        Player[] allPlayers = getController().getModel().getFlightBoard().getTurnOrder();

        // Sort players by credits in descending order (highest credits first)
        Player[] playersByCredits = Arrays.stream(allPlayers)
                .sorted(Comparator.comparingInt(Player::getCredits).reversed())
                .toArray(Player[]::new);

        // Print table header
        System.out.println("┌─────────────────────────────────────────────────────┐");
        System.out.println("│                   REWARDS LEADERBOARD               │");
        System.out.println("├─────────────────────────────────────────────────────┤");
        System.out.printf("│ %-4s  │ %-25s │ %-15s │%n", "Rank", "Player Name", "Credits");
        System.out.println("├───────┼───────────────────────────┼─────────────────┤");

        // Print each player's information ordered by credits
        for (int i = 0; i < playersByCredits.length; i++) {
            Player player = playersByCredits[i];
            String playerName = player.getName();
            int credits = player.getCredits();

            // Truncate player name if it's too long to fit in the table
            if (playerName.length() > 25) {
                playerName = playerName.substring(0, 22) + "...";
            }

            // Add medal emoji or indicator for top 3 positions
            String rankIndicator = getRankIndicator(i + 1);

            System.out.printf("│ %-4s │ %-25s │ %-15d │%n",
                    rankIndicator, playerName, credits);
        }

        // Print table footer with winner announcement
        System.out.println("└───────┴───────────────────────────┴─────────────────┘");

        if (playersByCredits.length > 0) {
            Player winner = playersByCredits[0];
            System.out.println("🏆 WINNER: " + winner.getName() +
                    " with " + winner.getCredits() + " credits! 🏆");
        }
    }

    /**
     * Returns a rank indicator string with special symbols for top positions.
     * Provides visual distinction for podium positions in the leaderboard.
     *
     * @param rank the player's rank position (1-based)
     * @return formatted rank string with appropriate symbol
     */
    private String getRankIndicator(int rank) {
        return switch (rank) {
            case 1 -> "🥇1st";
            case 2 -> "🥈2nd";
            case 3 -> "🥉3rd";
            default -> "  " + rank + "  ";
        };
    }

    /**
     * Allows a player to log out from the game.
     * If the player is not connected, an exception is thrown.
     *
     * @param name The name of the player logging out.
     * @throws InvalidParameters If the player with the given name is not connected.
     */
    @Override
    public void logout(String name) throws InvalidParameters {
        final Game model = this.getController().getModel();

        Player removed_player = model.getPlayer(name);
        if(removed_player == null){
            throw new InvalidParameters("Player with this name not connected");
        }
        model.removePlayer(name);
        if(model.getPlayers().isEmpty()){
            model.setState(new OffState(this.getController()));
        }
        System.out.println("Player " + name + " logged out");
    }



    @Override
    public List<String> getAvailableCommands(){
        return List.of("Leave");
    }
}
