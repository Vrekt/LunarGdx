package gdx.lunar.server.instance;

import gdx.lunar.server.world.ServerWorld;
import gdx.lunar.server.world.config.ServerWorldConfiguration;

/**
 * Represents an instance (interior, dungeon, etc) within a world.
 */
public abstract class Instance extends ServerWorld {

    protected int instanceId;

    public Instance(ServerWorldConfiguration configuration, String worldName, int instanceId) {
        super(configuration, worldName);
        this.instanceId = instanceId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }
}
