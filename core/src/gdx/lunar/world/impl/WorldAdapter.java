package gdx.lunar.world.impl;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.world.WorldConfiguration;
import gdx.lunar.world.AbstractGameWorld;
import gdx.lunar.utilities.PlayerSupplier;
import lunar.shared.entity.LunarEntity;
import lunar.shared.entity.player.impl.NetworkPlayer;

/**
 * Represents a basic (default) world.
 */
public class WorldAdapter extends AbstractGameWorld<NetworkPlayer, LunarEntity> {

    public WorldAdapter(PlayerSupplier playerSupplier, World world, WorldConfiguration configuration, Engine engine) {
        super(playerSupplier, world, configuration, engine);
    }

}
