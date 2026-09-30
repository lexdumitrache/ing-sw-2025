package Model.Board.AdventureCards;

import Controller.CardResolverVisitor;
import Controller.Controller;
import Controller.Exceptions.InvalidParameters;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Penalties.*;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Board.AdventureCards.Rewards.Credits;
import Model.Enums.CardLevel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Model.Enums.Criteria;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Represents the combat zone in the game, which consists of multiple lines of combat criteria and penalties.
 * This class implements Iterable to allow iteration over the combat lines.
 */
public class CombatZone extends AdventureCardFilip implements Iterable<CombatZoneLine> {
    private final List<CombatZoneLine> lines;

    public CombatZone(int id, CardLevel level, List<CombatZoneLine> lines) {
        super(id, level);
        this.lines = lines;
    }

    @Override
    public String getName() {
        return "Zona di Guerra";
    }

    @Override
    public String getDescription() {
        return "";
    }

    public List<CombatZoneLine> getLines() {
        return new ArrayList<>(this.lines);
    }

    @Override
    public Iterator<CombatZoneLine> iterator() {
        return this.getLines().iterator();
    }

    /**
     * Constructs a CombatZone from a JSON representation.
     * The JSON contains an array of lines, each with a criteria and a penalty.
     */
    public CombatZone(JsonObject json) {
        super(json);
        this.lines = new ArrayList<>();

        // Parse every entry in the "lines" array
        if (json.has("lines")) {
            JsonArray array = json.getAsJsonArray("lines");
            for (JsonElement elem : array) {
                JsonObject entry      = elem.getAsJsonObject();
                // 1) Read the criteria
                Criteria criteria     = Criteria.valueOf(
                        entry.get("criteria").getAsString().toUpperCase()
                );

                // 2) Read the penalty object

                if (entry.has("penalty")) {
                    JsonObject penObj     = entry.getAsJsonObject("penalty");
                    Penalty   penalty;

                    // 3A) If they gave a "shots" array → ProjectilePenalty
                    if (penObj.has("shots")) {
//                        List<CannonShot> shots = new ArrayList<>();
//                        for (JsonElement shotElem : penObj.getAsJsonArray("shots")) {
//                            JsonObject s    = shotElem.getAsJsonObject();
//                            boolean   large = s.get("isLarge").getAsBoolean();
//                            Side dir   = Side.valueOf(
//                                    s.get("direction").getAsString().toUpperCase()
//                            );
//                            shots.add(new CannonShot(large, dir));
//                        }
//                        penalty = new CannonShotPenalty(shots);
                        penalty= new CannonShotPenalty(entry);

                        // 3B) Otherwise, look for a "type" field and switch on it
                    } else if (penObj.has("type")) {
                        String type = penObj.get("type").getAsString();
                        switch (type) {
                            case "DaysPenalty"   -> penalty = new DaysPenalty(
                                    penObj.get("value").getAsInt()
                            );
                            case "CrewPenalty"   -> penalty = new CrewPenalty(
                                    penObj.get("value").getAsInt()
                            );
                            case "GoodsPenalty"  -> penalty = new GoodsPenalty(
                                    penObj.get("value").getAsInt()
                            );
                            default -> throw new IllegalArgumentException(
                                    "Unsupported penalty type: " + type+ " for CombatZone with ID: " + getId()
                            );
                        }

                        // 3C) Fallback if no "shots" or "type" (shouldn’t happen if your JSON is correct)
                    } else {
                        throw new IllegalArgumentException("JSON must contain 'shots' or 'type' field for CombatZone with ID: " + getId());
                    }

                    // 4) Add the line
                    this.lines.add(new CombatZoneLine(criteria, penalty));

                }else{
                    throw new IllegalArgumentException(
                            "JSON must contain 'penalty' object for CombatZone with ID: " + getId()
                    );
                }
            }
        }else{
            throw new IllegalArgumentException("JSON must contain 'lines' array for CombatZone with ID: " + getId());
        }
    }

    @Override
    public void visualize() {
        // 1) Print the generic card info
        super.visualize();

        // 2) Print each combat line
        System.out.println("Combat Lines:");
        for (int i = 0; i < 2; i++) {
            CombatZoneLine line = lines.get(i);
            RegularPenalty         pen  = (RegularPenalty) line.getPenalty();
            Criteria        crit = line.getOrderingCriteria();

            System.out.printf(" [%d] Criteria: %s%n", i + 1, crit);

            System.out.printf(
                    "      Penalty: %s (type: %s)%n",
                    pen.getAmount(),
                    pen.getClass().getSimpleName()
            );

        }
        CombatZoneLine line = lines.get(2);
        Penalty pen2  = line.getPenalty();
        Criteria crit = line.getOrderingCriteria();
        System.out.printf(" [%d] Criteria: %s%n", 3, crit);
        // CannonShotPenalty: iterable of CannonShot
        CannonShotPenalty csp = (CannonShotPenalty) pen2;
        int count = 0;
        System.out.printf("      Shots:%n");
        for (CannonShot shot : csp) {
            count++;
            System.out.printf(
                    "        #%d → large=%s, dir=%s%n",
                    count,
                    shot.isBig(),
                    shot.getSide()
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

        // 2) Print each combat line
        lines.add("Combat Lines:");
        for (int i = 0; i < 2; i++) {
            CombatZoneLine line = this.lines.get(i);
            RegularPenalty pen  = (RegularPenalty) line.getPenalty();
            Criteria       crit = line.getOrderingCriteria();

            lines.add(String.format(" [%d] Criteria: %s", i + 1, crit));
            lines.add(String.format(
                    "      Penalty: %s (type: %s)",
                    pen.getAmount(),
                    pen.getClass().getSimpleName()
            ));
        }

        CombatZoneLine line = this.lines.get(2);
        Penalty pen2  = line.getPenalty();
        Criteria crit = line.getOrderingCriteria();
        lines.add(String.format(" [%d] Criteria: %s", 3, crit));

        // CannonShotPenalty: iterable of CannonShot
        CannonShotPenalty csp = (CannonShotPenalty) pen2;
        int count = 0;
        lines.add("      Shots:");
        for (CannonShot shot : csp) {
            count++;
            lines.add(String.format(
                    "        #%d → large=%s, dir=%s",
                    count,
                    shot.isBig(),
                    shot.getSide()
            ));
        }

        return lines.toArray(new String[0]);
    }

    @Override
    public void accept(CardResolverVisitor cardResolverVisitor, Controller controller) throws InvalidMethodParameters, InvalidParameters {
        cardResolverVisitor.visit(this, controller);
    }
}
