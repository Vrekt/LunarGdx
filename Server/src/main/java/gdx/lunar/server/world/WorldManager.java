package gdx.lunar.server.world;

import gdx.lunar.server.game.utilities.Disposable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all the worlds within this server.
 */
public abstract class WorldManager implements Disposable {

    /**
     * Map of all worlds
     */
    protected final Map<String, World> worlds = new HashMap<>();

    public WorldManager() {

    }

    /**
     * Add a world
     *
     * @param name  the name
     * @param world the world
     */
    public void addWorld(String name, World world) {
        this.worlds.put(name, world);
    }

    /**
     * Retrieve a world by its name
     *
     * @param name the name
     * @return the world
     */
    public World getWorld(String name) {
        return worlds.get(name);
    }

    /**
     * @return all worlds
     */
    public Collection<World> getWorlds() {
        return worlds.values();
    }

    /**
     * Update all worlds.
     */
    public void update(float delta) {

    }

    @Override
    public void dispose() {
        getWorlds().forEach(World::dispose);
        worlds.clear();
    }
}
