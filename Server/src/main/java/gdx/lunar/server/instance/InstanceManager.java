package gdx.lunar.server.instance;

import gdx.lunar.server.game.utilities.Disposable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic manager for instances.
 * <p>
 * Like {@link gdx.lunar.server.world.WorldManager}
 */
public abstract class InstanceManager implements Disposable {

    /**
     * Map of all instances
     */
    protected final Map<String, InstanceWorld> instances = new HashMap<>();

    public InstanceManager() {

    }

    /**
     * Add a InstanceWorld
     *
     * @param name     the name
     * @param instance the instance
     */
    public void addInstance(String name, InstanceWorld instance) {
        this.instances.put(name, instance);
    }

    /**
     * Retrieve an InstanceWorld by its name
     *
     * @param name the name
     * @return the instance
     */
    public InstanceWorld getInstance(String name) {
        return instances.get(name);
    }

    /**
     * @return all instance
     */
    public Collection<InstanceWorld> getInstances() {
        return instances.values();
    }

    @Override
    public void dispose() {
        instances.clear();
    }

}
