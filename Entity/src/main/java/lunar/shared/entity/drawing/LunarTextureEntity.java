package lunar.shared.entity.drawing;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lunar.shared.entity.components.drawing.EntityTextureComponent;
import lunar.shared.entity.mapping.GlobalEntityMapper;
import lunar.shared.entity.player.LunarEntity;

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

    public TextureRegion currentRegionState;
    public Texture currentTextureState;

    public TextureRegion putRegion(String name, TextureRegion texture) {
        return GlobalEntityMapper.texture.get(entity).textureRegions.put(name, texture);
    }

    public Texture putTexture(String name, Texture texture) {
        return GlobalEntityMapper.texture.get(entity).textures.put(name, texture);
    }

    public TextureRegion getRegion(String name) {
        return GlobalEntityMapper.texture.get(entity).textureRegions.get(name);
    }

    public Texture getTexture(String name) {
        return GlobalEntityMapper.texture.get(entity).textures.get(name);
    }

    public void removeAllRegions() {
        GlobalEntityMapper.texture.get(entity).textureRegions.clear();
    }

    public void removeAllTextures() {
        GlobalEntityMapper.texture.get(entity).textures.clear();
    }

    @Override
    protected void addComponents() {
        entity.add(new EntityTextureComponent());
        super.addComponents();
    }
}
