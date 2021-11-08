package gdx.lunar.entity.network;

import gdx.lunar.entity.LunarEntity;
import gdx.lunar.instance.LunarInstance;
import gdx.lunar.world.LunarWorld;

/**
 * A basic networked {@link gdx.lunar.entity.LunarEntity}
 * TODO
 */
public class NetworkEntity extends LunarEntity {

    public NetworkEntity(String name, int entityId) {
        super(entityId);
        this.name = name;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void spawnEntityInWorld(LunarWorld world, float x, float y) {

    }

    @Override
    public void spawnEntityInInstance(LunarInstance instance, float x, float y, boolean destroyOtherBodies) {

    }

    @Override
    public void dispose() {

    }
}
