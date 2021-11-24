package gdx.lunar.entity.drawing;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.entity.player.LunarEntity;

/**
 * Represents an entity that could be animated.
 */
public abstract class LunarAnimatedEntity extends LunarEntity {

    public LunarAnimatedEntity(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public LunarAnimatedEntity(boolean initializeComponents) {
        super(initializeComponents);
    }

}
