package gdx.lunar.world.impl;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.physics.box2d.World;
import gdx.lunar.utilities.PlayerSupplier;
import gdx.lunar.world.AbstractGameWorld;
import gdx.lunar.world.WorldConfiguration;
import lunar.shared.entity.player.impl.NetworkPlayer;
import lunar.shared.entity.AbstractLunarEntity;

/**
 * Represents a basic (default) world.
 */
public class WorldAdapter extends AbstractGameWorld<NetworkPlayer, AbstractLunarEntity> {

    public WorldAdapter(PlayerSupplier playerSupplier, World world, WorldConfiguration configuration, Engine engine) {
        super(playerSupplier, world, configuration, engine);
    }

}
