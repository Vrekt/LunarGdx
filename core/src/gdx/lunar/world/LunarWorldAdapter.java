package gdx.lunar.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;

/**
 * Represents a basic or default impl of {@link LunarWorld}
 * Instead, this adapter handles rendering of local network players.
 */
public class LunarWorldAdapter extends LunarWorld {

    public LunarWorldAdapter(LunarEntityPlayer player, World world, float worldScale, boolean handlePhysics, boolean updatePlayer, boolean updateNetworkPlayers, boolean updateEntities) {
        super(player, world, worldScale, handlePhysics, updatePlayer, updateNetworkPlayers, updateEntities);
    }

    public LunarWorldAdapter(LunarEntityPlayer player, World world) {
        super(player, world);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        for (LunarNetworkEntityPlayer value : players.values()) value.render(batch, delta);
    }
}
