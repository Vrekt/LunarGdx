package gdx.lunar.server.instance;

import gdx.lunar.server.world.World;

public abstract class InstanceWorld extends World {

    public InstanceWorld(String instanceName, int maxPacketsPerTick, int capacity, int maxEntities, int maxEntityRequests) {
        super(instanceName, maxPacketsPerTick, capacity, maxEntities, maxEntityRequests);
    }

}
