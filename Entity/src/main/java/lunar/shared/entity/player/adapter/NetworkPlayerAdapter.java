package lunar.shared.entity.player.adapter;

import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntityNetworkPlayer;

/**
 * Represents a default multiplayer (player) state
 */
public class NetworkPlayerAdapter extends LunarEntityNetworkPlayer {
    private LunarWorld world;

    public NetworkPlayerAdapter(boolean initializeComponents) {
        super(initializeComponents);
    }

    public void setWorld(LunarWorld world) {
        this.world = world;
        setInWorld(world != null);
    }

    @Override
    public LunarWorld getWorld() {
        return world;
    }
}
