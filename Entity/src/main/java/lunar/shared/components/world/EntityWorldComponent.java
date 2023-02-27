package lunar.shared.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.LunarEntity;
import lunar.shared.player.LunarEntityPlayer;
import lunar.shared.player.mp.LunarNetworkEntityPlayer;

/**
 * Manages data about what world or instance an entity is in.
 */
public class EntityWorldComponent implements Component, Pool.Poolable {

    // world or instance, could be both.
    public LunarWorld<?, ?, ?> worldIn;
    public LunarInstance<?, ?, ?> instanceIn;

    public <P extends LunarEntityPlayer,
            N extends LunarNetworkEntityPlayer,
            E extends LunarEntity> LunarWorld<P, N, E> getWorldIn() {
        return (LunarWorld<P, N, E>) worldIn;
    }

    public <P extends LunarEntityPlayer,
            N extends LunarNetworkEntityPlayer,
            E extends LunarEntity> void setWorldIn(LunarWorld<P, N, E> worldIn) {
        this.worldIn = worldIn;
    }

    public <P extends LunarEntityPlayer,
            N extends LunarNetworkEntityPlayer,
            E extends LunarEntity> LunarInstance<P, N, E> getInstanceIn() {
        return (LunarInstance<P, N, E>) instanceIn;
    }

    public <P extends LunarEntityPlayer,
            N extends LunarNetworkEntityPlayer,
            E extends LunarEntity> void setInstanceIn(LunarInstance<P, N, E> instanceIn) {
        this.instanceIn = instanceIn;
    }

    @Override
    public void reset() {
        worldIn = null;
        instanceIn = null;
    }
}
