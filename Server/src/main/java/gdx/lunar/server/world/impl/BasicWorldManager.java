package gdx.lunar.server.world.impl;

import gdx.lunar.server.world.WorldManager;

public final class BasicWorldManager extends WorldManager {

    public BasicWorldManager() {
        worlds.put("LunarWorld", new BasicLunarWorld());
    }
}
