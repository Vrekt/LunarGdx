package lunar.shared.entity.texture;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lunar.shared.components.drawing.EntityTextureComponent;
import lunar.shared.entity.AbstractLunarEntity;
import lunar.shared.mapping.GlobalEntityMapper;

/**
 * Default implementation of {@link LunarTexturedEntity}
 */
public abstract class AbstractLunarTexturedEntity extends AbstractLunarEntity implements LunarTexturedEntity {

    protected Sprite sprite;

    protected TextureRegion currentRegion;
    protected Texture currentTexture;

    public AbstractLunarTexturedEntity(Entity entity, boolean addDefaultComponents) {
        super(entity, addDefaultComponents);
    }

    public AbstractLunarTexturedEntity(boolean addDefaultComponents) {
        super(addDefaultComponents);
    }

    @Override
    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public void addRegion(String regionName, TextureRegion region) {
        GlobalEntityMapper.texture.get(getEntity()).textureRegions.put(regionName, region);
    }

    @Override
    public TextureRegion getRegion(String regionName) {
        return GlobalEntityMapper.texture.get(entity).textureRegions.get(regionName);
    }

    @Override
    public void addTexture(String textureName, Texture texture) {
        GlobalEntityMapper.texture.get(entity).textures.put(textureName, texture);
    }

    @Override
    public Texture getTexture(String textureName) {
        return GlobalEntityMapper.texture.get(entity).textures.get(textureName);
    }

    @Override
    public void addComponents() {
        super.addComponents();
        entity.add(new EntityTextureComponent());
    }
}
