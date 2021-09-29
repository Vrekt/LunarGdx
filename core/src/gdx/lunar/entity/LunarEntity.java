package gdx.lunar.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.world.LunarWorld;

/**
 * The base model for all entities utilizing lunar.
 */
public abstract class LunarEntity implements Disposable {

    /**
     * The ID of this entity.
     */
    protected int entityId;

    /**
     * The interpolated position and velocity of this entity.
     */
    protected final Vector2 interpolated, velocity;

    /**
     * Previous and current positions.
     */
    protected float prevX, prevY, currentX, currentY;

    /**
     * The world this entity is in.
     */
    protected LunarWorld worldIn;

    public LunarEntity(int entityId) {
        this.entityId = entityId;
        this.interpolated = new Vector2();
        this.velocity = new Vector2();
    }

    public LunarEntity() {
        this(0);
    }

    /**
     * Update this entity
     *
     * @param delta the delta time
     */
    public abstract void update(float delta);

    /**
     * Spawn this entity in a world.
     *
     * @param world the world
     * @param x     X
     * @param y     Y
     */
    public abstract void spawnEntityInWorld(LunarWorld world, float x, float y);

    /**
     * @return this entities unique ID.
     */
    public int getEntityId() {
        return entityId;
    }

    /**
     * @return previous X
     */
    public float getPrevX() {
        return prevX;
    }

    /**
     * @return previous Y
     */
    public float getPrevY() {
        return prevY;
    }

    /**
     * @return current X
     */
    public float getX() {
        return currentX;
    }

    /**
     * @return current Y
     */
    public float getY() {
        return currentY;
    }

    public void setX(float currentX) {
        this.currentX = currentX;
    }

    public void setY(float currentY) {
        this.currentY = currentY;
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    /**
     * @return the entities interpolated position for better drawing.
     */
    public Vector2 getInterpolated() {
        return interpolated;
    }

    /**
     * @return the velocity of this entity.
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * @return the world this entity is in.
     */
    public LunarWorld getWorldIn() {
        return worldIn;
    }

    public void setWorldIn(LunarWorld worldIn) {
        this.worldIn = worldIn;
    }

}
