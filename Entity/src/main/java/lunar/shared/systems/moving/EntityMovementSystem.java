package lunar.shared.systems.moving;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import lunar.shared.components.position.EntityPositionComponent;
import lunar.shared.components.position.EntityVelocityComponent;
import lunar.shared.components.prop.EntityPropertiesComponent;

/**
 * Basic movement system for entities.
 * <p>
 * Allows easy extension of this class to implement any other logic you may desire.
 */
public class EntityMovementSystem extends IteratingSystem {
    protected ComponentMapper<EntityPositionComponent> p;
    protected ComponentMapper<EntityVelocityComponent> v;
    protected ComponentMapper<EntityPropertiesComponent> prop;

    protected float velocityFactor = 0.01f;

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

    public void setVelocityFactor(float velocityFactor) {
        this.velocityFactor = velocityFactor;
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        final EntityPropertiesComponent prop = this.prop.get(entity);
        if (prop.isMoving) handleEntityMovement(p.get(entity), v.get(entity));
    }

    /**
     * Handle entity movement
     *
     * @param pm position
     * @param pv vel
     */
    protected void handleEntityMovement(
            EntityPositionComponent pm,
            EntityVelocityComponent pv) {
        pm.position.x += pv.velocity.x * velocityFactor;
        pm.position.y += pv.velocity.y * velocityFactor;
    }

}
