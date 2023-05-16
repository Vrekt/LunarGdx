package lunar.shared.utility;

/**
 * A basic direction with 4 directions/rotations
 */
public enum BasicDirection {

    FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT;

    /**
     * Get opposite direction
     *
     * @param direction current direction
     * @return the new direction
     */
    public static BasicDirection getOppositeDirection(BasicDirection direction) {
        switch (direction) {
            case FACING_UP:
                return BasicDirection.FACING_DOWN;
            case FACING_DOWN:
                return BasicDirection.FACING_UP;
            case FACING_RIGHT:
                return BasicDirection.FACING_LEFT;
            default:
                return BasicDirection.FACING_RIGHT;
        }
    }
}
