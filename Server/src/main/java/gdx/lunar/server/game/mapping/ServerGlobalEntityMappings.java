package gdx.lunar.server.game.mapping;

import com.badlogic.ashley.core.ComponentMapper;
import gdx.lunar.server.game.entity.instance.EntityInstanceComponent;
import gdx.lunar.server.game.entity.position.EntityPositionComponent;
import gdx.lunar.server.game.entity.position.EntityVelocityComponent;

public class ServerGlobalEntityMappings {

    public static final ComponentMapper<EntityInstanceComponent> instance = ComponentMapper.getFor(EntityInstanceComponent.class);
    public static final ComponentMapper<EntityPositionComponent> position = ComponentMapper.getFor(EntityPositionComponent.class);
    public static final ComponentMapper<EntityVelocityComponent> velocity = ComponentMapper.getFor(EntityVelocityComponent.class);
}
