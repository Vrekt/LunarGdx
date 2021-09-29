package gdx.lunar.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;

/**
 * A basic lunar world.
 */
public final class BasicLunarWorld extends LunarWorld {

    public BasicLunarWorld(LunarEntityPlayer player, World world, float worldScale, boolean handlePhysics, boolean updatePlayer, boolean updateNetworkPlayers) {
        super(player, world, worldScale, handlePhysics, updatePlayer, updateNetworkPlayers);
    }

    public BasicLunarWorld(LunarEntityPlayer player, World world) {
        super(player, world);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        for (LunarNetworkEntityPlayer value : players.values()) value.render(batch, delta);
    }
}
