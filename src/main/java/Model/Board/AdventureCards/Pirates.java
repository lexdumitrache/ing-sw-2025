package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Board.AdventureCards.Penalties.CannonShotPenalty;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Penalties.GoodsPenalty;
import Model.Board.AdventureCards.Rewards.Credits;
import Model.Enums.CardLevel;
import Model.Board.AdventureCards.Projectiles.CannonShot;

import java.util.ArrayList;
import java.util.List;


import Model.Enums.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents the pirate card in the game.
 * This class extends the Enemy class, which handles the properties of enemies.
 */
public class Pirates extends Enemy<CannonShotPenalty, Credits> {

    public Pirates(int id, CardLevel level, int power, List<CannonShot> cannonShots, int days, int credits) {
        super(id, level, power, new CannonShotPenalty(cannonShots), days, new Credits(credits));
    }

    @Override
    public final String getName() {
        return "Pirati";
    }

    @Override
    public final String getDescription() {
        return "";
    }

    public Pirates(JsonObject json) {
        super(
                json,
                // build the cannon‐shot list from JSON
                new CannonShotPenalty(json),
                // credit reward
                new Credits(json)
        );

    }



    @Override
    public void visualize() {
        // 1) Common header (ID, Name, Level, Description, Image Path)
        super.visualize();

        // 2) Show the encounter power
        System.out.println("Power:                " + getPower());

        // 3) List each pirate shot from the loss penalty
        CannonShotPenalty shots = getLossPenalty();
        System.out.println("Pirate Shots:         " + shots.getClass().getSimpleName());
        int idx = 0;
        for (CannonShot shot : shots) {
            idx++;
            System.out.printf(
                    "  #%d → large=%s, dir=%s%n",
                    idx,
                    shot.isBig(),
                    shot.getSide()
            );
        }
        if (idx == 0) {
            System.out.println("  (no shots)");
        }

        // 4) Days penalty when you win
        DaysPenalty dp = getWinPenalty();
        System.out.printf(
                "Win Penalty:          %s (type: %s)%n",
                dp.getAmount(),
                dp.getClass().getSimpleName()
        );

        // 5) Credits reward when you win
        Credits cr = getWinReward();
        System.out.printf(
                "Win Reward:           %d credits (type: %s)%n",
                cr.getAmount(),
                cr.getClass().getSimpleName()
        );
    }

    public String[] visualizeString() {
        List<String> lines = new ArrayList<>();

        // 1) common header da super.visualize()
        lines.add("==========================");
        lines.add("ID: " + this.getId());
        lines.add("Nome: " + this.getName());
        lines.add("Livello: " + this.getLevel());

        // 2) Show the encounter power
        lines.add("Power:                " + getPower());

        // 3) List each pirate shot from the loss penalty
        CannonShotPenalty shots = getLossPenalty();
        lines.add("Pirate Shots:         " + shots.getClass().getSimpleName());
        int idx = 0;
        for (CannonShot shot : shots) {
            idx++;
            lines.add(String.format(
                    "  #%d → large=%s, dir=%s",
                    idx,
                    shot.isBig(),
                    shot.getSide()
            ));
        }
        if (idx == 0) {
            lines.add("  (no shots)");
        }

        // 4) Days penalty when you win
        DaysPenalty dp = getWinPenalty();
        lines.add(String.format(
                "Win Penalty:          %s (type: %s)",
                dp.getAmount(),
                dp.getClass().getSimpleName()
        ));

        // 5) Credits reward when you win
        Credits cr = getWinReward();
        lines.add(String.format(
                "Win Reward:           %d credits (type: %s)",
                cr.getAmount(),
                cr.getClass().getSimpleName()
        ));

        return lines.toArray(new String[0]);
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) {
        cardResolverVisitor.visit(this, controller);
    }

}
