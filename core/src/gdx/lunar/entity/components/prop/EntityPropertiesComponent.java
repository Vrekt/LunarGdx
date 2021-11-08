package gdx.lunar.entity.components.prop;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;

/**
 * Manages data about an entities ID and name.
 */
public class EntityPropertiesComponent implements Component, Pool.Poolable {

    public int entityId;
    public String entityName;

    // health and speed effects
    public float speed;
    public float health;

    // if the entity is moving.
    public boolean isMoving;

    @Override
    public void reset() {
        entityId = 0;
        entityName = null;

        speed = 0.0f;
        health = 0.0f;
        isMoving = false;
    }
}
