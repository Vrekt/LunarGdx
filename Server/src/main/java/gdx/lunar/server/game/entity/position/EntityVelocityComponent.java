package gdx.lunar.server.game.entity.position;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Manages data for an entities' velocity.
 */
public class EntityVelocityComponent implements Component, Pool.Poolable {

    public final Vector2 velocity = new Vector2(0, 0);
    public final Vector2 force = new Vector2(0, 0);
    public final Vector2 point = new Vector2(0, 0);

    public boolean hasForceApplied;

    public void setForce(float fx, float fy, float px, float py) {
        force.set(fx, fy);
        point.set(px, py);

        if (fx == 0.0 && fy == 0.0) {
            hasForceApplied = false;
        } else {
            hasForceApplied = true;
        }
    }

    @Override
    public void reset() {
        velocity.set(0, 0);
    }
}
