package gdx.lunar.world.map;

import com.badlogic.gdx.math.Vector2;

/**
 * Represents a singular tile, that could be networked.
 */
public abstract class LunarNetworkedTile {

    private final Vector2 position = new Vector2();
    private boolean isNetworked;
    private String tileTexture;

    /**
     * Create a new basic tile
     *
     * @param isNetworked if this tile is networked
     * @param tileTexture the tile texture asset.
     */
    public LunarNetworkedTile(boolean isNetworked, String tileTexture) {
        this.isNetworked = isNetworked;
        this.tileTexture = tileTexture;
    }

    public boolean isNetworked() {
        return isNetworked;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getTileTexture() {
        return tileTexture;
    }
}
