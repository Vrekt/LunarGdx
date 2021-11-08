package gdx.lunar.entity.mapping;

import com.badlogic.ashley.core.ComponentMapper;
import gdx.lunar.entity.components.config.EntityConfigurationComponent;
import gdx.lunar.entity.components.drawing.EntityTextureComponent;
import gdx.lunar.entity.components.instance.EntityInstanceComponent;
import gdx.lunar.entity.components.position.EntityPositionComponent;
import gdx.lunar.entity.components.position.EntityVelocityComponent;
import gdx.lunar.entity.components.prop.EntityPropertiesComponent;

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
