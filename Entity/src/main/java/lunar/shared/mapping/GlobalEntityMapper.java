package lunar.shared.mapping;

import com.badlogic.ashley.core.ComponentMapper;
import lunar.shared.components.drawing.EntityTextureComponent;
import lunar.shared.components.position.EntityPositionComponent;
import lunar.shared.components.position.EntityVelocityComponent;
import lunar.shared.components.prop.EntityPropertiesComponent;

/**
 * Global mappings for all components within LunarProtocolSettings.
 */
public final class GlobalEntityMapper {
    public static final ComponentMapper<EntityTextureComponent> texture = ComponentMapper.getFor(EntityTextureComponent.class);
    public static final ComponentMapper<EntityPositionComponent> position = ComponentMapper.getFor(EntityPositionComponent.class);
    public static final ComponentMapper<EntityVelocityComponent> velocity = ComponentMapper.getFor(EntityVelocityComponent.class);
    public static final ComponentMapper<EntityPropertiesComponent> properties = ComponentMapper.getFor(EntityPropertiesComponent.class);
}
