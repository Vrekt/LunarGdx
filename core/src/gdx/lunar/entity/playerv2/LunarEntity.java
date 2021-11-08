package gdx.lunar.entity.playerv2;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import gdx.lunar.entity.components.config.EntityConfigurationComponent;
import gdx.lunar.entity.components.instance.EntityInstanceComponent;
import gdx.lunar.entity.components.position.EntityPositionComponent;
import gdx.lunar.entity.components.position.EntityVelocityComponent;
import gdx.lunar.entity.components.prop.EntityPropertiesComponent;
import gdx.lunar.entity.mapping.GlobalEntityMapper;
import gdx.lunar.world.LunarWorld;

/**
 * A basic entity within the Lunar framework.
 */
public abstract class LunarEntity {

    // this entity
    protected Entity entity;

    // box2d body of this entity
    protected Body body;

    public LunarEntity(Entity entity, boolean initializeComponents) {
        this.entity = entity;
        if (initializeComponents) addComponents();
    }

    public LunarEntity(boolean initializeComponents) {
        if (initializeComponents) addComponents();
    }

    public void setWidth(float width) {
        GlobalEntityMapper.config.get(entity).size.x = width;
    }

    public void setHeight(float height) {
        GlobalEntityMapper.config.get(entity).size.y = height;
    }

    public float getWidth() {
        return GlobalEntityMapper.config.get(entity).size.x;
    }

    public float getHeight() {
        return GlobalEntityMapper.config.get(entity).size.y;
    }

    /**
     * Set the size of this entity
     *
     * @param width  w
     * @param height h
     */
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Set world scaling of this entity
     *
     * @param scaling scaling
     */
    public void setScaling(float scaling) {
        GlobalEntityMapper.config.get(entity).size.z = scaling;
    }

    /**
     * Set general configuration
     *
     * @param width   width
     * @param height  height
     * @param scaling (world) scaling
     */
    public void setConfig(float width, float height, float scaling) {
        GlobalEntityMapper.config.get(entity).setConfig(width, height, scaling);
    }

    public EntityConfigurationComponent getConfig() {
        return GlobalEntityMapper.config.get(entity);
    }

    public EntityPropertiesComponent getProperties() {
        return GlobalEntityMapper.properties.get(entity);
    }

    public void setEntityId(int entityId) {
        getProperties().entityId = entityId;
    }

    public void setEntityName(String name) {
        getProperties().entityName = name;
    }

    /**
     * Add components to the internal entity.
     */
    protected void addComponents() {
        if (entity == null) entity = new Entity();
        entity.add(new EntityPropertiesComponent());
        entity.add(new EntityPositionComponent());
        entity.add(new EntityVelocityComponent());
        entity.add(new EntityInstanceComponent());
        entity.add(new EntityConfigurationComponent());
    }

    /**
     * @return position of this entity.
     */
    public Vector2 getPosition() {
        return GlobalEntityMapper.position.get(entity).position;
    }

    /**
     * @return velocity of this entity.
     */
    public Vector2 getVelocity() {
        return GlobalEntityMapper.velocity.get(entity).velocity;
    }

    /**
     * @return previous position of this entity.
     */
    public Vector2 getPrevious() {
        return GlobalEntityMapper.position.get(entity).previous;
    }

    /**
     * @return interpolated position of this entity.
     */
    public Vector2 getInterpolated() {
        return GlobalEntityMapper.position.get(entity).interpolated;
    }

    public Body getBody() {
        return body;
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
     * Spawn this entity in the given world.
     *
     * @param world the world
     * @param x     the X location
     * @param y     the Y location
     */
    public abstract void spawnEntityInWorld(LunarWorld world, float x, float y);

}