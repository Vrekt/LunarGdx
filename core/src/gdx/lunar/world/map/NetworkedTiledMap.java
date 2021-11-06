package gdx.lunar.world.map;

import com.badlogic.gdx.maps.tiled.TiledMap;

/**
 * Represents a tiled map that is synced between players.
 */
public class NetworkedTiledMap {

    protected final TiledMap map;

    public NetworkedTiledMap(TiledMap map) {
        this.map = map;
    }

}
