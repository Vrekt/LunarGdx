package lunar.shared.drawing;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lunar.shared.mapping.GlobalEntityMapper;

/**
 * Represents an entity that could be animated.
 * <p>
 * TODO: GlobalEntityMapper.ANIMATION
 */
public abstract class LunarAnimatedEntity extends LunarTextureEntity {

    public LunarAnimatedEntity(Entity entity, boolean initializeComponents) {
        super(entity, initializeComponents);
    }

    public LunarAnimatedEntity(boolean initializeComponents) {
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

}
