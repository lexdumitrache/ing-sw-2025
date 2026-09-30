package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Rewards.Goods;
import Model.Enums.CardLevel;
import Model.Enums.Good;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents an abandoned station adventure card in the game.
 * This card has a crew requirement for landing, a reward in goods for landing,
 * and a penalty in days for landing.
 */
public class AbandonedStation extends AdventureCardFilip {
    private final int crew;
    private final Goods landingReward;
    private final DaysPenalty landingPenalty;

    public AbandonedStation(int id, CardLevel level, int crew, List<Good> goods, int days) {
        super(id, level);
        this.crew = crew;
        this.landingPenalty = new DaysPenalty(days);
        this.landingReward = new Goods(goods);
    }

    public int getCrew() {
        return crew;
    }

    public DaysPenalty getLandingPenalty() {
        return landingPenalty;
    }

    public Goods getLandingReward(){
        return landingReward;
    }

    @Override
    public String getName() {
        return "Stazione Abbandonata";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public AbandonedStation(JsonObject json) {
        super(json);

        if (json.has("crewRequired")) {
            // Default values
            this.crew = json.get("crewRequired").getAsInt();
        }else{
            throw new IllegalArgumentException("JSON does not contain 'crewRequired' at id " + getId());
        }

        if (json.has("days")) {
            int days = json.get("days").getAsInt();
            this.landingPenalty = new DaysPenalty(days);
        }else{
            throw new IllegalArgumentException("JSON does not contain 'days' at id " + getId());
        }

        // Parse cargo from reward
        List<Good> parsedGoods = new ArrayList<>();

        if (json.has("reward") && json.getAsJsonObject("reward").has("goods")) {
            JsonArray goodsArr = json.getAsJsonObject("reward").getAsJsonArray("goods");
            // Add default goods based on cargo count
            for (JsonElement goodEl : goodsArr) {
                parsedGoods.add(Good.valueOf(goodEl.getAsString().toUpperCase()));
            }
        }else{
            throw new IllegalArgumentException("JSON does not contain 'reward.goods' at id " + getId());
        }


        this.landingReward = new Goods(parsedGoods);
    }

    @Override
    public void visualize() {
        // 1) common header: ID, name, level, description, imagePath
        super.visualize();

        // 2) crew required
        System.out.println("Crew Required:       " + crew);

        // 3) landing delay penalty and its type
        System.out.printf(
                "Landing Delay:       %s (type: %s)%n",
                landingPenalty.getAmount(),
                landingPenalty.getClass().getSimpleName()
        );
        System.out.println();
        // 4) landing reward
        System.out.print("Landing Reward: ");
        for(Good good : landingReward) {
            System.out.print(good + " ");
        }

    }

    public String[] visualizeString() {
        List<String> lines = new ArrayList<>();

        // 1) common header da super.visualize()
        lines.add("==========================");
        lines.add("ID: " + this.getId());
        lines.add("Nome: " + this.getName());
        lines.add("Livello: " + this.getLevel());

        // 2) crew required
        lines.add("Crew Required:       " + crew);

        // 3) landing delay penalty and its type
        lines.add(String.format(
                "Landing Delay:       %s (type: %s)",
                landingPenalty.getAmount(),
                landingPenalty.getClass().getSimpleName()
        ));
        lines.add(""); // riga vuota

        // 4) landing reward
        StringBuilder rewardLine = new StringBuilder("Landing Reward: ");
        for(Good good : landingReward) {
            rewardLine.append(good).append(" ");
        }
        lines.add(rewardLine.toString());

        return lines.toArray(new String[0]);
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) {
        cardResolverVisitor.visit(this, controller);
    }


}
