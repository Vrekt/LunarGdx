package gdx.lunar.server.world.impl;

import gdx.lunar.server.world.WorldManager;

/**
 * A bare-bones {@link WorldManager}
 */
public class WorldManagerAdapter extends WorldManager {

    public WorldManagerAdapter() {
        worlds.put("WorldAdapter", new WorldAdapter());
    }

}
