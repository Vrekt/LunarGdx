package lunar.shared.entity.components.instance;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntity;
import lunar.shared.entity.player.LunarEntityPlayer;
import lunar.shared.entity.player.mp.LunarNetworkEntityPlayer;

/**
 * Manages data about what world or instance an entity is in.
 */
public class EntityInstanceComponent implements Component, Pool.Poolable {

    // world or instance, could be both.
    public LunarWorld<?, ?, ?> worldIn;
    public LunarInstance instanceIn;

    // box2d worlds this entity is in.
    public Array<World> worldsIn = new Array<>();

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

    @Override
    public void reset() {
        worldIn = null;
        instanceIn = null;
        worldsIn.clear();
    }
}
