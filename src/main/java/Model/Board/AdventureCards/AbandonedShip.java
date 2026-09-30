package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Board.AdventureCards.Penalties.CrewPenalty;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Rewards.Credits;
import Model.Enums.CardLevel;
import java.util.*;
import com.google.gson.JsonObject;

/**
 * Represents an abandoned ship adventure card in the game.
 * This card has a crew penalty for winning, a reward in credits for landing,
 * and a penalty in days for landing.
 */
public class AbandonedShip extends AdventureCardFilip {
    private final CrewPenalty winPenalty;
    private final Credits landingReward;
    private final DaysPenalty landingPenalty;

    public AbandonedShip(int id, CardLevel level, int crew, int credits, int days) {
        super(id, level);
        this.winPenalty = new CrewPenalty(crew);
        this.landingReward = new Credits(credits);
        this.landingPenalty = new DaysPenalty(days);
    }

    public int getCrew() {
        return winPenalty.getAmount();
    }

    public CrewPenalty getWinPenalty() {
        return winPenalty;
    }

    public Credits getLandingReward() {
        return landingReward;
    }

    public DaysPenalty getLandingPenalty() {
        return landingPenalty;
    }

    @Override
    public String getName() {
        return "Nave Abbandonata";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public AbandonedShip(JsonObject json) {
        super(json);

        // Default values
        int crew = 1;
        int credits = 0;
        int days = 0;

        // Parse from JSON if available
        if (json.has("crewRequired")) {
            crew = json.get("crewRequired").getAsInt();
        }else{
            throw new RuntimeException("Missing field 'crewRequired' in AbandonedShip JSON at ID: " + json.get("id").getAsInt());
        }

        if (json.has("reward") && json.getAsJsonObject("reward").has("credits")) {
            credits = json.getAsJsonObject("reward").get("credits").getAsInt();
        }else{
            throw new RuntimeException("Missing field 'reward.credits' in AbandonedShip JSON at ID: " + json.get("id").getAsInt());
        }

        if (json.has("days")) {
            days = json.get("days").getAsInt();
        }else{
            throw new RuntimeException("Missing field 'days' in AbandonedShip JSON at ID: " + json.get("id").getAsInt());
        }



        this.winPenalty = new CrewPenalty(crew);
        this.landingReward = new Credits(credits);
        this.landingPenalty = new DaysPenalty(days);
    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Nave Abbandonata");
        System.out.println("Crew: " + this.winPenalty.getAmount());
        //winpenalty generica serve dire che e in giorni etc...
        System.out.println("Credits: " + this.landingReward.getAmount());
        System.out.println("Days: " + this.landingPenalty.getAmount());
        System.out.println("---------------------------------------");
    }

    public String[] visualizeString() {
        List<String> lines = new ArrayList<>();

        // Aggiungi le linee di super.visualize() se necessario
        // lines.addAll(Arrays.asList(super.getVisualization()));

        lines.add("Nave Abbandonata");
        lines.add("Crew: " + this.winPenalty.getAmount());
        lines.add("Credits: " + this.landingReward.getAmount());
        lines.add("Days: " + this.landingPenalty.getAmount());
        lines.add("---------------------------------------");

        return lines.toArray(new String[0]);
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) {
        cardResolverVisitor.visit(this, controller);
    }

}