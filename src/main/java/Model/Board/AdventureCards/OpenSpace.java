package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Enums.CardLevel;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the open space in the game, which is a type of adventure card.
 * This class extends AdventureCardFilip, which handles the basic properties of adventure cards.
 */
public class OpenSpace extends AdventureCardFilip {

    public OpenSpace(int id, CardLevel level) {
        super(id, level);
    }

    @Override
    public String getName() {
        return "Spazio Aperto";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public OpenSpace(JsonObject json) {
        super(json);
    }

    @Override
    public void visualize() {
        super.visualize();
    }

    public String[] visualizeString() {
        List<String> lines = new ArrayList<>();

        // 1) common header da super.visualize()
        lines.add("==========================");
        lines.add("ID: " + this.getId());
        lines.add("Nome: " + this.getName());
        lines.add("Livello: " + this.getLevel());

        // 2) Crew Lost (commentato nell'originale)
        //lines.add("Crew Lost:       " + crewLost);

        return lines.toArray(new String[0]);
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) {
        cardResolverVisitor.visit(this, controller);
    }

}
