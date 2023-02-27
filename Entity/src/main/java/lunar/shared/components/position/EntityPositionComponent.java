package lunar.shared.components.position;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Manages data for an entities position.
 */
public class EntityPositionComponent implements Component, Pool.Poolable {

    public final Vector2 interpolated = new Vector2(0, 0);
    public final Vector2 previous = new Vector2(0, 0);
    public final Vector2 position = new Vector2(0, 0);

    @Override
    public void reset() {
        interpolated.set(0, 0);
        previous.set(0, 0);
        position.set(0, 0);
    }
}
