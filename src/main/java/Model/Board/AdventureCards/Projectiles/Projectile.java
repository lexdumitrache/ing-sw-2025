package Model.Board.AdventureCards.Projectiles;

import Model.Enums.Side;

import java.io.Serializable;

/**
 * Represents a generic projectile in the game.
 * This class serves as a base class for specific types of projectiles.
 * It implements Serializable to allow instances to be serialized.
 */
public abstract class Projectile implements Serializable {
    private final boolean big;
    private final Side side;

    public Projectile(boolean big, Side side) {
        this.big = big;
        this.side = side;
    }

    public final boolean isBig() {
        return big;
    }

    public final Side getSide() {
        return side;
    }
}
