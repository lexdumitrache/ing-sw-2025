package Model.Board.AdventureCards.Penalties;

/**
 * Represents a penalty that requires the player to pay a certain number of days.
 * This class extends RegularPenalty, which handles the amount of days to be paid.
 */
public class DaysPenalty extends RegularPenalty {
    public DaysPenalty(int amount) {
        super(amount);
    }
}
