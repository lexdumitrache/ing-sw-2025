package Model.Board.AdventureCards.Projectiles;

import Model.Enums.Side;

/**
 * Represents a cannon shot projectile in the game.
 * This class extends the Projectile class, which handles the properties of projectiles.
 */
public class CannonShot extends Projectile {
    public CannonShot(boolean big, Side side) {
        super(big, side);
    }
}
