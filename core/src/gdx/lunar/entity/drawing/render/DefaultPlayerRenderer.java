package gdx.lunar.entity.drawing.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import gdx.lunar.entity.drawing.Rotation;

import java.util.Map;

/**
 * Default/provided implementation of {@link LunarPlayerRenderer}
 */
public final class DefaultPlayerRenderer extends LunarPlayerRenderer {

    public DefaultPlayerRenderer(TextureAtlas atlas, Rotation rotation,
                                 Map<Rotation, String> walkingAnimationNames,
                                 Map<Rotation, String> idleAnimationNames,
                                 float width, float height, boolean offsetBox2d) {
        super(atlas, rotation, walkingAnimationNames, idleAnimationNames, width, height, offsetBox2d);
    }

    public DefaultPlayerRenderer(TextureAtlas atlas, Rotation rotation, float width, float height, boolean offsetBox2d) {
        super(atlas, rotation, width, height, offsetBox2d);
    }

    @Override
    public void load() {
        for (Rotation rotation : Rotation.values()) {
            final String walkingAnimationName = walkingAnimations.get(rotation);
            final String idleAnimationName = idleAnimations.get(rotation);
            final Animation<TextureRegion> walkingAnimation = createAnimation(walkingAnimationName, atlas, true);
            final TextureRegion idleTexture = atlas.findRegion(idleAnimationName);
            this.animations.put(rotation, new AnimationPair(walkingAnimation, idleTexture));

            if (rotation == this.rotation) {
                this.idle = idleTexture;
                this.animation = walkingAnimation;
            }
        }

        // free some memory
        this.walkingAnimations.clear();
        this.idleAnimations.clear();
    }

    @Override
    public void update(float delta, Rotation rotation, boolean hasVelocity) {
        animate = hasVelocity;

        if (!animate) animationTime = 0f;

        // ensure we are not constantly getting the texture over and over.
        if (!animate && rotation != lastIdleRotation) {
            lastIdleRotation = rotation;
            idle = animations.get(rotation).idle;
        }

        // ensure we are not constantly getting the animation over and over.
        if (animate && rotation != lastAnimationRotation) {
            lastAnimationRotation = rotation;
            animation = animations.get(rotation).animation;
        }

    }

    @Override
    public void render(float delta, float x, float y, SpriteBatch batch) {
        if (!animate) {
            drawIdleState(x, y, batch);
        } else {
            drawAnimationFrame(x, y, batch);
            animationTime += delta;
        }
    }
}
