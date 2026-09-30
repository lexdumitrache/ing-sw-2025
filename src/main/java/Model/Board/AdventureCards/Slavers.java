package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Board.AdventureCards.Penalties.CrewPenalty;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Rewards.Credits;
import Model.Enums.CardLevel;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a slavers encounter card in the game.
 * This card has a crew penalty for losing, a reward in credits for winning,
 * and a days penalty for winning.
 */
public class Slavers extends Enemy<CrewPenalty, Credits> {
    public Slavers(int id, CardLevel level, int power, int crew, int days, int credits) {
        super(id, level, power, new CrewPenalty(crew), days, new Credits(credits));
    }

    @Override
    public final String getName() {
        return "Schiavisti";
    }

    @Override
    public final String getDescription() {
        return "";
    }

    public Slavers(JsonObject json) {
        super(json,
              new CrewPenalty(json),
              new Credits(json)
        );
    }

    @Override
    public void visualize() {
        // 1) common header
        super.visualize();

        // 2) encounter power
        System.out.println("Power:                " + getPower());

        // 3) loss penalty (CrewPenalty)
        CrewPenalty cp = getLossPenalty();
        System.out.printf(
                "Loss Penalty:         %s (type: %s)%n",
                cp.getAmount(),
                cp.getClass().getSimpleName()
        );

        // 4) win penalty (DaysPenalty)
        DaysPenalty dp = getWinPenalty();
        System.out.printf(
                "Win Penalty:          %s (type: %s)%n",
                dp.getAmount(),
                dp.getClass().getSimpleName()
        );

        // 5) win reward (Credits)
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

        // 2) encounter power
        lines.add("Power:                " + getPower());

        // 3) loss penalty (CrewPenalty)
        CrewPenalty cp = getLossPenalty();
        lines.add(String.format(
                "Loss Penalty:         %s (type: %s)",
                cp.getAmount(),
                cp.getClass().getSimpleName()
        ));

        // 4) win penalty (DaysPenalty)
        DaysPenalty dp = getWinPenalty();
        lines.add(String.format(
                "Win Penalty:          %s (type: %s)",
                dp.getAmount(),
                dp.getClass().getSimpleName()
        ));

        // 5) win reward (Credits)
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
