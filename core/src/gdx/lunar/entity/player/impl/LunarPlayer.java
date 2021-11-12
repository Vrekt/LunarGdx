package gdx.lunar.entity.player.impl;

import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.network.AbstractConnection;

/**
 * A default player with basic input and movement handling.
 */
public class LunarPlayer extends LunarEntityPlayer {

    public LunarPlayer(boolean initializeComponents) {
        super(initializeComponents);
    }

    public LunarPlayer(boolean initializeComponents, String name, AbstractConnection connection) {
        super(initializeComponents);
        setEntityName(name);
        setConnection(connection);
    }

}
