package lunar.shared.utility;

/**
 * A basic direction with 4 directions/rotations
 */
public enum EntityFacingDirection {

    FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT;

    /**
     * Get opposite direction
     *
     * @param direction current direction
     * @return the new direction
     */
    public static EntityFacingDirection getOppositeDirection(EntityFacingDirection direction) {
        return switch (direction) {
            case FACING_UP -> EntityFacingDirection.FACING_DOWN;
            case FACING_DOWN -> EntityFacingDirection.FACING_UP;
            case FACING_RIGHT -> EntityFacingDirection.FACING_LEFT;
            default -> EntityFacingDirection.FACING_RIGHT;
        };
    }
}
