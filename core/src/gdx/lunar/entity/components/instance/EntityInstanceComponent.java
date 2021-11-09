package gdx.lunar.entity.components.instance;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.world.v2.LunarWorld;

/**
 * Manages data about what world or instance an entity is in.
 */
public class EntityInstanceComponent implements Component, Pool.Poolable {

    // world or instance, could be both.
    public LunarWorld<?, ?, ?> worldIn;
    public LunarInstance instanceIn;

    // box2d worlds this entity is in.
    public Array<World> worldsIn = new Array<>();

    @Override
    public void reset() {
        worldIn = null;
        instanceIn = null;
        worldsIn.clear();
    }
}
