package lunar.shared.entity.mapping;

import com.badlogic.ashley.core.ComponentMapper;
import lunar.shared.entity.components.config.EntityConfigurationComponent;
import lunar.shared.entity.components.drawing.EntityTextureComponent;
import lunar.shared.entity.components.instance.EntityInstanceComponent;
import lunar.shared.entity.components.position.EntityPositionComponent;
import lunar.shared.entity.components.position.EntityVelocityComponent;
import lunar.shared.entity.components.prop.EntityPropertiesComponent;

/**
 * Global mappings for all components within Lunar.
 */
public final class GlobalEntityMapper {

    public static final ComponentMapper<EntityConfigurationComponent> config = ComponentMapper.getFor(EntityConfigurationComponent.class);
    public static final ComponentMapper<EntityTextureComponent> texture = ComponentMapper.getFor(EntityTextureComponent.class);
    public static final ComponentMapper<EntityInstanceComponent> instance = ComponentMapper.getFor(EntityInstanceComponent.class);
    public static final ComponentMapper<EntityPositionComponent> position = ComponentMapper.getFor(EntityPositionComponent.class);
    public static final ComponentMapper<EntityVelocityComponent> velocity = ComponentMapper.getFor(EntityVelocityComponent.class);
    public static final ComponentMapper<EntityPropertiesComponent> properties = ComponentMapper.getFor(EntityPropertiesComponent.class);

}
