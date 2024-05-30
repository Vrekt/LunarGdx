package lunar.shared.entity.player.adapter;

import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntityPlayer;

/**
 * Represents a default player state
 */
public class PlayerAdapter extends LunarEntityPlayer {

    private LunarWorld world;

    public PlayerAdapter(boolean initializeComponents) {
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

    @Override
    public void update(float delta) {
        super.update(delta);

        if (body != null) body.setLinearVelocity(getVelocity());
    }
}
