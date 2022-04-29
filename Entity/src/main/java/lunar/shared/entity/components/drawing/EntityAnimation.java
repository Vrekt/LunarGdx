package lunar.shared.entity.components.drawing;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Manages an entities animation state
 */
public class EntityAnimation {

    // the actual animation
    public Animation<TextureRegion> animation;

    // key frame time.
    public float animationTime;
    // if animation should be drawn
    public boolean animate;

    public EntityAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public EntityAnimation() {
    }

    public TextureRegion getFrame() {
        return animation.getKeyFrame(animationTime);
    }

}
