package gdx.lunar.entity.drawing;

import com.badlogic.ashley.core.Entity;
import gdx.lunar.entity.components.drawing.EntityTextureComponent;
import gdx.lunar.entity.player.LunarEntity;

/**
 * Represents an entity that could be drawn or animated.
 */
public abstract class LunarTextureEntity extends LunarEntity {

    public LunarTextureEntity(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public LunarTextureEntity(boolean initializeComponents) {
        super(initializeComponents);
    }

    @Override
    protected void addComponents() {
        entity.add(new EntityTextureComponent());
        super.addComponents();
    }
}
