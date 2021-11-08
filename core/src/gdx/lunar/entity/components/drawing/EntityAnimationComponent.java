package gdx.lunar.entity.components.drawing;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages data about all entity animations
 */
public class EntityAnimationComponent implements Component, Pool.Poolable {

    // animations currently playing.
    public Bag<Integer> animationsPlaying = new Bag<>();

    // player animations sorted by their ID.
    public final Map<Integer, EntityAnimation> animations = new ConcurrentHashMap<>();

    // default idle animation ID.
    public int idleAnimationId;

    public TextureRegion getIdleAnimationFrame() {
        return animations.get(idleAnimationId).getFrame();
    }

    /**
     * Retrieve the current frame of the animation ID.
     *
     * @param id id
     * @return the frame
     */
    public TextureRegion getFrameOf(int id) {
        return animations.get(id).getFrame();
    }

    @Override
    public void reset() {
        animationsPlaying.clear();
        animations.clear();
    }
}
