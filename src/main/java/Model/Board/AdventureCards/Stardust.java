package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Model.Enums.CardLevel;
import Model.Exceptions.InvalidMethodParameters;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Stardust adventure card in the game.
 * This card is a special type of adventure card with no specific penalties or rewards defined.
 */
public class Stardust extends AdventureCardFilip {


    public Stardust(int id, CardLevel level) {
        super(id, level);
    }

    @Override
    public final String getName() {
        return "Polvere Stellare";
    }

    @Override
    public final String getDescription() {
        return "";
    }

    public Stardust(JsonObject json) {
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
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) throws InvalidMethodParameters {
        cardResolverVisitor.visit(this, controller);
    }
}
