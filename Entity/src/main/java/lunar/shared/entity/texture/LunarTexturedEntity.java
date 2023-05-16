package lunar.shared.entity.texture;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lunar.shared.entity.LunarEntity;

/**
 * Represents an entity that has a texture or sprite.
 * Mainly for convenience but does not have to be used.
 */
public interface LunarTexturedEntity extends LunarEntity {

    /**
     * @return the internal sprite of this entity if any
     */
    Sprite getSprite();

    /**
     * Set the sprite for this entity to use
     *
     * @param sprite the sprite
     */
    void setSprite(Sprite sprite);

    /**
     * Add a {@link TextureRegion} to be stored unique to this entity
     *
     * @param regionName the region name or key
     * @param region     the region or value
     */
    void addRegion(String regionName, TextureRegion region);

    /**
     * Get a {@link TextureRegion} by name
     *
     * @param regionName the region name
     * @return the region or {@code  null} if not found
     */
    TextureRegion getRegion(String regionName);

    /**
     * Add a {@link Texture} to be stored unique to this entity
     *
     * @param textureName the texture name or key
     * @param texture     the texture or value
     */
    void addTexture(String textureName, Texture texture);

    /**
     * Get a {@link Texture} by name
     *
     * @param textureName the texture name
     * @return the texture or {@code  null} if not found
     */
    Texture getTexture(String textureName);

}
