package lunar.shared.components;

import com.badlogic.ashley.core.ComponentMapper;

/**
 * Global mappings for all components within LunarProtocolSettings.
 */
public final class GlobalEntityMapper {
    public static final ComponentMapper<EntityTextureComponent> texture = ComponentMapper.getFor(EntityTextureComponent.class);
    public static final ComponentMapper<EntityTransformComponent> transform = ComponentMapper.getFor(EntityTransformComponent.class);
    public static final ComponentMapper<EntityPropertiesComponent> properties = ComponentMapper.getFor(EntityPropertiesComponent.class);
}
