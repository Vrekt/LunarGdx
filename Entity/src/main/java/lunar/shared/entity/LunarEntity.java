package lunar.shared.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.world.LunarWorld;
import lunar.shared.components.EntityPropertiesComponent;
import lunar.shared.components.EntityTextureComponent;
import lunar.shared.components.EntityTransformComponent;
import lunar.shared.components.GlobalEntityMapper;
import lunar.shared.utility.EntityBodyHandler;
import lunar.shared.utility.EntityBodyHandlerAdapter;
import lunar.shared.utility.EntityFacingDirection;

/**
 * Represents a base entity all other entities are derived from
 */
public abstract class LunarEntity implements Spawnable, Disposable {

    protected boolean inWorld;

    protected Entity entity;
    protected Body body;

    protected float interpolationAlpha = 1.0f;
    protected EntityBodyHandler definitionHandler = new EntityBodyHandlerAdapter();

    public LunarEntity(Entity entity, boolean addDefaultComponents) {
        this(addDefaultComponents);
        this.entity = entity;
    }

    public LunarEntity(boolean addDefaultComponents) {
        if (addDefaultComponents) addComponents();
    }

    /**
     * Override this method and provie your world method
     *
     * @return the world this player is in
     */
    public abstract LunarWorld getWorld();

    /**
     * @return {@code true} if the world in is not null.
     */
    public boolean isInWorld() {
        return inWorld;
    }

    /**
     * Set if this entity is in a world
     *
     * @param inWorld in world
     */
    public void setInWorld(boolean inWorld) {
        this.inWorld = inWorld;
    }

    /**
     * @return the entities unique ID
     */
    public int getEntityId() {
        return getPropertiesComponent().entityId;
    }

    /**
     * @param entityId the entities unique ID
     */
    public void setEntityId(int entityId) {
        getPropertiesComponent().entityId = entityId;
    }

    /**
     * @return the name of this entity
     */
    public String getName() {
        return getPropertiesComponent().entityName;
    }

    /**
     * @param name the name of this entity
     */
    public void setName(String name) {
        getPropertiesComponent().entityName = name;
    }

    /**
     * Set properties within {@link EntityPropertiesComponent}
     *
     * @param name the entity name
     * @param id   the entity unique ID
     */
    public void setProperties(String name, int id) {
        getPropertiesComponent().setProperties(id, name);
    }

    /**
     * The body handler used to create {@link Body}s
     *
     * @return the handler or {@code  null} if none
     */
    public EntityBodyHandler getBodyHandler() {
        return definitionHandler;
    }

    /**
     * Set the body handler to use to create a {@link Body}
     *
     * @param handler the handler
     */
    public void setBodyHandler(EntityBodyHandler handler) {
        this.definitionHandler = handler;
    }

    /**
     * Set if this entity has fixed rotation
     * This will only apply if the {@code getBodyHandler} is not {@code null}
     *
     * @param rotation state
     */
    public void setHasFixedRotation(boolean rotation) {
        if (definitionHandler != null) definitionHandler.setHasFixedRotation(rotation);
    }

    /**
     * @return the width of this entity
     */
    public float getWidth() {
        return getPropertiesComponent().width;
    }

    /**
     * @return the height of this entity
     */
    public float getHeight() {
        return getPropertiesComponent().height;
    }

    /**
     * @return the scaled width defined with {@code setSize}
     */
    public float getScaledWidth() {
        return getPropertiesComponent().getScaledWidth();
    }

    /**
     * @return {@code  getHeight} * {@code getWorldScale}
     */
    public float getScaledHeight() {
        return getPropertiesComponent().getScaledHeight();
    }

    /**
     * @param width the width of this entity
     */
    public void setWidth(float width) {
        getPropertiesComponent().width = width;
    }

    /**
     * @param height the height of this entity
     */
    public void setHeight(float height) {
        getPropertiesComponent().height = height;
    }

    /**
     * Set size and world/entity scaling
     *
     * @param width  width
     * @param height height
     * @param scale  scale
     */
    public void setSize(float width, float height, float scale) {
        getPropertiesComponent().setEntitySize(width, height, scale);
    }

    /**
     * @return the movement speed of this entity
     */
    public float getMoveSpeed() {
        return getPropertiesComponent().getSpeed();
    }

    /**
     * @param speed the movement speed of this entity
     */
    public void setMoveSpeed(float speed) {
        getPropertiesComponent().speed = speed;
    }

    /**
     * @return health of this entity defaulted to {@code  100.0f}
     */
    public float getHealth() {
        return getPropertiesComponent().health;
    }

    /**
     * @param health health of this entity
     */
    public void setHealth(float health) {
        getPropertiesComponent().health = health;
    }

    /**
     * Heal this entity by the provided amount
     *
     * @param amount the amount to heal by
     * @return the entities new health
     */
    public float heal(float amount) {
        return getPropertiesComponent().health += amount;
    }

    /**
     * Damage this entity by the provided amount
     *
     * @param amount the amount to damage by
     * @return the entities new health
     */
    public float damage(float amount) {
        return getPropertiesComponent().health -= amount;
    }

    /**
     * @return position of this entity, defaulted to {@code 0.0f,0.0f}
     */
    public Vector2 getPosition() {
        return GlobalEntityMapper.transform.get(entity).position;
    }

    /**
     * @return x position
     */
    public float getX() {
        return getPosition().x;
    }

    /**
     * @return y position
     */
    public float getY() {
        return getPosition().y;
    }

    /**
     * Set the position of this entity
     *
     * @param position  the position
     * @param transform if the box2d body should be transformed
     */
    public void setPosition(Vector2 position, boolean transform) {
        getPosition().set(position);
        if (transform && body != null) body.setTransform(position, getAngle());
    }

    /**
     * Set the position of this entity
     *
     * @param x         x position
     * @param y         y position
     * @param transform if the box2d body should be transformed
     */
    public void setPosition(float x, float y, boolean transform) {
        getPosition().set(x, y);
        if (transform && body != null) body.setTransform(x, y, getAngle());
    }

    /**
     * @return previous position of this entity used for interpolation
     */
    public Vector2 getPreviousPosition() {
        return GlobalEntityMapper.transform.get(entity).previous;
    }

    /**
     * Usually set right before updating an entities current position
     *
     * @param position set previous position
     */
    public void setPreviousPosition(Vector2 position) {
        getPreviousPosition().set(position);
    }

    /**
     * Usually set right before updating an entities current position
     *
     * @param x position
     * @param y y position
     */
    public void setPreviousPosition(float x, float y) {
        getPreviousPosition().set(x, y);
    }

    /**
     * @return interpolated position for drawing or anything else that requires smoothing
     */
    public Vector2 getInterpolatedPosition() {
        return GlobalEntityMapper.transform.get(entity).interpolated;
    }

    /**
     * @param position position
     */
    public void setInterpolatedPosition(Vector2 position) {
        getInterpolatedPosition().set(position);
    }

    /**
     * @param x x position
     * @param y y position
     */
    public void setInterpolatedPosition(float x, float y) {
        getInterpolatedPosition().set(x, y);
    }

    /**
     * Set amount of alpha or 'smoothing' to use when interpolating
     * Usually between 0.5f - 1.0f
     *
     * @param alpha alpha
     */
    public void setInterpolationAlpha(float alpha) {
        this.interpolationAlpha = alpha;
    }

    /**
     * Interpolate the position of this entity
     */
    public void interpolatePosition() {
        interpolatePosition(Interpolation.linear, interpolationAlpha);
    }

    /**
     * Interpolate the position of this entity
     *
     * @param alpha alpha to use for 'smoothing'
     */
    public void interpolatePosition(float alpha) {
        interpolatePosition(Interpolation.linear, alpha);
    }

    /**
     * Interpolate the position of this entity
     *
     * @param interpolation interpolation method to use
     * @param alpha         alpha to use for 'smoothing'
     */
    public void interpolatePosition(Interpolation interpolation, float alpha) {
        final Vector2 previous = getPreviousPosition();
        final Vector2 current = body.getPosition();
        setInterpolatedPosition(interpolation.apply(previous.x, current.x, alpha), interpolation.apply(previous.y, current.y, alpha));
    }

    /**
     * @return current linear velocity of this entity
     */
    public Vector2 getVelocity() {
        return GlobalEntityMapper.transform.get(entity).velocity;
    }

    /**
     * @param velocity the linear velocity
     */
    public void setVelocity(Vector2 velocity, boolean updateBody) {
        getVelocity().set(velocity);
        if (updateBody && body != null) body.setLinearVelocity(velocity);
    }

    /**
     * @param x linear X velocity
     * @param y linear Y velocity
     */
    public void setVelocity(float x, float y, boolean updateBody) {
        getVelocity().set(x, y);
        if (updateBody && body != null) body.setLinearVelocity(x, y);
    }

    /**
     * @return the angle or rotation of this entity
     */
    public float getAngle() {
        return getPropertiesComponent().angle;
    }

    /**
     * @param angle angle or rotation
     */
    public void setAngle(float angle) {
        getPropertiesComponent().angle = angle;
    }

    /**
     * Return a basic direction only supporting LEFT, RIGHT, UP, DOWN
     *
     * @return direction
     */
    public EntityFacingDirection getFacingDirection() {
        return getPropertiesComponent().direction;
    }

    /**
     * Set direction
     *
     * @param direction the direction
     */
    public void setFacingDirection(EntityFacingDirection direction) {
        getPropertiesComponent().direction = direction;
    }

    /**
     * @return the box2d {@link Body} or {@code  null} if none yet
     */
    public Body getBody() {
        return body;
    }

    /**
     * Set the body
     *
     * @param body the body
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * The ashley {@link Entity} of this entity
     *
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Add default components
     * {@link EntityPropertiesComponent}
     * {@link EntityTransformComponent}
     * {@link EntityTextureComponent}
     */
    public void addComponents() {
        if (entity == null) entity = new Entity();
        entity.add(new EntityPropertiesComponent());
        entity.add(new EntityTransformComponent());
        entity.add(new EntityTextureComponent());
    }

    /**
     * @return the transform component of this entity
     */
    public EntityTransformComponent getTransformComponent() {
        return GlobalEntityMapper.transform.get(entity);
    }

    /**
     * @return the properties component of this entity
     */
    public EntityPropertiesComponent getPropertiesComponent() {
        return GlobalEntityMapper.properties.get(entity);
    }

    /**
     * @return texture component of this entity
     */
    public EntityTextureComponent getTextureComponent() {
        return GlobalEntityMapper.texture.get(entity);
    }

    /**
     * Update this entity
     *
     * @param delta delta
     */
    public abstract void update(float delta);

    /**
     * Load this entity if required
     */
    public void loadEntity() {

    }

    @Override
    public void dispose() {
        entity.removeAll();
        inWorld = false;
    }
}
