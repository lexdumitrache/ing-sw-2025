package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Board.AdventureCards.Components.Planet;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Enums.CardLevel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Model.Enums.Good;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents a card that contains a list of planets, each with its own landing rewards and a landing penalty.
 * This class implements Iterable to allow iteration over the planets.
 */
public class Planets extends AdventureCardFilip implements Iterable<Planet>{
    private final List<Planet> planetList;
    private final DaysPenalty landingPenalty;

    public Planets(int id, CardLevel level, int days, List<Planet> planetList) {
        super(id, level);
        this.planetList = planetList;
        this.landingPenalty = new DaysPenalty(days);
    }

    public DaysPenalty getLandingPenalty() {
        return landingPenalty;
    }

    @Override
    public String getName() {
        return "Pianeti";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public Iterator<Planet> iterator() {
        return this.planetList.iterator();
    }

    public Planets(JsonObject json) {
        super(json);
        this.planetList = new ArrayList<>();

        if (json.has("planets")) {
            JsonArray array = json.getAsJsonArray("planets");
            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();

                List<Good> goods = new ArrayList<>();
                if (obj.has("goods")) {
                    JsonArray goodsArray = obj.getAsJsonArray("goods");
                    for (JsonElement g : goodsArray) {
                        goods.add(Good.valueOf(g.getAsString()));
                    }
                }else{
                    throw new IllegalArgumentException("Planet JSON object does not contain 'goods' array at id " + getId());
                }

                if(!obj.has("name")) {
                    throw new IllegalArgumentException("Planet JSON object does not contain 'name' field at id " + getId());
                }
                String name = obj.get("name").getAsString();
                this.planetList.add(new Planet(name, goods));
            }
        }else{
            throw new IllegalArgumentException("JSON does not contain 'planets' array at id " + getId());
        }

        // Default landing penalty
        int days = 0;
        if (json.has("landingPenalty") && json.getAsJsonObject("landingPenalty").has("value")) {
            days = json.getAsJsonObject("landingPenalty").get("value").getAsInt();
        }else{
            throw new IllegalArgumentException("JSON does not contain 'landingPenalty' object with 'value' field at id " + getId());
        }
        this.landingPenalty = new DaysPenalty(days);

    }

    @Override
    public void visualize() {
        // 1) print the shared header
        super.visualize();

        // 2) print each planet from your in-memory list
        System.out.println("Planets:");
        if (planetList.isEmpty()) {
            System.out.println("  (no planets)");
        } else {
            int idx = 0;
            for (Planet p : planetList) {
                idx++;
                System.out.printf("  #%d → %s%n", idx, p.getName());

                for(Good good : p.getLandingReward()) {
                    System.out.print(good + " ");
                }
                System.out.println();
            }
        }

        // 3) landing penalty and its type
        System.out.printf(
                "Landing Penalty:     %s (type: %s)%n",
                landingPenalty.getAmount(),
                landingPenalty.getClass().getSimpleName()
        );
    }

    public String[] visualizeString() {
        List<String> lines = new ArrayList<>();

        // 1) common header da super.visualize()
        lines.add("==========================");
        lines.add("ID: " + this.getId());
        lines.add("Nome: " + this.getName());
        lines.add("Livello: " + this.getLevel());

        // 2) print each planet from your in-memory list
        lines.add("Planets:");
        if (planetList.isEmpty()) {
            lines.add("  (no planets)");
        } else {
            int idx = 0;
            for (Planet p : planetList) {
                idx++;
                lines.add(String.format("  #%d → %s", idx, p.getName()));

                StringBuilder rewardLine = new StringBuilder();
                for(Good good : p.getLandingReward()) {
                    rewardLine.append(good).append(" ");
                }
                lines.add(rewardLine.toString());
            }
        }

        // 3) landing penalty and its type
        lines.add(String.format(
                "Landing Penalty:     %s (type: %s)",
                landingPenalty.getAmount(),
                landingPenalty.getClass().getSimpleName()
        ));

        return lines.toArray(new String[0]);
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) {
        cardResolverVisitor.visit(this, controller);
    }

    @Override
    public Planets clone() {
        Planets clone = new Planets(this.getId(), this.getLevel(), this.landingPenalty.getAmount(), new ArrayList<>());

        for (Planet planet : this.planetList) {
            clone.planetList.add(planet.clone());
        }

        return clone;
    }

}
