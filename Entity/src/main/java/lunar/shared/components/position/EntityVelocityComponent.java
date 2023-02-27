package lunar.shared.components.position;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * Manages data for an entities' velocity.
 */
public class EntityVelocityComponent implements Component, Pool.Poolable {

    public final Vector2 velocity = new Vector2(0, 0);
    public float forceX, forceY, pointX, pointY;


    public void setForce(float fx, float fy, float px, float py) {
        this.forceX = fx;
        this.forceY = fy;
        this.pointX = px;
        this.pointY = py;
    }

    @Override
    public void reset() {
        velocity.set(0, 0);
    }
}
