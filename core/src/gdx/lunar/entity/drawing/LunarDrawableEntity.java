package gdx.lunar.entity.drawing;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import gdx.lunar.entity.LunarEntity;

/**
 * An entity that has the ability to be drawn.
 */
public abstract class LunarDrawableEntity extends LunarEntity {

    /**
     * Rotation of the entity.
     */
    protected Rotation rotation;

    /**
     * The Box2d body for this entity.
     */
    protected Body body;

    public LunarDrawableEntity(int entityId) {
        super(entityId);
    }

    public LunarDrawableEntity() {
        super();
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Render this entity
     *
     * @param batch batching
     * @param delta the delta
     */
    public abstract void render(SpriteBatch batch, float delta);
}
