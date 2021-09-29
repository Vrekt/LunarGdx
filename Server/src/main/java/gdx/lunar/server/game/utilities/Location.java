package gdx.lunar.server.game.utilities;

/**
 * Represents a basic location
 */
public final class Location {

    /**
     * The X and Y
     */
    public float x = 0, y = 0;

    /**
     * Set this location
     *
     * @param x X
     * @param y Y
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set this location to another
     *
     * @param other the other
     */
    public void set(Location other) {
        this.x = other.x;
        this.y = other.y;
    }

}
