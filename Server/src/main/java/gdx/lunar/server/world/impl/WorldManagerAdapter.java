package gdx.lunar.server.world.impl;

import gdx.lunar.server.world.WorldManager;
import gdx.lunar.server.world.lobby.LobbyWorldAdapter;

/**
 * A bare-bones {@link WorldManager}
 */
public class WorldManagerAdapter extends WorldManager {

    public WorldManagerAdapter() {
        worlds.put("LunarWorld", new LunarWorldAdapter());
        worlds.put("Lobby", new LobbyWorldAdapter());
    }

}
