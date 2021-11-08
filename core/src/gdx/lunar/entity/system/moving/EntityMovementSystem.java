package gdx.lunar.entity.system.moving;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import gdx.lunar.entity.components.position.EntityPositionComponent;
import gdx.lunar.entity.components.position.EntityVelocityComponent;
import gdx.lunar.entity.components.prop.EntityPropertiesComponent;

/**
 * Basic movement system for entities.
 * <p>
 * Allows easy extension of this class to implement any other logic you may desire.
 */
public class EntityMovementSystem extends IteratingSystem {
    protected ComponentMapper<EntityPositionComponent> p;
    protected ComponentMapper<EntityVelocityComponent> v;
    protected ComponentMapper<EntityPropertiesComponent> prop;

    public EntityMovementSystem() {
        super(Family.all(EntityPositionComponent.class, EntityVelocityComponent.class).get());
        p = ComponentMapper.getFor(EntityPositionComponent.class);
        v = ComponentMapper.getFor(EntityVelocityComponent.class);
        prop = ComponentMapper.getFor(EntityPropertiesComponent.class);
    }

    public EntityMovementSystem(Family family, int priority) {
        super(family, priority);

        p = ComponentMapper.getFor(EntityPositionComponent.class);
        v = ComponentMapper.getFor(EntityVelocityComponent.class);
    }

    public void setPositionComponentMapper(ComponentMapper<EntityPositionComponent> p) {
        this.p = p;
    }

    public void setVelocityComponentMapper(ComponentMapper<EntityVelocityComponent> v) {
        this.v = v;
    }

    public void setPropertiesComponentMapper(ComponentMapper<EntityPropertiesComponent> prop) {
        this.prop = prop;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final EntityPropertiesComponent prop = this.prop.get(entity);
        if (prop.isMoving) {
            handleEntityMovement(entity, p.get(entity), v.get(entity), prop, deltaTime);
        }
    }

    /**
     * Handle entity movement
     *
     * @param entity the entity
     * @param pm     position
     * @param pv     vel
     * @param prop   prop
     */
    protected void handleEntityMovement(Entity entity,
                                        EntityPositionComponent pm,
                                        EntityVelocityComponent pv,
                                        EntityPropertiesComponent prop,
                                        float delta) {
        pm.position.x += pv.velocity.x * delta;
        pm.position.y += pv.velocity.y * delta;
    }

}
