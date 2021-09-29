package gdx.lunar.entity.drawing.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.entity.drawing.Rotation;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles drawing players and their animations.
 */
public abstract class LunarPlayerRenderer implements Disposable {

    /**
     * Animations by rotation
     */
    protected final Map<Rotation, AnimationPair> animations = new HashMap<>();

    protected final TextureAtlas atlas;
    protected final Map<Rotation, String> walkingAnimations;
    protected final Map<Rotation, String> idleAnimations;

    protected final float width, height;
    protected final boolean offsetBox2d;

    /**
     * Current rotation
     */
    protected Rotation rotation;

    /**
     * Active and idle
     */
    protected Animation<TextureRegion> animation;
    protected TextureRegion idle;

    protected float animationTime;
    protected boolean animate;

    /**
     * Track last known rotations
     */
    protected Rotation lastAnimationRotation, lastIdleRotation;

    /**
     * Initializes a new renderer.
     *
     * @param atlas                 the atlas of player textures and animations.
     * @param rotation              default starting rotation
     * @param walkingAnimationNames the names of the walking animations keyed by rotation.
     * @param idleAnimationNames    the names of the idle animations keyed by rotation.
     * @param width                 the width of the player
     * @param height                the height of the player
     * @param offsetBox2d           if player position should be offset to fit inside box2d bounds.
     */
    public LunarPlayerRenderer(TextureAtlas atlas, Rotation rotation,
                               Map<Rotation, String> walkingAnimationNames,
                               Map<Rotation, String> idleAnimationNames,
                               float width, float height, boolean offsetBox2d) {
        this.atlas = atlas;
        this.rotation = rotation;
        this.walkingAnimations = walkingAnimationNames;
        this.idleAnimations = idleAnimationNames;
        this.width = width;
        this.height = height;
        this.offsetBox2d = offsetBox2d;
    }

    /**
     * Initialize a new renderer.
     * This will use default settings for naming conventions
     *
     * @param atlas       the atlas
     * @param rotation    default starting rotation
     * @param width       the width of the player
     * @param height      the height of the player
     * @param offsetBox2d if player position should be offset to fit inside box2d bounds.
     */
    public LunarPlayerRenderer(TextureAtlas atlas, Rotation rotation, float width, float height, boolean offsetBox2d) {
        this.atlas = atlas;
        this.rotation = rotation;
        this.width = width;
        this.height = height;
        this.offsetBox2d = offsetBox2d;

        this.walkingAnimations = new HashMap<>();
        this.idleAnimations = new HashMap<>();

        this.walkingAnimations.put(Rotation.FACING_UP, "walking_up");
        this.walkingAnimations.put(Rotation.FACING_DOWN, "walking_down");
        this.walkingAnimations.put(Rotation.FACING_LEFT, "walking_left");
        this.walkingAnimations.put(Rotation.FACING_RIGHT, "walking_right");

        this.idleAnimations.put(Rotation.FACING_UP, "walking_up_idle");
        this.idleAnimations.put(Rotation.FACING_DOWN, "walking_down_idle");
        this.idleAnimations.put(Rotation.FACING_LEFT, "walking_left_idle");
        this.idleAnimations.put(Rotation.FACING_RIGHT, "walking_right_idle");
    }

    /**
     * Load this renderer.
     */
    public abstract void load();

    /**
     * Update this renderer
     *
     * @param delta       delta time
     * @param rotation    rotation of the player.
     * @param hasVelocity if the player has velocity.
     */
    public abstract void update(float delta, Rotation rotation, boolean hasVelocity);

    /**
     * Draw
     *
     * @param delta the delta time
     * @param x     x location to draw at
     * @param y     y location to draw at
     * @param batch the batch
     */
    public abstract void render(float delta, float x, float y, SpriteBatch batch);

    /**
     * offset the player position to fit within the box2d bounds
     *
     * @param x     x location to draw at
     * @param y     y location to draw at
     * @param batch batch
     */
    protected void drawIdleState(float x, float y, SpriteBatch batch) {
        if (offsetBox2d) {
            batch.draw(idle,
                    x - (width) / 2f,
                    y - (height) / 2f,
                    width,
                    height);
        } else {
            batch.draw(idle,
                    x,
                    y,
                    width,
                    height);
        }
    }

    /**
     * offset the player position to fit within the box2d bounds
     *
     * @param x     x location to draw at
     * @param y     y location to draw at
     * @param batch batch
     */
    protected void drawAnimationFrame(float x, float y, SpriteBatch batch) {
        if (offsetBox2d) {
            batch.draw(animation.getKeyFrame(animationTime),
                    x - (width) / 2f,
                    y - (height) / 2f,
                    width,
                    height);
        } else {
            batch.draw(animation.getKeyFrame(animationTime),
                    x,
                    y,
                    width,
                    height);
        }
    }

    /**
     * Create a new animation
     *
     * @param region the region name
     * @param atlas  the textures
     * @param loop   if the animation should be looping.
     * @return a new {@link Animation}
     */
    protected Animation<TextureRegion> createAnimation(String region, TextureAtlas atlas, boolean loop) {
        final Animation<TextureRegion> animation = new Animation<>(0.25f, atlas.findRegion(region, 1), atlas.findRegion(region, 2));
        if (loop) animation.setPlayMode(Animation.PlayMode.LOOP);
        return animation;
    }

    @Override
    public void dispose() {
        this.animations.clear();
        this.idle = null;
        this.animation = null;
        this.walkingAnimations.clear();
        this.idleAnimations.clear();
        this.atlas.dispose();
    }

    /**
     * Represents a complete pair of a movement stage.
     * The animation and idle state of the direction player is moving.
     */
    protected static class AnimationPair {
        protected final Animation<TextureRegion> animation;
        protected final TextureRegion idle;

        public AnimationPair(Animation<TextureRegion> animation, TextureRegion idle) {
            this.animation = animation;
            this.idle = idle;
        }
    }


}
