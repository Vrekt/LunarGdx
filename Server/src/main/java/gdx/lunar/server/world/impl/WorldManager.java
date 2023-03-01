package gdx.lunar.server.world.impl;

import gdx.lunar.server.world.AbstractWorldManager;

/**
 * A bare-bones {@link AbstractWorldManager}
 */
public class WorldManager extends AbstractWorldManager {

    public WorldManager() {
        worlds.put("WorldAdapter", new WorldAdapter());
    }

}
