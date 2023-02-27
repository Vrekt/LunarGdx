package lunar.shared.components.prop;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

/**
 * Manages data about an entities ID and name.
 */
public class EntityPropertiesComponent implements Component, Pool.Poolable {

    public int entityId;
    public String entityName;

    // width, height, scaling
    public Vector3 size = new Vector3();

    // if offset should be used with Box2d.
    // This will put players inside the Box2d bounding box.
    public boolean offset = true;

    // health and speed effects
    public float speed;
    public float health;

    // if the entity is moving.
    public boolean isMoving;

    public void setProperties(int entityId, String entityName) {
        this.entityId = entityId;
        this.entityName = entityName;
    }

    public void setConfig(float width, float height, float scaling) {
        size.set(width, height, scaling);
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public float getScaling() {
        return size.z;
    }

    public float getScaledWidth() {
        return size.x * size.z;
    }

    public float getScaledHeight() {
        return size.y * size.z;
    }

    @Override
    public void reset() {
        entityId = 0;
        entityName = null;

        speed = 0.0f;
        health = 0.0f;
        isMoving = false;
    }
}
