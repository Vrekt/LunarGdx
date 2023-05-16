package gdx.lunar.server.entity;

import com.badlogic.ashley.core.Entity;
import lunar.shared.entity.AbstractLunarEntity;

/**
 * Represents a server entity
 */
public class LunarServerEntity extends AbstractLunarEntity {
    public LunarServerEntity(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public LunarServerEntity(boolean initializeComponents) {
        super(initializeComponents);
    }

    @Override
    public void update(float delta) {

    }

}
