package lunar.shared.entity;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.world.LunarWorld;

/**
 * An entity that can be spawned into a world
 */
public interface Spawnable {

    /**
     * Spawn in a world
     *
     * @param world    the world
     * @param position the position
     */
    default <T extends LunarWorld> void spawnInWorld(T world, Vector2 position) {

    }

    /**
     * Spawn in a world
     *
     * @param world the world
     */
    default <T extends LunarWorld> void spawnInWorld(T world) {

    }

    /**
     * Remove from the current world in
     */
    default void removeFromWorld() {

    }

}
