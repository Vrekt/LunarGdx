package gdx.examples.basic;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.instance.config.InstanceConfiguration;
import lunar.shared.entity.player.LunarEntity;
import lunar.shared.entity.player.impl.LunarPlayer;
import lunar.shared.entity.player.impl.LunarPlayerMP;

public final class MultiplayerGameInstance extends LunarInstance<LunarPlayer, LunarPlayerMP, LunarEntity> {
    public MultiplayerGameInstance(DemoPlayer player, World world, InstanceConfiguration configuration, PooledEngine engine, int instanceId) {
        super(player, world, configuration, engine, instanceId);
    }

    public MultiplayerGameInstance(DemoPlayer player, World world, int instanceId) {
        super(player, world, instanceId);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        super.renderWorld(batch, delta);
    }
}
