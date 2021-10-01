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
    protected String entityName;

    /**
     * The ID of this entity
     */
    protected int entityId;

    /**
     * Initialize
     *
     * @param entityId ID
     */
    public Entity(int entityId) {
        this.entityId = entityId;
    }

    /**
     * @return the entity name
     */
    public String getName() {
        return entityName;
    }

    /**
     * Set this entities name
     *
     * @param entityName the name
     */
    public void setName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the entity ID
     */
    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
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
