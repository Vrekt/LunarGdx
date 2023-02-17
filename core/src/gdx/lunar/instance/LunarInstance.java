package gdx.lunar.instance;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.instance.config.InstanceConfiguration;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntity;
import lunar.shared.entity.player.LunarEntityPlayer;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;

/**
 * Represents a separate instance that could be networked.
 * <p>
 * For interiors, dungeons, etc
 */
public abstract class LunarInstance<P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity>
        extends LunarWorld<P, N, E> implements Disposable {

    // the ID of this instance
    protected int instanceId;

    // the location of this instance within {worldFrom}
    protected final Vector2 instanceLocation = new Vector2();
    // where the player can exist this instance
    protected final Vector2 exitLocation = new Vector2();

    // the parent world
    protected LunarWorld<P, N, E> worldFrom;

    public LunarInstance(P player, World world, InstanceConfiguration configuration, PooledEngine engine, int instanceId) {
        super(player, world, configuration, engine);
        this.instanceId = instanceId;
    }

    public LunarInstance(P player, World world, int instanceId) {
        super(player, world, new InstanceConfiguration(), new PooledEngine());
        this.instanceId = instanceId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setWorldFrom(LunarWorld<P, N, E> worldFrom) {
        this.worldFrom = worldFrom;
    }

    public LunarWorld<P, N, E> getWorldFrom() {
        return worldFrom;
    }

    public void setInstanceLocation(float x, float y) {
        instanceLocation.set(x, y);
    }

    public void setInstanceLocation(Vector2 position) {
        instanceLocation.set(position);
    }

    public void setExitLocation(float x, float y) {
        exitLocation.set(x, y);
    }

    public void setExitLocation(Vector2 vector2) {
        exitLocation.set(vector2);
    }

    /**
     * Enter this instance
     *
     * @param checkIfEnterable if {@code  true} then, {@code canEnterWorld} will be checked.
     * @param worldFrom        the world from
     * @return {@code  true} if the instance was entered, otherwise {@code  false}
     */
    public boolean enterInstance(boolean checkIfEnterable, LunarWorld<P, N, E> worldFrom) {
        if (checkIfEnterable && !canEnterWorld()) return false;
        setWorldFrom(worldFrom);
        player.spawnEntityInInstance(this);
        return true;
    }

    /**
     * If this instance should be loaded based on player position
     *
     * @param position position
     * @return {@code  true}
     */
    public boolean shouldLoadInstance(Vector2 position) {
        return getConfiguration().preLoadInstance && position.dst2(instanceLocation) <= getConfiguration().preLoadInstanceDistance;
    }

    /**
     * If this instance should be loaded based on player position
     *
     * @param x x
     * @param y y
     * @return {@code  true}
     */
    public boolean shouldLoadInstance(float x, float y) {
        return getConfiguration().preLoadInstance && Vector2.dst2(x, y, instanceLocation.x, instanceLocation.y) <= getConfiguration().preLoadInstanceDistance;
    }

    @Override
    public InstanceConfiguration getConfiguration() {
        return (InstanceConfiguration) super.getConfiguration();
    }

}
