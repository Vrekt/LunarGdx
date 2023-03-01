package gdx.lunar.server.world;

import gdx.lunar.server.game.utilities.Disposable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all the worlds within this server.
 */
public abstract class AbstractWorldManager implements Disposable {

    /**
     * Map of all worlds
     */
    protected final Map<String, ServerWorld> worlds = new HashMap<>();

    public AbstractWorldManager() {

    }

    /**
     * Add a world
     *
     * @param name  the name
     * @param world the world
     */
    public void addWorld(String name, ServerWorld world) {
        this.worlds.put(name, world);
    }

    /**
     * Retrieve a world by its name
     *
     * @param name the name
     * @return the world
     */
    public ServerWorld getWorld(String name) {
        return worlds.get(name);
    }

    public boolean worldExists(String name) {
        return worlds.containsKey(name);
    }

    /**
     * @return all worlds
     */
    public Collection<ServerWorld> getWorlds() {
        return worlds.values();
    }

    /**
     * Update all worlds.
     */
    public void update(float delta) {
        for (ServerWorld value : worlds.values()) {
            value.tick();
        }
    }

    @Override
    public void dispose() {
        getWorlds().forEach(ServerWorld::dispose);
        worlds.clear();
    }
}
