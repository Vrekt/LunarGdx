package lunar.shared.entity.texture;

/**
 * The rotation of an entity or object
 */
public enum Rotation {

    FACING_UP, FACING_DOWN, FACING_LEFT, FACING_RIGHT;

    public static Rotation of(float rotation) {
        return Rotation.values()[(int) rotation];
    }

    /**
     * Get opposite rotation for interacting entities
     *
     * @param rotation current rotation
     * @return rotation
     */
    public static Rotation getOppositeRotation(Rotation rotation) {
        switch (rotation) {
            case FACING_UP:
                return Rotation.FACING_DOWN;
            case FACING_DOWN:
                return Rotation.FACING_UP;
            case FACING_RIGHT:
                return Rotation.FACING_LEFT;
            default:
                return Rotation.FACING_RIGHT;
        }
    }

    /**
     * Get opposite rotation for interacting entities
     *
     * @param rotation current rotation
     * @return rotation
     */
    public static Rotation getOppositeRotation(float rotation) {
        switch (of(rotation)) {
            case FACING_UP:
                return Rotation.FACING_DOWN;
            case FACING_DOWN:
                return Rotation.FACING_UP;
            case FACING_RIGHT:
                return Rotation.FACING_LEFT;
            default:
                return Rotation.FACING_RIGHT;
        }
    }

}
