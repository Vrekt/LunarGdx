package lunar.shared.entity;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.world.LunarWorld;

/**
 * Something that can be spawned into a world
 */
public interface Spawnable {

    /**
     * Spawn in a world
     *
     * @param world    the world
     * @param position the position
     */
    default void spawnInWorld(LunarWorld world, Vector2 position) {

    }

    /**
     * Spawn in a world
     *
     * @param world the world
     */
    default void spawnInWorld(LunarWorld world) {

    }

    /**
     * Remove from a world
     *
     * @param world the world
     */
    default void removeFromWorld(LunarWorld world) {

    }

}
