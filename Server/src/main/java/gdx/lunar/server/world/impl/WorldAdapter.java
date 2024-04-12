package gdx.lunar.server.world.impl;

import gdx.lunar.server.world.AbstractServerWorld;
import gdx.lunar.server.world.config.ServerWorldConfiguration;

/**
 * Represents a basic world adapter.
 */
public class WorldAdapter extends AbstractServerWorld{

    public WorldAdapter(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

    public WorldAdapter() {
        super(new ServerWorldConfiguration(), "WorldAdapter");
    }

}
