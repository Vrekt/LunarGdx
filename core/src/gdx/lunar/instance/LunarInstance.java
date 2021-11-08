package gdx.lunar.instance;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.entity.player.LunarEntityPlayer;

/**
 * Represents a separate instance that could be networked.
 * <p>
 * For instance interiors.
 */
public abstract class LunarInstance extends ScreenAdapter {

    // local box2d world for this interior.
    protected final World world;

    protected float stepTime = 1.0f / 60.0f;
    protected float maxFrameTime = 0.25f;
    protected float accumulator;

    protected int velocityIterations = 8, positionIterations = 3;

    protected LunarInstance(World world, int velocityIterations, int positionIterations) {
        this.world = world;
        this.velocityIterations = velocityIterations;
        this.positionIterations = positionIterations;
    }

    /**
     * Creates a default {@code world}
     */
    public LunarInstance() {
        world = new World(Vector2.Zero, true);
    }

    public World getWorld() {
        return world;
    }

    public void setVelocityIterations(int velocityIterations) {
        this.velocityIterations = velocityIterations;
    }

    public void setPositionIterations(int positionIterations) {
        this.positionIterations = positionIterations;
    }

    public void setStepTime(float stepTime) {
        this.stepTime = stepTime;
    }

    public void setMaxFrameTime(float maxFrameTime) {
        this.maxFrameTime = maxFrameTime;
    }

    /**
     * Step the internal physics world
     *
     * @param player the player to pre-update.
     * @param delta  delta time
     */
    public void stepPhysicsWorld(LunarEntityPlayer player, float delta) {
        accumulator += delta;

        while (accumulator >= stepTime) {
            player.preUpdate();

            world.step(stepTime, velocityIterations, positionIterations);
            accumulator -= stepTime;
        }
    }

    /**
     * Step the internal physics world
     *
     * @param delta delta time
     */
    public void stepPhysicsWorld(float delta) {
        accumulator += delta;

        while (accumulator >= stepTime) {
            world.step(stepTime, velocityIterations, positionIterations);
            accumulator -= stepTime;
        }
    }

}
