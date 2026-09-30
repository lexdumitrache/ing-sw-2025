package Model.Board.AdventureCards.Projectiles;

import Model.Enums.Side;

/**
 * Represents a meteor projectile in the game.
 * This class extends the Projectile class, which handles the properties of projectiles.
 */
public class Meteor extends Projectile {
    public Meteor(boolean big, Side side) {
        super(big, side);
    }
}
