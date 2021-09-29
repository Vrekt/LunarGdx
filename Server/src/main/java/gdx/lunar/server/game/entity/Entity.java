package gdx.lunar.server.game.entity;

import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.game.utilities.Location;

/**
 * Represents a basic entity.
 */
public abstract class Entity implements Disposable {

    /**
     * The location of this entity
     */
    protected final Location location = new Location();

    /**
     * The entity name
     */
    protected final String entityName;

    /**
     * The ID of this entity
     */
    protected final int entityId;

    /**
     * Initialize
     *
     * @param entityName name
     * @param entityId   ID
     */
    public Entity(String entityName, int entityId) {
        this.entityName = entityName;
        this.entityId = entityId;
    }

    /**
     * @return the entity name
     */
    public String getName() {
        return entityName;
    }

    /**
     * @return the entity ID
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * Update this entity
     */
    public abstract void update();

    /**
     * @return X
     */
    public float getX() {
        return location.x;
    }

    /**
     * @return Y
     */
    public float getY() {
        return location.y;
    }

    /**
     * Set location
     *
     * @param x x
     * @param y y
     */
    public void setLocation(float x, float y) {
        location.set(x, y);
    }

}
