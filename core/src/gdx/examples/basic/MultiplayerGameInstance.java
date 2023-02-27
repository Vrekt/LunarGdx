package gdx.examples.basic;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.instance.config.InstanceConfiguration;
import lunar.shared.entity.LunarEntity;
import lunar.shared.player.impl.LunarPlayer;
import lunar.shared.player.impl.LunarPlayerMP;

public final class MultiplayerGameInstance extends LunarInstance<LunarPlayer, LunarPlayerMP, LunarEntity> {
    public MultiplayerGameInstance(LunarPlayer player, World world, InstanceConfiguration configuration, PooledEngine engine, int instanceId) {
        super(player, world, configuration, engine, instanceId);
    }

    public MultiplayerGameInstance(LunarPlayer player, World world, int instanceId) {
        super(player, world, instanceId);
    }

    @Override
    public float update(float d) {
        return super.update(d);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        super.renderWorld(batch, delta);
    }
}
