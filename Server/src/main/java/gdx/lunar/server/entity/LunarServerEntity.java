package gdx.lunar.server.entity;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.LunarEntity;
import lunar.shared.player.LunarEntityPlayer;
import lunar.shared.player.mp.LunarNetworkEntityPlayer;

public class LunarServerEntity extends LunarEntity {
    public LunarServerEntity(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world, float x, float y) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInWorld(LunarWorld<P, N, E> world) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInWorld(LunarWorld<P, N, E> world) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInInstance(LunarInstance<P, N, E> instance, float x, float y) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void spawnEntityInInstance(LunarInstance<P, N, E> instance) {

    }

    @Override
    public <P extends LunarEntityPlayer, N extends LunarNetworkEntityPlayer, E extends LunarEntity> void removeEntityInInstance(LunarInstance<P, N, E> instance) {

    }
}
