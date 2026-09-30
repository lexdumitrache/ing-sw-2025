package Model.Board.AdventureCards.Components;

import Model.Board.AdventureCards.Penalties.Penalty;
import Model.Enums.Criteria;

import java.io.Serializable;

/**
 * Represents a line in the combat zone adventure card.
 * Each line consists of an ordering criteria and a penalty.
 */
public class CombatZoneLine implements Serializable {
    private final Criteria orderingCriteria;
    private final Penalty penalty;

    public CombatZoneLine(Criteria orderingCriteria, Penalty penalty) {
        this.orderingCriteria = orderingCriteria;
        this.penalty = penalty;
    }

    public final Criteria getOrderingCriteria() {
        return this.orderingCriteria;
    }

    public final Penalty getPenalty() {
        return this.penalty;
    }
}
