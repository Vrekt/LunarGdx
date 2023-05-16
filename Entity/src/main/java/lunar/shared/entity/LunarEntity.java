package lunar.shared.entity;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.world.LunarWorld;
import lunar.shared.components.prop.EntityPropertiesComponent;
import lunar.shared.utility.BasicDirection;
import lunar.shared.utility.EntityBodyHandler;

/**
 * Represents a base entity that all other entities are expanded from
 */
public interface LunarEntity extends Disposable, Spawnable {

    /**
     * Get the current {@link  LunarWorld} this entity is in
     *
     * @return the world or {@code null} if none
     */
    LunarWorld getWorldIn();

    /**
     * Set the current world in
     *
     * @param worldIn the world or {@code  null}
     */
    void setWorldIn(LunarWorld worldIn);

    /**
     * @return {@code true} if the world in is not null.
     */
    boolean isInWorld();

    /**
     * Set if this entity is in a world
     * if the provided world passed through in {@code setWorldIn} is NOT {@code  null} then this field
     * will be automatically set to {@code  true}
     *
     * @param inWorld in world
     */
    void setInWorld(boolean inWorld);

    /**
     * @return the entities unique ID
     */
    int getEntityId();

    /**
     * @param entityId the entities unique ID
     */
    void setEntityId(int entityId);

    /**
     * @return the name of this entity
     */
    String getName();

    /**
     * @param name the name of this entity
     */
    void setName(String name);

    /**
     * }
     * Set properties within {@link EntityPropertiesComponent}
     *
     * @param name the entity name
     * @param id   the entity unique ID
     */
    void setProperties(String name, int id);

    /**
     * The body handler used to create {@link Body}s
     *
     * @return the handler or {@code  null} if none
     */
    EntityBodyHandler getBodyHandler();

    /**
     * Set the body handler to use to create a {@link Body}
     *
     * @param handler the handler
     */
    void setBodyHandler(EntityBodyHandler handler);

    /**
     * Set if this entity has fixed rotation
     * This will only apply if the {@code getBodyHandler} is not {@code null}
     *
     * @param rotation state
     */
    void setHasFixedRotation(boolean rotation);

    /**
     * @return the width of this entity
     */
    float getWidth();

    /**
     * @return the height of this entity
     */
    float getHeight();

    /**
     * The world or entity scale for this entity
     * Typically this is the game/world scale that is used globally.
     *
     * @return the world or entity scale
     */
    float getWorldScale();

    /**
     * @return {@code  getWidth} * {@code getWorldScale}
     */
    float getScaledWidth();

    /**
     * @return {@code  getHeight} * {@code getWorldScale}
     */
    float getScaledHeight();

    /**
     * @param width the width of this entity
     */
    void setWidth(float width);

    /**
     * @param height the height of this entity
     */
    void setHeight(float height);

    /**
     * Set the world or entity scale
     *
     * @param scale the scale
     */
    void setWorldScale(float scale);

    /**
     * Set size and world/entity scaling
     *
     * @param width  width
     * @param height height
     * @param scale  scale
     */
    void setSize(float width, float height, float scale);

    /**
     * @return the movement speed of this entity
     */
    float getMoveSpeed();

    /**
     * @param speed the movement speed of this entity
     */
    void setMoveSpeed(float speed);

    /**
     * @return health of this entity defaulted to {@code  100.0f}
     */
    float getHealth();

    /**
     * @param health health of this entity
     */
    void setHealth(float health);

    /**
     * @return position of this entity, defaulted to {@code 0.0f,0.0f}
     */
    Vector2 getPosition();

    /**
     * @return x position
     */
    float getX();

    /**
     * @return y position
     */
    float getY();

    /**
     * Set the position of this entity
     * If {@code  transform} is {@code  true} then if the entity has a {@link Body}
     * That bodies position will be set to that position using {@code setTransform}
     * Otherwise if {@code false} only the {@link Vector2} position will be changed
     *
     * @param position  the position
     * @param transform if transforming
     */
    void setPosition(Vector2 position, boolean transform);

    /**
     * Set the position of this entity
     * If {@code  transform} is {@code  true} then if the entity has a {@link Body}
     * That bodies position will be set to that position using {@code setTransform}
     * Otherwise if {@code false} only the {@link Vector2} position will be changed
     *
     * @param x         x position
     * @param y         y position
     * @param transform if transforming
     */
    void setPosition(float x, float y, boolean transform);

    /**
     * This is defined by a variable and not by their velocity
     *
     * @return {@code true} if moving
     */
    boolean isMoving();

    /**
     * @param moving state
     */
    void setMoving(boolean moving);

    /**
     * @return previous position of this entity used for interpolation
     */
    Vector2 getPreviousPosition();

    /**
     * Usually set right before updating an entities current position
     *
     * @param position set previous position
     */
    void setPreviousPosition(Vector2 position);

    /**
     * Usually set right before updating an entities current position
     *
     * @param x position
     * @param y y position
     */
    void setPreviousPosition(float x, float y);

    /**
     * @return interpolated position for drawing or anything else that requires smoothing
     */
    Vector2 getInterpolatedPosition();

    /**
     * @param position position
     */
    void setInterpolatedPosition(Vector2 position);

    /**
     * @param x x position
     * @param y y position
     */
    void setInterpolatedPosition(float x, float y);

    /**
     * @return current linear velocity of this entity
     */
    Vector2 getVelocity();

    /**
     * @param velocity the linear velocity
     */
    void setVelocity(Vector2 velocity);

    /**
     * @param x linear X velocity
     * @param y linear Y velocity
     */
    void setVelocity(float x, float y);

    /**
     * Set amount of alpha or 'smoothing' to use when interpolating
     * Usually between 0.5f - 1.0f
     *
     * @param alpha alpha
     */
    void setInterpolationAlpha(float alpha);

    /**
     * Interpolate the position of this entity
     */
    void interpolatePosition();

    /**
     * Interpolate the position of this entity
     *
     * @param alpha alpha to use for 'smoothing'
     */
    void interpolatePosition(float alpha);

    /**
     * Interpolate the position of this entity
     *
     * @param interpolation interpolation method to use
     * @param alpha         alpha to use for 'smoothing'
     */
    void interpolatePosition(Interpolation interpolation, float alpha);

    /**
     * @return the angle or rotation of this entity
     */
    float getAngle();

    /**
     * @param angle angle or rotation
     */
    void setAngle(float angle);

    /**
     * Return a basic direction only supporting LEFT, RIGHT, UP, DOWN
     *
     * @return direction
     */
    BasicDirection getDirection();

    /**
     * Set direction
     *
     * @param direction the direction
     */
    void setDirection(BasicDirection direction);

    /**
     * @return the box2d {@link Body} or {@code  null} if none yet
     */
    Body getBody();

    /**
     * Set the body
     *
     * @param body the body
     */
    void setBody(Body body);

    /**
     * The ashley {@link Entity} of this entity
     *
     * @return the entity
     */
    Entity getEntity();

    /**
     * Set a new entity and dispose the old current entity if needed.
     * It is up to you to remove the entity from any systems you have using it.
     *
     * @param entity           the entity
     * @param disposeOldEntity if {@code true} the old {@link Entity} will be disposed of and removed.
     */
    void setEntity(Entity entity, boolean disposeOldEntity);

    /**
     * Add default components
     * {@link EntityPropertiesComponent}
     * {@link lunar.shared.components.position.EntityPositionComponent}
     * {@link lunar.shared.components.position.EntityVelocityComponent}
     */
    void addComponents();

    /**
     * @return the properties of this entity
     */
    EntityPropertiesComponent getProperties();

    /**
     * Update this entity
     *
     * @param delta the delta time
     */
    void update(float delta);

    /**
     * Load any required assets or data for this entity
     */
    void loadEntity();

}
