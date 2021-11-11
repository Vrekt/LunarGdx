package gdx.lunar.server.game.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import gdx.lunar.server.game.entity.position.EntityPositionComponent;
import gdx.lunar.server.game.entity.position.EntityVelocityComponent;
import gdx.lunar.server.game.mapping.ServerGlobalEntityMappings;
import gdx.lunar.server.game.utilities.Disposable;
import gdx.lunar.server.world.World;

/**
 * Represents a basic entity.
 */
public abstract class LunarEntity implements Disposable {

    // this entity
    protected Entity entity;
    protected int entityId;
    protected String entityName;
    protected float rotation;
    // if this entity is in any world.
    protected World worldIn;
    protected boolean inWorld;

    public LunarEntity(Entity entity, boolean initializeComponents) {
        this.entity = entity;
        if (initializeComponents) addComponents();
    }

    public LunarEntity(boolean initializeComponents) {
        if (initializeComponents) addComponents();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public int getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    /**
     * Add components to the internal entity.
     */
    protected void addComponents() {
        if (entity == null) entity = new Entity();
        entity.add(new EntityPositionComponent());
        entity.add(new EntityVelocityComponent());
    }

    /**
     * @return position of this entity.
     */
    public Vector2 getPosition() {
        return ServerGlobalEntityMappings.position.get(entity).position;
    }

    /**
     * @return velocity of this entity.
     */
    public Vector2 getVelocity() {
        return ServerGlobalEntityMappings.velocity.get(entity).velocity;
    }

    /**
     * @return previous position of this entity.
     */
    public Vector2 getPrevious() {
        return ServerGlobalEntityMappings.position.get(entity).previous;
    }

    /**
     * @return interpolated position of this entity.
     */
    public Vector2 getInterpolated() {
        return ServerGlobalEntityMappings.position.get(entity).interpolated;
    }

    /**
     * Set the position
     *
     * @param x X
     * @param y Y
     */
    public void setPosition(float x, float y) {
        getPosition().set(x, y);
    }

    /**
     * Set the velocity
     *
     * @param x X
     * @param y Y
     */
    public void setVelocity(float x, float y) {
        getVelocity().set(x, y);
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getRotation() {
        return rotation;
    }

    /**
     * @return the internal Ashley entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Set this entities Ashley entity
     *
     * @param entity     the entity
     * @param disposeOld if {@code true} the internal entity will be removed of,
     *                   you should handle removing this entity from the system.
     */
    public void setEntity(Entity entity, boolean disposeOld) {
        if (disposeOld) this.entity.removeAll();
        this.entity = entity;
    }

    /**
     * Send this entity to other entities
     */
    public void sendEntityToOtherEntity(LunarEntity entity) {

    }

}
