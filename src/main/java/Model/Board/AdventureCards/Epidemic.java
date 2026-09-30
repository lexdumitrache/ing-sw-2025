package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Enums.CardLevel;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an epidemic event card in the game.
 * This class extends AdventureCardFilip, which handles the properties of adventure cards.
 */
public class Epidemic extends AdventureCardFilip {



    public Epidemic(int id, CardLevel level) {
        super(id, level);
    }

    public Epidemic(JsonObject json) {
        super(json);
    }

    @Override
    public void visualize() {
        // 1) Generic card info: ID, name, level, description, imagePath
        super.visualize();
        //System.out.println("Crew Lost:       " + crewLost);
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
    public String getName() {
        return "Epidemia";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) {
        cardResolverVisitor.visit(this, controller);
    }
}
