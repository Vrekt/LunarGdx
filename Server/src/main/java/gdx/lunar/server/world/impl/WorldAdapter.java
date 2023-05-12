package gdx.lunar.server.world.impl;

import gdx.lunar.server.entity.LunarServerEntity;
import gdx.lunar.server.entity.LunarServerPlayerEntity;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.server.world.AbstractServerWorld;

/**
 * Represents a basic world adapter.
 */
public class WorldAdapter extends AbstractServerWorld<LunarServerPlayerEntity, LunarServerEntity> {

    public WorldAdapter(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

    public WorldAdapter() {
        super(new ServerWorldConfiguration(), "WorldAdapter");
    }

}
