package lunar.shared.components.prop;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import lunar.shared.utility.BasicDirection;

/**
 * Manages properties about an entity
 */
public class EntityPropertiesComponent implements Component, Pool.Poolable {

    public int entityId;
    public String entityName;
    public float width, height, scaling;
    public float speed;
    public float health;
    public float angle;
    public BasicDirection direction;
    public boolean isMoving;

    public void setProperties(int entityId, String entityName) {
        this.entityId = entityId;
        this.entityName = entityName;
    }

    /**
     * Set the size of this entity
     *
     * @param width      the width
     * @param height     the height
     * @param worldScale a world scale.
     */
    public void setEntitySize(float width, float height, float worldScale) {
        this.width = width;
        this.height = height;
        this.scaling = worldScale;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getScaling() {
        return scaling;
    }

    public float getScaledWidth() {
        return width * scaling;
    }

    public float getScaledHeight() {
        return height * scaling;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public BasicDirection getDirection() {
        return direction;
    }

    public void setDirection(BasicDirection direction) {
        this.direction = direction;
    }

    @Override
    public void reset() {
        entityId = -1;
        entityName = null;
        speed = 0.0f;
        health = 0.0f;
        isMoving = false;
    }
}
