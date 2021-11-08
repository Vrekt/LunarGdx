package gdx.lunar.entity.components.drawing;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Manages data about an entities current texture or animation to draw.
 */
public class EntityTextureComponent implements Component {

    // region or texture.
    public TextureRegion textureRegion;
    public Texture texture;

}
