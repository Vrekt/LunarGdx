package gdx.lunar.world.impl;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.entity.player.LunarEntity;
import gdx.lunar.entity.player.impl.LunarPlayer;
import gdx.lunar.entity.player.impl.LunarPlayerMP;
import gdx.lunar.world.LunarWorld;
import gdx.lunar.world.WorldConfiguration;

/**
 * Represents a basic (default) world.
 */
public class WorldAdapter extends LunarWorld<LunarPlayer, LunarPlayerMP, LunarEntity> {

    public WorldAdapter(LunarPlayer player, World world, float worldScale, boolean handlePhysics, boolean updatePlayer, boolean updateNetworkPlayers, boolean updateEntities, PooledEngine engine) {
        super(player, world, worldScale, handlePhysics, updatePlayer, updateNetworkPlayers, updateEntities, engine);
    }

    public WorldAdapter(LunarPlayer player, World world, WorldConfiguration configuration, PooledEngine engine) {
        super(player, world, configuration, engine);
    }

    public WorldAdapter(LunarPlayer player, World world) {
        super(player, world);
    }

    public void renderWorld(SpriteBatch batch) {

    }

}
