package gdx.lunar.world.impl;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.world.AbstractGameWorld;
import gdx.lunar.world.WorldConfiguration;
import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.adapter.NetworkPlayerAdapter;

/**
 * Represents a basic (default) world.
 */
public class WorldAdapter extends AbstractGameWorld<NetworkPlayerAdapter, LunarEntity> {
    public WorldAdapter(World world, WorldConfiguration configuration, Engine engine) {
        super(world, configuration, engine);
    }
}
