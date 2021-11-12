package gdx.lunar.entity.system.animation;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import gdx.lunar.entity.components.drawing.EntityAnimation;
import gdx.lunar.entity.components.drawing.EntityAnimationComponent;
import gdx.lunar.entity.components.position.EntityPositionComponent;
import gdx.lunar.entity.player.LunarEntity;

/**
 * Basic animation system for entities.
 */
public class EntityAnimationSystem extends IteratingSystem {

    protected ComponentMapper<EntityPositionComponent> p;
    protected ComponentMapper<EntityAnimationComponent> a;

    public EntityAnimationSystem() {
        super(Family.all(EntityPositionComponent.class, EntityAnimationComponent.class).get());
        p = ComponentMapper.getFor(EntityPositionComponent.class);
        a = ComponentMapper.getFor(EntityAnimationComponent.class);
    }

    public EntityAnimationSystem(Family family, int priority) {
        super(family, priority);

        p = ComponentMapper.getFor(EntityPositionComponent.class);
        a = ComponentMapper.getFor(EntityAnimationComponent.class);
    }

    public void setPositionComponentMapper(ComponentMapper<EntityPositionComponent> p) {
        this.p = p;
    }

    public void setAnimationComponentMapper(ComponentMapper<EntityAnimationComponent> a) {
        this.a = a;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final EntityAnimationComponent component = a.get(entity);
        for (int i = 0; i < component.animationsPlaying.size(); i++) {
            final EntityAnimation animation = component.animations.get(i);
            if (animation.animate) {
                animation.animationTime += deltaTime;
            } else {
                animation.animationTime = 0.0f;
            }
        }
    }

    /**
     * Render all playing entity animations
     *
     * @param entity the entity
     * @param batch  drawing batch
     */
    public void renderEntityAnimation(LunarEntity entity, SpriteBatch batch) {
        final EntityAnimationComponent component = a.get(entity.getEntity());
        if (component.animationsPlaying.isEmpty()) {
            // indicates this entity is idle, play idle animation.
            drawIdleState(entity.getPosition(), entity.getConfig().size, entity.getConfig().offset, a.get(entity.getEntity()).getIdleAnimationFrame(), batch);
        } else {
            for (int i = 0; i < component.animationsPlaying.getCapacity(); i++) {
                final EntityAnimation animation = component.animations.get(i);
                if (animation.animate) {
                    drawAnimation(entity.getPosition(), entity.getConfig().size, entity.getConfig().offset, animation.getFrame(), batch);
                }
            }
        }
    }

    /**
     * offset the player position to fit within the box2d bounds
     *
     * @param offsetBox2d see {@link gdx.lunar.entity.components.config.EntityConfigurationComponent}
     * @param idle        texture
     * @param batch       batch
     */
    protected void drawIdleState(Vector2 position, Vector3 size, boolean offsetBox2d, TextureRegion idle, SpriteBatch batch) {
        if (offsetBox2d) {
            batch.draw(idle,
                    position.x - (size.x) / 2f,
                    position.y - (size.y) / 2f,
                    size.x,
                    size.y);
        } else {
            batch.draw(idle,
                    position.x,
                    position.y,
                    size.x,
                    size.y);
        }
    }

    /**
     * offset the player position to fit within the box2d bounds
     *
     * @param offsetBox2d see {@link gdx.lunar.entity.components.config.EntityConfigurationComponent}
     * @param frame       texture
     * @param batch       batch
     */
    protected void drawAnimation(Vector2 position, Vector3 size, boolean offsetBox2d, TextureRegion frame, SpriteBatch batch) {
        if (offsetBox2d) {
            batch.draw(frame,
                    position.x - (size.x) / 2f,
                    position.y - (size.y) / 2f,
                    size.x,
                    size.y);
        } else {
            batch.draw(frame,
                    position.x,
                    position.y,
                    size.x,
                    size.y);
        }
    }

}
