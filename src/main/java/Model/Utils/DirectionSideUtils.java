package Model.Utils;

import Model.Enums.Direction;
import Model.Enums.Side;

public class DirectionSideUtils {

    /**
     * Converts a Direction enum value to the corresponding Side enum value.
     */
    public static Side convertDirectionToSide(Direction dir) {
        switch (dir) {
            case UP:
                return Side.FRONT;
            case DOWN:
                return Side.REAR;
            case LEFT:
                return Side.LEFT;
            case RIGHT:
                return Side.RIGHT;
            default:
                throw new IllegalArgumentException("Unknown Direction: " + dir);
        }
    }

    /**
     * Converts a Side enum value to the corresponding Direction enum value.
     */
    public static Direction convertSideToDirection(Side side) {
        switch (side) {
            case FRONT:
                return Direction.UP;
            case REAR:
                return Direction.DOWN;
            case LEFT:
                return Direction.LEFT;
            case RIGHT:
                return Direction.RIGHT;
            default:
                throw new IllegalArgumentException("Unknown Side: " + side);
        }
    }
}
