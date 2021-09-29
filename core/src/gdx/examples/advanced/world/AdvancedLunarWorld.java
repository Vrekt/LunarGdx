package gdx.examples.advanced.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import gdx.examples.advanced.entity.NetworkPlayer;
import gdx.lunar.entity.player.LunarEntityPlayer;
import gdx.lunar.entity.player.LunarNetworkEntityPlayer;
import gdx.lunar.world.LunarWorld;

public class AdvancedLunarWorld extends LunarWorld {

    public AdvancedLunarWorld(LunarEntityPlayer player, World world) {
        super(player, world);

        setVelocityIterations(6);
        setPositionIterations(3);
        setHandlePhysics(false);
        setUpdatePlayer(false);
        setUpdateNetworkPlayers(false);

        this.worldScale = (1 / 16.0f);
    }

    @Override
    public void update(float d) {
        // handle updating this world ourselves.

        for (LunarNetworkEntityPlayer value : players.values()) value.preUpdate();
        final float delta = Math.min(d, maxFrameTime);
        accumulator += delta;

        player.preUpdate();
        while (accumulator >= stepTime) {
            player.preUpdate();

            world.step(stepTime, velocityIterations, positionIterations);
            accumulator -= stepTime;
        }

        player.update(delta);
        player.interpolate(1.0f);

        for (LunarNetworkEntityPlayer value : players.values()) {
            value.update(delta);
            value.interpolate(0.5f);
        }
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        for (LunarNetworkEntityPlayer value : players.values()) {
            if (((NetworkPlayer) value).myCustomProperty()) {
                // for example, this player could be hidden so don't draw them.
                continue;
            } else {
                value.render(batch, delta);
            }
        }
    }
}
