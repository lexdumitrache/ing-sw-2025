package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Board.AdventureCards.Projectiles.Meteor;
import Model.Enums.CardLevel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Model.Enums.Side;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents a meteor swarm in the game.
 * This class extends AdventureCardFilip and implements Iterable<Meteor>.
 * It contains a list of meteors, each with a size and direction.
 */
public class MeteorSwarm extends AdventureCardFilip implements Iterable<Meteor> {
    private final List<Meteor> meteors;

    public MeteorSwarm(int id, CardLevel level, List<Meteor> meteors) {
        super(id, level);

        this.meteors = meteors;
    }

    @Override
    public String getName() {
        return "Pioggia di Meteoriti";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public Iterator<Meteor> iterator() {
        return meteors.iterator();
    }

    /**
     * Constructs a MeteorSwarm from a JSON object.
     * Parses the number of large and small meteors and the direction.
     */
    public MeteorSwarm(JsonObject json) {
        super(json);
        this.meteors = new ArrayList<>();

        if(!json.has("meteors")) {
            throw new IllegalArgumentException("JSON does not contain 'meteors' array at id " + getId());
        }

        JsonArray meteorsArray = json.getAsJsonArray("meteors");

        for (JsonElement e : meteorsArray) {
            JsonObject m = e.getAsJsonObject();

            if(!m.has("large")){
                throw new IllegalArgumentException("Meteor JSON object does not contain 'large' field at id " + getId());
            }
            boolean large = m.get("large").getAsBoolean();

            if(!m.has("direction")){
                throw new IllegalArgumentException("Meteor JSON object does not contain 'direction' field at id " + getId());
            }

            Side dir     = Side.valueOf(m.get("direction").getAsString().toUpperCase());
            this.meteors.add(new Meteor(large, dir));
        }
    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Total Meteors:  " + meteors.size());
        System.out.println("Details:");
        for (int i = 0; i < meteors.size(); i++) {
            Meteor m = meteors.get(i);
            System.out.printf(
                    "  #%d → large=%s, dir=%s%n",
                    i + 1,
                    m.isBig(),
                    m.getSide()
            );
        }
    }

    public String[] visualizeString() {
        List<String> lines = new ArrayList<>();

        // 1) common header da super.visualize()
        lines.add("==========================");
        lines.add("ID: " + this.getId());
        lines.add("Nome: " + this.getName());
        lines.add("Livello: " + this.getLevel());

        // 2) Total meteors and details
        lines.add("Total Meteors:  " + meteors.size());
        lines.add("Details:");
        for (int i = 0; i < meteors.size(); i++) {
            Meteor m = meteors.get(i);
            lines.add(String.format(
                    "  #%d → large=%s, dir=%s",
                    i + 1,
                    m.isBig(),
                    m.getSide()
            ));
        }

        return lines.toArray(new String[0]);
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) {
        cardResolverVisitor.visit(this, controller);
    }

}
