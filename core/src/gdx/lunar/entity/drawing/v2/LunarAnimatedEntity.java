package gdx.lunar.entity.drawing.v2;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.entity.components.drawing.EntityAnimationComponent;
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

    @Override
    protected void addComponents() {
        entity.add(new EntityAnimationComponent());
        super.addComponents();
    }

}
